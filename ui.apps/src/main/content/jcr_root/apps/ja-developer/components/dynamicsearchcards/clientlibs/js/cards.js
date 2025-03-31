document.addEventListener("DOMContentLoaded", () => {
    const component = document.querySelector(".cmp-dynamic-search-cards");
    if (!component) return;

    const endpoint = component.dataset.endpoint;
    const container = document.getElementById("cards-container");
    const searchInput = document.getElementById("search-input");
    const filtersContainer = document.getElementById("tag-filters");
    const loadingIndicator = document.getElementById("cards-loading");

    const showLoader = () => loadingIndicator && (loadingIndicator.style.display = "flex");
    const hideLoader = () => loadingIndicator && (loadingIndicator.style.display = "none");

    console.log("📡 Llamando al endpoint:", endpoint);

    let allCards = [];

    const renderCards = (cards) => {
        container.innerHTML = "";

        if (!cards.length) {
            container.innerHTML = `<p>No se encontraron resultados.</p>`;
            return;
        }

        cards.forEach(card => {
            const cardEl = document.createElement("div");
            cardEl.className = "cmp-card";
            cardEl.setAttribute("role", "listitem");

            const tagsHTML = (card.tags || []).map(tag => `<span>${tag}</span>`).join("");

            cardEl.innerHTML = `
                <img src="${card.image}" alt="${card.title}" loading="lazy" />
                <h3>${card.title}</h3>
                <p>${card.description}</p>
                <div class="card-tags">${tagsHTML}</div>
            `;

            container.appendChild(cardEl);
        });
    };

    const filterCards = () => {
        const query = (searchInput?.value || "").toLowerCase();
        const activeTag = document.querySelector(".tag-active")?.dataset.tag || "all";

        const filtered = allCards.filter(card => {
            const matchesText = card.title.toLowerCase().includes(query) ||
                card.description.toLowerCase().includes(query);
            const matchesTag = activeTag === "all" || (card.tags || []).includes(activeTag);
            return matchesText && matchesTag;
        });

        renderCards(filtered);
    };

    const setupFilters = (tags) => {
        filtersContainer.innerHTML = "";
        const uniqueTags = Array.from(new Set(tags));

        ["all", ...uniqueTags].forEach(tag => {
            const btn = document.createElement("button");
            btn.textContent = tag;
            btn.dataset.tag = tag;
            btn.className = tag === "all" ? "tag-active" : "";

            btn.addEventListener("click", () => {
                document.querySelectorAll("#tag-filters button").forEach(b => b.classList.remove("tag-active"));
                btn.classList.add("tag-active");
                filterCards();
            });

            filtersContainer.appendChild(btn);
        });
    };

    showLoader();

    fetch(endpoint)
        .then(res => {
            if (!res.ok) throw new Error("Respuesta del servlet no válida");
            return res.json();
        })
        .then(data => {
            console.log("Datos recibidos:", data);
            allCards = data;

            const allTags = allCards.flatMap(card => card.tags || []);
            setupFilters(allTags);
            renderCards(allCards);

            //  Activar búsqueda en vivo una vez tengamos los datos
            searchInput?.addEventListener("input", filterCards);
        })
        .catch(err => {
            console.error("Error al obtener datos del servlet:", err);
            container.innerHTML = `<p style="color:red;">Error al cargar tarjetas</p>`;
        })
        .finally(hideLoader);
});
