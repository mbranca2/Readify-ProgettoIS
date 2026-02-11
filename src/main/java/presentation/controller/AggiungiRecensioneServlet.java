package presentation.controller;

import business.model.Recensione;
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

@WebServlet(name = "AggiungiRecensioneServlet", urlPatterns = {"/recensioni/aggiungi"})
public class AggiungiRecensioneServlet extends HttpServlet {

    private ReviewService reviewService;

    @Override
    public void init() throws ServletException {
        this.reviewService = ServiceFactory.reviewService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contextPath = request.getContextPath();
        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        int idLibro = parseIntSafe(request.getParameter("idLibro"));
        int voto = parseIntSafe(request.getParameter("voto"));
        String commento = request.getParameter("commento");

        if (idLibro <= 0) {
            response.sendRedirect(contextPath + "/home?error=invalid_book");
            return;
        }

        Recensione recensione = new Recensione();
        recensione.setIdLibro(idLibro);
        recensione.setIdUtente(utente.getIdUtente());
        recensione.setVoto(voto);
        recensione.setCommento(commento);

        boolean ok = reviewService.addReview(recensione);

        if (ok) {
            response.sendRedirect(contextPath + "/dettaglio-libro?id=" + idLibro + "&review=ok");
        } else {
            response.sendRedirect(contextPath + "/dettaglio-libro?id=" + idLibro + "&review=not_allowed");
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
