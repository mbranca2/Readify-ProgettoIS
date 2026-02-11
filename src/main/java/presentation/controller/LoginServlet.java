package presentation.controller;

import business.model.Carrello;
import business.model.Utente;
import business.service.ServiceFactory;
import business.service.account.AccountService;
import business.service.account.AccountServiceException;
import business.service.cart.CartFacade;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String USER_ATTRIBUTE = "utente";
    private AccountService accountService;
    private CartFacade cartFacade;

    @Override
    public void init() throws ServletException {
        this.accountService = ServiceFactory.accountService();
        this.cartFacade = ServiceFactory.cartFacade();
    }

    void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    void setCartFacade(CartFacade cartFacade) {
        this.cartFacade = cartFacade;
    }

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
            Utente utente;
            try {
                utente = accountService.login(email.trim(), password);
            } catch (AccountServiceException ex) {
                handleLoginError(request, response, ex.getMessage(), email);
                return;
            }
            if (utente == null) {
                handleLoginError(request, response, "Email o password non corretti.", email);
                return;
            }

            HttpSession oldSession = request.getSession(false);
            Carrello carrelloTemporaneo = null;

            if (oldSession != null) {
                carrelloTemporaneo = (Carrello) oldSession.getAttribute("carrello");
                oldSession.invalidate();
            }

            HttpSession newSession = request.getSession(true);
            if (request.isSecure()) {
                response.setHeader("Set-Cookie",
                        "JSESSIONID=" + newSession.getId() + "; HttpOnly; Secure; SameSite=Strict");
            }
            newSession.setAttribute("utente", utente);
            newSession.setAttribute("idUtente", utente.getIdUtente());

            Carrello carrello = cartFacade.syncAfterLogin(utente.getIdUtente(), carrelloTemporaneo);
            newSession.setAttribute("carrello", carrello);

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

        } catch (Exception e) {
            e.printStackTrace();
            handleLoginError(request, response,
                    "Si è verificato un errore durante il login. Riprova più tardi.", email);
        }
    }

    private void handleLoginError(HttpServletRequest request, HttpServletResponse response, String errorMessage, String email)
            throws ServletException, IOException {
        request.setAttribute("error", errorMessage);
        request.setAttribute("email", email != null ? email : "");
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }
}
