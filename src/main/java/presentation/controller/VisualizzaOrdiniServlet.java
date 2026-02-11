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

@WebServlet(name = "VisualizzaOrdiniServlet", urlPatterns = {"/ordini"})
public class VisualizzaOrdiniServlet extends HttpServlet {

    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        this.orderService = ServiceFactory.orderService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.setAttribute("ordini", orderService.listByUser(utente.getIdUtente()));
        request.getRequestDispatcher("/WEB-INF/jsp/ordini.jsp").forward(request, response);
    }
}
