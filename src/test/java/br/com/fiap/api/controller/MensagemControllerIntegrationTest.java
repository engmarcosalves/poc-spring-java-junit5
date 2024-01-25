package br.com.fiap.api.controller;

import io.restassured.RestAssured;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static br.com.fiap.api.utils.MensagemHelper.gerarMensagem;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@Transactional
public class MensagemControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    class RegistrarMensagem {
        @Test
        void devePermitirRegistarMensagem() {
            var mensagem = gerarMensagem();

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagem)
//                    .log().all()
            .when()
                    .post("/mensagens")
            .then()
                    .statusCode(HttpStatus.CREATED.value());
//                    .log().all();

        }

        @Test
        void deveGerarExcecao_QuandoRegistrarMensagem_PayloadXML() {
            fail("teste nao implementado");
        }
    }

    @Nested
    class BuscarMensagem {

        @Test
        void devePermitirBuscarMensagem() {
            fail("teste nao implementado");
        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExiste() {
            fail("teste nao implementado");
        }
    }

    @Nested
    class AlterarMensagem {

        @Test
        void devePermitirAlterarMensagem() {
            fail("teste nao implementado");
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste() {
            fail("teste nao implementado");
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() {
            fail("teste nao implementado");
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_ApresentaPayloadComXML() {
            fail("teste nao implementado");
        }

    }

    @Nested
    class RemoverMensagem {

        @Test
        void devePermitirRemoverMensagem() {
            fail("teste nao implementado");
        }

        @Test
        void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() {
            fail("teste nao implementado");
        }

    }

    @Nested
    class ListarMensagem {

        @Test
        void devePermitirListarMensagens() {
            fail("teste nao implementado");
        }

        @Test
        void devePermitirListarMensagens_QuandoNaoInformadoPaginacao() {
            fail("teste nao implementado");
        }


    }

}
