package com.cibertec.sistema_facturacion_g4.shared.utils;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class NumberToWordsConverter {
    public String convertAmountToWords(BigDecimal amount) {
        int wholePart = amount.intValue();
        int decimalPart = amount.remainder(BigDecimal.ONE).multiply(new BigDecimal("100")).intValue();

        return convertNumberToWords(wholePart) + " Y " +
                String.format("%02d", decimalPart) + "/100 SOLES";
    }

    private String convertNumberToWords(int number) {
        if (number == 0)
            return "CERO";
        if (number < 0)
            return "MENOS " + convertNumberToWords(-number);

        String[] units = { "", "UNO", "DOS", "TRES", "CUATRO", "CINCO", "SEIS", "SIETE", "OCHO", "NUEVE" };
        String[] tens = { "", "DIEZ", "VEINTE", "TREINTA", "CUARENTA", "CINCUENTA",
                "SESENTA", "SETENTA", "OCHENTA", "NOVENTA" };
        String[] special = { "DIEZ", "ONCE", "DOCE", "TRECE", "CATORCE", "QUINCE",
                "DIECISEIS", "DIECISIETE", "DIECIOCHO", "DIECINUEVE" };
        String[] hundreds = { "", "CIENTO", "DOSCIENTOS", "TRESCIENTOS", "CUATROCIENTOS",
                "QUINIENTOS", "SEISCIENTOS", "SETECIENTOS", "OCHOCIENTOS", "NOVECIENTOS" };

        if (number < 10)
            return units[number];
        if (number < 20)
            return special[number - 10];
        if (number < 100) {
            int t = number / 10;
            int u = number % 10;
            return tens[t] + (u > 0 ? " Y " + units[u] : "");
        }
        if (number == 100)
            return "CIEN";
        if (number < 1000) {
            int h = number / 100;
            int remainder = number % 100;
            return hundreds[h] + (remainder > 0 ? " " + convertNumberToWords(remainder) : "");
        }
        if (number < 1000000) {
            int thousands = number / 1000;
            int remainder = number % 1000;
            String thousandsText = thousands == 1 ? "MIL" : convertNumberToWords(thousands) + " MIL";
            return thousandsText + (remainder > 0 ? " " + convertNumberToWords(remainder) : "");
        }

        return String.valueOf(number);
    }
}
