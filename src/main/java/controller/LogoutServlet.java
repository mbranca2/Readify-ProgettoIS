package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Invalido la sessione esistente
        HttpSession session = request.getSession(false);
        if (session != null) {
            // Rimuovo solo l'attributo utente invece di invalidare l'intera sessione
            session.removeAttribute("utente");
        }
        
        // Reindirizzo alla home page con un parametro di successo
        String contextPath = request.getContextPath();
        response.sendRedirect(contextPath + "/?logout=success");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
