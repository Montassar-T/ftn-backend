package com.ftn.backend.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailUtils {

    public static String normalize(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }
}
