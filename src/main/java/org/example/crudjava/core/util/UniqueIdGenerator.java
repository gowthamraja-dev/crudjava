package org.example.crudjava.core.util;

import java.util.UUID;

public final class UniqueIdGenerator {

    private UniqueIdGenerator() {
    }

    public static String generate() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

