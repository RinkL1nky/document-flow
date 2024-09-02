package ru.egartech.documentflow.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties("email-client")
public class EmailClientProperties {

    private final String senderAddress;

}
