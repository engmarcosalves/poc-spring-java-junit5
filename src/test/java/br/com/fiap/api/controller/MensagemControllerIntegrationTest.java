package br.com.fiap.api.controller;

import br.com.fiap.api.model.Mensagem;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static br.com.fiap.api.utils.MensagemHelper.gerarMensagem;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
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
            var id = "c0feb288-5c99-42fd-9be7-c59867714f3e";
            when()
                    .get("/mensagens/{id}", id)
            .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    class AlterarMensagem {

        @Test
        void devePermitirAlterarMensagem() {
            var id = UUID.fromString("9ca7c72c-0957-4c7d-bdc2-325266842f21");
            var mensagem = Mensagem.builder()
                    .id(id)
                    .usuario("Eve")
                    .conteudo("Conteudo da Mensagem")
                    .build();

            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mensagem)
            .when()
                .put("/mensagens/{id}", id)
            .then()
                .statusCode(HttpStatus.ACCEPTED.value())
                .body(matchesJsonSchemaInClasspath("schemas/mensagem.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste() {
            var id = UUID.fromString("9ca7c72c-0957-4c7d-bdc2-325266842f2");
            var mensagem = Mensagem.builder()
                    .id(id)
                    .usuario("Eve")
                    .conteudo("Conteudo da Mensagem")
                    .build();

            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mensagem)
            .when()
                .put("/mensagens/{id}", id)
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Mensagem não encontrada"));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() {
            var id = UUID.fromString("9ca7c72c-0957-4c7d-bdc2-325266842f21");
            var mensagem = gerarMensagem();
            mensagem.setId(UUID.fromString("9ca7c72c-0957-4c7d-bdc2-325266842f2A"));

            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mensagem)
            .when()
                .put("/mensagens/{id}", id)
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .body(equalTo("mensagem atualizada não apresenta o ID correto"));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_ApresentaPayloadComXML() {
            var id = UUID.fromString("9ca7c72c-0957-4c7d-bdc2-325266842f21");
            String xmlPaylod = "<mensagem><id>9ca7c72c-0957-4c7d-bdc2-325266842f21</id><usuario>Ana</usuario><conteudo>Mensagem do Conteudo</conteudo></mensagem>";

            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(xmlPaylod)
            .when()
                .put("/mensagens/{id}", id)
            .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"))
                .body("error", equalTo("Bad Request"))
                .body("path", equalTo("/mensagens/9ca7c72c-0957-4c7d-bdc2-325266842f21"))
                .body("path", containsString("/mensagens"));
        }

    }

    @Nested
    class RemoverMensagem {

        @Test
        void devePermitirRemoverMensagem() {
            var id = UUID.fromString("52ea107b-7b58-446f-bbde-22a20cb8c2bc");

            when()
                .delete("/mensagens/{id}", id)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("mensagem removida"));
        }

        @Test
        void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() {
            var id = UUID.fromString("52ea107b-7b58-446f-bbde-22a20cb8c2b");

            when()
                    .delete("/mensagens/{id}", id)
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("Mensagem não encontrada"));
        }

    }

    @Nested
    class ListarMensagem {

        @Test
        void devePermitirListarMensagens() {
            given()
                .queryParam("page", "0")
                .queryParam("size", "10")
            .when()
                .get("/mensagens")
            .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/mensagem.page.schema.json"));
        }

        @Test
        void devePermitirListarMensagens_QuandoNaoInformadoPaginacao() {
            given()
            .when()
                .get("/mensagens")
            .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/mensagem.page.schema.json"));
        }
    }

}
