package com.pfc.thindesk.controller;

import com.pfc.thindesk.config.UsuarioLogado;
import com.pfc.thindesk.dto.ChamadoForm;
import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.entity.Prioridade;
import com.pfc.thindesk.entity.StatusChamado;
import com.pfc.thindesk.repository.ClienteRepository;
import com.pfc.thindesk.repository.TipoChamadoRepository;
import com.pfc.thindesk.service.ChamadoService;
import com.pfc.thindesk.service.RegraNegocioException;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/chamados")
public class ChamadoController {

    private final ChamadoService chamadoService;
    private final TipoChamadoRepository tipoRepository;
    private final ClienteRepository clienteRepository;

    public ChamadoController(ChamadoService chamadoService, TipoChamadoRepository tipoRepository,
                             ClienteRepository clienteRepository) {
        this.chamadoService = chamadoService;
        this.tipoRepository = tipoRepository;
        this.clienteRepository = clienteRepository;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) StatusChamado status,
                         @AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        model.addAttribute("chamados", chamadoService.listar(usuario, status));
        model.addAttribute("filtro", status);
        model.addAttribute("statusLista", StatusChamado.values());
        model.addAttribute("pagina", "chamados");
        return "chamados";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new ChamadoForm());
        return formulario(model);
    }

    @PostMapping("/novo")
    public String abrir(@Valid @ModelAttribute("form") ChamadoForm form, BindingResult erros,
                        @AuthenticationPrincipal UsuarioLogado usuario, Model model,
                        RedirectAttributes redirect) {
        if (erros.hasErrors()) {
            return formulario(model);
        }
        Chamado chamado = chamadoService.abrir(form, usuario);
        redirect.addFlashAttribute("sucesso", "Chamado aberto com sucesso.");
        return "redirect:/chamados/" + chamado.getId();
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable String id, @AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        model.addAttribute("chamado", chamadoService.buscar(id, usuario));
        model.addAttribute("pagina", "chamados");
        return "chamados/atualizar";
    }

    @PostMapping("/{id}/assumir")
    @PreAuthorize("hasAnyRole('TECNICO','ADMIN')")
    public String assumir(@PathVariable String id, @AuthenticationPrincipal UsuarioLogado usuario,
                          RedirectAttributes redirect) {
        return executar(id, redirect, "Você assumiu o chamado.", () -> chamadoService.assumir(id, usuario));
    }

    @PostMapping("/{id}/concluir")
    @PreAuthorize("hasAnyRole('TECNICO','ADMIN')")
    public String concluir(@PathVariable String id, @RequestParam(required = false) String solucao,
                           @AuthenticationPrincipal UsuarioLogado usuario, RedirectAttributes redirect) {
        return executar(id, redirect, "Chamado concluído.", () -> chamadoService.concluir(id, solucao, usuario));
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable String id, @AuthenticationPrincipal UsuarioLogado usuario,
                           RedirectAttributes redirect) {
        return executar(id, redirect, "Chamado cancelado.", () -> chamadoService.cancelar(id, usuario));
    }

    private String executar(String id, RedirectAttributes redirect, String mensagem, Runnable acao) {
        try {
            acao.run();
            redirect.addFlashAttribute("sucesso", mensagem);
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/chamados/" + id;
    }

    private String formulario(Model model) {
        model.addAttribute("tipos", tipoRepository.findAllByOrderByNomeAsc());
        model.addAttribute("clientes", clienteRepository.findAllByOrderByNomeAsc());
        model.addAttribute("prioridades", Prioridade.values());
        model.addAttribute("pagina", "chamados");
        return "chamados/novo";
    }
}
