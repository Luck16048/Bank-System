package org.example.util;

import java.util.UUID;

public class AccountGeneratorUtil {
    public static String generate() {
        return "PL" + UUID.randomUUID().toString().replace("-", "").substring(0, 26);
    }
}
