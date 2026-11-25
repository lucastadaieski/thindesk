package com.pfc.thindesk.service;

import com.pfc.thindesk.config.UsuarioLogado;
import com.pfc.thindesk.dto.ChamadoForm;
import com.pfc.thindesk.dto.DashboardDados;
import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.entity.Prioridade;
import com.pfc.thindesk.entity.StatusChamado;
import com.pfc.thindesk.repository.ChamadoRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Regras de negócio dos chamados:
 * - usuário comum vê e cancela só os próprios chamados;
 * - técnico e administrador veem todos, assumem e concluem;
 * - chamado concluído ou cancelado não pode mais ser alterado.
 */
@Service
public class ChamadoService {

    public static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");

    private final ChamadoRepository repository;

    public ChamadoService(ChamadoRepository repository) {
        this.repository = repository;
    }

    public List<Chamado> listar(UsuarioLogado usuario, StatusChamado filtro) {
        List<Chamado> chamados = usuario.isTecnico()
                ? repository.findAllByOrderByDataAberturaDesc()
                : repository.findByUsuarioEmailOrderByDataAberturaDesc(usuario.getEmail());
        if (filtro == null) {
            return chamados;
        }
        return chamados.stream().filter(c -> c.getStatus() == filtro).toList();
    }

    public Chamado buscar(String id, UsuarioLogado usuario) {
        Chamado chamado = repository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Chamado não encontrado"));
        if (!usuario.isTecnico() && !usuario.getEmail().equals(chamado.getUsuarioEmail())) {
            throw new AccessDeniedException("Você só pode ver os seus chamados");
        }
        return chamado;
    }

    public Chamado abrir(ChamadoForm form, UsuarioLogado usuario) {
        Chamado chamado = new Chamado();
        chamado.setTitulo(form.getTitulo().trim());
        chamado.setDescricao(form.getDescricao().trim());
        chamado.setTipo(form.getTipo());
        chamado.setPrioridade(form.getPrioridade() == null ? Prioridade.MEDIA : form.getPrioridade());
        chamado.setCliente(vazioParaNulo(form.getCliente()));
        chamado.setUsuario(usuario.getNome());
        chamado.setUsuarioEmail(usuario.getEmail());
        chamado.setStatus(StatusChamado.ABERTO);
        chamado.setDataAbertura(agora());
        chamado.setDataAtualizacao(chamado.getDataAbertura());
        return repository.save(chamado);
    }

    /** O técnico assume o chamado: status EM_ANDAMENTO e ele vira o responsável. */
    public Chamado assumir(String id, UsuarioLogado tecnico) {
        exigirTecnico(tecnico);
        Chamado chamado = buscar(id, tecnico);
        exigirEmAberto(chamado);
        chamado.setTecnico(tecnico.getNome());
        chamado.setTecnicoEmail(tecnico.getEmail());
        chamado.setStatus(StatusChamado.EM_ANDAMENTO);
        chamado.setDataAtualizacao(agora());
        return repository.save(chamado);
    }

    public Chamado concluir(String id, String solucao, UsuarioLogado tecnico) {
        exigirTecnico(tecnico);
        Chamado chamado = buscar(id, tecnico);
        exigirEmAberto(chamado);
        if (solucao == null || solucao.isBlank()) {
            throw new RegraNegocioException("Descreva a solução para concluir o chamado.");
        }
        if (chamado.getTecnico() == null) {
            chamado.setTecnico(tecnico.getNome());
            chamado.setTecnicoEmail(tecnico.getEmail());
        }
        chamado.setSolucao(solucao.trim());
        chamado.setStatus(StatusChamado.CONCLUIDO);
        chamado.setDataConclusao(agora());
        chamado.setDataAtualizacao(chamado.getDataConclusao());
        return repository.save(chamado);
    }

    /** Quem abriu pode cancelar enquanto ninguém assumiu; técnico pode cancelar qualquer um em aberto. */
    public Chamado cancelar(String id, UsuarioLogado usuario) {
        Chamado chamado = buscar(id, usuario);
        exigirEmAberto(chamado);
        if (!usuario.isTecnico() && chamado.getStatus() != StatusChamado.ABERTO) {
            throw new RegraNegocioException("O chamado já está em atendimento e não pode mais ser cancelado.");
        }
        chamado.setStatus(StatusChamado.CANCELADO);
        chamado.setDataAtualizacao(agora());
        return repository.save(chamado);
    }

    public DashboardDados dashboard(UsuarioLogado usuario) {
        List<Chamado> chamados = listar(usuario, null);

        Map<StatusChamado, Long> porStatus = new LinkedHashMap<>();
        for (StatusChamado s : StatusChamado.values()) {
            porStatus.put(s, 0L);
        }
        Map<String, Long> porTipo = new TreeMap<>();
        Map<String, Long> porPrioridade = new LinkedHashMap<>();
        for (Prioridade p : Prioridade.values()) {
            porPrioridade.put(p.getDescricao(), 0L);
        }
        long comigo = 0;

        for (Chamado c : chamados) {
            if (c.getStatus() != null) {
                porStatus.merge(c.getStatus(), 1L, Long::sum);
            }
            porTipo.merge(c.getTipo() == null ? "Sem tipo" : c.getTipo(), 1L, Long::sum);
            if (c.getPrioridade() != null && !c.isFinalizado()) {
                porPrioridade.merge(c.getPrioridade().getDescricao(), 1L, Long::sum);
            }
            if (c.getStatus() == StatusChamado.EM_ANDAMENTO && usuario.getEmail().equals(c.getTecnicoEmail())) {
                comigo++;
            }
        }

        return new DashboardDados(
                chamados.size(),
                porStatus.get(StatusChamado.ABERTO),
                porStatus.get(StatusChamado.EM_ANDAMENTO),
                porStatus.get(StatusChamado.CONCLUIDO),
                porStatus.get(StatusChamado.CANCELADO),
                comigo,
                porTipo,
                porPrioridade,
                chamados.stream().limit(6).toList());
    }

    private void exigirTecnico(UsuarioLogado usuario) {
        if (!usuario.isTecnico()) {
            throw new AccessDeniedException("Somente técnicos podem atender chamados");
        }
    }

    private void exigirEmAberto(Chamado chamado) {
        if (chamado.isFinalizado()) {
            throw new RegraNegocioException("Este chamado já foi " + chamado.getStatus().getDescricao().toLowerCase()
                    + " e não pode mais ser alterado.");
        }
    }

    private static String vazioParaNulo(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }

    static LocalDateTime agora() {
        return LocalDateTime.now(FUSO).withNano(0);
    }
}
