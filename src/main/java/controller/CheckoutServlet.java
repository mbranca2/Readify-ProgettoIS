package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.Carrello;
import model.bean.Indirizzo;
import model.bean.Utente;
import model.dao.IndirizzoDAO;

import java.io.IOException;
import java.util.List;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final IndirizzoDAO indirizzoDAO = new IndirizzoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Carrello carrello = (Carrello) session.getAttribute("carrello");
        if (carrello == null || carrello.isVuoto()) {
            req.setAttribute("errore", "Il carrello è vuoto.");
            req.getRequestDispatcher("/WEB-INF/jsp/visualizzaCarrello.jsp").forward(req, resp);
            return;
        }

        List<Indirizzo> indirizzi = indirizzoDAO.trovaIndirizziPerUtente(utente.getIdUtente());
        req.setAttribute("indirizzi", indirizzi);

        req.getRequestDispatcher("/WEB-INF/jsp/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Carrello carrello = (Carrello) session.getAttribute("carrello");
        if (carrello == null || carrello.isVuoto()) {
            request.setAttribute("errore", "Il carrello è vuoto.");
            request.getRequestDispatcher("/WEB-INF/jsp/visualizzaCarrello.jsp").forward(request, response);
            return;
        }

        int idIndirizzoSpedizione;
        try {
            idIndirizzoSpedizione = Integer.parseInt(request.getParameter("indirizzoSpedizione"));
        } catch (Exception e) {
            List<Indirizzo> indirizzi = indirizzoDAO.trovaIndirizziPerUtente(utente.getIdUtente());
            request.setAttribute("indirizzi", indirizzi);
            request.setAttribute("errore", "Seleziona un indirizzo di spedizione valido.");
            request.getRequestDispatcher("/WEB-INF/jsp/checkout.jsp").forward(request, response);
            return;
        }

        Indirizzo indirizzo = indirizzoDAO.trovaIndirizzoPerId(idIndirizzoSpedizione);
        if (indirizzo == null || indirizzo.getIdUtente() != utente.getIdUtente()) {
            List<Indirizzo> indirizzi = indirizzoDAO.trovaIndirizziPerUtente(utente.getIdUtente());
            request.setAttribute("indirizzi", indirizzi);
            request.setAttribute("errore", "Indirizzo di spedizione non valido.");
            request.getRequestDispatcher("/WEB-INF/jsp/checkout.jsp").forward(request, response);
            return;
        }

        request.setAttribute("indirizzoSpedizione", idIndirizzoSpedizione);
        request.getRequestDispatcher("/WEB-INF/jsp/pagamento.jsp").forward(request, response);
    }
}
