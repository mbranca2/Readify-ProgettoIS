// Inizializzazione al caricamento del documento
document.addEventListener('DOMContentLoaded', function() {
    initTooltips();
    initModals();    
    initDropdowns();
    initResponsiveNav();
});

function initTooltips() {
    const tooltipElements = document.querySelectorAll('[data-toggle="tooltip"]');
    tooltipElements.forEach(el => {
        el.addEventListener('mouseenter', showTooltip);
        el.addEventListener('mouseleave', hideTooltip);
    });
}

function showTooltip(e) {
    const tooltipText = this.getAttribute('title') || this.getAttribute('data-original-title');
    if (!tooltipText) return;

    this.setAttribute('data-original-title', tooltipText);
    this.removeAttribute('title');

    const tooltip = document.createElement('div');
    tooltip.className = 'custom-tooltip';
    tooltip.textContent = tooltipText;

    const rect = this.getBoundingClientRect();
    tooltip.style.position = 'absolute';
    tooltip.style.top = `${rect.top - 40}px`;
    tooltip.style.left = `${rect.left + (rect.width / 2) - (tooltip.offsetWidth / 2)}px`;
    
    document.body.appendChild(tooltip);
    this.tooltip = tooltip;
}

function hideTooltip() {
    if (this.tooltip) {
        document.body.removeChild(this.tooltip);
        this.tooltip = null;
        const originalTitle = this.getAttribute('data-original-title');
        if (originalTitle) {
            this.setAttribute('title', originalTitle);
        }
    }
}

function initModals() {
    document.querySelectorAll('[data-toggle="modal"]').forEach(trigger => {
        trigger.addEventListener('click', function(e) {
            e.preventDefault();
            const modalId = this.getAttribute('data-target');
            const modal = document.querySelector(modalId);
            if (modal) {
                modal.style.display = 'block';
                document.body.style.overflow = 'hidden';
            }
        });
    });

    document.querySelectorAll('.modal-close, .modal .btn-close').forEach(btn => {
        btn.addEventListener('click', function() {
            const modal = this.closest('.modal');
            if (modal) {
                modal.style.display = 'none';
                document.body.style.overflow = '';
            }
        });
    });

    window.addEventListener('click', function(e) {
        if (e.target.classList.contains('modal')) {
            e.target.style.display = 'none';
            document.body.style.overflow = '';
        }
    });
}

function initDropdowns() {
    document.querySelectorAll('.dropdown-toggle').forEach(toggle => {
        toggle.addEventListener('click', function(e) {
            e.preventDefault();
            const dropdown = this.nextElementSibling;
            if (dropdown && dropdown.classList.contains('dropdown-menu')) {
                dropdown.classList.toggle('show');
            }
        });
    });

    document.addEventListener('click', function(e) {
        if (!e.target.matches('.dropdown-toggle')) {
            document.querySelectorAll('.dropdown-menu').forEach(menu => {
                menu.classList.remove('show');
            });
        }
    });
}

function initResponsiveNav() {
    const navbarToggler = document.querySelector('.navbar-toggler');
    if (navbarToggler) {
        const navbarCollapse = document.querySelector(navbarToggler.getAttribute('data-target'));
        
        navbarToggler.addEventListener('click', function() {
            navbarCollapse.classList.toggle('show');
            this.classList.toggle('active');
        });

        document.querySelectorAll('.nav-link').forEach(link => {
            link.addEventListener('click', () => {
                navbarCollapse.classList.remove('show');
                navbarToggler.classList.remove('active');
            });
        });
    }
}

function showAlert(message, type = 'info') {
    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.textContent = message;
    
    const container = document.querySelector('.alerts-container') || createAlertsContainer();
    container.appendChild(alert);

    setTimeout(() => {
        alert.style.opacity = '0';
        setTimeout(() => alert.remove(), 300);
    }, 5000);
}

function createAlertsContainer() {
    const container = document.createElement('div');
    container.className = 'alerts-container';
    document.body.appendChild(container);
    return container;
}

function fetchData(url, options = {}) {
    return fetch(url, {
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        ...options
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    });
}

document.addEventListener('DOMContentLoaded', function() {
    const currentPath = window.location.pathname;
    document.querySelectorAll('.nav-link').forEach(link => {
        if (link.getAttribute('href') === currentPath) {
            link.classList.add('active');
        }
    });
});

function validateForm(form) {
    let isValid = true;
    const inputs = form.querySelectorAll('input[required], select[required], textarea[required]');
    
    inputs.forEach(input => {
        if (!input.value.trim()) {
            isValid = false;
            input.classList.add('is-invalid');
        } else {
            input.classList.remove('is-invalid');
        }
    });
    
    return isValid;
}

document.addEventListener('submit', function(e) {
    if (e.target.matches('form[data-validate]')) {
        if (!validateForm(e.target)) {
            e.preventDefault();
            showAlert('Per favore, compila tutti i campi obbligatori.', 'danger');
        }
    }
});

const style = document.createElement('style');
style.textContent = `
    .custom-tooltip {
        position: fixed;
        background: #333;
        color: #fff;
        padding: 5px 10px;
        border-radius: 4px;
        font-size: 14px;
        z-index: 9999;
        pointer-events: none;
        transform: translateX(-50%);
    }
    
    .modal {
        display: none;
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0,0,0,0.5);
        z-index: 1000;
        overflow-y: auto;
    }
    
    .modal-content {
        background: #fff;
        margin: 5% auto;
        padding: 20px;
        max-width: 600px;
        border-radius: 8px;
        position: relative;
    }
    
    .btn-close {
        position: absolute;
        top: 10px;
        right: 10px;
        background: none;
        border: none;
        font-size: 1.5rem;
        cursor: pointer;
    }
    
    .dropdown-menu {
        display: none;
        position: absolute;
        background: #fff;
        border: 1px solid #ddd;
        border-radius: 4px;
        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        z-index: 1000;
    }
    
    .dropdown-menu.show {
        display: block;
    }
    
    .alerts-container {
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 1100;
        max-width: 300px;
    }
    
    .alert {
        margin-bottom: 10px;
        padding: 10px 15px;
        border-radius: 4px;
        animation: slideIn 0.3s ease-out;
        transition: opacity 0.3s ease;
    }
    
    @keyframes slideIn {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
`;

document.head.appendChild(style);
