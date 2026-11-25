package com.pfc.thindesk.service;

import com.pfc.thindesk.dto.UsuarioForm;
import com.pfc.thindesk.entity.Perfil;
import com.pfc.thindesk.entity.Usuario;
import com.pfc.thindesk.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listarEquipe() {
        return repository.findByPerfilInOrderByNomeAsc(List.of(Perfil.TECNICO, Perfil.ADMIN));
    }

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public Usuario cadastrar(UsuarioForm form) {
        String email = form.getEmail().trim().toLowerCase();
        if (repository.existsByEmail(email)) {
            throw new RegraNegocioException("Já existe um usuário com o e-mail " + email + ".");
        }
        if (form.getSenha() == null || form.getSenha().length() < 6) {
            throw new RegraNegocioException("A senha precisa ter pelo menos 6 caracteres.");
        }
        Usuario usuario = new Usuario(form.getNome().trim(), email,
                passwordEncoder.encode(form.getSenha()), form.getPerfil(), form.getSetor());
        return repository.save(usuario);
    }

    /** Cria o usuário só se o e-mail ainda não existir (usado na carga inicial). */
    public void criarSeNaoExistir(String nome, String email, String senha, Perfil perfil, String setor) {
        if (!repository.existsByEmail(email)) {
            repository.save(new Usuario(nome, email, passwordEncoder.encode(senha), perfil, setor));
        }
    }
}
