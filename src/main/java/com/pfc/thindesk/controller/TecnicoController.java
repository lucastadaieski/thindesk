package com.pfc.thindesk.controller;

import com.pfc.thindesk.dto.UsuarioForm;
import com.pfc.thindesk.entity.Perfil;
import com.pfc.thindesk.service.RegraNegocioException;
import com.pfc.thindesk.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Equipe técnica e cadastro de usuários (somente o administrador cadastra). */
@Controller
@RequestMapping("/tecnicos")
public class TecnicoController {

    private final UsuarioService usuarioService;

    public TecnicoController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TECNICO','ADMIN')")
    public String listar(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new UsuarioForm());
        }
        return pagina(model);
    }

    @PostMapping("/salvar")
    @PreAuthorize("hasRole('ADMIN')")
    public String salvar(@Valid @ModelAttribute("form") UsuarioForm form, BindingResult erros,
                         Model model, RedirectAttributes redirect) {
        if (erros.hasErrors()) {
            return pagina(model);
        }
        try {
            usuarioService.cadastrar(form);
        } catch (RegraNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            return pagina(model);
        }
        redirect.addFlashAttribute("sucesso", "Usuário " + form.getEmail() + " cadastrado.");
        return "redirect:/tecnicos";
    }

    private String pagina(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("perfis", Perfil.values());
        model.addAttribute("pagina", "tecnicos");
        return "tecnicos";
    }
}
