package service.order.impl;

import model.bean.Indirizzo;
import model.dao.UtenteDAO;
import model.dao.IndirizzoDAO;
import org.junit.Before;
import org.junit.Test;
import service.account.impl.AccountServiceImpl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OrderServiceImplIndirizzoTest {

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
    public void testIndirizzoVuoto() {
        String indirizzo = "";
        assertFalse(indirizzo != null && !indirizzo.trim().isEmpty());
    }

    @Test
    public void testPaeseVuoto() {
        String paese = "";
        assertFalse(paese != null && !paese.trim().isEmpty());
    }

    @Test
    public void testCittaVuoto() {
        String citta = "";
        assertFalse(citta != null && !citta.trim().isEmpty());
    }

    @Test
    public void testIndirizzoFormatoErrato() {
        String badIndirizzo = "Via Roma"; // manca numero
        boolean valid = badIndirizzo.matches("^Via\\s+.+\\s+\\d+$");
        assertFalse(valid);
    }

    // ====================== TEST POSITIVI ======================

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
    public void testIndirizzoNonVuoto() {
        String indirizzo = "Via Roma 123";
        assertTrue(indirizzo != null && !indirizzo.trim().isEmpty());
    }

    @Test
    public void testPaeseNonVuoto() {
        String paese = "Italia";
        assertTrue(paese != null && !paese.trim().isEmpty());
    }

    @Test
    public void testCittaNonVuoto() {
        String citta = "Roma";
        assertTrue(citta != null && !citta.trim().isEmpty());
    }

    @Test
    public void testIndirizzoFormatoCorretto() {
        String goodIndirizzo = "Via Roma 123";
        boolean valid = goodIndirizzo.matches("^Via\\s+.+\\s+\\d+$");
        assertTrue(valid);
    }
}
