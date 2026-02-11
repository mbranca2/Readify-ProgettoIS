package presentation.controller.admin;

import business.model.Utente;
import business.service.ServiceFactory;
import business.service.review.AdminReviewService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/admin/recensioni/elimina")
public class EliminaRecensioneAdminServlet extends HttpServlet {

    private AdminReviewService adminReviewService;

    @Override
    public void init() throws ServletException {
        this.adminReviewService = ServiceFactory.adminReviewService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

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

        int idRecensione = parseIntSafe(req.getParameter("idRecensione"));

        if (idRecensione <= 0) {
            resp.sendRedirect(req.getContextPath() + "/admin/recensioni?error=invalid_id");
            return;
        }

        boolean ok = adminReviewService.deleteById(idRecensione);
        resp.sendRedirect(req.getContextPath() + "/admin/recensioni?" + (ok ? "deleted=1" : "error=delete_failed"));
    }

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return -1;
        }
    }
}
