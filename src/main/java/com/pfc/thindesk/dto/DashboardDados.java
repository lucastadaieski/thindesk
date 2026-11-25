package com.pfc.thindesk.dto;

import com.pfc.thindesk.entity.Chamado;

import java.util.List;
import java.util.Map;

/** Números exibidos no dashboard. */
public class DashboardDados {

    private final long total;
    private final long abertos;
    private final long emAndamento;
    private final long concluidos;
    private final long cancelados;
    private final long comigo;
    private final Map<String, Long> porTipo;
    private final Map<String, Long> porPrioridade;
    private final List<Chamado> recentes;

    public DashboardDados(long total, long abertos, long emAndamento, long concluidos, long cancelados,
                          long comigo, Map<String, Long> porTipo, Map<String, Long> porPrioridade,
                          List<Chamado> recentes) {
        this.total = total;
        this.abertos = abertos;
        this.emAndamento = emAndamento;
        this.concluidos = concluidos;
        this.cancelados = cancelados;
        this.comigo = comigo;
        this.porTipo = porTipo;
        this.porPrioridade = porPrioridade;
        this.recentes = recentes;
    }

    /** Percentual de chamados concluídos entre os que não foram cancelados. */
    public long getTaxaConclusao() {
        long validos = total - cancelados;
        return validos == 0 ? 0 : Math.round(concluidos * 100.0 / validos);
    }

    public long getTotal() { return total; }
    public long getAbertos() { return abertos; }
    public long getEmAndamento() { return emAndamento; }
    public long getConcluidos() { return concluidos; }
    public long getCancelados() { return cancelados; }
    public long getComigo() { return comigo; }
    public Map<String, Long> getPorTipo() { return porTipo; }
    public Map<String, Long> getPorPrioridade() { return porPrioridade; }
    public List<Chamado> getRecentes() { return recentes; }
}
