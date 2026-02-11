package presentation.controller.admin;

import business.service.ServiceFactory;
import business.service.catalog.AdminCatalogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/admin/libri/elimina")
public class EliminaLibroServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private AdminCatalogService adminCatalogService;

    @Override
    public void init() throws ServletException {
        this.adminCatalogService = ServiceFactory.adminCatalogService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID libro mancante");
            return;
        }

        try {
            int idLibro = Integer.parseInt(idParam);
            boolean eliminato = adminCatalogService.removeBook(idLibro);

            if (eliminato) {
                response.sendRedirect(request.getContextPath() + "/admin/libri?success=Libro eliminato con successo");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/libri?errore=Impossibile eliminare il libro");
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID libro non valido");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/libri?errore=Errore durante l'eliminazione del libro");
        }
    }
}
