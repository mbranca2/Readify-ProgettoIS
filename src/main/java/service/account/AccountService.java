package service.account;

import model.bean.Utente;

public interface AccountService {

    /**
     * Effettua login. Lancia AccountServiceException se credenziali errate.
     */
    Utente login(String email, String password);

    /**
     * Registra utente + indirizzo. Lancia AccountServiceException se fallisce
     * (es. email già presente o errore DB).
     */
    void register(RegistrationData data);
}
