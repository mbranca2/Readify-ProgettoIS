package business.service.catalog;

import java.math.BigDecimal;

public record CatalogEvent(CatalogEventType type, int bookId, Integer oldStock, Integer newStock, BigDecimal oldPrice,
                           BigDecimal newPrice) {
}
