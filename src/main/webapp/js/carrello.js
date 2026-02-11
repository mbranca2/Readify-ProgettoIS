document.addEventListener('DOMContentLoaded', () => {
    initCartHandlers();
});

function initCartHandlers() {
    document.querySelectorAll('.quantity-btn').forEach(btn => {
        btn.addEventListener('click', onQuantityButtonClick);
    });

    document.querySelectorAll('.quantity-input').forEach(input => {
        input.addEventListener('change', onQuantityInputChange);
        input.addEventListener('blur', onQuantityInputChange);
        input.addEventListener('keydown', e => {
            if (e.key === 'Enter') {
                e.preventDefault();
                input.blur();
            }
        });
    });

    document.querySelectorAll('.remove-item, .remove-btn').forEach(btn => {
        btn.addEventListener('click', onRemoveClick);
    });
}

async function onQuantityButtonClick(e) {
    e.preventDefault();

    const btn = e.currentTarget;
    const productId = btn.dataset.productId;
    const input = document.querySelector(`.quantity-input[data-product-id="${productId}"]`);
    if (!input) return;

    const max = parseInt(input.getAttribute('max') || '999999', 10);
    const min = parseInt(input.getAttribute('min') || '1', 10);
    const current = parseInt(input.value || '1', 10);

    let next = current;
    if (btn.classList.contains('plus')) next = current + 1;
    if (btn.classList.contains('minus')) next = current - 1;

    if (Number.isNaN(next)) next = min;
    next = clamp(next, min, max);

    if (next === current) return;

    await updateQuantity(productId, next, {previous: current, input});
}

async function onQuantityInputChange(e) {
    const input = e.currentTarget;
    const productId = input.dataset.productId;

    const max = parseInt(input.getAttribute('max') || '999999', 10);
    const min = parseInt(input.getAttribute('min') || '1', 10);

    const prev = parseInt(input.value || String(min), 10);

    let next = parseInt(input.value || String(min), 10);
    if (Number.isNaN(next)) next = min;
    next = clamp(next, min, max);

    input.value = next;

    await updateQuantity(productId, next, {previous: prev, input});
}

async function onRemoveClick(e) {
    e.preventDefault();

    const btn = e.currentTarget;
    const productId = btn.dataset.productId;
    if (!productId) return;

    const conferma = window.confirm('Sei sicuro di voler rimuovere questo prodotto dal carrello?');
    if (!conferma) return;

    setRowDisabled(productId, true);

    try {
        const data = await postCartAction('rimuovi', productId);

        if (!data.success) {
            toast(data.message || 'Errore durante la rimozione del prodotto', 'danger');
            setRowDisabled(productId, false);
            return;
        }

        const row = document.querySelector(`.cart-item[data-product-id="${productId}"]`);
        if (row) {
            row.style.opacity = '0';
            row.style.transition = 'opacity 0.2s ease';
            setTimeout(() => row.remove(), 220);
        }

        applyCartUpdate(data);
        toast('Prodotto rimosso dal carrello', 'success');
    } catch (err) {
        toast(err.message || 'Si \u00e8 verificato un errore durante la rimozione del prodotto', 'danger');
        setRowDisabled(productId, false);
    }
}

async function updateQuantity(productId, quantity, ctx) {
    if (!productId) return;

    const input = ctx?.input || document.querySelector(`.quantity-input[data-product-id="${productId}"]`);
    if (!input) return;

    const prev = typeof ctx?.previous === 'number' && !Number.isNaN(ctx.previous)
        ? ctx.previous
        : parseInt(input.value || '1', 10);

    setRowDisabled(productId, true);

    try {
        const data = await postCartAction('aggiorna', productId, quantity);

        if (!data.success) {
            input.value = prev;
            toast(data.message || 'Errore durante l\u2019aggiornamento della quantit\u00e0', 'danger');
            return;
        }

        const serverItem = Array.isArray(data.articoli)
            ? data.articoli.find(a => String(a.idLibro) === String(productId))
            : null;

        if (serverItem && typeof serverItem.quantita === 'number') input.value = String(serverItem.quantita);

        applyCartUpdate(data);
    } catch (err) {
        input.value = prev;
        toast(err.message || 'Si \u00e8 verificato un errore durante l\u2019aggiornamento del carrello', 'danger');
    } finally {
        setRowDisabled(productId, false);
    }
}

