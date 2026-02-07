function validateRegistrationForm() {
    // predno i valori del form
    const form = document.getElementById('registrationForm');
    const nome = document.getElementById('nome').value.trim();
    const cognome = document.getElementById('cognome').value.trim();
    const email = document.getElementById('email').value.trim();
    const telefono = document.getElementById('telefono')?.value.trim() || '';
    const password = document.getElementById('password').value;
    const confermaPassword = document.getElementById('confermaPassword').value;

    resetErrorMessages();
    
    let isValid = true;
    
    // Validazione nome
    if (nome === '') {
        showError('nome', 'Il nome è obbligatorio');
        isValid = false;
    } else if (nome.length < 2) {
        showError('nome', 'Il nome deve contenere almeno 2 caratteri');
        isValid = false;
    }
    
    // Validazione cognome
    if (cognome === '') {
        showError('cognome', 'Il cognome è obbligatorio');
        isValid = false;
    } else if (cognome.length < 2) {
        showError('cognome', 'Il cognome deve contenere almeno 2 caratteri');
        isValid = false;
    }
    
    // Validazione email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (email === '') {
        showError('email', 'L\'email è obbligatoria');
        isValid = false;
    } else if (!emailRegex.test(email)) {
        showError('email', 'Inserisci un indirizzo email valido');
        isValid = false;
    }
    
    // Validazione password
    if (password === '') {
        showError('password', 'La password è obbligatoria');
        isValid = false;
    } else if (password.length < 8) {
        showError('password', 'La password deve contenere almeno 8 caratteri');
        isValid = false;
    } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(password)) {
        showError('password', 'La password deve contenere almeno una lettera maiuscola, una minuscola e un numero');
        isValid = false;
    }
    
    // Validazione conferma password
    if (confermaPassword === '') {
        showError('confermaPassword', 'Conferma la password');
        isValid = false;
    } else if (password !== confermaPassword) {
        showError('confermaPassword', 'Le password non coincidono');
        isValid = false;
    }
    
    // Validazione telefono (opzionale)
    if (telefono && !/^[0-9\-\+\(\)\s]{8,15}$/.test(telefono)) {
        showError('telefono', 'Inserisci un numero di telefono valido');
        isValid = false;
    }
    
    const submitButton = form.querySelector('button[type="submit"]');
    if (isValid && submitButton) {
        submitButton.disabled = true;
        submitButton.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Registrazione in corso...';
    }
    
    return isValid;
}

function showError(fieldId, message) {
    let errorDiv;
    const field = document.getElementById(fieldId);
    const formGroup = field.closest('.form-group') || field.parentNode;

    let existingError = formGroup.querySelector('.invalid-feedback');
    if (!existingError) {
        errorDiv = document.createElement('div');
        errorDiv.className = 'invalid-feedback';
        errorDiv.dataset.generated = 'true';
        formGroup.appendChild(errorDiv);
    } else {
        errorDiv = existingError;
    }
    
    errorDiv.textContent = message;
    field.classList.add('is-invalid');
    
    // Aggiungo un listener per rimuovere l'errore quando l'utente inizia a scrivere
    const clearError = () => {
        field.classList.remove('is-invalid');
        if (errorDiv && errorDiv.dataset.generated === 'true') {
            errorDiv.remove();
        }
        field.removeEventListener('input', clearError);
        field.removeEventListener('change', clearError);
    };
    
    field.addEventListener('input', clearError);
    field.addEventListener('change', clearError);

    if (isValid) {
        field.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
}

function resetErrorMessages() {
    document.querySelectorAll('.invalid-feedback[data-generated="true"]').forEach(el => el.remove());
    document.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
}

// Aggiungi la validazione al form di registrazione quando il DOM è caricato
document.addEventListener('DOMContentLoaded', function() {
    const registrationForm = document.getElementById('registrationForm');
    if (registrationForm) {
        registrationForm.addEventListener('submit', function(event) {
            if (!validateRegistrationForm()) {
                event.preventDefault();
            }
        });

        registrationForm.addEventListener('focusin', (event) => {
            const target = event.target;
            if (!(target instanceof HTMLElement) || !target.matches('input, textarea, select')) {
                return;
            }
            const formGroup = target.closest('.form-group');
            if (!formGroup) {
                return;
            }
            const hint = formGroup.querySelector('.format-hint');
            if (hint) {
                hint.classList.remove('is-visible');
            }
        });

        registrationForm.addEventListener('focusout', (event) => {
            const target = event.target;
            if (!(target instanceof HTMLElement) || !target.matches('input, textarea, select')) {
                return;
            }
            const formGroup = target.closest('.form-group');
            if (!formGroup) {
                return;
            }
            const hint = formGroup.querySelector('.format-hint');
            if (hint && target.value.trim() !== '') {
                hint.classList.add('is-visible');
            }
        });

        registrationForm.addEventListener('input', (event) => {
            const target = event.target;
            if (!(target instanceof HTMLElement) || !target.matches('input, textarea, select')) {
                return;
            }
            const formGroup = target.closest('.form-group');
            if (!formGroup) {
                return;
            }
            const hint = formGroup.querySelector('.format-hint');
            if (hint && target.value.trim() === '') {
                hint.classList.remove('is-visible');
            }
        });
    }
});

function validatePasswordChange() {
    resetErrorMessages();

    let isValid = true;
    const vecchiaPassword = document.getElementById('vecchiaPassword').value;
    const nuovaPassword = document.getElementById('nuovaPassword').value;
    const confermaPassword = document.getElementById('confermaPassword').value;

    // Validazione password attuale
    if (!vecchiaPassword) {
        showError('vecchiaPassword', 'La password attuale è obbligatoria');
        isValid = false;
    }

    // Validazione nuova password
    if (!nuovaPassword) {
        showError('nuovaPassword', 'La nuova password è obbligatoria');
        isValid = false;
    } else if (nuovaPassword.length < 8) {
        showError('nuovaPassword', 'La password deve contenere almeno 8 caratteri');
        isValid = false;
    } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(nuovaPassword)) {
        showError('nuovaPassword', 'La password deve contenere almeno una lettera maiuscola, una minuscola e un numero');
        isValid = false;
    }

    // Validazione conferma password
    if (!confermaPassword) {
        showError('confermaPassword', 'La conferma password è obbligatoria');
        isValid = false;
    } else if (nuovaPassword !== confermaPassword) {
        showError('confermaPassword', 'Le password non coincidono');
        isValid = false;
    }

    return isValid;
}
