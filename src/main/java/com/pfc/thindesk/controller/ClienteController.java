package com.pfc.thindesk.controller;

import com.pfc.thindesk.entity.Cliente;
import com.pfc.thindesk.repository.ClienteRepository;
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
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteRepository repository;

    public ClienteController(ClienteRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", repository.findAllByOrderByNomeAsc());
        model.addAttribute("novoCliente", new Cliente());
        model.addAttribute("pagina", "clientes");
        return "clientes";
    }

    @PostMapping("/salvar")
    @PreAuthorize("hasAnyRole('TECNICO','ADMIN')")
    public String salvar(@ModelAttribute Cliente cliente, RedirectAttributes redirect) {
        if (cliente.getNome() == null || cliente.getNome().isBlank()) {
            redirect.addFlashAttribute("erro", "Informe o nome do cliente.");
            return "redirect:/clientes";
        }
        cliente.setId(null);
        cliente.setNome(cliente.getNome().trim());
        repository.save(cliente);
        redirect.addFlashAttribute("sucesso", "Cliente cadastrado.");
        return "redirect:/clientes";
    }

    @PostMapping("/excluir/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String excluir(@PathVariable String id, RedirectAttributes redirect) {
        repository.deleteById(id);
        redirect.addFlashAttribute("sucesso", "Cliente excluído.");
        return "redirect:/clientes";
    }
}
