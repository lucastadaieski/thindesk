package com.pfc.thindesk.service;

/** Erro de regra de negócio: a mensagem é mostrada ao usuário na tela. */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
