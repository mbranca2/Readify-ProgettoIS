package presentation.controller;

import business.model.Recensione;
import business.service.ServiceFactory;
import business.service.review.ReviewService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/recensioni")
public class RecensioniServlet extends HttpServlet {

    private ReviewService reviewService;

    @Override
    public void init() throws ServletException {
        this.reviewService = ServiceFactory.reviewService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = req.getParameter("libroId");

        int libroId;
        try {
            libroId = Integer.parseInt(idStr);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        List<Recensione> recensioni = reviewService.listByBook(libroId);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String json = new Gson().toJson(recensioni);
        try (PrintWriter out = resp.getWriter()) {
            out.print(json);
            out.flush();
        }
    }
}
