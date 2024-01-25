package br.com.fiap.api.utils;

import br.com.fiap.api.model.Mensagem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;

public abstract class MensagemHelper {

    public static Mensagem gerarMensagem() {
        return Mensagem.builder()
                .usuario("Usuário da Mensagem")
                .conteudo("Conteúdo da Mensagem")
                .usuario("Nome usuário")
                .gostei(0)
                .build();
    }

    public static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(object);
    }
}
