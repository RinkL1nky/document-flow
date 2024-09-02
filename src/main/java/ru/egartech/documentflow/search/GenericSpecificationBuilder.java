package ru.egartech.documentflow.search;

import jakarta.persistence.criteria.*;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class GenericSpecificationBuilder<T> {

    private final List<Specification<T>> specificationList = new ArrayList<>();

    public <Y extends Comparable<? super Y>> GenericSpecificationBuilder<T> withLessThanOrEquals(String key,
                                                                                                 Y value) {
        if(value == null) {
            return this;
        }
        specificationList.add((Specification<T>)
                (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(getPath(root, key), value)
        );
        return this;
    }

    public <Y extends Comparable<? super Y>> GenericSpecificationBuilder<T> withGreaterThanOrEquals(String key,
                                                                                                    Y value) {
        if(value == null) {
            return this;
        }
        specificationList.add((Specification<T>)
                (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(getPath(root, key), value)
        );
        return this;
    }

    public GenericSpecificationBuilder<T> withEquals(String key, Object value) {
        if(value == null) {
            return this;
        }
        specificationList.add((Specification<T>)
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(getPath(root, key), value)
        );
        return this;
    }

    public GenericSpecificationBuilder<T> withContains(String key, String value) {
        if(value == null) {
            return this;
        }
        specificationList.add((Specification<T>)
                (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(getPath(root, key)),
                        "%" + value.trim().toLowerCase() + "%")
        );
        return this;
    }

    private <V> Path<V> getPath(Root<T> root, String attributeName) {
        Path<V> path = null;
        for (String part : attributeName.split("\\.")) {
            path = (path == null) ? root.get(part) : path.get(part);
        }
        return path;
    }

    public Specification<T> build() {
        Specification<T> result = null;
        for(Specification<T> specification : specificationList) {
            if(result == null) {
                result = specification;
                continue;
            }
            result = Specification.where(result).and(specification);
        }

        return result;
    }
}
