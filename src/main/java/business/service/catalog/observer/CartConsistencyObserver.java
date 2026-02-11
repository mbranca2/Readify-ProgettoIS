package business.service.catalog.observer;

import business.service.catalog.CatalogEvent;
import business.service.catalog.CatalogEventType;
import business.service.catalog.CatalogObserver;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class CartConsistencyObserver implements CatalogObserver {

    @Override
    public void onCatalogChanged(Connection conn, CatalogEvent event) throws Exception {
        int bookId = event.bookId();
        CatalogEventType type = event.type();

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
        if (event.newStock() == null) return;

        int newStock = event.newStock();
        int bookId = event.bookId();

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
        BigDecimal newPrice = event.newPrice();
        if (newPrice == null) return;

        int bookId = event.bookId();

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
