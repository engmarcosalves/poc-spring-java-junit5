package br.com.fiap.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mensagem {

    @Id
    private UUID id;

    @Column(nullable = false)
    @NotEmpty(message = "usuário não pode estar vazio")
    private String usuario;

    @Column(nullable = false)
    @NotEmpty(message = "conteúdo não pode estar vazio")
    private String conteudo;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSS")
    private LocalDateTime dataCriacao;

    @Builder.Default
    private int gostei = 0;

    @PrePersist
    public void prePersist() {
        var timestamp = LocalDateTime.now();
        dataCriacao = timestamp;
    }
}
