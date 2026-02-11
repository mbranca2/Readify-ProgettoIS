package presentation.controller;

import business.service.ServiceFactory;
import business.service.account.AccountService;
import business.service.account.AccountServiceException;
import business.service.account.RegistrationData;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import presentation.util.ValidatoreForm;

import java.io.IOException;
import java.util.Map;

@WebServlet("/registrazione")
public class RegistrazioneServlet extends HttpServlet {
    private AccountService accountService;

    @Override
    public void init() throws ServletException {
        this.accountService = ServiceFactory.accountService();
    }

    void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = ValidatoreForm.pulisciInput(request.getParameter("email"));
        String password = request.getParameter("password");
        String confermaPassword = request.getParameter("confermaPassword");
        String nome = ValidatoreForm.pulisciInput(request.getParameter("nome"));
        String cognome = ValidatoreForm.pulisciInput(request.getParameter("cognome"));
        String telefono = ValidatoreForm.pulisciInput(request.getParameter("telefono"));
        String via = ValidatoreForm.pulisciInput(request.getParameter("via"));
        String citta = ValidatoreForm.pulisciInput(request.getParameter("citta"));
        String cap = ValidatoreForm.pulisciInput(request.getParameter("cap"));
        String provincia = ValidatoreForm.pulisciInput(request.getParameter("provincia"));
        String paese = ValidatoreForm.pulisciInput(request.getParameter("paese"));
        Map<String, String> errori = ValidatoreForm.validaRegistrazione(
                nome, cognome, email, password, confermaPassword, telefono, via, citta, cap, provincia, paese);

        if (!errori.isEmpty()) {
            request.setAttribute("nome", nome);
            request.setAttribute("cognome", cognome);
            request.setAttribute("email", email);
            request.setAttribute("telefono", telefono);
            request.setAttribute("errori", errori);
            request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
            return;
        }

        try {
            RegistrationData data = new RegistrationData(
                    email,
                    password,
                    nome,
                    cognome,
                    telefono,
                    via,
                    citta,
                    cap,
                    provincia,
                    paese
            );

            accountService.register(data);

            response.sendRedirect(request.getContextPath() + "/login?registrazione=successo");

        } catch (AccountServiceException e) {
            request.setAttribute("erroreRegistrazione", e.getMessage());
            request.setAttribute("nome", nome);
            request.setAttribute("cognome", cognome);
            request.setAttribute("email", email);
            request.setAttribute("telefono", telefono);
            request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("erroreRegistrazione", "Si è verificato un errore durante la registrazione. Riprova più tardi.");
            request.setAttribute("nome", nome);
            request.setAttribute("cognome", cognome);
            request.setAttribute("email", email);
            request.setAttribute("telefono", telefono);
            request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
        }
    }
}
