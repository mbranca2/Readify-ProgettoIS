(function () {
    function $(sel) { return document.querySelector(sel); }
    function $all(sel) { return Array.from(document.querySelectorAll(sel)); }

    function toNumber(v) {
        if (v == null) return NaN;
        const s = String(v).replace(",", ".").trim();
        const n = parseFloat(s);
        return isNaN(n) ? NaN : n;
    }

    function normalize(s) {
        return (s || "").toString().trim().toLowerCase();
    }

    const state = {
        categoriaId: 0,
        q: "",
        min: null,
        max: null,
        availability: "all",
        sort: "rel"
    };

    function getCards() {
        return $all(".book-card");
    }

    function parseDisponibile(value) {
        const s = String(value || "").trim().toLowerCase();
        if (!s) return false;
        return s === "true" || s === "1" || s === "yes" || s === "si";
    }

    function cardData(card) {
        const titolo = normalize(card.getAttribute("data-titolo")) || normalize(card.querySelector(".book-title")?.textContent);
        const autore = normalize(card.getAttribute("data-autore")) || normalize(card.querySelector(".book-author")?.textContent);
        const prezzo = toNumber(card.getAttribute("data-prezzo"));
        const disponibile = parseDisponibile(card.getAttribute("data-disponibile"));
        const cats = (card.getAttribute("data-categorie") || "")
            .split(",")
            .map(x => x.trim())
            .filter(Boolean);
        return { titolo, autore, prezzo, disponibile, cats };
    }

    function matches(card) {
        const d = cardData(card);

        if (state.categoriaId && state.categoriaId !== 0) {
            if (!d.cats.includes(String(state.categoriaId))) return false;
        }

        if (state.q) {
            const ok = d.titolo.includes(state.q) || d.autore.includes(state.q);
            if (!ok) return false;
        }

        if (state.availability === "in" && !d.disponibile) return false;
        if (state.availability === "out" && d.disponibile) return false;

        if (state.min != null && !isNaN(state.min)) {
            if (isNaN(d.prezzo) || d.prezzo < state.min) return false;
        }

        if (state.max != null && !isNaN(state.max)) {
            if (isNaN(d.prezzo) || d.prezzo > state.max) return false;
        }

        return true;
    }

    function apply() {
        const cards = getCards();

        const visible = [];
        cards.forEach(card => {
            const ok = matches(card);
            card.style.display = ok ? "" : "none";
            if (ok) visible.push(card);
        });

        sortVisible(visible);

        const grid = $("#booksGrid");
        if (grid) {
            visible.forEach(card => grid.appendChild(card));
        }

        const countEl = $("#resultsCount");
        if (countEl) countEl.textContent = String(visible.length);

        const noRes = $("#noResults");
        if (noRes) noRes.style.display = (visible.length === 0) ? "" : "none";

        renderActiveFilters();
    }

    function sortVisible(visibleCards) {
        const sort = state.sort;
        if (!sort || sort === "rel") return;

        const getTitle = (c) => normalize(c.querySelector(".book-title")?.textContent);
        const getPrice = (c) => toNumber(c.getAttribute("data-prezzo"));

        visibleCards.sort((a, b) => {
            if (sort === "priceAsc") return (getPrice(a) - getPrice(b));
            if (sort === "priceDesc") return (getPrice(b) - getPrice(a));
            if (sort === "titleAsc") return getTitle(a).localeCompare(getTitle(b));
            if (sort === "titleDesc") return getTitle(b).localeCompare(getTitle(a));
            return 0;
        });
    }

    function setCategoria(id) {
        state.categoriaId = id;
        $all(".cat-chip").forEach(b => b.classList.remove("active"));
        const btn = document.querySelector(".cat-chip[data-categoria-id='" + id + "']");
        if (btn) btn.classList.add("active");
        apply();
    }

    function setQuery(q) {
        state.q = normalize(q);
        apply();
    }

    function setPrice(min, max) {
        state.min = (min === "" || min == null) ? null : toNumber(min);
        state.max = (max === "" || max == null) ? null : toNumber(max);
        apply();
    }

    function setAvailability(v) {
        state.availability = v || "all";
        $all(".avail-chip").forEach(b => b.classList.remove("active"));
        const btn = document.querySelector(".avail-chip[data-availability='" + state.availability + "']");
        if (btn) btn.classList.add("active");
        apply();
    }

    function setSort(v) {
        state.sort = v || "rel";
        apply();
    }

    function renderActiveFilters() {
        const host = $("#activeFilters");
        if (!host) return;
        host.innerHTML = "";

        function chip(label, onClear) {
            const el = document.createElement("span");
            el.className = "af-chip";
            const t = document.createElement("span");
            t.textContent = label;
            const b = document.createElement("button");
            b.type = "button";
            b.textContent = "✕";
            b.addEventListener("click", onClear);
            el.appendChild(t);
            el.appendChild(b);
            host.appendChild(el);
        }

        if (state.q) {
            chip("Ricerca: " + state.q, () => {
                const input = $("#searchInput");
                if (input) input.value = "";
                setQuery("");
            });
        }

        if (state.categoriaId && state.categoriaId !== 0) {
            const btn = document.querySelector(".cat-chip[data-categoria-id='" + state.categoriaId + "']");
            const name = btn ? btn.textContent.trim() : ("Categoria " + state.categoriaId);
            chip("Categoria: " + name, () => setCategoria(0));
        }

        if (state.min != null && !isNaN(state.min)) {
            chip("Min: " + state.min, () => {
                const i = $("#priceMin");
                if (i) i.value = "";
                setPrice("", $("#priceMax")?.value || "");
            });
        }

        if (state.max != null && !isNaN(state.max)) {
            chip("Max: " + state.max, () => {
                const i = $("#priceMax");
                if (i) i.value = "";
                setPrice($("#priceMin")?.value || "", "");
            });
        }

        if (state.availability === "in") {
            chip("Disponibili", () => setAvailability("all"));
        }

        if (state.availability === "out") {
            chip("Esauriti", () => setAvailability("all"));
        }
    }

    function resetAll() {
        setCategoria(0);

        const s = $("#searchInput");
        if (s) s.value = "";
        state.q = "";

        const min = $("#priceMin");
        const max = $("#priceMax");
        if (min) min.value = "";
        if (max) max.value = "";
        state.min = null;
        state.max = null;

        state.availability = "all";
        $all(".avail-chip").forEach(b => b.classList.remove("active"));
        const allBtn = document.querySelector(".avail-chip[data-availability='all']");
        if (allBtn) allBtn.classList.add("active");

        const sort = $("#sortSelect");
        if (sort) sort.value = "rel";
        state.sort = "rel";

        apply();
    }

    function bind() {
        $all(".cat-chip").forEach(btn => {
            btn.addEventListener("click", () => setCategoria(parseInt(btn.getAttribute("data-categoria-id"), 10) || 0));
        });

        const search = $("#searchInput");
        if (search) search.addEventListener("input", () => setQuery(search.value));

        const clear = $("#clearSearch");
        if (clear) clear.addEventListener("click", () => {
            const s = $("#searchInput");
            if (s) s.value = "";
            setQuery("");
        });

        const min = $("#priceMin");
        const max = $("#priceMax");
        if (min) min.addEventListener("input", () => {});
        if (max) max.addEventListener("input", () => {});
        const applyBtn = $("#applyFilters");
        if (applyBtn) applyBtn.addEventListener("click", () => setPrice(min?.value || "", max?.value || ""));

        $all(".avail-chip").forEach(btn => {
            btn.addEventListener("click", () => {
                const choice = btn.getAttribute("data-availability") || "all";
                setAvailability(choice);
            });
        });

        document.addEventListener("click", (event) => {
            const btn = event.target.closest(".avail-chip");
            if (!btn) return;
            const choice = btn.getAttribute("data-availability") || "all";
            setAvailability(choice);
        });

        const reset = $("#resetFilters");
        if (reset) reset.addEventListener("click", resetAll);

        const sort = $("#sortSelect");
        if (sort) sort.addEventListener("change", () => setSort(sort.value));

        const panel = $("#filtersPanel");
        const open = $("#openFilters");
        const close = $("#closeFilters");

        function openPanel() {
            if (!panel) return;
            panel.classList.add("open");
        }

        function closePanel() {
            if (!panel) return;
            panel.classList.remove("open");
        }

        if (open) open.addEventListener("click", openPanel);
        if (close) close.addEventListener("click", closePanel);

        window.addEventListener("keydown", (e) => {
            if (e.key === "Escape") closePanel();
        });

        window.addEventListener("resize", () => {
            if (window.innerWidth >= 980) closePanel();
        });
    }

    document.addEventListener("DOMContentLoaded", () => {
        bind();
        apply();
    });
})();
