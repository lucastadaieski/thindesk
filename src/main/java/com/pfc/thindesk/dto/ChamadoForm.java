package com.pfc.thindesk.dto;

import com.pfc.thindesk.entity.Prioridade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Dados digitados na abertura de um chamado. */
public class ChamadoForm {

    @NotBlank(message = "Informe um título")
    @Size(max = 100, message = "O título pode ter no máximo 100 caracteres")
    private String titulo;

    @NotBlank(message = "Descreva o problema")
    @Size(max = 2000, message = "A descrição pode ter no máximo 2000 caracteres")
    private String descricao;

    @NotBlank(message = "Escolha o tipo do chamado")
    private String tipo;

    @NotNull(message = "Escolha a prioridade")
    private Prioridade prioridade = Prioridade.MEDIA;

    private String cliente;

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Prioridade getPrioridade() { return prioridade; }
    public void setPrioridade(Prioridade prioridade) { this.prioridade = prioridade; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
}
