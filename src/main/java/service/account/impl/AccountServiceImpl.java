package service.account.impl;

import model.bean.Indirizzo;
import model.bean.Utente;
import model.dao.IndirizzoDAO;
import model.dao.UtenteDAO;
import service.account.AccountService;
import service.account.AccountServiceException;
import service.account.RegistrationData;
import utils.HashUtil;

import java.util.Objects;

public class AccountServiceImpl implements AccountService {

    private final UtenteDAO utenteDAO;
    private final IndirizzoDAO indirizzoDAO;

    public AccountServiceImpl(UtenteDAO utenteDAO, IndirizzoDAO indirizzoDAO) {
        this.utenteDAO = Objects.requireNonNull(utenteDAO);
        this.indirizzoDAO = Objects.requireNonNull(indirizzoDAO);
    }

    @Override
    public Utente login(String email, String password) {
        Utente u = UtenteDAO.login(email, password); // il tuo DAO è statico: lo riusiamo
        if (u == null) {
            throw new AccountServiceException("Email o password non corretti");
        }
        return u;
    }

    @Override
    public void register(RegistrationData d) {
        try {
            String ruolo = "registrato";
            String passwordCifrata = HashUtil.sha1(d.password);

            Utente nuovo = new Utente();
            nuovo.setEmail(d.email);
            nuovo.setPasswordCifrata(passwordCifrata);
            nuovo.setNome(d.nome);
            nuovo.setCognome(d.cognome);
            nuovo.setTelefono(d.telefono);
            nuovo.setRuolo(ruolo);

            boolean okUtente = utenteDAO.inserisciUtente(nuovo);
            if (!okUtente) {
                throw new AccountServiceException("Registrazione fallita. L'email potrebbe essere già registrata.");
            }

            Indirizzo indirizzo = new Indirizzo();
            indirizzo.setIdUtente(nuovo.getIdUtente());
            indirizzo.setVia(d.via);
            indirizzo.setCitta(d.citta);
            indirizzo.setCap(d.cap);
            indirizzo.setProvincia(d.provincia != null ? d.provincia.toUpperCase() : null);
            indirizzo.setPaese(d.paese);

            boolean okIndirizzo = indirizzoDAO.inserisciIndirizzo(indirizzo);
            if (!okIndirizzo) {
                // rollback come fai ora
                utenteDAO.eliminaUtente(nuovo.getIdUtente());
                throw new AccountServiceException("Errore durante la creazione dell'indirizzo");
            }
        } catch (AccountServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new AccountServiceException("Errore durante la registrazione", e);
        }
    }
}
