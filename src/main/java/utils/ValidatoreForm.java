package utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ValidatoreForm {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$");

    private static final Pattern TELEFONO_PATTERN =
            Pattern.compile("^\\+?[0-9\\s-]{6,}$");

    private static final Pattern CAP_PATTERN = Pattern.compile("^\\d{5}$");

    private static final Pattern PROVINCIA_PATTERN = Pattern.compile("^[A-Za-z]{2}$");

    public static Map<String, String> validaRegistrazione(String nome, String cognome, String email,
                                                          String password, String confermaPassword,
                                                          String telefono, String via, String citta, String cap,
                                                          String provincia, String paese) {

        Map<String, String> errori = validaDatiPersonali(
                nome, cognome, email, password,
                confermaPassword, telefono
        );

        Map<String, String> erroriIndirizzo = validaIndirizzo(
                via, citta, cap, provincia, paese
        );

        errori.putAll(erroriIndirizzo);

        return errori;

    }

    public static Map<String, String> validaIndirizzo(
            String via, String citta, String cap, String provincia, String paese) {

        Map<String, String> errori = new HashMap<>();

        if (via == null || via.trim().isEmpty()) {
            errori.put("via", "L'indirizzo è obbligatorio");
        } else if (via.trim().length() < 5) {
            errori.put("via", "L'indirizzo è troppo corto");
        } else if (via.trim().length() > 100) {
            errori.put("via", "L'indirizzo non può superare i 100 caratteri");
        }

        if (citta == null || citta.trim().isEmpty()) {
            errori.put("citta", "La città è obbligatoria");
        } else if (citta.trim().length() < 2) {
            errori.put("citta", "La città deve contenere almeno 2 caratteri");
        } else if (citta.trim().length() > 50) {
            errori.put("citta", "La città non può superare i 50 caratteri");
        }

        String capTrim = cap == null ? null : cap.trim();
        if (capTrim == null || capTrim.isEmpty()) {
            errori.put("cap", "Il CAP è obbligatorio");
        } else if (!CAP_PATTERN.matcher(capTrim).matches()) {
            errori.put("cap", "Inserisci un CAP valido (5 cifre)");
        }

        String provinciaTrim = provincia == null ? null : provincia.trim().toUpperCase();
        if (provinciaTrim == null || provinciaTrim.isEmpty()) {
            errori.put("provincia", "La provincia è obbligatoria");
        } else if (!PROVINCIA_PATTERN.matcher(provinciaTrim).matches()) {
            errori.put("provincia", "Inserisci una sigla di provincia valida (es: RM, MI, TO)");
        }

        if (paese == null || paese.trim().isEmpty()) {
            errori.put("paese", "Il paese è obbligatorio");
        }
        return errori;
    }

    public static String pulisciInput(String input) {
        if (input == null) {
            return null;
        }
        String pulito = input.trim();
        pulito = pulito.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");
        return pulito;
    }

    private static Map<String, String> validaDatiPersonali(
            String nome, String cognome, String email,
            String password, String confermaPassword,
            String telefono) {

        Map<String, String> errori = new HashMap<>();

        if (nome == null || nome.trim().isEmpty()) {
            errori.put("nome", "Il nome è obbligatorio");
        } else if (nome.trim().length() < 2) {
            errori.put("nome", "Il nome deve contenere almeno 2 caratteri");
        } else if (nome.trim().length() > 50) {
            errori.put("nome", "Il nome non può superare i 50 caratteri");
        }

        if (cognome == null || cognome.trim().isEmpty()) {
            errori.put("cognome", "Il cognome è obbligatorio");
        } else if (cognome.trim().length() < 2) {
            errori.put("cognome", "Il cognome deve contenere almeno 2 caratteri");
        } else if (cognome.trim().length() > 50) {
            errori.put("cognome", "Il cognome non può superare i 50 caratteri");
        }

        String emailTrim = email == null ? null : email.trim();
        if (emailTrim == null || emailTrim.isEmpty()) {
            errori.put("email", "L'email è obbligatoria");
        } else if (!EMAIL_PATTERN.matcher(emailTrim).matches()) {
            errori.put("email", "Inserisci un indirizzo email valido");
        } else if (emailTrim.length() > 100) {
            errori.put("email", "L'email non può superare i 100 caratteri");
        }

        if (password == null || password.isEmpty()) {
            errori.put("password", "La password è obbligatoria");
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            errori.put("password",
                    "La password deve contenere almeno 8 caratteri, " +
                            "una lettera maiuscola, una minuscola e un numero");
        }

        if (confermaPassword == null || !password.equals(confermaPassword)) {
            errori.put("confermaPassword", "Le password non coincidono");
        }

        String telTrim = telefono == null ? null : telefono.trim();
        if (telTrim != null && !telTrim.isEmpty() &&
                !TELEFONO_PATTERN.matcher(telTrim).matches()) {
            errori.put("telefono", "Inserisci un numero di telefono valido");
        }
        return errori;
    }
}
