package business.service.order.impl;

import business.model.Carrello;
import business.model.DettaglioOrdine;
import business.model.Ordine;
import business.model.StatoOrdine;
import business.service.order.OrderService;
import business.service.order.OrderServiceException;
import data.dao.CarrelloDAO;
import data.dao.OrdineDAO;
import data.util.DBManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;

public class OrderServiceImpl implements OrderService {

    private final OrdineDAO ordineDAO;
    private final CarrelloDAO carrelloDAO;

    public OrderServiceImpl(OrdineDAO ordineDAO, CarrelloDAO carrelloDAO) {
        this.ordineDAO = Objects.requireNonNull(ordineDAO);
        this.carrelloDAO = Objects.requireNonNull(carrelloDAO);
    }

    @Override
    public Ordine placeOrder(int idUtente, int idIndirizzo, Carrello carrello) throws OrderServiceException {
        if (idUtente <= 0) throw new OrderServiceException("Utente non valido.");
        if (idIndirizzo <= 0) throw new OrderServiceException("Indirizzo di spedizione non valido.");
        if (carrello == null || carrello.isVuoto()) throw new OrderServiceException("Il carrello è vuoto.");

        List<DettaglioOrdine> dettagli = carrello.getDettagli();
        if (dettagli == null || dettagli.isEmpty()) throw new OrderServiceException("Il carrello è vuoto.");

        BigDecimal totale = carrello.getTotale();
        if (totale == null || totale.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OrderServiceException("Totale ordine non valido.");
        }

        Connection conn = null;
        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false);

            Ordine ordine = new Ordine();
            ordine.setIdUtente(idUtente);
            ordine.setIdIndirizzo(idIndirizzo);
            ordine.setStato(StatoOrdine.IN_ATTESA);
            ordine.setDettagli(dettagli);
            ordine.setTotale(totale);

            boolean ok = ordineDAO.salvaOrdine(conn, ordine);
            if (!ok) {
                conn.rollback();
                throw new OrderServiceException("Impossibile completare l'ordine (disponibilità insufficiente o errore di salvataggio).");
            }

            carrelloDAO.svuotaCarrelloUtente(conn, idUtente);

            conn.commit();
            return ordine;

        } catch (OrderServiceException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (Exception ignored) {
            }
            throw e;

        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (Exception ignored) {
            }
            throw new OrderServiceException("Errore durante l'elaborazione dell'ordine.", e);

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public List<Ordine> listByUser(int idUtente) {
        if (idUtente <= 0) return List.of();
        return ordineDAO.trovaPerIdUtente(idUtente);
    }

    @Override
    public boolean cancelOrder(int idOrdine, int idUtente) throws OrderServiceException {
        if (idOrdine <= 0) throw new OrderServiceException("Ordine non valido.");
        if (idUtente <= 0) throw new OrderServiceException("Utente non valido.");

        Connection conn = null;
        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false);

            boolean cancelled = ordineDAO.cancelIfPending(conn, idOrdine, idUtente);
            if (!cancelled) {
                conn.rollback();
                return false;
            }

            ordineDAO.restoreStockForOrder(conn, idOrdine);

            conn.commit();
            return true;

        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (Exception ignored) {
            }
            throw new OrderServiceException("Errore durante l'annullamento dell'ordine.", e);

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception ignored) {
            }
        }
    }
}
