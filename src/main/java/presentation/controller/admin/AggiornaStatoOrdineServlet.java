package presentation.controller.admin;

import business.model.StatoOrdine;
import business.model.Utente;
import business.service.ServiceFactory;
import business.service.order.AdminOrderService;
import business.service.order.AdminOrderServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/admin/ordine/stato")
public class AggiornaStatoOrdineServlet extends HttpServlet {

    private AdminOrderService adminOrderService;

    @Override
    public void init() throws ServletException {
        this.adminOrderService = ServiceFactory.adminOrderService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Utente admin = (Utente) session.getAttribute("utente");
        if (!"admin".equals(admin.getRuolo())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        int idOrdine = parseIntSafe(req.getParameter("idOrdine"));
        String statoRaw = req.getParameter("nuovoStato");

        if (idOrdine <= 0 || statoRaw == null || statoRaw.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/ordine?id=" + idOrdine + "&error=invalid_input");
            return;
        }

        StatoOrdine nuovoStato;
        try {
            nuovoStato = StatoOrdine.valueOf(statoRaw.trim());
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/admin/ordine?id=" + idOrdine + "&error=invalid_state");
            return;
        }

        try {
            adminOrderService.updateStatus(idOrdine, nuovoStato);
            resp.sendRedirect(req.getContextPath() + "/admin/ordine?id=" + idOrdine + "&updated=1");
        } catch (AdminOrderServiceException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/ordine?id=" + idOrdine + "&error=update_failed");
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
