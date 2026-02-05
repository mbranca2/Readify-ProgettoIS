package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.Recensione;
import model.bean.Utente;
import service.ServiceFactory;
import service.review.ReviewService;

import java.io.IOException;

@WebServlet("/recensioni/aggiungi")
public class AggiungiRecensioneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ReviewService reviewService = ServiceFactory.reviewService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int idLibro;
        int voto;

        try {
            idLibro = Integer.parseInt(request.getParameter("idLibro"));
            voto = Integer.parseInt(request.getParameter("voto"));
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri non validi");
            return;
        }

        String commento = request.getParameter("commento");

        Recensione recensione = new Recensione();
        recensione.setIdLibro(idLibro);
        recensione.setIdUtente(utente.getIdUtente());
        recensione.setVoto(voto);
        recensione.setCommento(commento);

        boolean ok = reviewService.addReview(recensione);

        if (ok) {
            response.sendRedirect(request.getContextPath() + "/dettaglio-libro?id=" + idLibro + "&review=ok");
        } else {
            response.sendRedirect(request.getContextPath() + "/dettaglio-libro?id=" + idLibro + "&review=err");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}
