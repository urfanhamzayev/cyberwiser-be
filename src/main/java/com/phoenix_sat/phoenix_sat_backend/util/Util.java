package com.phoenix_sat.phoenix_sat_backend.util;

import java.util.UUID;

public class Util {
    public static String generateRandomUUID() {
        return UUID.randomUUID().toString().replace("-","");

    }
}
