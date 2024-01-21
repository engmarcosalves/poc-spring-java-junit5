package br.com.fiap.api.repository;

import br.com.fiap.api.model.Mensagem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static br.com.fiap.api.utils.MensagemHelper.gerarMensagem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class MensagemRepositoryTest {

    @Mock
    private MensagemRepository mensagemRepository;

    AutoCloseable openMocks;

    @BeforeEach
    void setup() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void devePermitirRegistrarMensagem() {
        // Arrange (Preparar)
        var mensagem = gerarMensagem();

        when(mensagemRepository.save(any(Mensagem.class))).thenReturn(mensagem);

        // Act (Executar)
        var mensagemArmazenada = mensagemRepository.save(mensagem);

        // Assert (Validar)
        assertThat(mensagemArmazenada)
                .isNotNull()
                .isEqualTo(mensagem);

        // Aqui garantimos que ao menos estamos tendo uma comunicação correta com o banco de dados.
        // Se por exemplo, conseguimos salvar a mensagem na linha 45 e na linha 55 recebmoe um erro
        // informando que gravou 2 vezes ou nenhume vez, significa que não houve nenhuma interação com o BD.
        verify(mensagemRepository, times(1)).save(any(Mensagem.class));
    }

    @Test
    void devePermitirBuscarMensagem() {
        // Arrange
        var id = UUID.randomUUID();
        var mensagem = gerarMensagem();
        mensagem.setId(id);

        when(mensagemRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(mensagem));

        // Act
        var mensagemRecebidaOpcional = mensagemRepository.findById(id);

        // Assert
        assertThat(mensagemRecebidaOpcional)
                .isPresent()
                .containsSame(mensagem);

        mensagemRecebidaOpcional.ifPresent(mensagemRecebida -> {
            assertThat(mensagemRecebida.getId()).isEqualTo(mensagem.getId());
            assertThat(mensagemRecebida.getConteudo()).isEqualTo(mensagem.getConteudo());
            assertThat(mensagemRecebida.getUsuario()).isEqualTo(mensagem.getUsuario());
            assertThat(mensagemRecebida.getDataCriacao()).isEqualTo(mensagem.getDataCriacao());
        });

    }

    @Test
    void devePermitirRemoverMensagem() {
        // Arrange
        var id = UUID.randomUUID();
        doNothing().when(mensagemRepository).deleteById(any(UUID.class));

        // Act
        mensagemRepository.deleteById(id);

        // Assert
        verify(mensagemRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void devePermitirListarMensagens() {
        // Arrange
        var mensagem1 = gerarMensagem();
        var mensagem2 = gerarMensagem();
        var listaMensagens = Arrays.asList(
                mensagem1,
                mensagem2);
        when(mensagemRepository.findAll()).thenReturn(listaMensagens);

        // Act
        var mensagensRecebidas = mensagemRepository.findAll();

        // Assert
        assertThat(mensagensRecebidas)
                .hasSize(2)
                .containsExactlyInAnyOrder(mensagem1, mensagem2);
        verify(mensagemRepository, times(1)).findAll();
    }
}