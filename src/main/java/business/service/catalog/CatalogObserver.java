package business.service.catalog;

import java.sql.Connection;

public interface CatalogObserver {
    void onCatalogChanged(Connection conn, CatalogEvent event) throws Exception;
}
