// ============================================================
// КОНФИГУРАЦИЯ И СОСТОЯНИЕ
// ============================================================
const API_URL = 'http://localhost:8080';
const PAGE_SIZE = 9;

const state = {
    token: localStorage.getItem('token') || null,
    email: localStorage.getItem('email') || null,
    events: [],
    currentPage: 0,
    totalPages: 0,
    totalElements: 0,

    // Фильтры
    filterType: null,
    filterMinPrice: null,
    filterMaxPrice: null,
    filterDateFrom: null,
    filterDateTo: null,
    searchQuery: '',
    searchDebounce: null,

    currentEvent: null,
    editingEvent: null,
    myTickets: [],
    currentReviewRating: 0,
};

// ============================================================
// БАЗОВЫЙ API И ОБРАБОТКА ОШИБОК
// ============================================================
async function apiCall(path, options = {}) {
    const headers = {
        'Content-Type': 'application/json',
        ...(options.headers || {})
    };

    // Подставляем токен, если он есть и это не публичный запрос
    if (state.token && !options.noAuth) {
        headers['Authorization'] = `Bearer ${state.token}`;
    }

    try {
        const response = await fetch(`${API_URL}${path}`, {
            ...options,
            headers,
        });

        const text = await response.text();
        let data = null;
        try {
            data = text ? JSON.parse(text) : null;
        } catch {
            data = text;
        }

        // 401 Unauthorized — токен протух или неверный
        if (response.status === 401 && state.token && !options.noAuth) {
            handleUnauthorized();
            const error = new Error('Сессия истекла. Войдите снова.');
            error.status = 401;
            throw error;
        }

        // 403 Forbidden — нет прав (например, не организатор)
        if (response.status === 403) {
            const error = new Error('Недостаточно прав для этого действия');
            error.status = 403;
            throw error;
        }

        // 400 Bad Request — ошибки валидации
        if (response.status === 400) {
            const message = data?.message || data?.error || 'Ошибка валидации';
            const error = new Error(message);
            error.status = 400;
            error.details = data?.details || null;
            throw error;
        }

        if (!response.ok) {
            const message = data?.message || data?.error || data || `Ошибка сервера: ${response.status}`;
            throw new Error(message);
        }

        return data;
    } catch (e) {
        // Если это не 401 (который мы уже обработали), показываем ошибку
        if (e.status !== 401) {
            showApiError(e);
        }
        throw e;
    }
}

/**
 * Показывает ошибку в тосте. Если есть details (валидация) — выводит их списком.
 */
function showApiError(e, fallbackMessage = 'Произошла ошибка') {
    if (e.details && typeof e.details === 'object' && Object.keys(e.details).length > 0) {
        const detailsText = Object.values(e.details).join('\n');
        showToast(detailsText, 'warning', e.message || 'Ошибка валидации');
    } else {
        showToast(e.message || fallbackMessage, 'error');
    }
}

// ============================================================
// УТИЛИТЫ И ФОРМАТИРОВАНИЕ
// ============================================================
function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

function getInitials(str) {
    if (!str) return '?';
    return str.substring(0, 2).toUpperCase();
}

function formatPrice(price) {
    if (price === null || price === undefined) return '—';
    const num = Number(price);
    if (num === 0) return 'Бесплатно';
    return new Intl.NumberFormat('ru-RU', {
        style: 'currency', currency: 'RUB', maximumFractionDigits: 0,
    }).format(num);
}

function formatDate(dateString) {
    if (!dateString) return '—';
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('ru-RU', {
        day: 'numeric', month: 'long', year: 'numeric',
        hour: '2-digit', minute: '2-digit',
    }).format(date);
}

function formatDateShort(dateString) {
    if (!dateString) return '—';
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('ru-RU', {
        day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit',
    }).format(date);
}

function renderStars(rating) {
    let html = '';
    for (let i = 1; i <= 5; i++) {
        html += i <= rating ? '★' : '☆';
    }
    return html;
}

const TYPE_LABELS = {
    MUSIC: '🎵 Музыка', GAMING: '🎮 Игры', FOOD: '🍔 Еда',
    BUSINESS: '💼 Бизнес', WALK: '🚶 Прогулки', HOBBIES: '🎨 Хобби', ANOTHER: '✨ Другое',
};
function getTypeLabel(type) { return TYPE_LABELS[type] || type || 'Событие'; }

function getPlaceholderGradient(seed) {
    const gradients = [
        'linear-gradient(135deg, #EE4540, #801336)',
        'linear-gradient(135deg, #510A32, #EE4540)',
        'linear-gradient(135deg, #2D142C, #C72C41)',
        'linear-gradient(135deg, #C72C41, #510A32)',
        'linear-gradient(135deg, #801336, #2D142C)',
    ];
    const index = seed ? seed.length % gradients.length : 0;
    return gradients[index];
}

