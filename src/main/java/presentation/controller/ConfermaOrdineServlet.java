package presentation.controller;

import business.model.Carrello;
import business.model.Indirizzo;
import business.model.Ordine;
import business.model.Utente;
import business.service.ServiceFactory;
import business.service.account.AccountService;
import business.service.address.AddressService;
import business.service.order.OrderService;
import business.service.order.OrderServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import presentation.util.ValidatoreForm;
import presentation.util.ValidatorePagamento;

import java.io.IOException;
import java.util.Map;

@WebServlet("/conferma-ordine")
public class ConfermaOrdineServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private OrderService orderService;
    private AddressService addressService;
    private AccountService accountService;

    @Override
    public void init() throws ServletException {
        this.orderService = ServiceFactory.orderService();
        this.addressService = ServiceFactory.addressService();
        this.accountService = ServiceFactory.accountService();
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

        Map<String, String> erroriPagamento = ValidatorePagamento.validaPagamento(
                nomeTitolare, numeroCarta, scadenza, cvv);

        if (!erroriPagamento.isEmpty()) {
            request.setAttribute("erroriPagamento", erroriPagamento);
            request.setAttribute("errore", "Dati di pagamento non validi.");
            forwardToPayment(request, response, utente, idIndirizzoSpedizione);
            return;
        }

        try {
            Ordine ordine = orderService.placeOrder(utente.getIdUtente(), idIndirizzoSpedizione, carrello);
            carrello.svuota();
            session.setAttribute("carrello", carrello);

            Indirizzo indirizzoOrdine = null;
            if (idIndirizzoSpedizione != null && idIndirizzoSpedizione > 0
                    && addressService.isOwnedByUser(idIndirizzoSpedizione, utente.getIdUtente())) {
                indirizzoOrdine = addressService.getById(idIndirizzoSpedizione);
            }

            request.setAttribute("ordine", ordine);
            request.setAttribute("indirizzoOrdine", indirizzoOrdine);
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

            if (!accountService.addAddress(utente.getIdUtente(), indirizzo)) {
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

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void setAddressService(AddressService addressService) {
        this.addressService = addressService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
}
