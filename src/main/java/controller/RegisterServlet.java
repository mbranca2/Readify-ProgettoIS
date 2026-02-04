package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Indirizzo;
import model.Utente;
import model.dao.IndirizzoDAO;
import model.dao.UtenteDAO;
import utils.HashUtil;
import utils.ValidatoreForm;

import java.io.IOException;
import java.util.Map;

@WebServlet("/registrazione")
public class RegisterServlet extends HttpServlet {
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
        boolean privacyAccettata = request.getParameter("privacy") != null;

        Map<String, String> errori = ValidatoreForm.validaRegistrazione(
                nome, cognome, email, password, confermaPassword, telefono, privacyAccettata, via, citta, cap, provincia, paese);
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
                Indirizzo indirizzo = new Indirizzo();
                indirizzo.setIdUtente(nuovoUtente.getIdUtente());
                indirizzo.setVia(via);
                indirizzo.setCitta(citta);
                indirizzo.setCap(cap);
                indirizzo.setProvincia(provincia.toUpperCase());
                indirizzo.setPaese(paese);
                IndirizzoDAO indirizzoDAO = new IndirizzoDAO();
                boolean indirizzoInserito = indirizzoDAO.inserisciIndirizzo(indirizzo);
                if (!indirizzoInserito) {
                    utenteDAO.eliminaUtente(nuovoUtente.getIdUtente());
                    throw new ServletException("Errore durante la creazione dell'indirizzo");
                }
                response.sendRedirect(request.getContextPath() + "/login?registrazione=successo");
            } else {
                request.setAttribute("erroreRegistrazione", "Registrazione fallita. L'email potrebbe essere già registrata.");
                request.setAttribute("nome", nome);
                request.setAttribute("cognome", cognome);
                request.setAttribute("email", email);
                request.setAttribute("telefono", telefono);
                request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
            }
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
