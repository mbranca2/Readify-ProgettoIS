package service.catalog;

import java.math.BigDecimal;

public class CatalogEvent {
    private final CatalogEventType type;
    private final int bookId;

    private final Integer oldStock;
    private final Integer newStock;
    private final BigDecimal oldPrice;
    private final BigDecimal newPrice;

    public CatalogEvent(CatalogEventType type,
                        int bookId,
                        Integer oldStock,
                        Integer newStock,
                        BigDecimal oldPrice,
                        BigDecimal newPrice) {
        this.type = type;
        this.bookId = bookId;
        this.oldStock = oldStock;
        this.newStock = newStock;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
    }

    public CatalogEventType getType() { return type; }
    public int getBookId() { return bookId; }

    public Integer getOldStock() { return oldStock; }
    public Integer getNewStock() { return newStock; }

    public BigDecimal getOldPrice() { return oldPrice; }
    public BigDecimal getNewPrice() { return newPrice; }
}
