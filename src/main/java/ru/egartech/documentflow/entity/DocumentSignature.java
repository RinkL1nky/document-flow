package ru.egartech.documentflow.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class DocumentSignature {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Employee signer;

}
