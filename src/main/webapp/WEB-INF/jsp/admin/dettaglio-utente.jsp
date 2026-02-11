<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${utente.nome} ${utente.cognome} - Dettaglio Utente | Readify</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/user-detail.css">
    <meta name="description"
          content="Dettagli utente ${utente.nome} ${utente.cognome} - Pannello di amministrazione Readify">
</head>
<body>
<jsp:include page="../header.jsp"/>

<main class="user-detail-page">
    <div class="user-detail-container">
        <div class="user-card">
            <!-- Header -->
            <div class="user-header">
                <h1 class="user-title">${utente.nome} ${utente.cognome}</h1>
            </div>

            <!-- Informazioni di base -->
            <div class="user-section">
                <h2 class="section-title" data-icon="id-card">
                    Informazioni Personali
                </h2>
                <div class="info-grid">
                    <div class="info-row">
                        <span class="info-label">Nome</span>
                        <span class="info-value">${utente.nome}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Cognome</span>
                        <span class="info-value">${utente.cognome}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Email</span>
                        <span class="info-value">${utente.email}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Data di Registrazione</span>
                        <span class="info-value">
                        <fmt:formatDate value="${utente.dataRegistrazione}" pattern="dd/MM/yyyy HH:mm"/>
                    </span>
                    </div>
                </div>
            </div>

            <!-- Ruolo e Stato -->
            <div class="user-section">
                <h2 class="section-title" data-icon="user-shield">
                    Ruolo e Stato
                </h2>
                <div class="info-grid">
                    <div class="info-row">
                        <span class="info-label">Ruolo</span>
                        <span class="info-value">
                        <span class="status-badge status-active">
                            ${utente.ruolo}
                        </span>
                    </span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Stato Account</span>
                        <span class="info-value">
                        <span class="status-badge status-active">
                            Attivo
                        </span>
                    </span>
                    </div>
                </div>
            </div>

            <!-- Azioni -->
            <div class="action-buttons">
                <a href="${pageContext.request.contextPath}/admin/utenti/modifica?id=${utente.idUtente}"
                   class="btn btn-primary" id="editBtn"
                   aria-label="Modifica utente">
                    Modifica
                </a>
                <button onclick="confermaEliminazione(${utente.idUtente})"
                        class="btn btn-danger"
                        id="deleteBtn"
                        type="button"
                        aria-label="Elimina utente">
                    Elimina
                </button>
                <a href="${pageContext.request.contextPath}/admin/utenti"
                   class="btn btn-secondary"
                   aria-label="Torna alla lista utenti">
                    Torna alla lista
                </a>
            </div>
        </div>
    </div>
</main>

<jsp:include page="../footer.jsp"/>
<script>
    document.addEventListener('DOMContentLoaded', function () {
    });

    function confermaEliminazione(id) {
        const nomeUtente = '${utente.nome} ${utente.cognome}';
        const conferma = confirm(`Sei sicuro di voler eliminare l'utente "${nomeUtente}"?\n\nQuesta azione è irreversibile.`);

        if (conferma) {
            const deleteBtn = document.getElementById('deleteBtn');
            const originalText = deleteBtn.innerHTML;

            deleteBtn.disabled = true;
            deleteBtn.innerHTML = '<span class="loading" aria-hidden="true"></span> Eliminazione in corso...';

            const actionUrl = '${pageContext.request.contextPath}/admin/utenti/elimina';
            const body = new URLSearchParams();
            body.set('id', id);

            fetch(actionUrl, {
                method: 'POST',
                body: body.toString(),
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            })
                .then(async response => {
                    if (response.ok) {
                        window.location.href = '${pageContext.request.contextPath}/admin/utenti?success=Utente+eliminato+con+successo';
                        return;
                    }
                    const message = await response.text();
                    throw new Error(message || 'Errore durante l\'eliminazione');
                })
                .catch(error => {
                    console.error('Errore:', error);
                    deleteBtn.innerHTML = originalText;
                    deleteBtn.disabled = false;
                    alert(error.message || 'Si è verificato un errore durante l\'eliminazione. Riprova più tardi.');
                });
        }
    }
</script>
</body>
</html>


