package com.pfc.thindesk.repository;

import com.pfc.thindesk.entity.Perfil;
import com.pfc.thindesk.entity.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByPerfilInOrderByNomeAsc(List<Perfil> perfis);
}
