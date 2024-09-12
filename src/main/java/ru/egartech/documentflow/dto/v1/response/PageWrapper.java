package ru.egartech.documentflow.dto.v1.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class PageWrapper<T> {

    private final Integer totalPages;

    private final Long totalItems;

    private final Integer pageNumber;

    private final Integer itemCount;

    private final List<T> items;

}
