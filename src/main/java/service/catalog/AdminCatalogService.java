package service.catalog;

import model.bean.Libro;

public interface AdminCatalogService {

    Libro getBookById(int idLibro);

    boolean updateBook(Libro updatedBook);

    /**
     * Rimuove un libro dal catalogo e notifica gli observer.
     */
    boolean removeBook(int idLibro);
}
