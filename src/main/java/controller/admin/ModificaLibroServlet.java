package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.bean.Categoria;
import model.bean.Libro;
import model.dao.CategoriaDAO;
import service.ServiceFactory;
import service.catalog.AdminCatalogService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/admin/libri/modifica")
public class ModificaLibroServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AdminCatalogService adminCatalogService = ServiceFactory.adminCatalogService();

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

            List<Categoria> categorie = new CategoriaDAO().trovaTutteCategorie();
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
            libro.setPrezzo(new BigDecimal(prezzoStr.replace(",", ".")));

            libro.setDisponibilita(Integer.parseInt(request.getParameter("quantita")));

            int idCategoria = Integer.parseInt(request.getParameter("categoria"));
            libro.aggiungiCategoria(idCategoria);

            libro.setDescrizione(request.getParameter("descrizione"));

            String copertina = request.getParameter("copertina");
            if (copertina != null && !copertina.trim().isEmpty()) {
                libro.setCopertina(copertina);
            }

            boolean aggiornato = adminCatalogService.updateBook(libro);

            if (aggiornato) {
                response.sendRedirect(request.getContextPath() +
                        "/admin/libri?success=Libro aggiornato con successo");
            } else {
                request.setAttribute("errore", "Impossibile aggiornare il libro");
                request.setAttribute("libro", libro);
                request.getRequestDispatcher("/WEB-INF/jsp/admin/modifica-libro.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dati non validi");
        }
    }
}
