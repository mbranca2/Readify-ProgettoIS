package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.bean.Libro;
import service.ServiceFactory;
import service.catalog.CatalogService;
import service.review.ReviewService;

import java.io.IOException;

@WebServlet("/dettaglio-libro")
public class DettaglioLibroServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CatalogService catalogService = ServiceFactory.catalogService();
    private final ReviewService reviewService = ServiceFactory.reviewService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID libro mancante");
            return;
        }

        int idLibro;
        try {
            idLibro = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID libro non valido");
            return;
        }

        Libro libro = catalogService.getById(idLibro);
        if (libro == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Libro non trovato");
            return;
        }

        request.setAttribute("libro", libro);
        request.setAttribute("recensioni", reviewService.listByBook(idLibro));
        request.getRequestDispatcher("/WEB-INF/jsp/dettaglioLibro.jsp").forward(request, response);
    }
}
