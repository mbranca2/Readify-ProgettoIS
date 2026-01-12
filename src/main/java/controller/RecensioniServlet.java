package controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Recensione;
import model.dao.RecensioneDAO;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/recensioni")
public class RecensioniServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("libroId");
        int libroId;

        try {
            libroId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        RecensioneDAO recensioneDAO = new RecensioneDAO();
        List<Recensione> recensioni = recensioneDAO.trovaRecensioniPerLibro(libroId);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Gson gson = new Gson();
        String json = gson.toJson(recensioni);

        PrintWriter out = resp.getWriter();
        out.print(json);
        out.flush();
    }
}
