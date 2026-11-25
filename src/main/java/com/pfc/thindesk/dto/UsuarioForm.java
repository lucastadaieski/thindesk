package com.pfc.thindesk.dto;

import com.pfc.thindesk.entity.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Cadastro de técnicos e usuários feito pelo administrador. */
public class UsuarioForm {

    @NotBlank(message = "Informe o nome")
    private String nome;

    @NotBlank(message = "Informe o e-mail")
    @Email(message = "E-mail inválido")
    private String email;

    @Size(min = 6, message = "A senha precisa ter pelo menos 6 caracteres")
    private String senha;

    @NotNull(message = "Escolha o perfil")
    private Perfil perfil = Perfil.TECNICO;

    private String setor;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }

    public String getSetor() { return setor; }
    public void setSetor(String setor) { this.setor = setor; }
}
