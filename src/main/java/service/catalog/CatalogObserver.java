package service.catalog;

import java.sql.Connection;

public interface CatalogObserver {
    /**
     * Reazione ad un evento catalogo.
     * Esegue usando la stessa Connection (quindi nella stessa transazione del service).
     */
    void onCatalogChanged(Connection conn, CatalogEvent event) throws Exception;
}
