package com.pfc.thindesk;

import com.pfc.thindesk.config.UsuarioLogado;
import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.entity.StatusChamado;
import com.pfc.thindesk.repository.ChamadoRepository;
import com.pfc.thindesk.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Sobe o sistema inteiro com o MongoDB em memória e testa login, telas e o fluxo
 * completo de um chamado. Garante que todas as telas Thymeleaf renderizam.
 */
@SpringBootTest(properties = "MONGODB_URI=")
@AutoConfigureMockMvc
class ThindeskIntegracaoTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ChamadoRepository chamadoRepository;

    private UsuarioLogado logado(String email) {
        return new UsuarioLogado(usuarioRepository.findByEmail(email).orElseThrow());
    }

    @Test
    void telaDeLoginAbreComCredenciaisDeDemonstracao() throws Exception {
        mvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Acesso de demonstração")));
    }

    @Test
    void semLoginRedirecionaParaLogin() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void loginComUsuarioDeDemonstracao() throws Exception {
        mvc.perform(formLogin("/login").user("email", "tecnico@thindesk.com").password("senha", "tecnico123"))
                .andExpect(authenticated())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void loginComSenhaErradaFalha() throws Exception {
        mvc.perform(formLogin("/login").user("email", "tecnico@thindesk.com").password("senha", "errada"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?erro"));
    }

    @Test
    void dashboardAbreParaOsTresPerfis() throws Exception {
        for (String email : List.of("admin@thindesk.com", "tecnico@thindesk.com", "usuario@thindesk.com")) {
            mvc.perform(get("/").with(user(logado(email))))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Chamados por status")))
                    .andExpect(content().string(containsString("versão")));
        }
    }

    @Test
    void todasAsTelasAbremParaOAdministrador() throws Exception {
        UsuarioLogado admin = logado("admin@thindesk.com");
        for (String url : List.of("/chamados", "/chamados?status=ABERTO", "/chamados/novo", "/clientes",
                "/ajustes-horarios", "/tipo-chamado", "/tecnicos")) {
            mvc.perform(get(url).with(user(admin))).andExpect(status().isOk());
        }
    }

    @Test
    void usuarioComumNaoAcessaTelasDeTecnico() throws Exception {
        UsuarioLogado usuario = logado("usuario@thindesk.com");
        mvc.perform(get("/tecnicos").with(user(usuario))).andExpect(status().isForbidden());
        mvc.perform(post("/clientes/salvar").param("nome", "X").with(csrf()).with(user(usuario)))
                .andExpect(status().isForbidden());
    }

    @Test
    void formularioInvalidoMostraErros() throws Exception {
        mvc.perform(post("/chamados/novo").param("titulo", "").param("descricao", "").param("tipo", "")
                        .param("prioridade", "MEDIA").with(csrf()).with(user(logado("usuario@thindesk.com"))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Informe um título")));
    }

    @Test
    void fluxoCompletoDoChamado() throws Exception {
        UsuarioLogado usuario = logado("usuario@thindesk.com");
        UsuarioLogado tecnico = logado("tecnico@thindesk.com");

        MvcResult abertura = mvc.perform(post("/chamados/novo")
                        .param("titulo", "Computador não liga")
                        .param("descricao", "Nenhuma luz acende")
                        .param("tipo", "Hardware")
                        .param("prioridade", "ALTA")
                        .with(csrf()).with(user(usuario)))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        String id = abertura.getResponse().getRedirectedUrl().replace("/chamados/", "");

        mvc.perform(get("/chamados/" + id).with(user(usuario)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Computador não liga")));

        mvc.perform(post("/chamados/" + id + "/assumir").with(csrf()).with(user(tecnico)))
                .andExpect(flash().attributeExists("sucesso"));
        mvc.perform(post("/chamados/" + id + "/concluir").param("solucao", "Fonte trocada")
                        .with(csrf()).with(user(tecnico)))
                .andExpect(flash().attributeExists("sucesso"));

        Chamado chamado = chamadoRepository.findById(id).orElseThrow();
        assertEquals(StatusChamado.CONCLUIDO, chamado.getStatus());
        assertEquals("tecnico@thindesk.com", chamado.getTecnicoEmail());

        // Chamado concluído não pode ser alterado
        mvc.perform(post("/chamados/" + id + "/cancelar").with(csrf()).with(user(tecnico)))
                .andExpect(flash().attributeExists("erro"));
        // Usuário comum não pode concluir
        mvc.perform(post("/chamados/" + id + "/concluir").param("solucao", "x").with(csrf()).with(user(usuario)))
                .andExpect(status().isForbidden());

        mvc.perform(get("/chamados/" + id).with(user(tecnico)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Fonte trocada")));
    }

    @Test
    void chamadoInexistenteDa404() throws Exception {
        mvc.perform(get("/chamados/nao-existe").with(user(logado("admin@thindesk.com"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void dadosDeExemploForamCriados() {
        assertTrue(chamadoRepository.count() >= 7);
    }

    @Test
    void healthCheckPublico() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }
}
