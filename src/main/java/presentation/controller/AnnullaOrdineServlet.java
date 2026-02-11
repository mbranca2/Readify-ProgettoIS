package presentation.controller;

import business.model.Utente;
import business.service.ServiceFactory;
import business.service.order.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "AnnullaOrdineServlet", urlPatterns = {"/annulla-ordine"})
public class AnnullaOrdineServlet extends HttpServlet {

    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        this.orderService = ServiceFactory.orderService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int idOrdine;
        try {
            idOrdine = Integer.parseInt(request.getParameter("idOrdine"));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        boolean ok = orderService.cancelOrder(idOrdine, utente.getIdUtente());
        response.setStatus(ok ? HttpServletResponse.SC_OK : HttpServletResponse.SC_CONFLICT);
    }
}
