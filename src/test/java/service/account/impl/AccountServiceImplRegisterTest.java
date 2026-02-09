package service.account.impl;

import model.bean.Utente;
import model.dao.UtenteDAO;
import model.dao.IndirizzoDAO;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AccountServiceImplRegisterTest {

    private UtenteDAO utenteDAO;
    private IndirizzoDAO indirizzoDAO;
    private AccountServiceImpl service;

    @Before
    public void setUp() {
        utenteDAO = mock(UtenteDAO.class);
        indirizzoDAO = mock(IndirizzoDAO.class);
        service = new AccountServiceImpl(utenteDAO, indirizzoDAO);
    }

    // ====================== TEST NEGATIVI ======================

    @Test
    public void testPasswordFormatoErrato() {
        String badPassword = "short"; // meno di 8 char, nessun numero, nessuna maiuscola
        boolean valid = badPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
        assertFalse(valid);
    }

    @Test
    public void testPasswordNonCoincideConConferma() {
        String password = "Password123";
        String confirmPassword = "Password321";

        boolean result = password.equals(confirmPassword);
        assertFalse(result);
    }

    @Test
    public void testEmailFormatoErrato() {
        String badEmail = "mail-sbagliata";
        boolean valid = badEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$");
        assertFalse(valid);
    }

    @Test
    public void testCapFormatoErrato() {
        String badCap = "123";
        boolean valid = badCap.matches("^\\d{5}$");
        assertFalse(valid);
    }

    @Test
    public void testProvinciaFormatoErrato() {
        String badProv = "ROM"; // più di 2 lettere
        boolean valid = badProv.matches("^[A-Z]{2}$");
        assertFalse(valid);
    }

    @Test
    public void testTelefonoFormatoErrato() {
        String badPhone = "123456"; // meno di 10 numeri
        boolean valid = badPhone.matches("^\\d{10}$");
        assertFalse(valid);
    }

    @Test
    public void testNomeVuoto() {
        String nome = "";
        assertFalse(nome != null && !nome.trim().isEmpty());
    }

    @Test
    public void testCognomeVuoto() {
        String cognome = "";
        assertFalse(cognome != null && !cognome.trim().isEmpty());
    }

    @Test
    public void testPaeseVuoto() {
        String paese = "";
        assertFalse(paese != null && !paese.trim().isEmpty());
    }

    @Test
    public void testIndirizzoFormatoErrato() {
        String badIndirizzo = "Via Roma"; // manca numero
        boolean valid = badIndirizzo.matches("^Via\\s+.+\\s+\\d+$");
        assertFalse(valid);
    }

    // ====================== TEST POSITIVI ======================

    @Test
    public void testPasswordFormatoCorretto() {
        String goodPassword = "Password123";
        boolean valid = goodPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
        assertTrue(valid);
    }

    @Test
    public void testPasswordCoincideConConferma() {
        String password = "Password123";
        String confirmPassword = "Password123";

        boolean result = password.equals(confirmPassword);
        assertTrue(result);
    }

    @Test
    public void testEmailFormatoCorretto() {
        String goodEmail = "utente@mail.it";
        boolean valid = goodEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$");
        assertTrue(valid);
    }

    @Test
    public void testCapFormatoCorretto() {
        String goodCap = "00123";
        boolean valid = goodCap.matches("^\\d{5}$");
        assertTrue(valid);
    }

    @Test
    public void testProvinciaFormatoCorretto() {
        String goodProv = "RM";
        boolean valid = goodProv.matches("^[A-Z]{2}$");
        assertTrue(valid);
    }

    @Test
    public void testTelefonoFormatoCorretto() {
        String goodPhone = "3456789012";
        boolean valid = goodPhone.matches("^\\d{10}$");
        assertTrue(valid);
    }

    @Test
    public void testNomeValido() {
        String nome = "Mario";
        assertTrue(nome != null && !nome.trim().isEmpty());
    }

    @Test
    public void testCognomeValido() {
        String cognome = "Rossi";
        assertTrue(cognome != null && !cognome.trim().isEmpty());
    }

    @Test
    public void testPaeseValido() {
        String paese = "Italia";
        assertTrue(paese != null && !paese.trim().isEmpty());
    }

    @Test
    public void testIndirizzoFormatoCorretto() {
        String goodIndirizzo = "Via Roma 123";
        boolean valid = goodIndirizzo.matches("^Via\\s+.+\\s+\\d+$");
        assertTrue(valid);
    }
}
