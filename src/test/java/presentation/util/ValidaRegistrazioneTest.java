package presentation.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

// classe per valida registrazione.
class ValidaRegistrazioneTest {

    @Test
    @DisplayName("TC1.1.1 Registrazione: password non valida")
    void passwordFormatoErrato_errorePassword() {
        Map<String, String> err = valida(
                "robertorossi103@gmail.com",
                "Rosarossa",
                "Rosarossa",
                "3913435888",
                "20019",
                "MI"
        );

        assertTrue(err.containsKey("password"));
    }

    @Test
    @DisplayName("TC1.1.2 Registrazione: conferma password non coincide")
    void confermaPasswordNonMatch_erroreConferma() {
        Map<String, String> err = valida(
                "robertorossi103@gmail.com",
                "Rosarossa3",
                "Rosarossa5",
                "3913435888",
                "20019",
                "MI"
        );

        assertTrue(err.containsKey("confermaPassword"));
    }

    @Test
    @DisplayName("TC1.1.3 Registrazione: email non valida")
    void emailNonValida_erroreEmail() {
        Map<String, String> err = valida(
                "robertorossi103gmail.com",
                "Rosarossa3",
                "Rosarossa3",
                "3913435888",
                "20019",
                "MI"
        );

        assertTrue(err.containsKey("email"));
    }

    @Test
    @DisplayName("TC1.1.4 Registrazione: CAP non valido")
    void capNonValido_erroreCap() {
        Map<String, String> err = valida(
                "robertorossi103@gmail.com",
                "Rosarossa3",
                "Rosarossa3",
                "3913435888",
                "2001978",
                "MI"
        );

        assertTrue(err.containsKey("cap"));
    }

    @Test
    @DisplayName("TC1.1.5 Registrazione: provincia non valida")
    void provinciaNonValida_erroreProvincia() {
        Map<String, String> err = valida(
                "robertorossi103@gmail.com",
                "Rosarossa3",
                "Rosarossa3",
                "3913435888",
                "20019",
                "MIL"
        );

        assertTrue(err.containsKey("provincia"));
    }

    @Test
    @DisplayName("TC1.1.6 Registrazione: telefono non valido")
    void telefonoNonValido_erroreTelefono() {
        Map<String, String> err = valida(
                "robertorossi103@gmail.com",
                "Rosarossa3",
                "Rosarossa3",
                "ABC",
                "20019",
                "MI"
        );

        assertTrue(err.containsKey("telefono"));
    }

    @Test
    @DisplayName("TC1.1.7 Registrazione: dati validi")
    void datiValidi_nessunErrore() {
        Map<String, String> err = valida(
                "robertorossi103@gmail.com",
                "Rosarossa3",
                "Rosarossa3",
                "3913435888",
                "20019",
                "MI"
        );

        assertTrue(err.isEmpty(), "Attesi 0 errori, trovati: " + err);
    }

    private Map<String, String> valida(String email,
                                       String password,
                                       String confermaPassword,
                                       String telefono,
                                       String cap,
                                       String provincia) {
        return ValidatoreForm.validaRegistrazione(
                "Roberto",
                "Rossi",
                email,
                password,
                confermaPassword,
                telefono,
                "Via Alberto Sordi 212",
                "Milano",
                cap,
                provincia,
                "Italia"
        );
    }
}