package com.cibertec.sistema_facturacion_g4.shared.utils;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ValidationUtils {
    public static boolean isValidDNI(String dni) {
        return dni != null && dni.matches("\\d{8}");
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("[0-9]{9}");
    }

    public static boolean isValidEmail(String email) {
        return email != null
                && Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matcher(email).matches();
    }

    public static boolean isValidRUC(String ruc) {
        if (ruc == null) {
            return false;
        }
        String rucLimpio = ruc.trim();
        return rucLimpio.matches("^[0-9]{11}$");
    }

    public static boolean isValidAmount(BigDecimal amount) {
        if (amount == null) {
            return false;
        }
        return amount.compareTo(BigDecimal.ZERO) >= 0;
    }

    public static String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }

    public static String normalizeRUC(String ruc) {
        if (ruc == null) {
            return null;
        }
        return ruc.trim();
    }
}
