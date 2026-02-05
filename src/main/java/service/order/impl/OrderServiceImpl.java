package service.order.impl;

import model.bean.Carrello;
import model.bean.DettaglioOrdine;
import model.bean.Ordine;
import model.bean.StatoOrdine;
import model.dao.CarrelloDAO;
import model.dao.OrdineDAO;
import service.order.OrderService;
import service.order.OrderServiceException;
import utils.DBManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class OrderServiceImpl implements OrderService {

    private final OrdineDAO ordineDAO;
    private final CarrelloDAO carrelloDAO;

    public OrderServiceImpl(OrdineDAO ordineDAO, CarrelloDAO carrelloDAO) {
        this.ordineDAO = Objects.requireNonNull(ordineDAO);
        this.carrelloDAO = Objects.requireNonNull(carrelloDAO);
    }

    @Override
    public Ordine placeOrder(int idUtente, int idIndirizzo, Carrello carrello) {
        if (idUtente <= 0) {
            throw new OrderServiceException("Utente non valido.");
        }
        if (idIndirizzo <= 0) {
            throw new OrderServiceException("Indirizzo di spedizione non valido.");
        }
        if (carrello == null || carrello.isVuoto()) {
            throw new OrderServiceException("Il carrello è vuoto.");
        }

        Connection conn = null;
        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false);

            Ordine ordine = new Ordine();
            ordine.setIdUtente(idUtente);
            ordine.setIdIndirizzo(idIndirizzo);
            ordine.setStato(StatoOrdine.IN_ELABORAZIONE);
            ordine.setTotale(carrello.getTotale());

            for (Carrello.ArticoloCarrello articolo : carrello.getArticoli()) {
                DettaglioOrdine dettaglio = new DettaglioOrdine();
                dettaglio.setIdLibro(articolo.getLibro().getIdLibro());
                dettaglio.setQuantita(articolo.getQuantita());
                dettaglio.setPrezzoUnitario(articolo.getLibro().getPrezzo());
                dettaglio.setTitoloLibro(articolo.getLibro().getTitolo());
                ordine.aggiungiDettaglio(dettaglio);
            }

            boolean salvato = ordineDAO.salvaOrdine(conn, ordine);
            if (!salvato) {
                throw new OrderServiceException("Errore durante il salvataggio dell'ordine");
            }

            carrelloDAO.svuotaCarrelloUtente(conn, idUtente);

            conn.commit();
            return ordine;

        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ignored) {
            }

            if (e instanceof OrderServiceException) {
                throw (OrderServiceException) e;
            }
            throw new OrderServiceException("Si è verificato un errore durante l'elaborazione dell'ordine", e);

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }
}
