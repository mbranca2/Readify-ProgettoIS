package service.order.impl;

import org.junit.Test;

import java.time.YearMonth;

import static org.junit.Assert.*;

public class PaymentDataTest {

    // ====================== TEST NEGATIVI ======================

    @Test
    public void testTitolareVuoto() {
        String titolare = "";
        assertFalse(titolare != null && !titolare.trim().isEmpty());
    }

    @Test
    public void testNumeroCartaErrato() {
        String numeroCarta = "1234567890123"; // 13 cifre
        boolean valid = numeroCarta.matches("^\\d{16}$");
        assertFalse(valid);
    }

    @Test
    public void testCvvErrato() {
        String cvv = "12"; // 2 cifre
        boolean valid = cvv.matches("^\\d{3}$");
        assertFalse(valid);
    }

    @Test
    public void testScadenzaFormatoErrato() {
        String scadenza = "13/22"; // mese > 12
        boolean valid = scadenza.matches("^(0[1-9]|1[0-2])/\\d{2}$");
        assertFalse(valid);
    }

    @Test
    public void testScadenzaPassata() {
        String scadenza = "01/20"; // gennaio 2020, scaduta
        String[] parts = scadenza.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]) + 2000; // trasformo in anno completo
        YearMonth expiry = YearMonth.of(year, month);
        YearMonth now = YearMonth.now();
        assertTrue(expiry.isBefore(now));
    }

    // ====================== TEST POSITIVI ======================

    @Test
    public void testTitolareValido() {
        String titolare = "Mario Rossi";
        assertTrue(titolare != null && !titolare.trim().isEmpty());
    }

    @Test
    public void testNumeroCartaCorretto() {
        String numeroCarta = "1234567890123456"; // 16 cifre
        boolean valid = numeroCarta.matches("^\\d{16}$");
        assertTrue(valid);
    }

    @Test
    public void testCvvCorretto() {
        String cvv = "123";
        boolean valid = cvv.matches("^\\d{3}$");
        assertTrue(valid);
    }

    @Test
    public void testScadenzaValida() {
        // esempio: mese corrente o futuro
        YearMonth now = YearMonth.now();
        int month = now.getMonthValue();
        int year = now.getYear() % 100; // ultime due cifre
        String scadenza = String.format("%02d/%02d", month, year);

        boolean formatValid = scadenza.matches("^(0[1-9]|1[0-2])/\\d{2}$");

        String[] parts = scadenza.split("/");
        int parsedMonth = Integer.parseInt(parts[0]);
        int parsedYear = Integer.parseInt(parts[1]) + 2000;
        YearMonth expiry = YearMonth.of(parsedYear, parsedMonth);

        assertTrue(formatValid);
        assertFalse(expiry.isBefore(now));
    }
}
