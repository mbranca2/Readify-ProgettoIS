package service.catalog.impl;

import model.bean.Libro;
import model.dao.LibroDAO;
import service.catalog.AdminCatalogService;
import service.catalog.CatalogEvent;
import service.catalog.CatalogEventType;
import service.catalog.CatalogObserver;
import utils.DBManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminCatalogServiceImpl implements AdminCatalogService {

    private final LibroDAO libroDAO;
    private final List<CatalogObserver> observers = new ArrayList<>();

    public AdminCatalogServiceImpl(LibroDAO libroDAO) {
        this.libroDAO = Objects.requireNonNull(libroDAO);
    }

    public void registerObserver(CatalogObserver observer) {
        if (observer != null) observers.add(observer);
    }

    @Override
    public Libro getBookById(int idLibro) {
        return libroDAO.trovaLibroPerId(idLibro);
    }

    @Override
    public boolean updateBook(Libro updatedBook) {
        if (updatedBook == null || updatedBook.getIdLibro() <= 0) {
            return false;
        }

        Connection conn = null;
        try {
            Libro old = libroDAO.trovaLibroPerId(updatedBook.getIdLibro());
            Integer oldStock = old != null ? old.getDisponibilita() : null;
            BigDecimal oldPrice = old != null ? old.getPrezzo() : null;

            Integer newStock = updatedBook.getDisponibilita();
            BigDecimal newPrice = updatedBook.getPrezzo();

            boolean stockChanged = (oldStock != null && newStock != null && !oldStock.equals(newStock));
            boolean priceChanged = (oldPrice != null && newPrice != null && oldPrice.compareTo(newPrice) != 0);

            conn = DBManager.getConnection();
            conn.setAutoCommit(false);

            boolean ok = libroDAO.aggiornaLibro(conn, updatedBook);
            if (!ok) {
                conn.rollback();
                return false;
            }

            if (stockChanged) {
                notifyObservers(conn, new CatalogEvent(
                        CatalogEventType.STOCK_CHANGED,
                        updatedBook.getIdLibro(),
                        oldStock,
                        newStock,
                        oldPrice,
                        newPrice
                ));
            }

            if (priceChanged) {
                notifyObservers(conn, new CatalogEvent(
                        CatalogEventType.PRICE_CHANGED,
                        updatedBook.getIdLibro(),
                        oldStock,
                        newStock,
                        oldPrice,
                        newPrice
                ));
            }

            if (!stockChanged && !priceChanged) {
                notifyObservers(conn, new CatalogEvent(
                        CatalogEventType.BOOK_UPDATED,
                        updatedBook.getIdLibro(),
                        oldStock,
                        newStock,
                        oldPrice,
                        newPrice
                ));
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignored) {}
            return false;

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception ignored) {}
        }
    }

    @Override
    public boolean removeBook(int idLibro) {
        if (idLibro <= 0) return false;

        Connection conn = null;
        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM LibroCategoria WHERE id_libro = ?")) {
                ps.setInt(1, idLibro);
                ps.executeUpdate();
            }

            int rows;
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM Libro WHERE id_libro = ?")) {
                ps.setInt(1, idLibro);
                rows = ps.executeUpdate();
            }

            if (rows <= 0) {
                conn.rollback();
                return false;
            }

            notifyObservers(conn, new CatalogEvent(
                    CatalogEventType.BOOK_REMOVED,
                    idLibro,
                    null,
                    0,
                    null,
                    null
            ));

            conn.commit();
            return true;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignored) {}
            return false;

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception ignored) {}
        }
    }

    private void notifyObservers(Connection conn, CatalogEvent event) throws Exception {
        for (CatalogObserver obs : observers) {
            obs.onCatalogChanged(conn, event);
        }
    }
}
