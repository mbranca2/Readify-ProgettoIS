package presentation.controller;

import business.model.Utente;
import business.service.ServiceFactory;
import business.service.review.ReviewService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/recensioni/modifica")
public class ModificaRecensioneServlet extends HttpServlet {

    private ReviewService reviewService;

    @Override
    public void init() throws ServletException {
        this.reviewService = ServiceFactory.reviewService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String ctx = request.getContextPath();
        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null) {
            response.sendRedirect(ctx + "/login");
            return;
        }

        int idRecensione = parseIntSafe(request.getParameter("idRecensione"));
        int idLibro = parseIntSafe(request.getParameter("idLibro"));
        int voto = parseIntSafe(request.getParameter("voto"));
        String commento = request.getParameter("commento");

        if (idLibro <= 0) {
            response.sendRedirect(ctx + "/home?error=invalid_book");
            return;
        }

        boolean ok = reviewService.updateReview(idRecensione, utente.getIdUtente(), voto, commento);

        if (ok) {
            response.sendRedirect(ctx + "/dettaglio-libro?id=" + idLibro + "&review_edit=ok");
        } else {
            response.sendRedirect(ctx + "/dettaglio-libro?id=" + idLibro + "&review_edit=ko");
        }
    }

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return -1;
        }
    }
}
