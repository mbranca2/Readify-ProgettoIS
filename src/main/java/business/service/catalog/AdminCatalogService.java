package business.service.catalog;

import business.model.Libro;

public interface AdminCatalogService {

    Libro getBookById(int idLibro);

    void registerObserver(CatalogObserver observer);

    void unregisterObserver(CatalogObserver observer);

    boolean addBook(Libro newBook);

    boolean updateBook(Libro updatedBook);

    boolean removeBook(int idLibro);
}
