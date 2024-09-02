package ru.egartech.documentflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(updatable = false)
    private Long id;

    @NotBlank
    @Size(min = 4, max = 128)
    private String name;

    @NotBlank
    @Size(min = 4, max = 128)
    private String comment;

    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    private Document document;

    @NotNull
    @Future
    private LocalDateTime deadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Task parent;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Employee appointee;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @CreatedBy
    private Employee issuer;

    @NotNull
    @CreatedDate
    private Instant createdAt;

    public enum Status {
        WAITING,
        IN_PROGRESS,
        COMPLETED,
        REJECTED
    }

    public enum Type {
        SIGN,
        EDIT,
        ISSUE_NEW_DOCUMENT,
        CONFIRM_SUBSIDIARY_TASKS_COMPLETION,
        CONFIRM_EMAIL_SENDING
    }

}
