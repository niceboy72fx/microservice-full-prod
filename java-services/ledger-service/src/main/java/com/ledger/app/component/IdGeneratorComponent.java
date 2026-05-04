package com.ledger.app.component;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class IdGeneratorComponent {

    public String generate() {
        // TODO Replace with a business-specific identifier strategy if required.
        return UUID.randomUUID().toString();
    }
}
