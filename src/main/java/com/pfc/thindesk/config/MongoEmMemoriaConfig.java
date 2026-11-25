package com.pfc.thindesk.config;

import com.mongodb.ConnectionString;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;

/**
 * Quando a variável MONGODB_URI não está definida (ou está vazia), o sistema sobe
 * um MongoDB em memória. Assim qualquer pessoa roda o projeto sem configurar nada
 * (os dados somem ao reiniciar). Com MONGODB_URI, usa o MongoDB Atlas.
 */
@Configuration
@ConditionalOnExpression("'${MONGODB_URI:}'.isBlank()")
public class MongoEmMemoriaConfig {

    private static final Logger log = LoggerFactory.getLogger(MongoEmMemoriaConfig.class);

    private final int porta = portaLivre();

    @Bean(destroyMethod = "shutdown")
    public MongoServer mongoServer() {
        MongoServer servidor = new MongoServer(new MemoryBackend());
        servidor.bind("127.0.0.1", porta);
        log.warn("MONGODB_URI não definida: usando MongoDB EM MEMÓRIA na porta {} (dados não são persistidos).", porta);
        return servidor;
    }

    /**
     * Endereço do banco em memória. Substitui a propriedade spring.data.mongodb.uri,
     * e o Spring Boot cria o MongoClient a partir dele.
     */
    @Bean
    public MongoConnectionDetails mongoConnectionDetails(MongoServer mongoServer) {
        ConnectionString endereco = new ConnectionString("mongodb://127.0.0.1:" + porta);
        return () -> endereco;
    }

    private static int portaLivre() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível reservar uma porta para o MongoDB em memória", e);
        }
    }
}