package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.Carrello;
import model.bean.Indirizzo;
import model.bean.Ordine;
import model.bean.Utente;
import model.dao.IndirizzoDAO;
import service.ServiceFactory;
import service.order.OrderService;
import service.order.OrderServiceException;

import java.io.IOException;

@WebServlet("/ConfermaOrdine")
public class ConfermaOrdineServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final OrderService orderService = ServiceFactory.orderService();
    private final IndirizzoDAO indirizzoDAO = new IndirizzoDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Utente utente = (Utente) session.getAttribute("utente");
        Carrello carrello = (Carrello) session.getAttribute("carrello");
        if (carrello == null || carrello.isVuoto()) {
            response.sendRedirect(request.getContextPath() + "/carrello");
            return;
        }

        int idIndirizzoSpedizione;
        try {
            idIndirizzoSpedizione = Integer.parseInt(request.getParameter("indirizzoSpedizione"));
        } catch (Exception e) {
            request.setAttribute("errore", "Indirizzo di spedizione non valido.");
            request.getRequestDispatcher("/WEB-INF/jsp/pagamento.jsp").forward(request, response);
            return;
        }

        Indirizzo indirizzo = indirizzoDAO.trovaIndirizzoPerId(idIndirizzoSpedizione);
        if (indirizzo == null || indirizzo.getIdUtente() != utente.getIdUtente()) {
            request.setAttribute("errore", "Indirizzo di spedizione non valido.");
            request.setAttribute("indirizzoSpedizione", idIndirizzoSpedizione);
            request.getRequestDispatcher("/WEB-INF/jsp/pagamento.jsp").forward(request, response);
            return;
        }

        String nomeTitolare = request.getParameter("cardName");
        String numeroCarta = request.getParameter("cardNumber");
        String scadenza = request.getParameter("expiryDate");
        String cvv = request.getParameter("cvv");

        if (!validaDatiPagamento(nomeTitolare, numeroCarta, scadenza, cvv)) {
            request.setAttribute("errore", "Dati di pagamento non validi.");
            request.setAttribute("indirizzoSpedizione", idIndirizzoSpedizione);
            request.getRequestDispatcher("/WEB-INF/jsp/pagamento.jsp").forward(request, response);
            return;
        }

        try {
            Ordine ordine = orderService.placeOrder(utente.getIdUtente(), idIndirizzoSpedizione, carrello);

            // Coerenza UI: svuoto anche la sessione
            carrello.svuota();
            session.setAttribute("carrello", carrello);

            request.setAttribute("idOrdine", ordine.getIdOrdine());
            request.getRequestDispatcher("/WEB-INF/jsp/conferma-ordine.jsp").forward(request, response);

        } catch (OrderServiceException e) {
            request.setAttribute("errore", e.getMessage());
            request.setAttribute("indirizzoSpedizione", idIndirizzoSpedizione);
            request.getRequestDispatcher("/WEB-INF/jsp/pagamento.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errore", "Si è verificato un errore durante l'elaborazione dell'ordine.");
            request.setAttribute("indirizzoSpedizione", idIndirizzoSpedizione);
            request.getRequestDispatcher("/WEB-INF/jsp/pagamento.jsp").forward(request, response);
        }
    }

    private boolean validaDatiPagamento(String nomeTitolare, String numeroCarta, String scadenza, String cvv) {
        return nomeTitolare != null && !nomeTitolare.trim().isEmpty()
                && numeroCarta != null && numeroCarta.replace(" ", "").length() >= 13
                && scadenza != null && scadenza.matches("\\d{2}/\\d{2}")
                && cvv != null && cvv.matches("\\d{3,4}");
    }
}
