package ru.egartech.documentflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan({"ru.egartech.documentflow.properties"})
@SpringBootApplication
public class DocumentFlowApplication {

    public static void main(final String[] args) {
        SpringApplication.run(DocumentFlowApplication.class, args);
    }

}
