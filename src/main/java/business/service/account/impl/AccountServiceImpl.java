package business.service.account.impl;

import business.model.Indirizzo;
import business.model.Utente;
import data.dao.IndirizzoDAO;
import data.dao.UtenteDAO;
import business.service.account.AccountService;
import business.service.account.AccountServiceException;
import business.service.account.RegistrationData;
import data.util.DBManager;
import data.util.HashUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
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
        if (email == null || email.trim().isEmpty()) return null;
        if (password == null) return null;
        try {
            return UtenteDAO.login(email.trim(), password);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean register(RegistrationData data) throws AccountServiceException {
        if (data == null) throw new AccountServiceException("Dati di registrazione mancanti.");

        String email = safe(data.email);
        String password = data.password; // password può includere spazi
        String nome = safe(data.nome);
        String cognome = safe(data.cognome);
        String telefono = safe(data.telefono);

        String via = safe(data.via);
        String citta = safe(data.citta);
        String cap = safe(data.cap);
        String provincia = safe(data.provincia);
        String paese = safe(data.paese);
        if (paese.isEmpty()) paese = "Italia";

        if (email.isEmpty() || password == null || password.isEmpty()) {
            throw new AccountServiceException("Email e password sono obbligatorie.");
        }

        try (Connection conn = DBManager.getConnection()) {
            conn.setAutoCommit(false);

            if (utenteDAO.emailEsistente(conn, email)) {
                conn.rollback();
                throw new AccountServiceException("Email già registrata.");
            }

            Utente utente = new Utente();
            utente.setEmail(email);
            utente.setPasswordCifrata(HashUtil.sha1(password));
            utente.setNome(nome);
            utente.setCognome(cognome);
            utente.setTelefono(telefono);
            utente.setRuolo("registrato");

            boolean okUser = utenteDAO.inserisciUtente(conn, utente);
            if (!okUser || utente.getIdUtente() <= 0) {
                conn.rollback();
                throw new AccountServiceException("Errore durante la creazione dell'utente.");
            }

            Indirizzo indirizzo = new Indirizzo();
            indirizzo.setIdUtente(utente.getIdUtente());
            indirizzo.setVia(via);
            indirizzo.setCitta(citta);
            indirizzo.setCap(cap);
            indirizzo.setProvincia(provincia);
            indirizzo.setPaese(paese);

            boolean okAddr = indirizzoDAO.inserisciIndirizzo(conn, indirizzo);
            if (!okAddr) {
                conn.rollback();
                throw new AccountServiceException("Errore durante la creazione dell'indirizzo.");
            }

            conn.commit();
            return true;

        } catch (AccountServiceException e) {
            throw e;
        } catch (SQLException e) {
            throw new AccountServiceException("Errore DB durante la registrazione.", e);
        } catch (Exception e) {
            throw new AccountServiceException("Errore inatteso durante la registrazione.", e);
        }
    }

    @Override
    public boolean register(Utente utente) {
        if (utente == null) return false;
        try {
            return utenteDAO.inserisciUtente(utente);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Indirizzo> listAddresses(int idUtente) {
        if (idUtente <= 0) return Collections.emptyList();
        try {
            return indirizzoDAO.trovaIndirizziPerUtente(idUtente);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean addAddress(int idUtente, Indirizzo indirizzo) {
        if (idUtente <= 0 || indirizzo == null) return false;
        indirizzo.setIdUtente(idUtente);
        try {
            return indirizzoDAO.inserisciIndirizzo(indirizzo);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateAddress(int idUtente, Indirizzo indirizzo) {
        if (idUtente <= 0 || indirizzo == null || indirizzo.getIdIndirizzo() <= 0) return false;

        Indirizzo existing = indirizzoDAO.trovaIndirizzoPerId(indirizzo.getIdIndirizzo());
        if (existing == null || existing.getIdUtente() != idUtente) return false;

        indirizzo.setIdUtente(idUtente);

        try {
            return indirizzoDAO.aggiornaIndirizzo(indirizzo);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteAddress(int idUtente, int idIndirizzo) {
        if (idUtente <= 0 || idIndirizzo <= 0) return false;
        try {
            return indirizzoDAO.eliminaIndirizzo(idIndirizzo, idUtente);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean changePassword(int idUtente, String oldPassword, String newPassword) {
        if (idUtente <= 0) return false;
        if (oldPassword == null || oldPassword.isEmpty()) return false;
        if (newPassword == null || newPassword.trim().length() < 6) return false;

        try {
            return utenteDAO.changePassword(idUtente, oldPassword, newPassword.trim());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateProfile(Utente utente) {
        if (utente == null || utente.getIdUtente() <= 0) return false;
        try {
            return utenteDAO.aggiornaUtente(utente);
        } catch (Exception e) {
            return false;
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
