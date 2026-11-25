package com.pfc.thindesk.controller;

import com.pfc.thindesk.config.UsuarioLogado;
import com.pfc.thindesk.service.ChamadoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ChamadoService chamadoService;

    public HomeController(ChamadoService chamadoService) {
        this.chamadoService = chamadoService;
    }

    @Value("${app.demo.mostrar-credenciais}")
    private boolean mostrarCredenciais;
    @Value("${app.demo.senha-tecnico}")
    private String senhaTecnico;
    @Value("${app.demo.senha-usuario}")
    private String senhaUsuario;

    /** Tela de login. Em modo demonstração, mostra as credenciais de exemplo. */
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("mostrarCredenciais", mostrarCredenciais);
        if (mostrarCredenciais) {
            model.addAttribute("senhaTecnico", senhaTecnico);
            model.addAttribute("senhaUsuario", senhaUsuario);
        }
        return "login";
    }

    /** Dashboard: a tela inicial depois do login. */
    @GetMapping("/")
    public String dashboard(@AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        model.addAttribute("dados", chamadoService.dashboard(usuario));
        model.addAttribute("pagina", "dashboard");
        return "home";
    }
}
