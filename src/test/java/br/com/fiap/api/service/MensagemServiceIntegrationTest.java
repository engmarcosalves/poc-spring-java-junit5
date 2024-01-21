package br.com.fiap.api.service;

import br.com.fiap.api.exception.MensagemNotFoundException;
import br.com.fiap.api.model.Mensagem;
import br.com.fiap.api.repository.MensagemRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

import static br.com.fiap.api.utils.MensagemHelper.gerarMensagem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class MensagemServiceIntegrationTest {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Autowired
    private MensagemService mensagemService;

    @Nested
    class RegistrarMensagem {
        @Test
        void devePermitirRegistrarMensagem() {
            var mensagem = gerarMensagem();

            var resultadoObtido = mensagemService.registrarMensagem(mensagem);

            assertThat(resultadoObtido)
                    .isNotNull()
                    .isInstanceOf(Mensagem.class);
            assertThat(resultadoObtido.getId()).isNotNull();
            assertThat(resultadoObtido.getDataCriacao()).isNotNull();
            assertThat(resultadoObtido.getGostei()).isEqualTo(0);
        }
    }

    @Nested
    class BuscarMensagem {

        @Test
        void devePermitirBuscarMensagem() {
            var id = UUID.fromString("7714f0bd-eb6c-4e8a-85f4-4d70674c1ba8");

            var resultadoObtido = mensagemService.buscarMensagem(id);

            assertThat(resultadoObtido)
                    .isNotNull()
                    .isInstanceOf(Mensagem.class);
            assertThat(resultadoObtido.getId())
                    .isNotNull()
                    .isEqualTo(UUID.fromString("7714f0bd-eb6c-4e8a-85f4-4d70674c1ba8"));
            assertThat(resultadoObtido.getUsuario())
                    .isNotNull()
                    .isEqualTo("Adam");
            assertThat(resultadoObtido.getConteudo())
                    .isNotNull()
                    .isEqualTo("Conteudo da Mensagem 01");
            assertThat(resultadoObtido.getDataCriacao())
                    .isNotNull();
            assertThat(resultadoObtido.getGostei())
                    .isEqualTo(0);
        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExiste() {
            var id = UUID.fromString("6c8dc465-57d3-4f4d-81cf-593822cb3edb");

            assertThatThrownBy(() -> mensagemService.buscarMensagem(id))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem não encontrada");
        }

    }

    @Nested
    class AlterarMensagem {

        @Test
        void devePermitirAlterarMensagem() {
            var id = UUID.fromString("9ca7c72c-0957-4c7d-bdc2-325266842f21");
            var mensagemAtualizada = gerarMensagem();
            mensagemAtualizada.setId(id);

            var resultadoObtido = mensagemService.alterarMensagem(id, mensagemAtualizada);

            assertThat(resultadoObtido.getId()).isEqualTo(id);
            assertThat(resultadoObtido.getUsuario()).isNotEqualTo(mensagemAtualizada.getUsuario());
            assertThat(resultadoObtido.getConteudo()).isEqualTo(mensagemAtualizada.getConteudo());
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste() {
            var id = UUID.fromString("00cd2a59-56a3-44f0-aa79-d4c46278a73e");
            var mensagemAtualizada = gerarMensagem();
            mensagemAtualizada.setId(id);

            assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagemAtualizada))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem não encontrada");
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() {
            var id = UUID.fromString("9ca7c72c-0957-4c7d-bdc2-325266842f21");
            var mensagemAtualizada = gerarMensagem();
            mensagemAtualizada.setId(UUID.fromString("501d68d7-6c7b-4052-97f4-56fd6dda84e3"));

            assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagemAtualizada))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("mensagem atualizada não apresenta o ID correto");
        }

    }

    @Nested
    class RemoverMensagem {

        @Test
        void devePermitirRemoverMensagem() {
            var id = UUID.fromString("52ea107b-7b58-446f-bbde-22a20cb8c2bc");

            var resultadoObtido = mensagemService.removerMensagem(id);

            assertThat(resultadoObtido).isTrue();
        }

        @Test
        void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() {
            var id = UUID.fromString("28bcbe3e-cf95-4918-941d-c5c8552a8fb8");

            assertThatThrownBy(() -> mensagemService.removerMensagem(id))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem não encontrada");
        }

    }

    @Nested
    class ListarMensagens {
        @Test
        void devePermitirListarMensagens() {
            Page<Mensagem> listaDeMensagensObtida = mensagemService.listarMensagem(Pageable.unpaged());

            assertThat(listaDeMensagensObtida).hasSize(3);
            assertThat(listaDeMensagensObtida.getContent())
                    .asList()
                    .allSatisfy(mensagemObtida -> {
                        assertThat(mensagemObtida).isNotNull();
                    });
        }

    }
}
