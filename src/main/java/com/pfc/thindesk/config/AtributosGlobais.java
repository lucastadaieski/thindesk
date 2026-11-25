package com.pfc.thindesk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Deixa a versão do sistema disponível em todas as telas (rodapé da sidebar). */
@ControllerAdvice
public class AtributosGlobais {

    @Value("${app.versao}")
    private String versao;

    @ModelAttribute("versao")
    public String versao() {
        return versao;
    }
}