// ============================================================
// UI КОМПОНЕНТЫ (Тосты и Модалки)
// ============================================================
function showToast(message, type = 'info', title = null) {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;

    const icons = { success: '✓', error: '✕', warning: '⚠', info: 'ℹ' };
    const titles = { success: 'Успешно', error: 'Ошибка', warning: 'Внимание', info: 'Информация' };

    toast.innerHTML = `
        <div class="toast-icon">${icons[type] || 'ℹ'}</div>
        <div class="toast-content">
            <div class="toast-title">${escapeHtml(title || titles[type] || '')}</div>
            <div class="toast-message">${escapeHtml(message)}</div>
        </div>
    `;

    container.appendChild(toast);
    setTimeout(() => {
        toast.style.animation = 'slideIn 0.3s reverse';
        setTimeout(() => toast.remove(), 300);
    }, 5000);
}

function openModal(contentHtml, size = '') {
    const overlay = document.getElementById('modalOverlay');
    const modal = document.getElementById('modalContent');
    modal.className = `modal ${size}`;
    modal.innerHTML = contentHtml;
    overlay.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeModal() {
    const overlay = document.getElementById('modalOverlay');
    overlay.classList.remove('active');
    document.body.style.overflow = '';
    state.editingEvent = null;
    state.currentReviewRating = 0;
}

function closeModalOnOverlay(e) {
    if (e.target === e.currentTarget) closeModal();
}

document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') closeModal();
});

// ============================================================
// АУТЕНТИФИКАЦИЯ
// ============================================================
function renderHeader() {
    const container = document.getElementById('headerActions');
    if (state.token) {
        container.innerHTML = `
            <button class="btn btn-primary" onclick="openCreateEventModal()">
                <span>+</span> Создать
            </button>
            <button class="btn btn-secondary btn-sm" onclick="showMyEvents()">Мои события</button>
            <button class="btn btn-secondary btn-sm" onclick="showMyTickets()">🎟 Мои билеты</button>
            <div class="avatar" onclick="showProfileMenu()" title="${escapeHtml(state.email || '')}">
                ${getInitials(state.email)}
            </div>
        `;
    } else {
        container.innerHTML = `
            <button class="btn btn-secondary" onclick="openLoginModal()">Войти</button>
            <button class="btn btn-primary" onclick="openRegisterModal()">Регистрация</button>
        `;
    }
}

function logout() {
    state.token = null;
    state.email = null;
    localStorage.removeItem('token');
    localStorage.removeItem('email');
    renderHeader();
    showCatalog();
    showToast('Вы вышли из аккаунта', 'info');
}

function handleUnauthorized() {
    if (!state.token) return;
    state.token = null;
    state.email = null;
    localStorage.removeItem('token');
    localStorage.removeItem('email');
    closeModal();
    renderHeader();
    showCatalog();
    showToast('Сессия истекла. Войдите снова.', 'warning');
    openLoginModal();
}

function openLoginModal() {
    openModal(`
        <div class="modal-header">
            <div class="modal-title">Вход</div>
            <button class="modal-close" onclick="closeModal()">✕</button>
        </div>
        <div class="modal-body">
            <div class="form-group">
                <label class="form-label">Email</label>
                <input type="email" class="form-input" id="loginEmail" placeholder="you@example.com">
            </div>
            <div class="form-group">
                <label class="form-label">Пароль</label>
                <input type="password" class="form-input" id="loginPassword" placeholder="••••••">
            </div>
            <button class="btn btn-primary btn-block btn-lg" onclick="doLogin()">Войти</button>
            <p class="text-secondary" style="text-align: center; margin-top: 16px; font-size: 14px;">
                Нет аккаунта? <a href="#" onclick="closeModal(); openRegisterModal(); return false;" style="color: var(--accent); font-weight: 600;">Зарегистрироваться</a>
            </p>
        </div>
    `);
    setTimeout(() => document.getElementById('loginEmail')?.focus(), 100);
}

async function doLogin() {
    const email = document.getElementById('loginEmail').value.trim();
    const password = document.getElementById('loginPassword').value;
    if (!email || !password) return showToast('Заполните все поля', 'warning');

    try {
        const token = await apiCall('/login', {
            method: 'POST',
            body: JSON.stringify({ email, password }),
            noAuth: true,
        });

        state.token = token.trim();
        state.email = email;
        localStorage.setItem('token', state.token);
        localStorage.setItem('email', state.email);

        closeModal();
        renderHeader();
        showToast('Добро пожаловать!', 'success');
        showCatalog();
    } catch (e) {
        // Ошибка уже показана в apiCall
    }
}

function openRegisterModal() {
    openModal(`
        <div class="modal-header">
            <div class="modal-title">Регистрация</div>
            <button class="modal-close" onclick="closeModal()">✕</button>
        </div>
        <div class="modal-body">
            <div class="form-group">
                <label class="form-label">Email</label>
                <input type="email" class="form-input" id="regEmail" placeholder="you@example.com">
            </div>
            <div class="form-group">
                <label class="form-label">Полное имя</label>
                <input type="text" class="form-input" id="regFullName" placeholder="Иван Иванов">
            </div>
            <div class="form-group">
                <label class="form-label">Пароль</label>
                <input type="password" class="form-input" id="regPassword" placeholder="От 6 до 30 символов">
            </div>
            <button class="btn btn-primary btn-block btn-lg" onclick="doRegister()">Создать аккаунт</button>
        </div>
    `);
    setTimeout(() => document.getElementById('regEmail')?.focus(), 100);
}

