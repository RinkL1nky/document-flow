package ru.egartech.documentflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class EmailTask {

    @Id
    @Column(updatable = false)
    private Long id;

    @NotBlank
    private String destinationEmail;

    @NotBlank
    private String templateName;

    @NotBlank
    private String subject;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    private Task task;

}
