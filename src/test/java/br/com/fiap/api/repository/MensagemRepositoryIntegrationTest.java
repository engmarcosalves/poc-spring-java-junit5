package br.com.fiap.api.repository;

import br.com.fiap.api.model.Mensagem;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static br.com.fiap.api.utils.MensagemHelper.gerarMensagem;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class MensagemRepositoryIntegrationTest {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Test
    void devePermitirCriarTabela() {
        var totalDeRegistros = mensagemRepository.count();
        assertThat(totalDeRegistros).isGreaterThan(0);
    }

    @Test
    void devePemitirRegistrarMensagem() {
        // Arrange
        var id = UUID.randomUUID();
        var mensagem = gerarMensagem();
        mensagem.setId(id);

        // Act
        var mensagemRecebida = mensagemRepository.save(mensagem);

        // Assert
        assertThat(mensagemRecebida)
                .isInstanceOf(Mensagem.class)
                .isNotNull();
        assertThat(mensagemRecebida.getId()).isEqualTo(id);
        assertThat(mensagemRecebida.getConteudo()).isEqualTo(mensagem.getConteudo());
        assertThat(mensagemRecebida.getUsuario()).isEqualTo(mensagem.getUsuario());
        assertThat(mensagemRecebida.getDataCriacao()).isEqualTo(mensagem.getDataCriacao());
    }

    @Test
    void devePemitirBuscarMensagem() {
        // Arrange
        var id = UUID.fromString("7714f0bd-eb6c-4e8a-85f4-4d70674c1ba8");

        // Act
        var mensagemRecebidaOptional = mensagemRepository.findById(id);

        // Assert
        assertThat(mensagemRecebidaOptional).isPresent();

        mensagemRecebidaOptional.ifPresent(mensagemRecebida -> {
            assertThat(mensagemRecebida.getId()).isEqualTo(id);
        });
    }

    @Test
    void devePemitirRemoverMensagem() {
        // Arrange
        var id = UUID.fromString("9ca7c72c-0957-4c7d-bdc2-325266842f21");

        // Act
        mensagemRepository.deleteById(id);
        var mensagemRecebidaOptional = mensagemRepository.findById(id);

        // Assert
        assertThat(mensagemRecebidaOptional).isEmpty();
    }

    @Test
    void devePemitirListarMensagens() {
        // Act
        var resultadosObitidos = mensagemRepository.findAll();

        // Assert
        assertThat(resultadosObitidos).hasSizeGreaterThan(0);
    }

}
