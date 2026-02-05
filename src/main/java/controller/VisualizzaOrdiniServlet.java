package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.Ordine;
import model.bean.Utente;
import model.dao.OrdineDAO;

import java.io.IOException;
import java.util.List;

@WebServlet("/profilo/ordini")
public class VisualizzaOrdiniServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final OrdineDAO ordineDAO = new OrdineDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=/profilo/ordini");
            return;
        }

        try {
            System.out.println("ID utente: " + utente.getIdUtente());
            List<Ordine> ordini = ordineDAO.trovaPerIdUtente(utente.getIdUtente());
            System.out.println("Numero ordini trovati: " + (ordini != null ? ordini.size() : "null"));
            request.setAttribute("ordini", ordini);
            request.getRequestDispatcher("/WEB-INF/jsp/ordini.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Errore durante il recupero degli ordini: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errore", "Si è verificato un errore durante il recupero degli ordini.");
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
