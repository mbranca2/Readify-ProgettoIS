package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Categoria;
import model.Libro;
import model.dao.CategoriaDAO;
import model.dao.LibroDAO;

import java.io.IOException;
import java.util.List;

@WebServlet("/libri")
public class ListaLibriServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("ListaLibriServlet: Inizio elaborazione richiesta");
        LibroDAO libroDAO = new LibroDAO();
        CategoriaDAO categoriaDAO = new CategoriaDAO();
        
        // Carico tutti i libri e categore
        List<Libro> listaLibri = libroDAO.trovaTutti();
        List<Categoria> categorie = categoriaDAO.trovaTutteCategorie();
        
        if (listaLibri == null) {
            System.out.println("ListaLibriServlet: La lista dei libri è null");
        } else {
            System.out.println("ListaLibriServlet: Trovati " + listaLibri.size() + " libri");
            for (Libro libro : listaLibri) {
                System.out.println(" - " + libro.getTitolo() + " (ID: " + libro.getIdLibro() + ")");
            }
        }
        
        // Aggiungo le categorie alla richiesta
        req.setAttribute("categorie", categorie);
        req.setAttribute("libri", listaLibri);
        req.getRequestDispatcher("/jsp/catalogo.jsp").forward(req, resp);
        System.out.println("ListaLibriServlet: Reindirizzamento a /jsp/catalogo.jsp");
    }
}
