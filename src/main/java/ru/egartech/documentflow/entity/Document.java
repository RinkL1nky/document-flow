package ru.egartech.documentflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(updatable = false)
    private Long id;

    @NotBlank
    @Size(min = 4, max = 128)
    private String name;

    @NotBlank
    @Size(min = 4, max = 2048)
    private String comment;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(columnDefinition = "INT2")
    private DocumentType type;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    private FileMetadata file;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    private Document parent;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "document_signature", joinColumns = @JoinColumn(name = "document_id"))
    private Set<DocumentSignature> signatures = new HashSet<>();

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @CreatedBy
    private Employee issuer;

    @NotNull
    @CreatedDate
    private Instant createdAt;

}
