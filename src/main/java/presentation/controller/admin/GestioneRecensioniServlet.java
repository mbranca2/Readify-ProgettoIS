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

@WebServlet("/admin/recensioni")
public class GestioneRecensioniServlet extends HttpServlet {

    private AdminReviewService adminReviewService;

    @Override
    public void init() throws ServletException {
        this.adminReviewService = ServiceFactory.adminReviewService();
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

        req.setAttribute("reviews", adminReviewService.listAll());
        req.getRequestDispatcher("/WEB-INF/jsp/admin/gestione-recensioni.jsp").forward(req, resp);
    }
}
