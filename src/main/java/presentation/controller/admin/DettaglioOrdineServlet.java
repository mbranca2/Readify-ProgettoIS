package presentation.controller.admin;

import business.model.Utente;
import business.service.ServiceFactory;
import business.service.order.AdminOrderService;
import business.service.order.AdminOrderServiceException;
import business.service.order.dto.AdminOrderDetail;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/admin/ordine")
public class DettaglioOrdineServlet extends HttpServlet {

    private AdminOrderService adminOrderService;

    @Override
    public void init() throws ServletException {
        this.adminOrderService = ServiceFactory.adminOrderService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

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

        int idOrdine = parseIntSafe(req.getParameter("id"));
        if (idOrdine <= 0) {
            resp.sendRedirect(req.getContextPath() + "/admin/ordini?error=invalid_order");
            return;
        }

        try {
            AdminOrderDetail detail = adminOrderService.getOrderDetail(idOrdine);

            req.setAttribute("ordine", detail.ordine());
            req.setAttribute("utenteOrdine", detail.utente());
            req.setAttribute("indirizzoOrdine", detail.indirizzo());

            req.getRequestDispatcher("/WEB-INF/jsp/admin/dettaglio-ordine.jsp").forward(req, resp);

        } catch (AdminOrderServiceException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/ordini?error=" + encode(e.getMessage()));
        }
    }

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return -1;
        }
    }

    private String encode(String s) {
        if (s == null) return "error";
        return s.replace(" ", "_");
    }
}
