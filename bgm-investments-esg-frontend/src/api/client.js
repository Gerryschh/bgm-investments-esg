// /src/api/client.js
//
// Autenticazione via header custom JSESSIONID (apiKey) con proxy Vite:
//   - BASE = '/api' (vite proxy lo riscrive a /bgm-investments-esg-backend/v1)
//   - salva/legge SOLO 'JSESSIONID' in localStorage
//   - invia l'header 'JSESSIONID' su ogni richiesta
//   - /auth/me tollera 401
//   - nome export: Assets (non Securities)

const BASE = '/api';
const LS_KEY = 'JSESSIONID';

(function migrateLegacyKey() {
    try {
        const legacy = localStorage.getItem('JSessionid');
        if (legacy && !localStorage.getItem(LS_KEY)) {
            localStorage.setItem(LS_KEY, legacy);
        }
        localStorage.removeItem('JSessionid');
    } catch {
    }
})();

function getSessionId() {
    try {
        return localStorage.getItem(LS_KEY);
    } catch {
        return null;
    }
}

function setSessionId(token) {
    try {
        token ? localStorage.setItem(LS_KEY, token) : localStorage.removeItem(LS_KEY);
    } catch {
    }
}

function withTimeout(ms, signal) {
    const ctrl = new AbortController();
    const id = setTimeout(() => ctrl.abort('timeout'), ms);
    const cleanup = () => clearTimeout(id);
    const composed = new AbortController();
    const onAbort = () => composed.abort(signal?.reason || 'aborted');
    if (signal) signal.addEventListener('abort', onAbort, {once: true});
    // fan-in
    const forward = (e) => composed.abort(e?.message || e || 'timeout');
    ctrl.signal.addEventListener('abort', forward, {once: true});
    return {signal: composed.signal, cleanup};
}

async function parseResponse(res) {
    if (res.status === 204) return null;
    const ct = res.headers.get('content-type') || '';
    if (ct.includes('application/json')) return res.json();
    const text = await res.text();
    try {
        return JSON.parse(text);
    } catch {
        return text;
    }
}

export async function api(
    path,
    options = {},
    {tolerate401 = false, tolerateAuthErrors = false, signal, timeout = 15000} = {}
) {
    const headers = {'Content-Type': 'application/json', ...(options.headers || {})};
    const sid = getSessionId();
    if (sid) headers['JSESSIONID'] = sid;

    const {signal: sig, cleanup} = withTimeout(timeout, signal);
    try {
        const res = await fetch(`${BASE}${path}`, {
            method: options.method || 'GET',
            headers,
            body: options.body,
            signal: sig
        });
        if (!res.ok) {
            if (res.status === 401 && (tolerate401 || tolerateAuthErrors)) return null;
            if (res.status === 403 && tolerateAuthErrors) return null;
            const msg = await res.text().catch(() => res.statusText);
            const err = new Error(msg || `HTTP ${res.status}`);
            err.status = res.status;
            throw err;
        }
        return parseResponse(res);
    } finally {
        cleanup();
    }
}

// normalizza utente BE -> FE
function normalizeUser(u) {
    if (!u) return null;
    return {
        id: u.id,
        userName: u.nome,
        email: u.email,
        role: u.ruolo
    };
}

// === Autenticazione ===
export const Auth = {
    async login(email, password) {
        const resp = await api('/auth/login', {
            method: 'POST',
            body: JSON.stringify({email, password})
        });

        if (resp && typeof resp.sessionId === 'string' && resp.sessionId.trim() !== '') {
            setSessionId(resp.sessionId.trim());
        }

        return normalizeUser(resp?.user ?? null);
    },

    async me() {
        try {
            const u = await api('/auth/me', {}, { tolerateAuthErrors: true });
            return normalizeUser(u);
        } catch {
            return null;
        }
    },

    async logout() {
        try {
            await api('/auth/logout', {method: 'POST'}, {tolerate401: true});
        } finally {
            setSessionId(null);
        }
    }
};

// === Assets (catalogo titoli) ===
export const Assets = {
    list: async (activeOnly = true) => {
        const r = await api(`/assets?activeOnly=${activeOnly}`);
        return r?.items || [];
    },
    get: (id) => api(`/assets/${id}`)
};

// === Admin (gestione asset) ===
export const Admin = {
    createSec: (d) => api('/admin/assets', {method: 'POST', body: JSON.stringify(d)}),
    updateSec: (id, d) => api(`/admin/assets/${id}`, {method: 'PUT', body: JSON.stringify(d)}),
    deleteSec: (id) => api(`/admin/assets/${id}`, {method: 'DELETE'})
};

// === Portafogli e posizioni ===
export const Portfolios = {
    list: async () => {
        const r = await api('/portfolios');
        return r?.items || [];
    },
    create: (name) => api('/portfolios', {method: 'POST', body: JSON.stringify({nome: name})}),
    positions: async (id) => {
        const r = await api(`/portfolios/${id}/positions`);
        return r?.items || [];
    },
    addPosition: (id, assetId, quantita) =>
        api(`/portfolios/${id}/positions`, {method: 'POST', body: JSON.stringify({assetId, quantita})}),
    removePosition: (portfolioId, positionId) =>
        api(`/portfolios/${portfolioId}/positions/${positionId}`, {method: 'DELETE'})
};

// === Simulazioni ===
export const Simulations = {
    run: (portfolioId, months) =>
        api(`/portfolios/${portfolioId}/simulation`, {method: 'POST', body: JSON.stringify({mesi: months})})
};