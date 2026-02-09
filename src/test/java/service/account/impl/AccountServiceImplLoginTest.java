package service.account.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import model.bean.Utente;
import model.dao.UtenteDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import service.account.AccountService;
import utils.HashUtil;

// Classe di test per il LoginService
public class AccountServiceImplLoginTest {

    @Mock
    private UtenteDAO userRepository; // Repository simulato

    @InjectMocks
    private AccountService loginService; // Business Logic da testare

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // 1️⃣ Email con formato errato
    @Test
    public void testEmailFormatoErrato() {
        String email = "pippo"; // formato errato
        String password = "qualcosa";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            loginService.login(email, password);
        });

        assertEquals("Formato email non valido", exception.getMessage());
    }

    // 2️⃣ Email inesistente nel database
    @Test
    public void testEmailNonEsistente() {
        String email = "utente@nonpresente.com";
        String password = "password123";

        when(userRepository.trovaUtentePerEmail(email)).thenReturn(null); // mocka il DB

        Exception exception = assertThrows(RuntimeException.class, () -> {
            loginService.login(email, password);
        });

        assertEquals("Email non trovata", exception.getMessage());
    }

    // 3️⃣ Password sbagliata
    @Test
    public void testPasswordSbagliata() {
        String email = "utente@dominio.com";
        String password = "passwordSbagliata";

        Utente fakeUser = new Utente();
        fakeUser.setEmail(email);
        fakeUser.setPasswordCifrata(HashUtil.sha1(password));

        when(userRepository.trovaUtentePerEmail(email)).thenReturn(fakeUser);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            loginService.login(email, password);
        });

        assertEquals("Password errata", exception.getMessage());
    }

    // 4️⃣ Login corretto
    @Test
    public void testLoginCorretto() {
        String email = "utente@dominio.com";
        String password = "passwordCorretta";

        Utente fakeUser = new Utente();
        fakeUser.setEmail(email);
        fakeUser.setPasswordCifrata(HashUtil.sha1(password));

        when(userRepository.trovaUtentePerEmail(email)).thenReturn(fakeUser);

        Utente result = loginService.login(email, password);
        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    // 5️⃣ (Opzionale) campi vuoti
    @Test
    public void testCampiVuoti() {
        String email = "";
        String password = "";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            loginService.login(email, password);
        });

        assertEquals("Email e password non possono essere vuoti", exception.getMessage());
    }
}
