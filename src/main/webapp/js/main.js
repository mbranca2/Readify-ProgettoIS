document.addEventListener('DOMContentLoaded', () => {
    initDropdowns();
    markActiveNavLink();
    initFormValidation();
    initDismissOnEscape();
});

function initDropdowns() {
    const toggles = document.querySelectorAll('.dropdown-toggle');

    toggles.forEach(toggle => {
        toggle.addEventListener('click', e => {
            e.preventDefault();

            const dropdown = toggle.closest('.dropdown');
            const menu = dropdown ? dropdown.querySelector('.dropdown-menu') : null;
            if (!menu) return;

            const isOpen = menu.classList.contains('show');
            closeAllDropdowns();
            if (!isOpen) menu.classList.add('show');
        });
    });

    document.addEventListener('click', e => {
        const insideDropdown = e.target && e.target.closest && e.target.closest('.dropdown');
        if (!insideDropdown) closeAllDropdowns();
    });
}

function closeAllDropdowns() {
    document.querySelectorAll('.dropdown-menu.show').forEach(menu => {
        menu.classList.remove('show');
    });
}

function initDismissOnEscape() {
    document.addEventListener('keydown', e => {
        if (e.key === 'Escape') {
            closeAllDropdowns();
            closeAllModals();
        }
    });
}

function markActiveNavLink() {
    const currentPath = normalizePath(window.location.pathname);

    document.querySelectorAll('.nav-link').forEach(link => {
        const href = link.getAttribute('href');
        if (!href) return;

        const linkPath = normalizePath(href);
        if (linkPath === currentPath) link.classList.add('active');
    });
}

function normalizePath(path) {
    try {
        const url = new URL(path, window.location.origin);
        let p = url.pathname || '/';
        if (p.length > 1 && p.endsWith('/')) p = p.slice(0, -1);
        return p;
    } catch {
        let p = path || '/';
        if (p.length > 1 && p.endsWith('/')) p = p.slice(0, -1);
        return p;
    }
}

function initFormValidation() {
    document.addEventListener('submit', e => {
        const form = e.target;
        if (!form || !form.matches || !form.matches('form[data-validate]')) return;

        if (!validateForm(form)) {
            e.preventDefault();
            showAlert('Per favore, compila tutti i campi obbligatori.', 'danger');
        }
    });
}

function validateForm(form) {
    let isValid = true;
    const inputs = form.querySelectorAll('input[required], select[required], textarea[required]');

    inputs.forEach(input => {
        if (!String(input.value || '').trim()) {
            isValid = false;
            input.classList.add('is-invalid');
        } else {
            input.classList.remove('is-invalid');
        }
    });

    return isValid;
}

function showAlert(message, type = 'info') {
    const container = document.querySelector('.alerts-container') || createAlertsContainer();

    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.textContent = message;

    container.appendChild(alert);

    requestAnimationFrame(() => {
        alert.classList.add('show');
    });

    setTimeout(() => {
        alert.classList.remove('show');
        setTimeout(() => alert.remove(), 200);
    }, 4500);
}

function createAlertsContainer() {
    const container = document.createElement('div');
    container.className = 'alerts-container';
    document.body.appendChild(container);
    return container;
}

function closeAllModals() {
    document.querySelectorAll('.modal').forEach(modal => {
        if (getComputedStyle(modal).display !== 'none') {
            modal.style.display = 'none';
        }
    });
    document.body.style.overflow = '';
}

function fetchData(url, options = {}) {
    return fetch(url, {
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        ...options
    }).then(response => {
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return response.json();
    });
}