async function doRegister() {
    const email = document.getElementById('regEmail').value.trim();
    const fullName = document.getElementById('regFullName').value.trim();
    const password = document.getElementById('regPassword').value;

    if (!email || !fullName || !password) return showToast('Заполните все поля', 'warning');

    try {
        await apiCall('/register', {
            method: 'POST',
            body: JSON.stringify({ email, password, fullName }),
            noAuth: true,
        });
        showToast('Аккаунт создан! Теперь войдите.', 'success');
        closeModal();
        setTimeout(() => {
            openLoginModal();
            const loginEmail = document.getElementById('loginEmail');
            if (loginEmail) loginEmail.value = email;
        }, 300);
    } catch (e) {}
}

// ============================================================
// КАТАЛОГ СОБЫТИЙ
// ============================================================
function showCatalog() {
    document.getElementById('pageContent').innerHTML = `
        <h1 class="page-title">Все события</h1>
        <p class="page-subtitle">Найди своё следующее приключение</p>

        <div class="filters-row">
            <div class="chips" id="typeChips"></div>
            <button class="filter-btn" id="moreFiltersBtn" onclick="toggleFiltersPanel()">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="4" y1="6" x2="20" y2="6"></line><line x1="4" y1="12" x2="14" y2="12"></line><line x1="4" y1="18" x2="10" y2="18"></line></svg>
                Фильтры
            </button>
        </div>

        <div class="filters-panel" id="filtersPanel">
            <div class="filter-group">
                <label>Мин. цена</label>
                <input type="number" class="form-input" id="filterMinPrice" placeholder="0" onchange="applyFilters()">
            </div>
            <div class="filter-group">
                <label>Макс. цена</label>
                <input type="number" class="form-input" id="filterMaxPrice" placeholder="10000" onchange="applyFilters()">
            </div>
            <div class="filter-group">
                <label>С даты и времени</label>
                <input type="datetime-local" class="form-input" id="filterDateFrom" onchange="applyFilters()">
            </div>
            <div class="filter-group">
                <label>По дату и время</label>
                <input type="datetime-local" class="form-input" id="filterDateTo" onchange="applyFilters()">
            </div>
            <div class="filter-group" style="display: flex; align-items: flex-end;">
                <button class="btn btn-secondary btn-block" onclick="resetFilters()">Сбросить</button>
            </div>
        </div>

        <div id="eventsContainer"></div>
    `;
    renderTypeChips();
    loadEvents();
}

function renderTypeChips() {
    const container = document.getElementById('typeChips');
    if (!container) return;
    const types = [
        { value: null, label: 'Все' }, { value: 'MUSIC', label: '🎵 Музыка' },
        { value: 'GAMING', label: '🎮 Игры' }, { value: 'FOOD', label: '🍔 Еда' },
        { value: 'BUSINESS', label: '💼 Бизнес' }, { value: 'WALK', label: '🚶 Прогулки' },
        { value: 'HOBBIES', label: '🎨 Хобби' }, { value: 'ANOTHER', label: '✨ Другое' },
    ];
    container.innerHTML = types.map(t => `
        <button class="chip ${state.filterType === t.value ? 'active' : ''}" onclick="selectType('${t.value || ''}')">
            ${t.label}
        </button>
    `).join('');
}

function selectType(type) {
    state.filterType = type || null;
    state.currentPage = 0;
    renderTypeChips();
    loadEvents();
}

function toggleFiltersPanel() {
    document.getElementById('filtersPanel').classList.toggle('active');
    document.getElementById('moreFiltersBtn').classList.toggle('active');
}

function applyFilters() {
    state.filterMinPrice = document.getElementById('filterMinPrice')?.value || null;
    state.filterMaxPrice = document.getElementById('filterMaxPrice')?.value || null;
    const fromVal = document.getElementById('filterDateFrom')?.value;
    const toVal = document.getElementById('filterDateTo')?.value;

    state.filterDateFrom = fromVal ? fromVal + ':00' : null;
    state.filterDateTo = toVal ? toVal + ':00' : null;
    state.currentPage = 0;
    loadEvents();
}

function resetFilters() {
    state.filterType = null;
    state.filterMinPrice = null;
    state.filterMaxPrice = null;
    state.filterDateFrom = null;
    state.filterDateTo = null;
    state.searchQuery = '';
    state.currentPage = 0;
    document.getElementById('searchInput').value = '';
    if (document.getElementById('filtersPanel')) {
        document.getElementById('filterMinPrice').value = '';
        document.getElementById('filterMaxPrice').value = '';
        document.getElementById('filterDateFrom').value = '';
        document.getElementById('filterDateTo').value = '';
    }
    renderTypeChips();
    loadEvents();
    showToast('Фильтры сброшены', 'info');
}

function onSearchInput() {
    clearTimeout(state.searchDebounce);
    state.searchDebounce = setTimeout(() => {
        state.searchQuery = document.getElementById('searchInput').value.trim();
        state.currentPage = 0;
        if (document.getElementById('eventsContainer')) loadEvents();
    }, 500);
}

