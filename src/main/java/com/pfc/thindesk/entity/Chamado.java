package com.pfc.thindesk.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Chamado técnico. Mantém os campos do ThinDesk original
 * (descricao, status, tipo, tecnico, usuario) e acrescenta título, cliente,
 * prioridade, solução e as datas do ciclo de vida.
 */
@Document(collection = "chamados")
public class Chamado {

    @Id
    private String id;
    private String titulo;
    private String descricao;
    private StatusChamado status;
    private String tipo;
    private Prioridade prioridade;
    private String cliente;

    // Quem abriu (preenchido automaticamente)
    private String usuario;
    private String usuarioEmail;

    // Técnico responsável (preenchido quando um técnico assume)
    private String tecnico;
    private String tecnicoEmail;

    private String solucao;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataAtualizacao;
    private LocalDateTime dataConclusao;

    public boolean isFinalizado() {
        return status == StatusChamado.CONCLUIDO || status == StatusChamado.CANCELADO;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public StatusChamado getStatus() { return status; }
    public void setStatus(StatusChamado status) { this.status = status; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Prioridade getPrioridade() { return prioridade; }
    public void setPrioridade(Prioridade prioridade) { this.prioridade = prioridade; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getUsuarioEmail() { return usuarioEmail; }
    public void setUsuarioEmail(String usuarioEmail) { this.usuarioEmail = usuarioEmail; }

    public String getTecnico() { return tecnico; }
    public void setTecnico(String tecnico) { this.tecnico = tecnico; }

    public String getTecnicoEmail() { return tecnicoEmail; }
    public void setTecnicoEmail(String tecnicoEmail) { this.tecnicoEmail = tecnicoEmail; }

    public String getSolucao() { return solucao; }
    public void setSolucao(String solucao) { this.solucao = solucao; }

    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    public LocalDateTime getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDateTime dataConclusao) { this.dataConclusao = dataConclusao; }
}
