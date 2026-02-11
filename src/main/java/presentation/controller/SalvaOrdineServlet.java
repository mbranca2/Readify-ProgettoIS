package presentation.controller;

import business.model.Carrello;
import business.model.Ordine;
import business.model.Utente;
import business.service.ServiceFactory;
import business.service.order.OrderService;
import business.service.order.OrderServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/salva-ordine")
public class SalvaOrdineServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(SalvaOrdineServlet.class.getName());

    private final OrderService orderService = ServiceFactory.orderService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;
        Carrello carrello = (session != null) ? (Carrello) session.getAttribute("carrello") : null;

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (utente == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\": false, \"error\": \"Utente non autenticato\"}");
            return;
        }

        if (carrello == null || carrello.isVuoto()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\": false, \"error\": \"Il carrello è vuoto\"}");
            return;
        }

        int idIndirizzoSpedizione;
        try {
            idIndirizzoSpedizione = Integer.parseInt(request.getParameter("indirizzoSpedizione"));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\": false, \"error\": \"Indirizzo di spedizione non valido\"}");
            return;
        }

        try {
            Ordine ordine = orderService.placeOrder(utente.getIdUtente(), idIndirizzoSpedizione, carrello);
            carrello.svuota();
            session.setAttribute("carrello", carrello);

            response.getWriter().write("{\"success\": true, \"orderId\": " + ordine.getIdOrdine() + "}");

        } catch (OrderServiceException e) {
            logger.log(Level.WARNING, "Errore di dominio durante il salvataggio dell'ordine", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\": false, \"error\": \"" + escapeJson(e.getMessage()) + "\"}");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore durante il salvataggio dell'ordine", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"error\": \"Errore di sistema\"}");
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
