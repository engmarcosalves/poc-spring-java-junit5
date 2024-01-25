package br.com.fiap.api.controller;

import br.com.fiap.api.exception.MensagemNotFoundException;
import br.com.fiap.api.model.Mensagem;
import br.com.fiap.api.service.MensagemService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.UUID;

import static br.com.fiap.api.utils.MensagemHelper.asJsonString;
import static br.com.fiap.api.utils.MensagemHelper.gerarMensagem;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MensagemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MensagemService mensagemService;

    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
        MensagemController mensagemController = new MensagemController(mensagemService);
        //mockMvc = MockMvcBuilders.standaloneSetup(mensagemController).build();
        mockMvc = MockMvcBuilders.standaloneSetup(mensagemController)
                .addFilter((request, response, chain) -> {
                    response.setCharacterEncoding("UTF-8");
                    chain.doFilter(request, response);
                })
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class RegistrarMensagem {

        @Test
        void devePermitirRegistarMensagem() throws Exception {
            // Arrange
            var mensagem = gerarMensagem();
            when(mensagemService.registrarMensagem(any(Mensagem.class)))
                    .thenAnswer(i -> i.getArgument(0));

            // Act & Assert
            mockMvc.perform(
                        post("/mensagens")
                        .content(asJsonString(mensagem))
                        .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isCreated());

            verify(mensagemService, times(1))
                    .registrarMensagem(any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarMensagem_PayloadXML() throws Exception {
            String xmlPaylod = "<mensagem><usuario>Ana</usuario><conteudo>Mensagem do Conteudo</conteudo></mensagem>";

            mockMvc.perform(post("/mensagens")
                    .contentType(MediaType.APPLICATION_XML)
                    .content(xmlPaylod))
                .andExpect(status().isUnsupportedMediaType());

            verify(mensagemService, never()).registrarMensagem(any(Mensagem.class));
        }
    }

    @Nested
    class BuscarMensagem {

        @Test
        void devePermitirBuscarMensagem() throws Exception {
            var id = UUID.fromString("94dd81dd-9bb1-4e4d-a8e6-becc9aaa752f");
            var mensagem = gerarMensagem();
            when(mensagemService.buscarMensagem(any(UUID.class))).thenReturn(mensagem);

            mockMvc.perform(get("/mensagens/{id}", id)).andExpect(status().isOk());
            verify(mensagemService, times(1)).buscarMensagem(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExiste() throws Exception {
            var id = UUID.fromString("60a8a2fc-53db-488f-94cd-2bf548e79a5e");

            when(mensagemService.buscarMensagem(id)).thenThrow(MensagemNotFoundException.class);

            mockMvc.perform(get("/mensagens/{id}", id)).andExpect(status().isBadRequest());
            verify(mensagemService, times(1)).buscarMensagem(id);
        }
    }

    @Nested
    class AlterarMensagem {

        @Test
        void devePermitirAlterarMensagem() throws Exception {
            var id = UUID.fromString("05c88d1b-8946-4862-ac27-eb70c5f0835e");
            var mensagem = gerarMensagem();
            mensagem.setId(id);

            // vai receber o parametro que recebeu quando alterar e retornar a mensagem.
            //when(mensagemService.alterarMensagem(id, mensagem)).thenAnswer( i -> i.getArgument(1));  // TB FUNCIONA
            when(mensagemService.alterarMensagem(id, mensagem)).thenReturn(mensagem);

            mockMvc.perform(put("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(mensagem)))
                    .andExpect(status().isAccepted());

            verify(mensagemService, times(1)).alterarMensagem(id, mensagem);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_PayloadXML() throws Exception {
            var id = UUID.fromString("05c88d1b-8946-4862-ac27-eb70c5f0835e");
            String xmlPaylod = "<mensagem><id>"+ id.toString() +"</id><usuario>Ana</usuario><conteudo>Mensagem do Conteudo</conteudo></mensagem>";

            mockMvc.perform(put("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_XML)
                            .content(xmlPaylod))
                    .andExpect(status().isUnsupportedMediaType());

            verify(mensagemService, never()).alterarMensagem(any(UUID.class), any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste() throws Exception {
            var id = UUID.fromString("feb73717-956b-4ab4-b44f-b72c6cb7f1e0");
            var mensagem = gerarMensagem();
            mensagem.setId(id);
            var conteudoDaExcecao = "Mensagem não encontrada";

            when(mensagemService.alterarMensagem(id, mensagem))
                    .thenThrow(new MensagemNotFoundException(conteudoDaExcecao));

            mockMvc.perform(put("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagem)))
//                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(conteudoDaExcecao));

            verify(mensagemService, times(1))
                    .alterarMensagem(any(UUID.class), any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() throws Exception {
            var id = UUID.fromString("c45d7276-1439-450b-9454-5a76d5e4edce");
            var mensagem = gerarMensagem();
            mensagem.setId(UUID.fromString("4c9f3463-b83c-4cb9-869f-19e2dbc791ac"));
            var conteudoDaExcecao = "mensagem atualizada não apresenta o ID correto";

            when(mensagemService.alterarMensagem(id, mensagem))
                    .thenThrow(new MensagemNotFoundException(conteudoDaExcecao));

            mockMvc.perform(put("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagem)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(conteudoDaExcecao));

            verify(mensagemService, times(1)).alterarMensagem(any(UUID.class), any(Mensagem.class));
        }

    }

    @Nested
    class RemoverMensagem {

        @Test
        void devePermitirRemoverMensagem() throws Exception {
            var id = UUID.fromString("67413221-826d-4ff3-a105-37464b573af1");

            when(mensagemService.removerMensagem(id)).thenReturn(true);

            mockMvc.perform(delete("/mensagens/{id}", id))
                    .andExpect(status().isOk())
                            .andExpect(content().string("mensagem removida"));

            verify(mensagemService, times(1)).removerMensagem(id);
        }

        @Test
        void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() throws Exception {
            var id = UUID.fromString("e32147ab-ed79-49b1-b612-4dc6570dafaf");
            var mensagemDaExcecao = "Mensagem não encontrada";

            when(mensagemService.removerMensagem(id))
                    .thenThrow(new MensagemNotFoundException(mensagemDaExcecao));

            mockMvc.perform(delete("/mensagens/{id}", id))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(mensagemDaExcecao));

            verify(mensagemService, times(1)).removerMensagem(id);
        }

    }

    @Nested
    class ListarMensagens {
        @Test
        void devePermitirListarMensagens() throws Exception {
            var mensagem = gerarMensagem();
            var page = new PageImpl<>(Collections.singletonList(mensagem));

            when(mensagemService.listarMensagem(any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/mensagens")
                    .param("page", "0")
                    .param("size", "10"))
//                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", not(empty())))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.totalElements").value(1));

        }

    }

}