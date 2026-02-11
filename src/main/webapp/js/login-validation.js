function validateLoginForm() {
    resetErrorMessages();

    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    let isValid = true;

    // Validazione email
    if (email === '') {
        showError('email', 'L\'email è obbligatoria');
        isValid = false;
    } else if (!isValidEmail(email)) {
        showError('email', 'Formato email non valido');
        isValid = false;
    }

    // Validazione password
    if (password === '') {
        showError('password', 'La password è obbligatoria');
        isValid = false;
    } else if (password.length < 8) {
        showError('password', 'La password deve essere di almeno 8 caratteri');
        isValid = false;
    }

    return isValid;
}

function isValidEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function showError(fieldId, message) {
    const field = document.getElementById(fieldId);
    const errorDiv = document.createElement('div');
    errorDiv.className = 'invalid-feedback';
    errorDiv.textContent = message;

    field.classList.add('is-invalid');
    field.parentNode.appendChild(errorDiv);
}

function resetErrorMessages() {
    document.querySelectorAll('.invalid-feedback').forEach(el => el.remove());
    document.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
}