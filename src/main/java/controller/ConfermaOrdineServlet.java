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
import service.address.AddressService;
import service.order.OrderService;
import service.order.OrderServiceException;
import utils.ValidatoreForm;

import java.io.IOException;
import java.util.Map;

@WebServlet("/conferma-ordine")
public class ConfermaOrdineServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private OrderService orderService;
    private AddressService addressService;
    private IndirizzoDAO indirizzoDAO;

    @Override
    public void init() throws ServletException {
        this.orderService = ServiceFactory.orderService();
        this.addressService = ServiceFactory.addressService();
        this.indirizzoDAO = new IndirizzoDAO();
    }

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

        Integer idIndirizzoSpedizione = resolveShippingAddress(request, response, utente);
        if (idIndirizzoSpedizione == null) {
            return;
        }

        String nomeTitolare = request.getParameter("cardName");
        String numeroCarta = request.getParameter("cardNumber");
        String scadenza = request.getParameter("expiryDate");
        String cvv = request.getParameter("cvv");

        if (!validaDatiPagamento(nomeTitolare, numeroCarta, scadenza, cvv)) {
            request.setAttribute("errore", "Dati di pagamento non validi.");
            forwardToPayment(request, response, utente, idIndirizzoSpedizione);
            return;
        }

        try {
            Ordine ordine = orderService.placeOrder(utente.getIdUtente(), idIndirizzoSpedizione, carrello);
            carrello.svuota();
            session.setAttribute("carrello", carrello);

            request.setAttribute("idOrdine", ordine.getIdOrdine());
            request.getRequestDispatcher("/WEB-INF/jsp/conferma-ordine.jsp").forward(request, response);

        } catch (OrderServiceException e) {
            request.setAttribute("errore", e.getMessage());
            forwardToPayment(request, response, utente, idIndirizzoSpedizione);

        } catch (Exception e) {
            request.setAttribute("errore", "Si è verificato un errore durante l'elaborazione dell'ordine.");
            forwardToPayment(request, response, utente, idIndirizzoSpedizione);
        }
    }

    private boolean validaDatiPagamento(String nomeTitolare, String numeroCarta, String scadenza, String cvv) {
        return nomeTitolare != null && !nomeTitolare.trim().isEmpty()
                && numeroCarta != null && numeroCarta.replace(" ", "").length() >= 13
                && scadenza != null && scadenza.matches("\\d{2}/\\d{2}")
                && cvv != null && cvv.matches("\\d{3,4}");
    }

    private Integer resolveShippingAddress(HttpServletRequest request, HttpServletResponse response, Utente utente)
            throws ServletException, IOException {
        String indirizzoParam = request.getParameter("indirizzoSpedizione");
        if (indirizzoParam == null || indirizzoParam.isBlank() || "new".equalsIgnoreCase(indirizzoParam)) {
            String via = request.getParameter("via");
            String cap = request.getParameter("cap");
            String citta = request.getParameter("citta");
            String provincia = request.getParameter("provincia");
            String paese = request.getParameter("paese");

            Map<String, String> errori = ValidatoreForm.validaIndirizzo(via, citta, cap, provincia, paese);
            if (!errori.isEmpty()) {
                request.setAttribute("errore", errori.values().iterator().next());
                forwardToPayment(request, response, utente, "new");
                return null;
            }

            Indirizzo indirizzo = new Indirizzo();
            indirizzo.setIdUtente(utente.getIdUtente());
            indirizzo.setVia(ValidatoreForm.pulisciInput(via));
            indirizzo.setCap(ValidatoreForm.pulisciInput(cap));
            indirizzo.setCitta(ValidatoreForm.pulisciInput(citta));
            indirizzo.setProvincia(ValidatoreForm.pulisciInput(provincia).toUpperCase());
            indirizzo.setPaese(ValidatoreForm.pulisciInput(paese));

            if (!indirizzoDAO.inserisciIndirizzo(indirizzo)) {
                request.setAttribute("errore", "Errore durante il salvataggio dell'indirizzo.");
                forwardToPayment(request, response, utente, "new");
                return null;
            }

            return indirizzo.getIdIndirizzo();
        }

        int idIndirizzoSpedizione;
        try {
            idIndirizzoSpedizione = Integer.parseInt(indirizzoParam);
        } catch (Exception e) {
            request.setAttribute("errore", "Indirizzo di spedizione non valido.");
            forwardToPayment(request, response, utente, null);
            return null;
        }

        if (!addressService.isOwnedByUser(idIndirizzoSpedizione, utente.getIdUtente())) {
            request.setAttribute("errore", "Indirizzo di spedizione non valido.");
            forwardToPayment(request, response, utente, idIndirizzoSpedizione);
            return null;
        }

        return idIndirizzoSpedizione;
    }

    private void forwardToPayment(HttpServletRequest request, HttpServletResponse response, Utente utente,
                                  Object indirizzoSpedizione) throws ServletException, IOException {
        request.setAttribute("indirizzi", addressService.listByUser(utente.getIdUtente()));
        if (indirizzoSpedizione != null) {
            request.setAttribute("indirizzoSpedizione", indirizzoSpedizione);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/pagamento.jsp").forward(request, response);
    }
}
