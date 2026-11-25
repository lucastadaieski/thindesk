package com.pfc.thindesk.config;

import com.pfc.thindesk.entity.Perfil;
import com.pfc.thindesk.entity.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/** Usuário autenticado na sessão. Disponível nos controllers e nas telas. */
public class UsuarioLogado implements UserDetails {

    private final String id;
    private final String nome;
    private final String email;
    private final String senha;
    private final Perfil perfil;

    public UsuarioLogado(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.senha = usuario.getSenha();
        this.perfil = usuario.getPerfil();
    }

    /** Técnicos e administradores atendem chamados e veem todos eles. */
    public boolean isTecnico() {
        return perfil == Perfil.TECNICO || perfil == Perfil.ADMIN;
    }

    public boolean isAdmin() {
        return perfil == Perfil.ADMIN;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public Perfil getPerfil() { return perfil; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + perfil.name()));
    }

    @Override
    public String getPassword() { return senha; }

    @Override
    public String getUsername() { return email; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
