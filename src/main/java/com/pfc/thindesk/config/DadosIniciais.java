package com.pfc.thindesk.config;

import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.entity.Cliente;
import com.pfc.thindesk.entity.HorarioAtendimento;
import com.pfc.thindesk.entity.Perfil;
import com.pfc.thindesk.entity.Prioridade;
import com.pfc.thindesk.entity.StatusChamado;
import com.pfc.thindesk.entity.TipoChamado;
import com.pfc.thindesk.repository.ChamadoRepository;
import com.pfc.thindesk.repository.ClienteRepository;
import com.pfc.thindesk.repository.HorarioAtendimentoRepository;
import com.pfc.thindesk.repository.TipoChamadoRepository;
import com.pfc.thindesk.service.ChamadoService;
import com.pfc.thindesk.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Carga inicial: cria os usuários de demonstração (um por perfil) e, se o banco
 * estiver vazio, tipos de chamado, clientes, horários e alguns chamados de exemplo,
 * para que o dashboard já mostre dados no primeiro acesso.
 */
@Component
public class DadosIniciais implements CommandLineRunner {

    private final UsuarioService usuarioService;
    private final TipoChamadoRepository tipoRepository;
    private final ClienteRepository clienteRepository;
    private final HorarioAtendimentoRepository horarioRepository;
    private final ChamadoRepository chamadoRepository;

    @Value("${app.demo.senha-admin}")
    private String senhaAdmin;
    @Value("${app.demo.senha-tecnico}")
    private String senhaTecnico;
    @Value("${app.demo.senha-usuario}")
    private String senhaUsuario;
    @Value("${app.demo.dados-exemplo}")
    private boolean dadosExemplo;

    public DadosIniciais(UsuarioService usuarioService, TipoChamadoRepository tipoRepository,
                         ClienteRepository clienteRepository, HorarioAtendimentoRepository horarioRepository,
                         ChamadoRepository chamadoRepository) {
        this.usuarioService = usuarioService;
        this.tipoRepository = tipoRepository;
        this.clienteRepository = clienteRepository;
        this.horarioRepository = horarioRepository;
        this.chamadoRepository = chamadoRepository;
    }

    @Override
    public void run(String... args) {
        usuarioService.criarSeNaoExistir("Administrador", "admin@thindesk.com", senhaAdmin, Perfil.ADMIN, "TI");
        usuarioService.criarSeNaoExistir("João Técnico", "tecnico@thindesk.com", senhaTecnico, Perfil.TECNICO, "Suporte");
        usuarioService.criarSeNaoExistir("Maria Usuária", "usuario@thindesk.com", senhaUsuario, Perfil.USUARIO, "Financeiro");

        if (tipoRepository.count() == 0) {
            tipoRepository.saveAll(List.of(
                    new TipoChamado("Hardware", "Computadores, monitores e periféricos"),
                    new TipoChamado("Software", "Instalação e erros de programas"),
                    new TipoChamado("Rede", "Internet, Wi-Fi e VPN"),
                    new TipoChamado("Acesso", "Senhas, contas e permissões"),
                    new TipoChamado("Impressora", "Impressão e digitalização")));
        }

        if (!dadosExemplo) {
            return;
        }

        if (clienteRepository.count() == 0) {
            clienteRepository.saveAll(List.of(
                    new Cliente("Financeiro", "(11) 4000-1001", "Administrativo"),
                    new Cliente("Recursos Humanos", "(11) 4000-1002", "Administrativo"),
                    new Cliente("Comercial", "(11) 4000-1003", "Vendas"),
                    new Cliente("Produção", "(11) 4000-1004", "Operações")));
        }

        if (horarioRepository.count() == 0) {
            horarioRepository.saveAll(List.of(
                    new HorarioAtendimento("Suporte", "Segunda a sexta", "08:00", "18:00"),
                    new HorarioAtendimento("Suporte", "Sábado", "08:00", "12:00"),
                    new HorarioAtendimento("Infraestrutura", "Segunda a sexta", "07:00", "17:00")));
        }

        if (chamadoRepository.count() == 0) {
            LocalDateTime agora = LocalDateTime.now(ChamadoService.FUSO).withNano(0);
            chamadoRepository.saveAll(List.of(
                    exemplo("Impressora do 2º andar não imprime", "A impressora mostra erro de papel atolado.",
                            "Impressora", Prioridade.MEDIA, "Financeiro", StatusChamado.ABERTO, agora.minusHours(2), null),
                    exemplo("Sem acesso ao sistema de folha", "Senha expirada, preciso de redefinição.",
                            "Acesso", Prioridade.ALTA, "Recursos Humanos", StatusChamado.EM_ANDAMENTO, agora.minusHours(5), null),
                    exemplo("Wi-Fi caindo na sala de reunião", "A conexão cai a cada poucos minutos.",
                            "Rede", Prioridade.ALTA, "Comercial", StatusChamado.ABERTO, agora.minusDays(1), null),
                    exemplo("Instalar pacote Office", "Novo colaborador precisa do Office instalado.",
                            "Software", Prioridade.BAIXA, "Comercial", StatusChamado.CONCLUIDO, agora.minusDays(2),
                            "Office instalado e licença ativada."),
                    exemplo("Monitor piscando", "O monitor pisca ao ligar o computador.",
                            "Hardware", Prioridade.MEDIA, "Produção", StatusChamado.CONCLUIDO, agora.minusDays(3),
                            "Cabo HDMI substituído."),
                    exemplo("Teclado com teclas falhando", "As teclas A e S não funcionam.",
                            "Hardware", Prioridade.BAIXA, "Financeiro", StatusChamado.EM_ANDAMENTO, agora.minusDays(4), null),
                    exemplo("Erro ao abrir planilha", "Planilha de custos não abre no Excel.",
                            "Software", Prioridade.MEDIA, "Financeiro", StatusChamado.CANCELADO, agora.minusDays(5), null)));
        }
    }

    private Chamado exemplo(String titulo, String descricao, String tipo, Prioridade prioridade, String cliente,
                            StatusChamado status, LocalDateTime abertura, String solucao) {
        Chamado c = new Chamado();
        c.setTitulo(titulo);
        c.setDescricao(descricao);
        c.setTipo(tipo);
        c.setPrioridade(prioridade);
        c.setCliente(cliente);
        c.setStatus(status);
        c.setUsuario("Maria Usuária");
        c.setUsuarioEmail("usuario@thindesk.com");
        c.setDataAbertura(abertura);
        c.setDataAtualizacao(abertura.plusHours(1));
        if (status == StatusChamado.EM_ANDAMENTO || status == StatusChamado.CONCLUIDO) {
            c.setTecnico("João Técnico");
            c.setTecnicoEmail("tecnico@thindesk.com");
        }
        if (status == StatusChamado.CONCLUIDO) {
            c.setSolucao(solucao);
            c.setDataConclusao(abertura.plusHours(3));
            c.setDataAtualizacao(c.getDataConclusao());
        }
        return c;
    }
}
