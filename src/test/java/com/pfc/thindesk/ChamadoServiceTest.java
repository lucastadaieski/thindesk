package com.pfc.thindesk;

import com.pfc.thindesk.config.UsuarioLogado;
import com.pfc.thindesk.dto.ChamadoForm;
import com.pfc.thindesk.dto.DashboardDados;
import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.entity.Perfil;
import com.pfc.thindesk.entity.Prioridade;
import com.pfc.thindesk.entity.StatusChamado;
import com.pfc.thindesk.entity.Usuario;
import com.pfc.thindesk.repository.ChamadoRepository;
import com.pfc.thindesk.service.ChamadoService;
import com.pfc.thindesk.service.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Testes das regras de negócio dos chamados (sem banco). */
class ChamadoServiceTest {

    private ChamadoRepository repository;
    private ChamadoService service;

    private final UsuarioLogado maria = logado("u1", "Maria", "maria@teste.com", Perfil.USUARIO);
    private final UsuarioLogado pedro = logado("u2", "Pedro", "pedro@teste.com", Perfil.USUARIO);
    private final UsuarioLogado joao = logado("t1", "João", "joao@teste.com", Perfil.TECNICO);

    @BeforeEach
    void setUp() {
        repository = mock(ChamadoRepository.class);
        when(repository.save(any(Chamado.class))).thenAnswer(inv -> inv.getArgument(0));
        service = new ChamadoService(repository);
    }

    @Test
    void abrirPreencheSolicitanteStatusEData() {
        ChamadoForm form = new ChamadoForm();
        form.setTitulo("  Impressora parada ");
        form.setDescricao("Não imprime");
        form.setTipo("Impressora");
        form.setPrioridade(Prioridade.ALTA);

        Chamado chamado = service.abrir(form, maria);

        assertEquals("Impressora parada", chamado.getTitulo());
        assertEquals(StatusChamado.ABERTO, chamado.getStatus());
        assertEquals("maria@teste.com", chamado.getUsuarioEmail());
        assertEquals("Maria", chamado.getUsuario());
        assertNotNull(chamado.getDataAbertura());
    }

    @Test
    void usuarioNaoVeChamadoDeOutraPessoa() {
        when(repository.findById("c1")).thenReturn(Optional.of(chamado("c1", maria, StatusChamado.ABERTO)));
        assertThrows(AccessDeniedException.class, () -> service.buscar("c1", pedro));
    }

    @Test
    void tecnicoVeQualquerChamado() {
        when(repository.findById("c1")).thenReturn(Optional.of(chamado("c1", maria, StatusChamado.ABERTO)));
        assertEquals("c1", service.buscar("c1", joao).getId());
    }

    @Test
    void assumirColocaEmAndamentoComTecnico() {
        when(repository.findById("c1")).thenReturn(Optional.of(chamado("c1", maria, StatusChamado.ABERTO)));

        Chamado chamado = service.assumir("c1", joao);

        assertEquals(StatusChamado.EM_ANDAMENTO, chamado.getStatus());
        assertEquals("joao@teste.com", chamado.getTecnicoEmail());
    }

    @Test
    void usuarioComumNaoAssume() {
        assertThrows(AccessDeniedException.class, () -> service.assumir("c1", maria));
    }

    @Test
    void concluirExigeSolucao() {
        when(repository.findById("c1")).thenReturn(Optional.of(chamado("c1", maria, StatusChamado.EM_ANDAMENTO)));
        assertThrows(RegraNegocioException.class, () -> service.concluir("c1", "  ", joao));
        verify(repository, never()).save(any());
    }

    @Test
    void concluirGravaSolucaoEData() {
        when(repository.findById("c1")).thenReturn(Optional.of(chamado("c1", maria, StatusChamado.EM_ANDAMENTO)));

        Chamado chamado = service.concluir("c1", "Troquei o toner", joao);

        assertEquals(StatusChamado.CONCLUIDO, chamado.getStatus());
        assertEquals("Troquei o toner", chamado.getSolucao());
        assertNotNull(chamado.getDataConclusao());
    }

    @Test
    void chamadoConcluidoNaoPodeSerAlterado() {
        when(repository.findById("c1")).thenReturn(Optional.of(chamado("c1", maria, StatusChamado.CONCLUIDO)));
        assertThrows(RegraNegocioException.class, () -> service.concluir("c1", "outra", joao));
        assertThrows(RegraNegocioException.class, () -> service.cancelar("c1", joao));
    }

    @Test
    void usuarioNaoCancelaChamadoEmAtendimento() {
        when(repository.findById("c1")).thenReturn(Optional.of(chamado("c1", maria, StatusChamado.EM_ANDAMENTO)));
        assertThrows(RegraNegocioException.class, () -> service.cancelar("c1", maria));
    }

    @Test
    void usuarioCancelaChamadoAberto() {
        when(repository.findById("c1")).thenReturn(Optional.of(chamado("c1", maria, StatusChamado.ABERTO)));
        assertEquals(StatusChamado.CANCELADO, service.cancelar("c1", maria).getStatus());
    }

    @Test
    void dashboardContaPorStatusETipo() {
        Chamado emAndamento = chamado("c2", maria, StatusChamado.EM_ANDAMENTO);
        emAndamento.setTecnicoEmail("joao@teste.com");
        when(repository.findAllByOrderByDataAberturaDesc()).thenReturn(List.of(
                chamado("c1", maria, StatusChamado.ABERTO),
                emAndamento,
                chamado("c3", pedro, StatusChamado.CONCLUIDO),
                chamado("c4", pedro, StatusChamado.CANCELADO)));

        DashboardDados dados = service.dashboard(joao);

        assertEquals(4, dados.getTotal());
        assertEquals(1, dados.getAbertos());
        assertEquals(1, dados.getEmAndamento());
        assertEquals(1, dados.getConcluidos());
        assertEquals(1, dados.getCancelados());
        assertEquals(1, dados.getComigo());
        assertEquals(4L, dados.getPorTipo().get("Rede"));
        assertEquals(33, dados.getTaxaConclusao());
    }

    private static Chamado chamado(String id, UsuarioLogado dono, StatusChamado status) {
        Chamado c = new Chamado();
        c.setId(id);
        c.setTitulo("Chamado " + id);
        c.setDescricao("Teste");
        c.setTipo("Rede");
        c.setPrioridade(Prioridade.MEDIA);
        c.setUsuario(dono.getNome());
        c.setUsuarioEmail(dono.getEmail());
        c.setStatus(status);
        return c;
    }

    private static UsuarioLogado logado(String id, String nome, String email, Perfil perfil) {
        Usuario u = new Usuario(nome, email, "hash", perfil, null);
        u.setId(id);
        return new UsuarioLogado(u);
    }
}
