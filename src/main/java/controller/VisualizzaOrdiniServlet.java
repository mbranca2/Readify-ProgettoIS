package controller;

import model.Ordine;
import model.Utente;
import model.dao.OrdineDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/profilo/ordini")
public class VisualizzaOrdiniServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final OrdineDAO ordineDAO = new OrdineDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Verifico che l'utente è loggato
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utente");
        
        if (utente == null) {
            // se non è loggato, reindirizzo a login
            response.sendRedirect(request.getContextPath() + "/login?redirect=/profilo/ordini");
            return;
        }
        
        try {
            System.out.println("ID utente: " + utente.getIdUtente());
            // Recupero gli ordini dell'utente
            List<Ordine> ordini = ordineDAO.trovaPerIdUtente(utente.getIdUtente());
            System.out.println("Numero ordini trovati: " + (ordini != null ? ordini.size() : "null"));

            request.setAttribute("ordini", ordini);

            request.getRequestDispatcher("/jsp/ordini.jsp").forward(request, response);
            
        } catch (Exception e) {
            // Log dell'errore
            System.err.println("Errore durante il recupero degli ordini: " + e.getMessage());
            e.printStackTrace();
            
            // Imposto messaggio di errorre
            request.setAttribute("errore", "Si è verificato un errore durante il recupero degli ordini.");
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
