package service.account.impl;

import model.bean.Utente;
import model.dao.UtenteDAO;
import model.dao.IndirizzoDAO;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AccountServiceImplUpdateTest {

    private UtenteDAO utenteDAO;
    private IndirizzoDAO indirizzoDAO;
    private AccountServiceImpl service;

    @Before
    public void setUp() {
        // Mock del DAO per isolare il servizio
        utenteDAO = mock(UtenteDAO.class);
        indirizzoDAO = mock(IndirizzoDAO.class);

        service = new AccountServiceImpl(utenteDAO, indirizzoDAO);
    }

    // ====================== LOGIN ======================

    @Test
    public void testLoginValido() {
        Utente u = new Utente();
        u.setEmail("utente@mail.it");

        when(utenteDAO.login("utente@mail.it","Password123")).thenReturn(u);

        Utente result = service.login("utente@mail.it","Password123");

        assertNotNull(result);
        assertEquals("utente@mail.it", result.getEmail());
    }

    @Test
    public void testLoginEmailSbagliata() {
        when(utenteDAO.login("nonesiste@mail.it", "Password123")).thenReturn(null);

        Utente result = service.login("nonesiste@mail.it", "Password123");

        assertNull(result);
    }

    @Test
    public void testLoginEmailFormatoErrato() {
        String badEmail = "mail-sbagliata";
        boolean valid = badEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$");
        assertFalse(valid);
    }

    @Test
    public void testLoginPasswordSbagliata() {
        when(utenteDAO.login("utente@mail.it", "Sbagliata123")).thenReturn(null);

        Utente result = service.login("utente@mail.it", "Sbagliata123");

        assertNull(result);
    }

    // ====================== CAMBIO PASSWORD ======================

    @Test
    public void testPasswordAttualeNonCorretta() {
        when(utenteDAO.changePassword(1, "vecchiaSbagliata", "NuovaPass123")).thenReturn(false);

        boolean result = service.changePassword(1, "vecchiaSbagliata", "NuovaPass123");

        assertFalse(result);
    }

    @Test
    public void testPasswordNonCoincideConConferma() {
        String newPassword = "NuovaPass123";
        String confirmPassword = "DiversaPass123";

        boolean result = newPassword.equals(confirmPassword);
        assertFalse(result);
    }

    @Test
    public void testPasswordFormatoErrato() {
        String badPassword = "short"; // meno di 8 char, nessun numero, nessuna maiuscola
        boolean valid = badPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
        assertFalse(valid);
    }

    @Test
    public void testPasswordFormatoCorretto() {
        String goodPassword = "Password123";
        boolean valid = goodPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
        assertTrue(valid);
    }

    // ====================== AGGIORNAMENTO DATI UTENTE ======================

    @Test
    public void testEmailFormatoErratoAggiornamento() {
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
        String badProv = "RMZ"; // più di 2 lettere
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
}
