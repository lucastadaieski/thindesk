package com.pfc.thindesk.entity;

public enum Prioridade {
    BAIXA("Baixa", "secondary"),
    MEDIA("Média", "info"),
    ALTA("Alta", "danger");

    private final String descricao;
    private final String cor;

    Prioridade(String descricao, String cor) {
        this.descricao = descricao;
        this.cor = cor;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCor() {
        return cor;
    }
}
