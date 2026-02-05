package service.review.impl;

import model.bean.Recensione;
import model.dao.RecensioneDAO;
import service.review.ReviewService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ReviewServiceImpl implements ReviewService {

    private final RecensioneDAO recensioneDAO;

    public ReviewServiceImpl(RecensioneDAO recensioneDAO) {
        this.recensioneDAO = Objects.requireNonNull(recensioneDAO);
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
    public boolean addReview(Recensione recensione) {
        if (recensione == null) return false;

        if (recensione.getIdLibro() <= 0) return false;
        if (recensione.getIdUtente() <= 0) return false;

        int voto = recensione.getVoto();
        if (voto < 1 || voto > 5) return false;

        String commento = recensione.getCommento();
        if (commento == null) commento = "";
        commento = commento.trim();

        if (commento.length() > 2000) {
            commento = commento.substring(0, 2000);
        }
        recensione.setCommento(commento);

        try {
            return recensioneDAO.inserisciRecensione(recensione);
        } catch (Exception e) {
            return false;
        }
    }
}
