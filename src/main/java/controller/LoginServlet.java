package controller;

import jakarta.servlet.annotation.WebServlet;
import model.Utente;
import model.dao.UtenteDAO;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import model.Carrello;
import model.dao.CarrelloDAO;

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
                Carrello carrelloTemporaneo = null;

                if (oldSession != null) {
                    carrelloTemporaneo = (Carrello) oldSession.getAttribute("carrello");
                    oldSession.invalidate();
                }

                HttpSession newSession = request.getSession(true);

                // imposto il cookie di sessione con le opzioni di sicurezza
                if (request.isSecure()) {
                    response.setHeader("Set-Cookie", "JSESSIONID=" + newSession.getId() + "; HttpOnly; Secure; SameSite=Strict");
                }

                // memorizzo i dati dell'utente nella sessione
                newSession.setAttribute("utente", utente);
                newSession.setAttribute("idUtente", utente.getIdUtente());

                // se c'era un carrello temporaneo, lo salvo nel database
                if (carrelloTemporaneo != null && !carrelloTemporaneo.getArticoli().isEmpty()) {
                    try {
                        CarrelloDAO carrelloDAO = new CarrelloDAO();
                        carrelloDAO.salvaCarrello(utente.getIdUtente(), carrelloTemporaneo);
                        newSession.setAttribute("carrello", carrelloTemporaneo);
                    } catch (SQLException e) {
                        // Logga l'errore ma non interrompere il flusso di login
                        System.err.println("Errore durante il salvataggio del carrello: " + e.getMessage());
                        // Mantieni il carrello temporaneo in sessione per non perdere i dati
                        newSession.setAttribute("carrello", carrelloTemporaneo);
                    }
                } else {
                    //altrimenti carico il carrello esistente dell'utente
                    try {
                        CarrelloDAO carrelloDAO = new CarrelloDAO();
                        Carrello carrello = carrelloDAO.getCarrelloByUtente(utente.getIdUtente());
                        if (carrello != null) {
                            newSession.setAttribute("carrello", carrello);
                        } else {
                            //se non esiste un carrello,ne creo uno vuoto
                            newSession.setAttribute("carrello", new Carrello());
                        }
                    } catch (SQLException e) {
                        System.err.println("Errore durante il caricamento del carrello: " + e.getMessage());
                        //in caso di errore, creo un carrello vuoto
                        newSession.setAttribute("carrello", new Carrello());
                    }
                }

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
