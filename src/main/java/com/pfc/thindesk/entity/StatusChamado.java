package com.pfc.thindesk.entity;

/** Mesmos status do ThinDesk original (ABERTO, EM_ANDAMENTO, CONCLUIDO), mais CANCELADO. */
public enum StatusChamado {
    ABERTO("Aberto", "primary"),
    EM_ANDAMENTO("Em andamento", "warning"),
    CONCLUIDO("Concluído", "success"),
    CANCELADO("Cancelado", "secondary");

    private final String descricao;
    private final String cor;

    StatusChamado(String descricao, String cor) {
        this.descricao = descricao;
        this.cor = cor;
    }

    public String getDescricao() {
        return descricao;
    }

    /** Cor do Bootstrap usada nos badges e cards. */
    public String getCor() {
        return cor;
    }
}
