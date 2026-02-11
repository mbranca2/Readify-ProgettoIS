<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="it" class="user-edit-page">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Modifica i dati dell'utente ${utente.nome} ${utente.cognome} su Readify">
    <title>Modifica Utente - Readify</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/user-edit.css">
</head>
<body>
<div class="user-edit-container">
    <div class="page-header">
        <h2>Modifica Utente</h2>
        <p>Aggiorna i dati dell'utente</p>
    </div>

    <c:if test="${not empty errore}">
        <div class="alert alert-error">
                ${errore}
        </div>
    </c:if>
    <form method="post" action="${pageContext.request.contextPath}/admin/utenti" class="user-edit-form">
        <input type="hidden" name="azione" value="modifica">
        <input type="hidden" name="idUtente" value="${utente.idUtente}">

        <div class="form-group">
            <label for="nome">Nome:</label>
            <input type="text" id="nome" name="nome" value="${utente.nome}" required>
        </div>

        <div class="form-group">
            <label for="cognome">Cognome:</label>
            <input type="text" id="cognome" name="cognome" value="${utente.cognome}" required>
        </div>

        <div class="form-group">
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" value="${utente.email}" required>
        </div>

        <div class="form-group">
            <label for="password">Nuova Password: (lasciare vuoto per non modificare)</label>
            <input type="password" id="password" name="password">
        </div>

        <div class="form-group">
            <label for="ruolo">Ruolo:</label>
            <select id="ruolo" name="ruolo" required>
                <option value="CLIENTE" ${utente.ruolo == 'registrato' ? 'selected' : ''}>Cliente</option>
                <option value="AMMINISTRATORE" ${utente.ruolo == 'admin' ? 'selected' : ''}>Amministratore</option>
            </select>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">
                Salva Modifiche
            </button>
            <a href="${pageContext.request.contextPath}/admin/utenti" class="btn btn-outline">Annulla</a>
        </div>
    </form>
</div>
<script>
    function validateForm() {
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        if (password && password.length < 8) {
            alert('La password deve essere di almeno 8 caratteri');
            return false;
        }

        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailPattern.test(email)) {
            alert('Inserisci un indirizzo email valido');
            return false;
        }

        return true;
    }
</script>
</body>
</html>