async function postCartAction(azione, idLibro, quantita) {
    if (typeof contextPath === 'undefined') throw new Error('contextPath non definito');

    const params = new URLSearchParams();
    params.append('azione', azione);
    params.append('idLibro', idLibro);
    if (typeof quantita !== 'undefined') params.append('quantita', String(quantita));

    const res = await fetch(`${contextPath}/carrello`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: params.toString()
    });

    const data = await res.json().catch(() => null);

    if (!res.ok) {
        const msg = data && data.message ? data.message : `Errore HTTP (${res.status})`;
        throw new Error(msg);
    }

    return data || {success: false, message: 'Risposta non valida'};
}

function applyCartUpdate(data) {
    const totaleArticoli = toInt(data?.totaleArticoli);
    const totale = toNumber(data?.totale);

    const badge = document.querySelector('.cart-badge');
    if (badge) badge.textContent = String(totaleArticoli);

    const totaleArticoliEl = document.getElementById('totale-articoli');
    if (totaleArticoliEl) totaleArticoliEl.textContent = String(totaleArticoli);

    const totalePrezzoEl = document.getElementById('totale-prezzo');
    if (totalePrezzoEl) totalePrezzoEl.textContent = formatEuro(totale);

    if (Array.isArray(data?.articoli)) {
        data.articoli.forEach(item => {
            const id = item?.idLibro;
            if (typeof id === 'undefined' || id === null) return;

            const rowTotal = document.getElementById(`totale-${id}`);
            if (rowTotal && typeof item?.totale !== 'undefined') rowTotal.textContent = formatEuro(toNumber(item.totale));

            const input = document.querySelector(`.quantity-input[data-product-id="${id}"]`);
            if (input && typeof item?.quantita !== 'undefined') input.value = String(item.quantita);

            syncQuantityButtonsState(String(id));
        });
    }

    if (totaleArticoli === 0) {
        window.location.reload();
    }
}

function syncQuantityButtonsState(productId) {
    const input = document.querySelector(`.quantity-input[data-product-id="${productId}"]`);
    if (!input) return;

    const max = parseInt(input.getAttribute('max') || '999999', 10);
    const min = parseInt(input.getAttribute('min') || '1', 10);
    const q = parseInt(input.value || String(min), 10);

    const minus = document.querySelector(`.quantity-btn.minus[data-product-id="${productId}"]`);
    const plus = document.querySelector(`.quantity-btn.plus[data-product-id="${productId}"]`);

    if (minus) minus.disabled = !(q > min);
    if (plus) plus.disabled = !(q < max);
}

function setRowDisabled(productId, disabled) {
    const row = document.querySelector(`.cart-item[data-product-id="${productId}"]`);
    if (!row) return;

    row.setAttribute('aria-disabled', disabled ? 'true' : 'false');

    row.querySelectorAll(
        `.quantity-btn[data-product-id="${productId}"], 
         .quantity-input[data-product-id="${productId}"], 
         .remove-item[data-product-id="${productId}"], 
         .remove-btn[data-product-id="${productId}"]`
    ).forEach(el => {
        el.disabled = !!disabled;
        el.setAttribute('aria-disabled', disabled ? 'true' : 'false');
    });
}

function toast(message, type = 'info') {
    if (typeof showAlert === 'function') {
        showAlert(message, type);
        return;
    }
    alert(message);
}

function clamp(n, min, max) {
    return Math.max(min, Math.min(max, n));
}

function toInt(v) {
    const n = parseInt(String(v ?? '0'), 10);
    return Number.isNaN(n) ? 0 : n;
}

function toNumber(v) {
    if (typeof v === 'number') return v;
    const n = parseFloat(String(v ?? '0').replace(',', '.'));
    return Number.isNaN(n) ? 0 : n;
}

function formatEuro(value) {
    const n = toNumber(value);
    return `\u20ac${n.toFixed(2)}`;
}


