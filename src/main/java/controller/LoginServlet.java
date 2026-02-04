package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Carrello;
import model.Utente;
import model.dao.CarrelloDAO;
import model.dao.UtenteDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String USER_ATTRIBUTE = "utente";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String contextPath = request.getContextPath();

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
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
            return;
        }

        try {
            Utente utente = UtenteDAO.login(email.trim(), password);
            if (utente != null) {
                HttpSession oldSession = request.getSession(false);
                Carrello carrelloTemporaneo = null;

                if (oldSession != null) {
                    carrelloTemporaneo = (Carrello) oldSession.getAttribute("carrello");
                    oldSession.invalidate();
                }

                HttpSession newSession = request.getSession(true);
                if (request.isSecure()) {
                    response.setHeader("Set-Cookie", "JSESSIONID=" + newSession.getId() + "; HttpOnly; Secure; SameSite=Strict");
                }
                newSession.setAttribute("utente", utente);
                newSession.setAttribute("idUtente", utente.getIdUtente());

                if (carrelloTemporaneo != null && !carrelloTemporaneo.getArticoli().isEmpty()) {
                    try {
                        CarrelloDAO carrelloDAO = new CarrelloDAO();
                        carrelloDAO.salvaCarrello(utente.getIdUtente(), carrelloTemporaneo);
                        newSession.setAttribute("carrello", carrelloTemporaneo);
                    } catch (SQLException e) {
                        System.err.println("Errore durante il salvataggio del carrello: " + e.getMessage());
                        newSession.setAttribute("carrello", carrelloTemporaneo);
                    }
                } else {
                    try {
                        CarrelloDAO carrelloDAO = new CarrelloDAO();
                        Carrello carrello = carrelloDAO.getCarrelloByUtente(utente.getIdUtente());
                        if (carrello != null) {
                            newSession.setAttribute("carrello", carrello);
                        } else {
                            newSession.setAttribute("carrello", new Carrello());
                        }
                    } catch (SQLException e) {
                        System.err.println("Errore durante il caricamento del carrello: " + e.getMessage());
                        newSession.setAttribute("carrello", new Carrello());
                    }
                }

                String redirectAfterLogin = (String) newSession.getAttribute("redirectAfterLogin");
                if (redirectAfterLogin != null && !redirectAfterLogin.isEmpty()) {
                    newSession.removeAttribute("redirectAfterLogin");
                    response.sendRedirect(redirectAfterLogin);
                } else {
                    String redirectPath = "admin".equals(utente.getRuolo())
                            ? contextPath + "/admin/dashboard"
                            : contextPath + "/home";
                    response.sendRedirect(redirectPath);
                }
            } else {
                handleLoginError(request, response, "Email o password non corretti", email);
            }
        } catch (Exception e) {
            e.printStackTrace();
            handleLoginError(request, response, "Si è verificato un errore durante il login. Riprova più tardi.", email);
        }
    }

    private void handleLoginError(HttpServletRequest request, HttpServletResponse response, String errorMessage, String email)
            throws ServletException, IOException {
        request.setAttribute("error", errorMessage);
        request.setAttribute("email", email != null ? email : "");
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }
}
