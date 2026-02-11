package business.service.review.impl;

import business.model.Recensione;
import business.service.review.ReviewService;
import data.dao.OrdineDAO;
import data.dao.RecensioneDAO;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ReviewServiceImpl implements ReviewService {

    private final RecensioneDAO recensioneDAO;
    private final OrdineDAO ordineDAO;

    public ReviewServiceImpl(RecensioneDAO recensioneDAO, OrdineDAO ordineDAO) {
        this.recensioneDAO = Objects.requireNonNull(recensioneDAO);
        this.ordineDAO = Objects.requireNonNull(ordineDAO);
    }

    @Override
    public List<Recensione> listByBook(int idLibro) {
        if (idLibro <= 0) return Collections.emptyList();
        try {
            return recensioneDAO.trovaRecensioniPerLibro(idLibro);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean canUserReview(int idUtente, int idLibro) {
        if (idUtente <= 0 || idLibro <= 0) return false;
        return ordineDAO.hasUserPurchasedBook(idUtente, idLibro);
    }

    @Override
    public boolean addReview(Recensione recensione) {
        if (recensione == null) return false;

        int idLibro = recensione.getIdLibro();
        int idUtente = recensione.getIdUtente();

        if (idLibro <= 0) return false;
        if (idUtente <= 0) return false;

        int voto = recensione.getVoto();
        if (voto < 1 || voto > 5) return false;
        if (!canUserReview(idUtente, idLibro)) {
            return false;
        }

        if (recensioneDAO.trovaRecensionePerUtenteELibro(idUtente, idLibro) != null) {
            return false;
        }

        String commento = recensione.getCommento();
        if (commento == null) commento = "";
        commento = commento.trim();
        if (commento.length() > 2000) commento = commento.substring(0, 2000);
        recensione.setCommento(commento);

        try {
            return recensioneDAO.inserisciRecensione(recensione);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateReview(int idRecensione, int idUtente, int voto, String commento) {
        if (idRecensione <= 0 || idUtente <= 0) return false;
        if (voto < 1 || voto > 5) return false;

        Recensione existing = recensioneDAO.trovaRecensionePerId(idRecensione);
        if (existing == null) return false;

        // Solo proprietario
        if (existing.getIdUtente() != idUtente) return false;

        // Coerenza RF: deve aver acquistato quel libro (stessa regola del create)
        if (!canUserReview(idUtente, existing.getIdLibro())) return false;

        if (commento == null) commento = "";
        commento = commento.trim();
        if (commento.length() > 2000) commento = commento.substring(0, 2000);

        return recensioneDAO.aggiornaRecensione(idRecensione, idUtente, voto, commento);
    }

    @Override
    public boolean deleteReview(int idRecensione, int idUtente) {
        if (idRecensione <= 0 || idUtente <= 0) return false;

        Recensione existing = recensioneDAO.trovaRecensionePerId(idRecensione);
        if (existing == null) return false;

        // Solo proprietario
        if (existing.getIdUtente() != idUtente) return false;

        return recensioneDAO.eliminaRecensione(idRecensione, idUtente);
    }
}
