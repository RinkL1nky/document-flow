package ru.egartech.documentflow.exceptionhandler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.jpa.vendor.Database;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum ConstraintErrorCode {
    DUPLICATE_KEY(Database.POSTGRESQL, "23505",
            "Value violates unique key constraint {0}", "DUPLICATE_KEY_VALUE"),
    FOREIGN_KEY(Database.POSTGRESQL,"23503",
            "Value violates foreign key constraint {0}", "FOREIGN_KEY_NOT_FOUND"),
    CHECK(Database.POSTGRESQL, "23514",
            "Value violates key check {0}", "CHECK_CONSTRAINT_VIOLATION");

    private final Database database;
    private final String sqlState;
    private final String message;
    private final String errorCode;

    public static Optional<ConstraintErrorCode> findOne(String sqlState, Database database) {
        return Arrays.stream(ConstraintErrorCode.values())
                .filter(e -> e.sqlState.equals(sqlState) && e.database.equals(database))
                .findFirst();
    }

}
