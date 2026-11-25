package com.pfc.thindesk.entity;

public enum Perfil {
    USUARIO("Usuário"),
    TECNICO("Técnico"),
    ADMIN("Administrador");

    private final String descricao;

    Perfil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
