package presentation.util;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ValidatorePagamento {

    private static final Pattern NUMERO_CARTA = Pattern.compile("^\\d{16}$");
    private static final Pattern SCADENZA = Pattern.compile("^(0[1-9]|1[0-2])\\/\\d{2}$");
    private static final Pattern CVV = Pattern.compile("^\\d{3}$");

    private ValidatorePagamento() {
    }

    public static Map<String, String> validaPagamento(String titolare, String numeroCarta, String scadenza, String cvv) {
        Map<String, String> err = new HashMap<>();

        if (titolare == null || titolare.trim().isEmpty()) {
            err.put("titolare", "Il titolare della carta è obbligatorio");
        }

        String n = (numeroCarta == null) ? "" : numeroCarta.replace(" ", "").trim();
        if (!NUMERO_CARTA.matcher(n).matches()) {
            err.put("numeroCarta", "Numero carta non valido (16 cifre)");
        }

        String s = (scadenza == null) ? "" : scadenza.trim();
        if (!SCADENZA.matcher(s).matches()) {
            err.put("scadenza", "Scadenza non valida (MM/YY)");
        } else {
            int mese = Integer.parseInt(s.substring(0, 2));
            int anno2 = Integer.parseInt(s.substring(3, 5));
            int anno = 2000 + anno2;

            YearMonth exp = YearMonth.of(anno, mese);
            YearMonth now = YearMonth.now();

            if (exp.isBefore(now)) {
                err.put("scadenza", "La carta è scaduta");
            }
        }

        String c = (cvv == null) ? "" : cvv.trim();
        if (!CVV.matcher(c).matches()) {
            err.put("cvv", "CVV non valido (3 cifre)");
        }

        return err;
    }
}
