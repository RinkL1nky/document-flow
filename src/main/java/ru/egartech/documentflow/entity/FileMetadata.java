package ru.egartech.documentflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;

@ToString
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
@Setter
@Entity
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(updatable = false)
    private Long id;

    @NotBlank
    private String bucket;

    @NotBlank
    private String path;

    @NotBlank
    private String filename;

    @NotBlank
    private String contentType;

    private LocalDateTime expiresAt;

    @ToString.Exclude
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @CreatedBy
    private Employee uploader;

    @NotNull
    @CreatedDate
    private Instant createdAt;

    @Transient
    public boolean isExpired() {
        if(getExpiresAt() == null) {
            return false;
        }
        return !getExpiresAt().isAfter(LocalDateTime.now());
    }

}
