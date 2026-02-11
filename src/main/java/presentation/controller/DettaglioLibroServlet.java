package presentation.controller;

import business.model.Libro;
import business.model.Utente;
import business.service.ServiceFactory;
import business.service.catalog.CatalogService;
import business.service.review.ReviewService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "DettaglioLibroServlet", urlPatterns = {"/dettaglio-libro"})
public class DettaglioLibroServlet extends HttpServlet {

    private CatalogService catalogService;
    private ReviewService reviewService;

    @Override
    public void init() throws ServletException {
        this.catalogService = ServiceFactory.catalogService();
        this.reviewService = ServiceFactory.reviewService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int idLibro = parseIntSafe(request.getParameter("id"));
        if (idLibro <= 0) {
            response.sendRedirect(request.getContextPath() + "/home?error=invalid_book");
            return;
        }

        Libro libro = null;
        try {
            libro = catalogService.getById(idLibro);
        } catch (Exception ignored) {
        }

        if (libro == null) {
            response.sendRedirect(request.getContextPath() + "/home?error=book_not_found");
            return;
        }

        request.setAttribute("libro", libro);
        request.setAttribute("recensioni", reviewService.listByBook(idLibro));

        boolean canReview = false;
        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;
        if (utente != null) {
            canReview = reviewService.canUserReview(utente.getIdUtente(), idLibro);
        }
        request.setAttribute("canReview", canReview);
        request.getRequestDispatcher("/WEB-INF/jsp/dettaglioLibro.jsp").forward(request, response);
    }

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return -1;
        }
    }
}
