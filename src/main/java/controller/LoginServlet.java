package controller;

import jakarta.servlet.annotation.WebServlet;
import model.Utente;
import model.dao.UtenteDAO;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String USER_ATTRIBUTE = "utente";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String contextPath = request.getContextPath();

// Verifico che i dati inseriti siano corretti
        Map<String, String> errori = new HashMap<>();

        if (email == null || email.trim().isEmpty()) {
            errori.put("email", "L'email è obbligatoria");
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errori.put("email", "Formato email non valido");
        }

        if (password == null || password.isEmpty()) {
            errori.put("password", "La password è obbligatoria");
        } else if (password.length() < 8) {
            errori.put("password", "La password deve essere di almeno 8 caratteri");
        }

        if (!errori.isEmpty()) {
            request.setAttribute("errori", errori);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            return;
        }

        try {
            // provo ad loggare l'utente con le credenziali fornite
            Utente utente = UtenteDAO.login(email.trim(), password);

            if (utente != null) {
                // successo
                HttpSession oldSession = request.getSession(false);
                if (oldSession != null) {
                    oldSession.invalidate();
                }

                HttpSession newSession = request.getSession(true);
                // imposto il cookie di sessione con le opzioni di sicurezza
                if (request.isSecure()) {
                    response.setHeader("Set-Cookie", "JSESSIONID=" + newSession.getId() + "; HttpOnly; Secure; SameSite=Strict");
                }

                // memorizzo i dati dell'utente nella sessione
                newSession.setAttribute("utente", utente);

                String redirectAfterLogin = (String) newSession.getAttribute("redirectAfterLogin");

                if (redirectAfterLogin != null && !redirectAfterLogin.isEmpty()) {
                    //evito loop
                    newSession.removeAttribute("redirectAfterLogin");
                    response.sendRedirect(redirectAfterLogin);
                } else {
                    // scelgo pagina in base al ruolo
                    String redirectPath = "admin".equals(utente.getRuolo())
                            ? contextPath + "/jsp/admin/dashboard.jsp"
                            : contextPath + "/";

                    response.sendRedirect(redirectPath);
                }
            } else {
                // Se le credenziali non sono corrette, mostro un messaggio di errore
                handleLoginError(request, response, "Email o password non corretti", email);
            }
        } catch (Exception e) {
            e.printStackTrace();
            handleLoginError(request, response, "Si è verificato un errore durante il login. Riprova più tardi.", email);
        }
    }
    
    private void handleLoginError(HttpServletRequest request, HttpServletResponse response, 
                                String errorMessage, String email) 
            throws ServletException, IOException {
        request.setAttribute("error", errorMessage);
        request.setAttribute("email", email != null ? email : "");
        request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
    }
}
