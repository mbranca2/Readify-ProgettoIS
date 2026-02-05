package service.catalog.observer;

import service.catalog.CatalogEvent;
import service.catalog.CatalogEventType;
import service.catalog.CatalogObserver;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class CartConsistencyObserver implements CatalogObserver {

    @Override
    public void onCatalogChanged(Connection conn, CatalogEvent event) throws Exception {
        int bookId = event.getBookId();
        CatalogEventType type = event.getType();

        if (type == CatalogEventType.BOOK_REMOVED) {
            removeBookFromAllCarts(conn, bookId);
            return;
        }

        if (type == CatalogEventType.STOCK_CHANGED) {
            handleStockChange(conn, event);
            return;
        }

        if (type == CatalogEventType.PRICE_CHANGED) {
            handlePriceChange(conn, event);
        }
    }

    private void handleStockChange(Connection conn, CatalogEvent event) throws Exception {
        if (event.getNewStock() == null) return;

        int newStock = event.getNewStock();
        int bookId = event.getBookId();

        if (newStock <= 0) {
            removeBookFromAllCarts(conn, bookId);
            return;
        }

        String updateQty = "UPDATE DettaglioCarrello SET quantita = ? WHERE id_libro = ? AND quantita > ?";
        try (PreparedStatement ps = conn.prepareStatement(updateQty)) {
            ps.setInt(1, newStock);
            ps.setInt(2, bookId);
            ps.setInt(3, newStock);
            ps.executeUpdate();
        }
    }

    private void handlePriceChange(Connection conn, CatalogEvent event) throws Exception {
        BigDecimal newPrice = event.getNewPrice();
        if (newPrice == null) return;

        int bookId = event.getBookId();

        String updatePrice = "UPDATE DettaglioCarrello SET prezzo_unitario = ? WHERE id_libro = ?";
        try (PreparedStatement ps = conn.prepareStatement(updatePrice)) {
            ps.setBigDecimal(1, newPrice);
            ps.setInt(2, bookId);
            ps.executeUpdate();
        }
    }

    private void removeBookFromAllCarts(Connection conn, int bookId) throws Exception {
        String delete = "DELETE FROM DettaglioCarrello WHERE id_libro = ?";
        try (PreparedStatement ps = conn.prepareStatement(delete)) {
            ps.setInt(1, bookId);
            ps.executeUpdate();
        }
    }
}
