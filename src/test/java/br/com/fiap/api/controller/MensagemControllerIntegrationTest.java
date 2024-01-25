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
import static io.restassured.RestAssured.when;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
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
            .when()
                    .post("/mensagens")
            .then()
                    .log().all()
                    .statusCode(HttpStatus.CREATED.value())
                    .body(matchesJsonSchemaInClasspath("schemas/mensagem.schema.json"));


        }

        @Test
        void deveGerarExcecao_QuandoRegistrarMensagem_PayloadXML() {
            String xmlPaylod = "<mensagem><usuario>Ana</usuario><conteudo>Mensagem do Conteudo</conteudo></mensagem>";

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(xmlPaylod)
                    .log().all()
            .when()
                    .post("/mensagens")
            .then()
                    .log().all()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"))
                    .body("error", equalTo("Bad Request"))
                    .body("path", equalTo("/mensagens"));
        }
    }

    @Nested
    class BuscarMensagem {

        @Test
        void devePermitirBuscarMensagem() {

            // OBS: tem que ser um ID real que tenha na base de dados, pois é um teste integrado.
            var id = "7714f0bd-eb6c-4e8a-85f4-4d70674c1ba8";

            when()
                .get("/mensagens/{id}", id)
            .then()
                    .log().all()
                .statusCode(HttpStatus.OK.value());
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
