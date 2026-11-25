package com.pfc.thindesk.controller;

import com.pfc.thindesk.entity.HorarioAtendimento;
import com.pfc.thindesk.repository.HorarioAtendimentoRepository;
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
@RequestMapping("/ajustes-horarios")
public class HorarioAtendimentoController {

    private final HorarioAtendimentoRepository repository;

    public HorarioAtendimentoController(HorarioAtendimentoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("horarios", repository.findAllByOrderBySetorAsc());
        model.addAttribute("novoHorario", new HorarioAtendimento());
        model.addAttribute("pagina", "horarios");
        return "ajustes-horarios";
    }

    @PostMapping("/salvar")
    @PreAuthorize("hasRole('ADMIN')")
    public String salvar(@ModelAttribute HorarioAtendimento horario, RedirectAttributes redirect) {
        if (vazio(horario.getSetor()) || vazio(horario.getDiaSemana())
                || vazio(horario.getHorarioInicio()) || vazio(horario.getHorarioFim())) {
            redirect.addFlashAttribute("erro", "Preencha setor, dia, início e fim.");
            return "redirect:/ajustes-horarios";
        }
        if (horario.getHorarioFim().compareTo(horario.getHorarioInicio()) <= 0) {
            redirect.addFlashAttribute("erro", "O horário de fim precisa ser depois do início.");
            return "redirect:/ajustes-horarios";
        }
        horario.setId(null);
        repository.save(horario);
        redirect.addFlashAttribute("sucesso", "Horário salvo.");
        return "redirect:/ajustes-horarios";
    }

    @PostMapping("/deletar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deletar(@PathVariable String id, RedirectAttributes redirect) {
        repository.deleteById(id);
        redirect.addFlashAttribute("sucesso", "Horário removido.");
        return "redirect:/ajustes-horarios";
    }

    private static boolean vazio(String texto) {
        return texto == null || texto.isBlank();
    }
}