async function loadEvents() {
    const container = document.getElementById('eventsContainer');
    if (!container) return;

    container.innerHTML = `<div class="events-grid">${Array(6).fill('<div class="skeleton skeleton-card"></div>').join('')}</div>`;

    try {
        const params = new URLSearchParams();
        params.append('page', state.currentPage);
        params.append('size', PAGE_SIZE);
        params.append('sort', 'eventDate,asc');
        if (state.filterType) params.append('type', state.filterType);
        if (state.filterMinPrice) params.append('minPrice', state.filterMinPrice);
        if (state.filterMaxPrice) params.append('maxPrice', state.filterMaxPrice);
        if (state.filterDateFrom) params.append('dateFrom', state.filterDateFrom);
        if (state.filterDateTo) params.append('dateTo', state.filterDateTo);
        if (state.searchQuery) params.append('search', state.searchQuery);

        const response = await apiCall(`/events?${params.toString()}`);

        state.events = response.content || [];
        state.totalPages = response.totalPages || 1;
        renderEvents();
    } catch (e) {
        container.innerHTML = `<div class="empty-state"><div class="empty-state-icon">😕</div><h3>Не удалось загрузить</h3><button class="btn btn-primary mt-4" onclick="loadEvents()">Повторить</button></div>`;
    }
}

function renderEvents() {
    const container = document.getElementById('eventsContainer');
    if (state.events.length === 0) {
        container.innerHTML = `<div class="empty-state"><div class="empty-state-icon">🔍</div><h3>Ничего не найдено</h3><button class="btn btn-secondary mt-4" onclick="resetFilters()">Сбросить фильтры</button></div>`;
        return;
    }

    const cards = state.events.map(event => {
        const cover = event.coverImageUrl
            ? `background-image: url('${escapeHtml(event.coverImageUrl)}')`
            : `background: ${getPlaceholderGradient(event.name || '')}`;
        const priceClass = Number(event.price) === 0 ? 'free' : '';

        return `
            <div class="event-card" onclick="openEventDetail(${event.id})">
                <div class="event-cover" style="${cover}">
                    <div class="event-type">${getTypeLabel(event.type)}</div>
                </div>
                <div class="event-body">
                    <div class="event-name">${escapeHtml(event.name)}</div>
                    <div class="event-meta">
                        <div class="event-meta-row">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
                            ${formatDateShort(event.eventDate)}
                        </div>
                        <div class="event-meta-row">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle cx="12" cy="10" r="3"></circle></svg>
                            ${escapeHtml(event.location)}
                        </div>
                    </div>
                    <div class="event-footer">
                        <div class="event-price ${priceClass}">${formatPrice(event.price)}</div>
                    </div>
                </div>
            </div>
        `;
    }).join('');

    container.innerHTML = `<div class="events-grid">${cards}</div>${renderPagination()}`;
}

function renderPagination() {
    if (state.totalPages <= 1) return '';
    const current = state.currentPage;
    const total = state.totalPages;
    let pages = [];
    pages.push(`<button class="page-btn" ${current === 0 ? 'disabled' : ''} onclick="goToPage(${current - 1})">←</button>`);

    for (let i = 0; i < total; i++) {
        if (i === 0 || i === total - 1 || Math.abs(i - current) <= 1) {
            pages.push(`<button class="page-btn ${i === current ? 'active' : ''}" onclick="goToPage(${i})">${i + 1}</button>`);
        } else if (i === 1 || i === total - 2) {
            pages.push(`<span style="color:var(--text-muted)">...</span>`);
        }
    }
    pages.push(`<button class="page-btn" ${current >= total - 1 ? 'disabled' : ''} onclick="goToPage(${current + 1})">→</button>`);

    return `<div class="pagination">${pages.join('')}</div>`;
}

