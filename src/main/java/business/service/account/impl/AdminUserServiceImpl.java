package business.service.account.impl;

import business.model.Utente;
import business.service.account.AdminUserService;
import data.dao.UtenteDAO;
import data.util.HashUtil;

import java.util.List;
import java.util.Objects;

public class AdminUserServiceImpl implements AdminUserService {

    private final UtenteDAO utenteDAO;

    public AdminUserServiceImpl(UtenteDAO utenteDAO) {
        this.utenteDAO = Objects.requireNonNull(utenteDAO);
    }

    @Override
    public List<Utente> listAll() {
        return utenteDAO.trovaTuttiUtenti();
    }

    @Override
    public Utente getById(int idUtente) {
        if (idUtente <= 0) return null;
        return utenteDAO.trovaUtentePerId(idUtente);
    }

    @Override
    public boolean create(Utente utente) {
        if (utente == null) return false;
        try {
            return utenteDAO.inserisciUtente(utente);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean create(Utente utente, String rawPassword) {
        if (utente == null) return false;
        if (rawPassword == null || rawPassword.isEmpty()) return false;
        utente.setPasswordCifrata(HashUtil.sha1(rawPassword));
        return create(utente);
    }

    @Override
    public boolean update(Utente utente) {
        if (utente == null || utente.getIdUtente() <= 0) return false;
        try {
            return utenteDAO.aggiornaUtente(utente);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean update(Utente utente, String rawPassword) {
        if (utente == null || utente.getIdUtente() <= 0) return false;
        if (rawPassword != null && !rawPassword.isEmpty()) {
            utente.setPasswordCifrata(HashUtil.sha1(rawPassword));
        }
        return update(utente);
    }

    @Override
    public boolean delete(int idUtente) {
        if (idUtente <= 0) return false;
        try {
            return utenteDAO.eliminaUtente(idUtente);
        } catch (Exception e) {
            return false;
        }
    }
}
