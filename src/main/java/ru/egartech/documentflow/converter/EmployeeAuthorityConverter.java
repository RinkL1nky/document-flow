package ru.egartech.documentflow.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Converter
public class EmployeeAuthorityConverter implements AttributeConverter<GrantedAuthority, String> {

    @Override
    public String convertToDatabaseColumn(GrantedAuthority authority) {
        return authority.getAuthority();
    }

    @Override
    public SimpleGrantedAuthority convertToEntityAttribute(String string) {
        return new SimpleGrantedAuthority(string);
    }
}
