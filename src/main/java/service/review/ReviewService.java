package service.review;

import model.bean.Recensione;

import java.util.List;

public interface ReviewService {

    List<Recensione> listByBook(int idLibro);

    /**
     * Inserisce una recensione (utente loggato).
     */
    boolean addReview(Recensione recensione);
}