function goToPage(page) {
    state.currentPage = page;
    loadEvents();
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// ============================================================
// ДЕТАЛИ СОБЫТИЯ
// ============================================================
async function openEventDetail(id) {
    openModal('<div class="loader"></div>', 'modal-lg');
    try {
        const event = await apiCall(`/eventById/${id}`);
        state.currentEvent = event;

        let myRegistration = null;
        if (state.token && event.organizerEmail !== state.email) {
            try { myRegistration = await apiCall(`/users/me/tickets/${id}`); } catch {}
        }

        let participants = null;
        if (state.token && event.organizerEmail === state.email) {
            try { participants = await apiCall(`/events/${id}/participants`); } catch {}
        }

        let reviews = [];
        try { reviews = await apiCall(`/events/${id}/reviews`); } catch {}

        renderEventDetail(event, myRegistration, participants, reviews);
    } catch (e) {
        closeModal();
    }
}

function renderEventDetail(event, myRegistration, participants, reviews) {
    reviews = reviews || [];
    const cover = event.imageUrls && event.imageUrls.length > 0
        ? `background-image: url('${escapeHtml(event.imageUrls[0])}')`
        : `background: ${getPlaceholderGradient(event.name || '')}`;

    const isOwner = state.email && event.organizerEmail === state.email;
    const priceClass = Number(event.price) === 0 ? 'success' : 'accent';
    const isPast = new Date(event.eventDate) < new Date();
    const isActive = event.status === 'ACTIVE';

    // Кнопки действий
    let actionBlock = '';
    if (isOwner) {
        if (isActive) {
            actionBlock += `<button class="btn btn-danger btn-block mb-2" onclick="cancelEvent(${event.id})">Отменить событие</button>`;
        }
        actionBlock += `<button class="btn btn-secondary btn-block mb-2" onclick="closeModal(); openEditEventModal(${event.id})">✏️ Редактировать</button>`;
        actionBlock += `<button class="btn btn-danger btn-block" onclick="deleteEvent(${event.id})">🗑 Удалить навсегда</button>`;

        if (participants && participants.length > 0) {
            actionBlock += `
                <div class="sidebar-card mt-2">
                    <div class="sidebar-label mb-2">Участники (${participants.length})</div>
                    <div class="participants-list">
                        ${participants.map(p => `
                            <div class="participant-item">
                                <div class="participant-avatar">${getInitials(p.fullName || p.email)}</div>
                                <div class="participant-info">
                                    <div class="participant-name">${escapeHtml(p.fullName)}</div>
                                    <div class="participant-email">${escapeHtml(p.email)}</div>
                                </div>
                            </div>
                        `).join('')}
                    </div>
                </div>
            `;
        }
    } else if (!state.token) {
        actionBlock = `<button class="btn btn-primary btn-block btn-lg" onclick="closeModal(); openLoginModal();">Войдите, чтобы записаться</button>`;
    } else if (myRegistration && myRegistration.registrationStatus === 'CONFIRMED') {
        actionBlock = `
            <div class="ticket-badge">✓ Вы записаны</div>
            <div style="font-size: 12px; color: var(--text-muted); text-align: center; margin-bottom: 12px; font-family: monospace;">Билет: ${escapeHtml(myRegistration.ticketNumber?.substring(0,8))}</div>
            <button class="btn btn-danger btn-block" onclick="cancelRegistration(${event.id})">Отменить запись</button>
        `;
    } else if (isPast || !isActive) {
        actionBlock = `<button class="btn btn-secondary btn-block btn-lg" disabled>Событие завершено или отменено</button>`;
    } else {
        actionBlock = `<button class="btn btn-primary btn-block btn-lg" onclick="registerToEvent(${event.id})">🎟 Записаться</button>`;
    }

    // Может ли оставить отзыв
    const canReview = state.token && !isOwner && isPast && myRegistration && myRegistration.registrationStatus === 'CONFIRMED';

    const reviewActionBlock = `
        <div class="reviews-section">
            <div class="reviews-header">
                <div class="reviews-title">Отзывы (${reviews.length})</div>
                ${canReview ? `<button class="btn btn-primary btn-sm" onclick="openCreateReviewModal(${event.id})">✍️ Оставить отзыв</button>` : ''}
            </div>
            ${reviews.length === 0 ? '<div class="empty-state-small">Пока нет отзывов</div>' : ''}
            <div class="reviews-list">
                ${reviews.map(r => `
                    <div class="review-item">
                        <div class="review-header">
                            <div class="review-avatar">${getInitials(r.authorFullName)}</div>
                            <div class="review-author">
                                <div class="review-author-name">${escapeHtml(r.authorFullName)}</div>
                                <div class="review-date">${formatDate(r.createdAt)}</div>
                            </div>
                            <div class="review-rating">${renderStars(r.rating)}</div>
                        </div>
                        <div class="review-text">${escapeHtml(r.text || '')}</div>
                        ${r.imageUrls && r.imageUrls.length > 0 ? `
                            <div class="review-images">
                                ${r.imageUrls.map(url => `<div class="review-image" style="background-image: url('${escapeHtml(url)}')" onclick="window.open('${escapeHtml(url)}', '_blank')"></div>`).join('')}
                            </div>
                        ` : ''}
                    </div>
                `).join('')}
            </div>
        </div>
    `;

    openModal(`
        <div class="event-detail-cover" style="${cover}">
            <button class="modal-close" style="position: absolute; top: 16px; right: 16px; z-index: 3;" onclick="closeModal()">✕</button>
        </div>
        <div class="event-detail-header">
            <div class="event-detail-type" style="background:${isActive ? 'var(--accent)' : 'var(--danger)'}">
                ${getTypeLabel(event.type)} ${!isActive ? '(' + event.status + ')' : ''}
            </div>
            <h1 class="event-detail-title">${escapeHtml(event.name)}</h1>
            <div class="event-detail-meta">
                <div class="event-detail-meta-item">📅 ${formatDate(event.eventDate)}</div>
                <div class="event-detail-meta-item">📍 ${escapeHtml(event.location)}</div>
            </div>
        </div>
        <div class="event-detail-body">
            <div>
                <div class="sidebar-label mb-2">Описание</div>
                <div class="event-detail-description">${escapeHtml(event.description)}</div>
                ${reviewActionBlock}
            </div>
            <div class="event-detail-sidebar">
                <div class="sidebar-card">
                    <div class="sidebar-label">Цена</div>
                    <div class="sidebar-value ${priceClass}">${formatPrice(event.price)}</div>
                </div>
                <div class="sidebar-card">
                    <div class="sidebar-label">Участники</div>
                    <div class="sidebar-value">${event.currentParticipants || 0} / ${event.maxParticipants}</div>
                </div>
                <div class="sidebar-card">
                    <div class="sidebar-label mb-2">Организатор</div>
                    <div class="organizer-card" style="border:none; padding:0; background:transparent;">
                        <div class="organizer-avatar">${getInitials(event.organizerEmail)}</div>
                        <div class="organizer-info">
                            <div class="organizer-email">${escapeHtml(event.organizerEmail)}</div>
                        </div>
                    </div>
                </div>
                ${actionBlock}
            </div>
        </div>
    `, 'modal-lg');
}

// ============================================================
// ДЕЙСТВИЯ С СОБЫТИЕМ (Запись, Отмена, Удаление)
// ============================================================
async function registerToEvent(id) {
    try {
        await apiCall(`/events/${id}/register`, { method: 'POST' });
        showToast('Вы успешно записались!', 'success');
        closeModal();
        setTimeout(() => openEventDetail(id), 300);
    } catch (e) {}
}

async function cancelRegistration(id) {
    if (!confirm('Отменить запись на событие?')) return;
    try {
        await apiCall(`/events/${id}/register`, { method: 'DELETE' });
        showToast('Запись отменена', 'success');
        closeModal();
        setTimeout(() => openEventDetail(id), 300);
    } catch (e) {}
}

async function cancelEvent(id) {
    if (!confirm('Отменить событие? Все билеты будут аннулированы.')) return;
    try {
        await apiCall(`/events/${id}/cancel`, { method: 'POST' });
        showToast('Событие отменено', 'success');
        closeModal();
        if (document.getElementById('eventsContainer')) loadEvents();
    } catch (e) {}
}

async function deleteEvent(id) {
    if (!confirm('Удалить событие навсегда?')) return;
    try {
        await apiCall(`/deleteEvent/${id}`, { method: 'DELETE' });
        showToast('Событие удалено', 'success');
        closeModal();
        if (document.getElementById('eventsContainer')) loadEvents();
        else showMyEvents();
    } catch (e) {}
}

// ============================================================
// ОТЗЫВЫ (Создание)
// ============================================================
function openCreateReviewModal(eventId) {
    state.currentReviewRating = 0;
    openModal(`
        <div class="modal-header">
            <div class="modal-title">Оставить отзыв</div>
            <button class="modal-close" onclick="closeModal()">✕</button>
        </div>
        <div class="modal-body">
            <div class="form-group">
                <label class="form-label">Ваша оценка *</label>
                <div class="star-input" id="starInput">
                    ${[1, 2, 3, 4, 5].map(n => `<button type="button" onclick="setReviewRating(${n})">☆</button>`).join('')}
                    <span class="star-input-value" id="starValue">Выберите оценку</span>
                </div>
            </div>
            <div class="form-group">
                <label class="form-label">Текст отзыва</label>
                <textarea class="form-textarea" id="reviewText" maxlength="1000" placeholder="Поделитесь впечатлениями..."></textarea>
            </div>
            <button class="btn btn-primary btn-block btn-lg" onclick="submitReview(${eventId})">Отправить</button>
        </div>
    `);
}

function setReviewRating(rating) {
    state.currentReviewRating = rating;
    const buttons = document.querySelectorAll('#starInput button');
    buttons.forEach((btn, idx) => {
        btn.textContent = idx < rating ? '★' : '☆';
        btn.classList.toggle('active', idx < rating);
    });
    const labels = ['', 'Ужасно', 'Плохо', 'Нормально', 'Хорошо', 'Отлично'];
    document.getElementById('starValue').textContent = labels[rating] || '';
}

async function submitReview(eventId) {
    if (state.currentReviewRating < 1) return showToast('Поставьте оценку', 'warning');
    const text = document.getElementById('reviewText').value.trim();

    try {
        await apiCall(`/events/${eventId}/reviews`, {
            method: 'POST',
            body: JSON.stringify({
                rating: state.currentReviewRating,
                text,
                imageUrls: []
            })
        });
        showToast('Отзыв опубликован!', 'success');
        closeModal();
        setTimeout(() => openEventDetail(eventId), 300);
    } catch (e) {}
}

// ============================================================
// ПРОФИЛЬ
// ============================================================
async function showProfileMenu() {
    openModal('<div class="loader"></div>');
    try {
        const profile = await apiCall('/profiles/me');
        const avatar = profile.avatarUrl ? `background-image: url('${escapeHtml(profile.avatarUrl)}'); background-size: cover;` : '';

        openModal(`
            <div class="modal-header">
                <div class="modal-title">Профиль</div>
                <button class="modal-close" onclick="closeModal()">✕</button>
            </div>
            <div class="modal-body">
                <div class="organizer-card mb-4">
                    <div class="organizer-avatar" style="${avatar}">${profile.avatarUrl ? '' : getInitials(state.email)}</div>
                    <div class="organizer-info">
                        <div class="organizer-email">${escapeHtml(profile.fullName)}</div>
                        <div style="font-size: 12px; color: var(--text-muted);">⭐ ${(profile.rating || 0).toFixed(1)} · ${profile.reviewsCount || 0} отзывов</div>
                    </div>
                </div>
                ${profile.bio ? `<div class="mb-4" style="font-size:14px; color:var(--text-secondary);">${escapeHtml(profile.bio)}</div>` : ''}

                <button class="btn btn-block btn-primary mb-2" onclick="openEditProfileModal()">✏️ Редактировать профиль</button>
                <button class="btn btn-block btn-danger" onclick="logout()">🚪 Выйти</button>
            </div>
        `);
    } catch (e) {
        closeModal();
    }
}

async function openEditProfileModal() {
    openModal('<div class="loader"></div>');
    try {
        const profile = await apiCall('/profiles/me');
        openModal(`
            <div class="modal-header">
                <div class="modal-title">Редактировать профиль</div>
                <button class="modal-close" onclick="closeModal()">✕</button>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label class="form-label">О себе</label>
                    <textarea class="form-textarea" id="editBio" maxlength="500">${escapeHtml(profile.bio || '')}</textarea>
                </div>
                <div class="form-group">
                    <label class="form-label">Аватар (URL)</label>
                    <input type="url" class="form-input" id="editAvatar" value="${escapeHtml(profile.avatarUrl || '')}">
                </div>
                <div class="form-group" style="display:flex; align-items:center; gap:12px; padding: 12px; background: var(--surface-2); border-radius: 8px;">
                    <input type="checkbox" id="editAllow" ${profile.allowMessages ? 'checked' : ''} style="width:20px; height:20px; cursor:pointer;">
                    <label for="editAllow" style="cursor:pointer; font-size:14px;">Разрешить сообщения</label>
                </div>
                <button class="btn btn-primary btn-block" onclick="submitProfileForm()">Сохранить</button>
            </div>
        `);
    } catch (e) {
        closeModal();
    }
}

async function submitProfileForm() {
    const bio = document.getElementById('editBio').value.trim();
    const avatarUrl = document.getElementById('editAvatar').value.trim();
    const allowMessages = document.getElementById('editAllow').checked;

    try {
        await apiCall('/profiles/me', {
            method: 'PATCH',
            body: JSON.stringify({ bio, avatarUrl, allowMessages }),
        });
        showToast('Профиль обновлён!', 'success');
        closeModal();
    } catch (e) {}
}

// ============================================================
// МОИ БИЛЕТЫ
// ============================================================
async function showMyTickets() {
    document.getElementById('pageContent').innerHTML = `<h1 class="page-title">Мои билеты</h1><div class="loader"></div>`;
    try {
        const tickets = await apiCall('/users/me/tickets');
        const container = document.getElementById('pageContent');

        if (!tickets || tickets.length === 0) {
            container.innerHTML = `<div class="empty-state"><div class="empty-state-icon">🎟</div><h3>У вас пока нет билетов</h3><button class="btn btn-primary mt-4" onclick="showCatalog()">В каталог</button></div>`;
            return;
        }

        container.innerHTML = `
            <h1 class="page-title">Мои билеты</h1>
            <div class="events-grid">
                ${tickets.map(t => `
                    <div class="ticket-card">
                        <div class="ticket-header">
                            <div>
                                <div class="ticket-title">${escapeHtml(t.eventName)}</div>
                                <div class="ticket-number">🎟 №${escapeHtml(t.ticketNumber?.substring(0,8).toUpperCase())}</div>
                            </div>
                        </div>
                        <div class="ticket-info">
                            <div class="ticket-info-item">📅 ${formatDate(t.eventDate)}</div>
                            <div class="ticket-info-item">📍 ${escapeHtml(t.location)}</div>
                        </div>
                        <div class="ticket-actions">
                            <button class="btn btn-secondary btn-sm" onclick="openEventDetail(${t.eventId})">Открыть</button>
                            <button class="btn btn-danger btn-sm" onclick="cancelRegistration(${t.eventId})">Отменить</button>
                        </div>
                    </div>
                `).join('')}
            </div>
        `;
    } catch (e) {
        document.getElementById('pageContent').innerHTML = `<div class="empty-state"><h3>Ошибка загрузки</h3></div>`;
    }
}

// ============================================================
// МОИ СОБЫТИЯ (Организатор)
// ============================================================
async function showMyEvents() {
    document.getElementById('pageContent').innerHTML = `<h1 class="page-title">Мои события</h1><div class="loader"></div>`;
    try {
        const params = new URLSearchParams({ page: 0, size: 100, organizer: state.email });
        const response = await apiCall(`/events?${params.toString()}`);
        const myEvents = response.content || [];
        const container = document.getElementById('pageContent');

        if (myEvents.length === 0) {
            container.innerHTML = `<div class="empty-state"><h3>У вас пока нет событий</h3><button class="btn btn-primary mt-4" onclick="openCreateEventModal()">+ Создать</button></div>`;
            return;
        }

        container.innerHTML = `
            <h1 class="page-title">Мои события</h1>
            <div class="events-grid">
                ${myEvents.map(e => {
                    const cover = e.coverImageUrl ? `background-image: url('${escapeHtml(e.coverImageUrl)}')` : `background: ${getPlaceholderGradient(e.name)}`;
                    return `
                        <div class="event-card" onclick="openEventDetail(${e.id})">
                            <div class="event-cover" style="${cover}"><div class="event-type">${getTypeLabel(e.type)}</div></div>
                            <div class="event-body">
                                <div class="event-name">${escapeHtml(e.name)}</div>
                                <div class="event-meta"><span>📅 ${formatDateShort(e.eventDate)}</span></div>
                            </div>
                        </div>
                    `;
                }).join('')}
            </div>
        `;
    } catch (e) {
        document.getElementById('pageContent').innerHTML = `<div class="empty-state"><h3>Ошибка загрузки</h3></div>`;
    }
}

// ============================================================
// СОЗДАНИЕ / РЕДАКТИРОВАНИЕ СОБЫТИЯ
// ============================================================
function openCreateEventModal() {
    state.editingEvent = null;
    renderEventForm();
}

async function openEditEventModal(id) {
    openModal('<div class="loader"></div>');
    try {
        const event = await apiCall(`/eventById/${id}`);
        state.editingEvent = event;
        renderEventForm(event);
    } catch (e) { closeModal(); }
}

function renderEventForm(event = null) {
    const isEdit = !!event;
    const imageUrls = event?.imageUrls?.length > 0 ? event.imageUrls : [''];

    openModal(`
        <div class="modal-header">
            <div class="modal-title">${isEdit ? 'Редактировать событие' : 'Создать событие'}</div>
            <button class="modal-close" onclick="closeModal()">✕</button>
        </div>
        <div class="modal-body">
            <div class="form-group">
                <label class="form-label">Название *</label>
                <input type="text" class="form-input" id="eName" maxlength="100" value="${escapeHtml(event?.name || '')}">
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Тип *</label>
                    <select class="form-select" id="eType">
                        ${Object.entries(TYPE_LABELS).map(([val, label]) => `<option value="${val}" ${event?.type === val ? 'selected' : ''}>${label}</option>`).join('')}
                    </select>
                </div>
                <div class="form-group">
                    <label class="form-label">Дата и время *</label>
                    <input type="datetime-local" class="form-input" id="eDate">
                </div>
            </div>
            <div class="form-group">
                <label class="form-label">Место *</label>
                <input type="text" class="form-input" id="eLoc" maxlength="200" value="${escapeHtml(event?.location || '')}">
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Цена (₽) *</label>
                    <input type="number" class="form-input" id="ePrice" min="0" value="${event?.price || 0}">
                </div>
                <div class="form-group">
                    <label class="form-label">Макс. участников *</label>
                    <input type="number" class="form-input" id="eMax" min="1" value="${event?.maxParticipants || 10}">
                </div>
            </div>
            <div class="form-group">
                <label class="form-label">Описание *</label>
                <textarea class="form-textarea" id="eDesc" maxlength="3000">${escapeHtml(event?.description || '')}</textarea>
            </div>
            <div class="form-group">
                <label class="form-label">Изображения (URL)</label>
                <div id="imageInputs">
                    ${imageUrls.map(url => `<input type="url" class="form-input image-url-input mb-2" value="${escapeHtml(url)}" placeholder="https://...">`).join('')}
                </div>
                <button class="btn btn-secondary btn-sm mt-2" onclick="addImageInput()">+ Добавить</button>
            </div>
            <button class="btn btn-primary btn-block btn-lg" onclick="submitEventForm(${isEdit ? event.id : 'null'})">
                ${isEdit ? 'Сохранить' : 'Создать'}
            </button>
        </div>
    `);

    setTimeout(() => {
        document.getElementById('eName')?.focus();
        if (event?.eventDate) {
            const dt = new Date(event.eventDate);
            const local = new Date(dt.getTime() - dt.getTimezoneOffset() * 60000);
            document.getElementById('eDate').value = local.toISOString().slice(0, 16);
        }
    }, 100);
}

function addImageInput() {
    const container = document.getElementById('imageInputs');
    const input = document.createElement('input');
    input.type = 'url';
    input.className = 'form-input image-url-input mb-2';
    input.placeholder = 'https://...';
    container.appendChild(input);
}

async function submitEventForm(eventId) {
    const name = document.getElementById('eName').value.trim();
    const type = document.getElementById('eType').value;
    const dateVal = document.getElementById('eDate').value;
    const location = document.getElementById('eLoc').value.trim();
    const price = parseFloat(document.getElementById('ePrice').value) || 0;
    const maxParticipants = parseInt(document.getElementById('eMax').value) || 0;
    const description = document.getElementById('eDesc').value.trim();
    const imageUrls = Array.from(document.querySelectorAll('.image-url-input')).map(i => i.value.trim()).filter(Boolean);

    if (!name || !dateVal || !location || !description) return showToast('Заполните обязательные поля', 'warning');

    const payload = {
        name, type, eventDate: dateVal + ':00', location, price, maxParticipants, description, imageUrls
    };

    try {
        if (eventId) {
            await apiCall(`/updateEvent/${eventId}`, { method: 'PUT', body: JSON.stringify(payload) });
            showToast('Событие обновлено!', 'success');
        } else {
            await apiCall('/createEvent', { method: 'POST', body: JSON.stringify(payload) });
            showToast('Событие создано!', 'success');
        }
        closeModal();
        if (document.getElementById('eventsContainer')) loadEvents();
        else showMyEvents();
    } catch (e) {}
}

// ============================================================
// ИНИЦИАЛИЗАЦИЯ
// ============================================================
document.addEventListener('DOMContentLoaded', () => {
    renderHeader();
    showCatalog();
});