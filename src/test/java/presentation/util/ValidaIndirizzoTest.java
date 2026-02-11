package presentation.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ValidaIndirizzoTest {

    @Test
    @DisplayName("CP Indirizzo: dati validi")
    void indirizzoValido_ok() {
        Map<String, String> err = ValidatoreForm.validaIndirizzo(
                "Via Roma 10",
                "Salerno",
                "84100",
                "SA",
                "Italia"
        );

        assertNotNull(err);
        assertTrue(err.isEmpty(), "Attesi 0 errori, trovati: " + err);
    }

    @Test
    @DisplayName("CP Indirizzo: via vuota")
    void viaVuota_fail() {
        Map<String, String> err = ValidatoreForm.validaIndirizzo(
                "   ",
                "Salerno",
                "84100",
                "SA",
                "Italia"
        );

        assertTrue(err.containsKey("via"));
        assertEquals("L'indirizzo è obbligatorio", err.get("via"));
    }

    @Test
    @DisplayName("CP Indirizzo: città troppo corta")
    void cittaTroppoCorta_fail() {
        Map<String, String> err = ValidatoreForm.validaIndirizzo(
                "Via Roma 10",
                "A",
                "84100",
                "SA",
                "Italia"
        );

        assertTrue(err.containsKey("citta"));
        assertEquals("La città deve contenere almeno 2 caratteri", err.get("citta"));
    }

    @Test
    @DisplayName("CP Indirizzo: CAP non valido")
    void capNonValido_fail() {
        Map<String, String> err = ValidatoreForm.validaIndirizzo(
                "Via Roma 10",
                "Salerno",
                "1234",
                "SA",
                "Italia"
        );

        assertTrue(err.containsKey("cap"));
        assertEquals("Inserisci un CAP valido (5 cifre)", err.get("cap"));
    }

    @Test
    @DisplayName("CP Indirizzo: provincia non valida")
    void provinciaNonValida_fail() {
        Map<String, String> err = ValidatoreForm.validaIndirizzo(
                "Via Roma 10",
                "Salerno",
                "84100",
                "SAA",
                "Italia"
        );

        assertTrue(err.containsKey("provincia"));
        assertEquals("Inserisci una sigla di provincia valida (es: RM, MI, TO)", err.get("provincia"));
    }
}