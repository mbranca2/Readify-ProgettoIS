<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Modifica Libro - Readify</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/book-edit.css">
</head>
<body>
<div class="book-edit-container">
    <div class="page-header">
        <h1>Modifica Libro</h1>
    </div>

    <c:if test="${not empty errore}">
        <div class="alert alert-error">${errore}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/admin/libri/modifica" method="post" class="book-edit-form">
        <input type="hidden" name="idLibro" value="${libro.idLibro}">
        <input type="hidden" name="copertina" value="${libro.copertina}">

        <div class="form-group">
            <label for="titolo">Titolo *</label>
            <input type="text" id="titolo" name="titolo" class="form-control" value="${libro.titolo}" required>
        </div>

        <div class="form-group">
            <label for="autore">Autore *</label>
            <input type="text" id="autore" name="autore" class="form-control" value="${libro.autore}" required>
        </div>

        <div class="form-group">
            <label for="prezzo">Prezzo (€) *</label>
            <input type="number" id="prezzo" name="prezzo" class="form-control" step="0.01" min="0"
                   value="${libro.prezzo}" required>
        </div>

        <div class="form-group">
            <label for="quantita">Quantità *</label>
            <input type="number" id="quantita" name="quantita" class="form-control" value="${libro.disponibilita}"
                   min="0" required>
        </div>

        <div class="form-group">
            <label for="categoria">Categoria *</label>
            <select id="categoria" name="categoria" class="form-control" required>
                <option value="">Seleziona una categoria</option>
                <c:forEach items="${categorie}" var="cat">
                    <option value="${cat.idCategoria}" ${libro.categorie.contains(cat.idCategoria) ? 'selected' : ''}>
                            ${cat.nomeCategoria}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label for="descrizione">Descrizione</label>
            <textarea id="descrizione" name="descrizione" rows="4" class="form-control">${libro.descrizione}</textarea>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">Salva Modifiche</button>
            <a href="${pageContext.request.contextPath}/admin/libri" class="btn btn-outline">Annulla</a>
        </div>
    </form>
</div>
<script>
    document.querySelector('.book-edit-form').addEventListener('submit', function (e) {
        const prezzo = document.getElementById('prezzo');
        const quantita = document.getElementById('quantita');

        if (parseFloat(prezzo.value) < 0) {
            e.preventDefault();
            alert('Il prezzo non può essere negativo');
            prezzo.focus();
            return false;
        }

        if (parseInt(quantita.value) < 0) {
            e.preventDefault();
            alert('La quantità non può essere negativa');
            quantita.focus();
            return false;
        }

        return true;
    });
</script>
</body>
</html>