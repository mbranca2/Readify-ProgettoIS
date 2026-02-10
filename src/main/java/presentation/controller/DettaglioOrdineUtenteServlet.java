package presentation.controller;

import business.model.Indirizzo;
import business.model.Ordine;
import business.model.Utente;
import business.service.ServiceFactory;
import business.service.address.AddressService;
import business.service.order.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/dettaglio-ordine")
public class DettaglioOrdineUtenteServlet extends HttpServlet {

    private OrderService orderService;
    private AddressService addressService;

    @Override
    public void init() throws ServletException {
        this.orderService = ServiceFactory.orderService();
        this.addressService = ServiceFactory.addressService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int idOrdine = parseIntSafe(req.getParameter("id"));
        if (idOrdine <= 0) {
            resp.sendRedirect(req.getContextPath() + "/profilo?tab=orders&error=invalid_order");
            return;
        }

        List<Ordine> ordini = orderService.listByUser(utente.getIdUtente());
        Ordine ordine = ordini.stream()
                .filter(o -> o.getIdOrdine() == idOrdine)
                .findFirst()
                .orElse(null);

        if (ordine == null) {
            resp.sendRedirect(req.getContextPath() + "/profilo?tab=orders&error=order_not_found");
            return;
        }

        Indirizzo indirizzo = null;
        if (ordine.getIdIndirizzo() > 0
                && addressService.isOwnedByUser(ordine.getIdIndirizzo(), utente.getIdUtente())) {
            indirizzo = addressService.getById(ordine.getIdIndirizzo());
        }

        req.setAttribute("ordine", ordine);
        req.setAttribute("indirizzoOrdine", indirizzo);
        req.getRequestDispatcher("/WEB-INF/jsp/dettaglio-ordine.jsp").forward(req, resp);
    }

    private int parseIntSafe(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return -1;
        }
    }
}
