package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Utente;
import model.dao.UtenteDAO;
import utils.HashUtil;
import utils.ValidatoreForm;

import java.io.IOException;
import java.util.Map;

@WebServlet("/registrazione")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = ValidatoreForm.pulisciInput(request.getParameter("email"));
        String password = request.getParameter("password");
        String confermaPassword = request.getParameter("confermaPassword");
        String nome = ValidatoreForm.pulisciInput(request.getParameter("nome"));
        String cognome = ValidatoreForm.pulisciInput(request.getParameter("cognome"));
        String telefono = ValidatoreForm.pulisciInput(request.getParameter("telefono"));
        boolean privacyAccettata = request.getParameter("privacy") != null;

        Map<String, String> errori = ValidatoreForm.validaRegistrazione(
                nome, cognome, email, password, confermaPassword, telefono, privacyAccettata);

        if (!errori.isEmpty()) {
            request.setAttribute("nome", nome);
            request.setAttribute("cognome", cognome);
            request.setAttribute("email", email);
            request.setAttribute("telefono", telefono);
            request.setAttribute("errori", errori);
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            return;
        }

        try {
            String ruolo = "registrato";
            String passwordCifrata = HashUtil.sha1(password);

            Utente nuovoUtente = new Utente();
            nuovoUtente.setEmail(email);
            nuovoUtente.setPasswordCifrata(passwordCifrata);
            nuovoUtente.setNome(nome);
            nuovoUtente.setCognome(cognome);
            nuovoUtente.setTelefono(telefono);
            nuovoUtente.setRuolo(ruolo);

            UtenteDAO utenteDAO = new UtenteDAO();
            boolean successo = utenteDAO.inserisciUtente(nuovoUtente);

            if (successo) {
                response.sendRedirect(request.getContextPath() + "/jsp/login.jsp?registrazione=successo");
            } else {
                request.setAttribute("erroreRegistrazione", "Registrazione fallita. L'email potrebbe essere già registrata.");
                request.setAttribute("nome", nome);
                request.setAttribute("cognome", cognome);
                request.setAttribute("email", email);
                request.setAttribute("telefono", telefono);
                request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("erroreRegistrazione", "Si è verificato un errore durante la registrazione. Riprova più tardi.");
            request.setAttribute("nome", nome);
            request.setAttribute("cognome", cognome);
            request.setAttribute("email", email);
            request.setAttribute("telefono", telefono);
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
        }
    }
}
