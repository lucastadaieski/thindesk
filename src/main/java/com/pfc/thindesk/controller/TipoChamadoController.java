package com.pfc.thindesk.controller;

import com.pfc.thindesk.entity.TipoChamado;
import com.pfc.thindesk.repository.TipoChamadoRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tipo-chamado")
public class TipoChamadoController {

    private final TipoChamadoRepository repository;

    public TipoChamadoController(TipoChamadoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TECNICO','ADMIN')")
    public String listar(Model model) {
        model.addAttribute("tipos", repository.findAllByOrderByNomeAsc());
        model.addAttribute("novoTipo", new TipoChamado());
        model.addAttribute("pagina", "tipos");
        return "tipo-chamado";
    }

    @PostMapping("/salvar")
    @PreAuthorize("hasRole('ADMIN')")
    public String salvar(@ModelAttribute TipoChamado tipo, RedirectAttributes redirect) {
        if (tipo.getNome() == null || tipo.getNome().isBlank()) {
            redirect.addFlashAttribute("erro", "Informe o nome do tipo.");
        } else if (repository.existsByNome(tipo.getNome().trim())) {
            redirect.addFlashAttribute("erro", "Esse tipo já existe.");
        } else {
            tipo.setId(null);
            tipo.setNome(tipo.getNome().trim());
            repository.save(tipo);
            redirect.addFlashAttribute("sucesso", "Tipo de chamado cadastrado.");
        }
        return "redirect:/tipo-chamado";
    }

    @PostMapping("/excluir/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String excluir(@PathVariable String id, RedirectAttributes redirect) {
        repository.deleteById(id);
        redirect.addFlashAttribute("sucesso", "Tipo de chamado excluído.");
        return "redirect:/tipo-chamado";
    }
}
