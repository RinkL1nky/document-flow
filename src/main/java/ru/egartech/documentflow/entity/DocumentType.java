package ru.egartech.documentflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class DocumentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, columnDefinition = "INT2")
    private Long id;

    @NotBlank
    @Size(min = 4, max = 128)
    @Column(unique = true)
    private String name;

}
