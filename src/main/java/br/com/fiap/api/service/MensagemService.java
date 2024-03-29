package br.com.fiap.api.service;

import br.com.fiap.api.model.Mensagem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

public interface MensagemService {

    Mensagem registrarMensagem(Mensagem mensagem);

    Mensagem buscarMensagem(UUID id);

    Mensagem alterarMensagem(UUID id, Mensagem mensagemAtualizada);

    boolean removerMensagem(UUID id);

    Page<Mensagem> listarMensagem(Pageable pageable);
}