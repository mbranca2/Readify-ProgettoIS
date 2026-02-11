package presentation.controller.admin;

import business.model.Categoria;
import business.model.Libro;
import business.service.ServiceFactory;
import business.service.catalog.AdminCatalogService;
import business.service.catalog.CategoryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/libri/modifica")
public class ModificaLibroServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private AdminCatalogService adminCatalogService;
    private CategoryService categoryService;

    @Override
    public void init() throws ServletException {
        this.adminCatalogService = ServiceFactory.adminCatalogService();
        this.categoryService = ServiceFactory.categoryService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID libro mancante");
            return;
        }

        try {
            int idLibro = Integer.parseInt(idParam);
            Libro libro = adminCatalogService.getBookById(idLibro);

            if (libro == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Libro non trovato");
                return;
            }

            List<Categoria> categorie = categoryService.listAll();
            request.setAttribute("categorie", categorie);
            request.setAttribute("libro", libro);
            request.getRequestDispatcher("/WEB-INF/jsp/admin/modifica-libro.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID libro non valido");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("idLibro");

        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID libro mancante");
            return;
        }

        try {
            int idLibro = Integer.parseInt(idParam);
            Libro libro = adminCatalogService.getBookById(idLibro);

            if (libro == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Libro non trovato");
                return;
            }

            libro.setTitolo(request.getParameter("titolo"));
            libro.setAutore(request.getParameter("autore"));

            String prezzoStr = request.getParameter("prezzo");
            if (prezzoStr != null) prezzoStr = prezzoStr.replace(",", ".");
            libro.setPrezzo(new BigDecimal(prezzoStr));
            libro.setDisponibilita(Integer.parseInt(request.getParameter("quantita")));

            String catStr = request.getParameter("categoria");
            if (catStr != null && !catStr.isEmpty()) {
                int idCategoria = Integer.parseInt(catStr);
                libro.setCategorie(new ArrayList<>());
                libro.aggiungiCategoria(idCategoria);
            }

            libro.setDescrizione(request.getParameter("descrizione"));

            String copertina = request.getParameter("copertina");
            if (copertina != null && !copertina.trim().isEmpty()) {
                libro.setCopertina(copertina);
            }

            boolean aggiornato = adminCatalogService.updateBook(libro);

            if (aggiornato) {
                response.sendRedirect(request.getContextPath() + "/admin/libri?success=Libro aggiornato con successo");
            } else {
                List<Categoria> categorie = categoryService.listAll();
                request.setAttribute("categorie", categorie);
                request.setAttribute("errore", "Impossibile aggiornare il libro");
                request.setAttribute("libro", libro);
                request.getRequestDispatcher("/WEB-INF/jsp/admin/modifica-libro.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dati non validi");
        }
    }
}
