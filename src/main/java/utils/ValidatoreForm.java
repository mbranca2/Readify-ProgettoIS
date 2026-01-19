package utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ValidatoreForm {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    // Requisiti minimi password:
    // - 8+ caratteri
    // - Una maiuscola
    // - Una minuscola
    // - Un numero
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$");

    private static final Pattern TELEFONO_PATTERN =
            Pattern.compile("^\\+?[0-9\\s-]{6,}$");

    private static final Pattern CAP_PATTERN = Pattern.compile("^\\d{5}$");

    private static final Pattern PROVINCIA_PATTERN = Pattern.compile("^[A-Za-z]{2}$");


    public static Map<String, String> validaRegistrazione(
            String nome, String cognome, String email, 
            String password, String confermaPassword, 
            String telefono, boolean privacyAccettata,
            String via, String citta, String cap,
            String provincia, String paese) {

        Map<String, String> errori = validaDatiPersonali(
                nome, cognome, email, password,
                confermaPassword, telefono, privacyAccettata
        );

        // validazione dell'idnirizzo
        Map<String, String> erroriIndirizzo = validaIndirizzo(
                via, citta, cap, provincia, paese
        );

        errori.putAll(erroriIndirizzo);

        return errori;

    }

    public static Map<String, String> validaIndirizzo(
            String via, String citta, String cap, String provincia, String paese) {

        Map<String, String> errori = new HashMap<>();

        // Validazione via
        if (via == null || via.trim().isEmpty()) {
            errori.put("via", "L'indirizzo è obbligatorio");
        } else if (via.trim().length() < 5) {
            errori.put("via", "L'indirizzo è troppo corto");
        } else if (via.length() > 100) {
            errori.put("via", "L'indirizzo non può superare i 100 caratteri");
        }

        // Validazione città
        if (citta == null || citta.trim().isEmpty()) {
            errori.put("citta", "La città è obbligatoria");
        } else if (citta.trim().length() < 2) {
            errori.put("citta", "La città deve contenere almeno 2 caratteri");
        } else if (citta.length() > 50) {
            errori.put("citta", "La città non può superare i 50 caratteri");
        }

        // Validazione CAP
        if (cap == null || cap.trim().isEmpty()) {
            errori.put("cap", "Il CAP è obbligatorio");
        } else if (!CAP_PATTERN.matcher(cap).matches()) {
            errori.put("cap", "Inserisci un CAP valido (5 cifre)");
        }

        // Validazione provincia
        if (provincia == null || provincia.trim().isEmpty()) {
            errori.put("provincia", "La provincia è obbligatoria");
        } else if (!PROVINCIA_PATTERN.matcher(provincia).matches()) {
            errori.put("provincia", "Inserisci una sigla di provincia valida (es: RM, MI, TO)");
        }

        // Validazione paese
        if (paese == null || paese.trim().isEmpty()) {
            errori.put("paese", "Il paese è obbligatorio");
        }

        return errori;
    }


    public static String pulisciInput(String input) {
        if (input == null) {
            return null;
        }
        // Rimuove spazi bianchi all'inizio e alla fine
        String pulito = input.trim();
        pulito = pulito.replace("<", "&lt;")
                      .replace(">", "&gt;")
                      .replace("\"", "&quot;")
                      .replace("'", "&#x27;")
                      .replace("/", "&#x2F;");
        return pulito;
    }

    private static Map<String, String> validaDatiPersonali(
            String nome, String cognome, String email,
            String password, String confermaPassword,
            String telefono, boolean privacyAccettata) {

        Map<String, String> errori = new HashMap<>();

        // Validazione nome
        if (nome == null || nome.trim().isEmpty()) {
            errori.put("nome", "Il nome è obbligatorio");
        } else if (nome.trim().length() < 2) {
            errori.put("nome", "Il nome deve contenere almeno 2 caratteri");
        } else if (nome.length() > 50) {
            errori.put("nome", "Il nome non può superare i 50 caratteri");
        }

        // Validazione cognome
        if (cognome == null || cognome.trim().isEmpty()) {
            errori.put("cognome", "Il cognome è obbligatorio");
        } else if (cognome.trim().length() < 2) {
            errori.put("cognome", "Il cognome deve contenere almeno 2 caratteri");
        } else if (cognome.length() > 50) {
            errori.put("cognome", "Il cognome non può superare i 50 caratteri");
        }

        // Validazione email
        if (email == null || email.trim().isEmpty()) {
            errori.put("email", "L'email è obbligatoria");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            errori.put("email", "Inserisci un indirizzo email valido");
        } else if (email.length() > 100) {
            errori.put("email", "L'email non può superare i 100 caratteri");
        }

        // Validazione password
        if (password == null || password.isEmpty()) {
            errori.put("password", "La password è obbligatoria");
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            errori.put("password",
                    "La password deve contenere almeno 8 caratteri, " +
                            "una lettera maiuscola, una minuscola e un numero");
        }

        // Validazione conferma password
        if (!password.equals(confermaPassword)) {
            errori.put("confermaPassword", "Le password non coincidono");
        }

        // Validazione telefono (opzionale)
        if (telefono != null && !telefono.trim().isEmpty() &&
                !TELEFONO_PATTERN.matcher(telefono).matches()) {
            errori.put("telefono", "Inserisci un numero di telefono valido");
        }

        // Validazione accettazione privacy
        if (!privacyAccettata) {
            errori.put("privacy", "È necessario accettare l'informativa sulla privacy");
        }

        return errori;
    }
}
