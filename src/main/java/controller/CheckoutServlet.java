package controller;

import model.Carrello;
import model.Ordine;
import model.DettaglioOrdine;
import model.Utente;
import model.dao.OrdineDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // controllo se l'utente sta cercando di fare il checkout senza essere loggato o con il carrello vuoto
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
            return;
        }

        Carrello carrello = (Carrello) session.getAttribute("carrello");
        if (carrello == null || carrello.isVuoto()) {
            req.setAttribute("errore", "Il carrello è vuoto.");
            req.getRequestDispatcher("/jsp/visualizzaCarrello.jsp").forward(req, resp);
            return;
        }
        
        // reindirizzo al checkout all'utente
        req.getRequestDispatcher("/jsp/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }

        Carrello carrello = (Carrello) session.getAttribute("carrello");
        if (carrello == null || carrello.isVuoto()) {
            request.setAttribute("errore", "Il carrello è vuoto.");
            request.getRequestDispatcher("/jsp/visualizzaCarrello.jsp").forward(request, response);
            return;
        }

        // passo alla schermata di pagamento
        response.sendRedirect(request.getContextPath() + "/jsp/pagamento.jsp");
    }
}
