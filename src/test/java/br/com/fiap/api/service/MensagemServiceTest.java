package br.com.fiap.api.service;

import br.com.fiap.api.exception.MensagemNotFoundException;
import br.com.fiap.api.model.Mensagem;
import br.com.fiap.api.repository.MensagemRepository;
import br.com.fiap.api.utils.MensagemHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static br.com.fiap.api.utils.MensagemHelper.gerarMensagem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MensagemServiceTest {

    private MensagemService mensagemService;

    @Mock
    private MensagemRepository mensagemRepository;

    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
        mensagemService = new MensagemServiceImpl(mensagemRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Test
    void devePermitirRegistrarMensagem() {
        // Arrange
        var mensagem = gerarMensagem();
        when(mensagemRepository.save(any(Mensagem.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        var mensagemRegistrada = mensagemService.registrarMensagem(mensagem);

        // Assert
        assertThat(mensagemRegistrada).isInstanceOf(Mensagem.class).isNotNull();

        assertThat(mensagemRegistrada.getConteudo()).isEqualTo(mensagem.getConteudo());
        assertThat(mensagemRegistrada.getUsuario()).isEqualTo(mensagem.getUsuario());
        assertThat(mensagem.getId()).isNotNull();
        verify(mensagemRepository, times(1)).save(any(Mensagem.class));
    }

    @Test
    void devePermitirBuscarMensagem() {
        // Arrange
        var id = UUID.randomUUID();
        var mensagem = MensagemHelper.gerarMensagem();
        mensagem.setId(id);

        when(mensagemRepository.findById(any(UUID.class))).thenReturn(Optional.of(mensagem));

        // Act
        var mensagemObtida = mensagemService.buscarMensagem(id);

        // Assert
        assertThat(mensagemObtida).isEqualTo(mensagem);
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExiste() {
        var id = UUID.randomUUID();
        when(mensagemRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mensagemService.buscarMensagem(id)).isInstanceOf(MensagemNotFoundException.class).hasMessage("Mensagem não encontrada");

        verify(mensagemRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void devePermitirAlterarMensagem() {
        // Arrange
        var id = UUID.randomUUID();
        var mensagemAntiga = MensagemHelper.gerarMensagem();
        mensagemAntiga.setId(id);

        var mensagemNova = new Mensagem();
        mensagemNova.setId(mensagemAntiga.getId());
        mensagemNova.setUsuario(mensagemAntiga.getUsuario());
        mensagemNova.setConteudo("Conteuado Alterado");

        when(mensagemRepository.findById(id)).thenReturn(Optional.of(mensagemAntiga));

        when(mensagemRepository.save(any(Mensagem.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        var mensagemObtida = mensagemService.alterarMensagem(id, mensagemNova);

        // Assert
        assertThat(mensagemObtida).isInstanceOf(Mensagem.class).isNotNull();
        assertThat(mensagemObtida.getId()).isEqualTo(mensagemNova.getId());
        assertThat(mensagemObtida.getUsuario()).isEqualTo(mensagemNova.getUsuario());
        assertThat(mensagemObtida.getConteudo()).isEqualTo(mensagemNova.getConteudo());
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, times(1)).save(any(Mensagem.class));

    }

    @Test
    void deveGerarExeccao_QuandoAlterarMensagem_IdNaoExiste() {
        // Arrange
        var id = UUID.randomUUID();
        var mensagem = gerarMensagem();
        mensagem.setId(id);

        when(mensagemRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagem)).isInstanceOf(MensagemNotFoundException.class).hasMessage("Mensagem não encontrada");

        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, never()).save(any(Mensagem.class));

    }

    @Test
    void deveGerarExeccao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() {
        // Arrange
        var id = UUID.randomUUID();
        var mensagemAntiga = MensagemHelper.gerarMensagem();
        mensagemAntiga.setId(id);

        var mensagemNova = MensagemHelper.gerarMensagem();
        mensagemNova.setId(UUID.randomUUID());

        when(mensagemRepository.findById(id)).thenReturn(Optional.of(mensagemAntiga));

        // Act & Assert


        assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagemNova)).isInstanceOf(MensagemNotFoundException.class).hasMessage("mensagem atualizada não apresenta o ID correto");

        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, never()).save(any(Mensagem.class));

    }

    @Test
    void devePermitirRemoverMensagem() {
        // Arrange
        var id = UUID.fromString("f2083b25-0cca-497a-bff9-373afa22bd09");
        var mensagem = gerarMensagem();
        mensagem.setId(id);
        when(mensagemRepository.findById(id)).thenReturn(Optional.of(mensagem));
        doNothing().when(mensagemRepository).deleteById(id);

        // Act
        var mensagemFoiRemovida = mensagemService.removerMensagem(id);

        // Assert
        assertThat(mensagemFoiRemovida).isTrue();
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() {
        // Arrange
        var id = UUID.fromString("6c883654-5a43-41d9-aa7b-9ef52b2f0a73");
        when(mensagemRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> mensagemService.removerMensagem(id)).isInstanceOf(MensagemNotFoundException.class).hasMessage("Mensagem não encontrada");
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void devePermitirListarMensagem() {
        // Arrange
        Page<Mensagem> listaDeMensagens = new PageImpl<>(Arrays.asList(MensagemHelper.gerarMensagem(), MensagemHelper.gerarMensagem()));
        when(mensagemRepository.listarMensagens(any(Pageable.class))).thenReturn(listaDeMensagens);

        // Act
        var resultadoObtido = mensagemService.listarMensagem(Pageable.unpaged());

        // Assert
        assertThat(resultadoObtido).hasSize(2);
        assertThat(resultadoObtido.getContent()).asList().allSatisfy(mensagem -> {
            assertThat(mensagem).isNotNull().isInstanceOf(Mensagem.class);
        });
        verify(mensagemRepository, times(1)).listarMensagens(any(Pageable.class));
    }

}