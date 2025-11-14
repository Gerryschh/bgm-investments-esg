import {useEffect, useMemo, useState} from 'react';
import {Admin, Assets} from '../api/client';
import Spinner from '../components/Spinner';
import SimBox from '../components/SimBox';

/* ----------------------------------------------
   POPUP CUSTOM ELIMINAZIONE
---------------------------------------------- */
function DeletePopup({show, onCancel, onConfirm}) {
    if (!show) return null;
    return (
        <div
            style={{
                position: "fixed",
                inset: 0,
                background: "rgba(0,0,0,.45)",
                display: "grid",
                placeItems: "center",
                zIndex: 9999
            }}
        >
            <div
                style={{
                    background: "var(--card-bg,#0b1b2b)",
                    padding: 20,
                    borderRadius: 14,
                    maxWidth: 360,
                    width: "90%",
                    textAlign: "center",
                    boxShadow: "0 10px 40px rgba(0,0,0,0.45)"
                }}
            >
                <h3 style={{margin: 0, marginBottom: 10}}>Elimina titolo?</h3>
                <p style={{opacity: 0.9}}>Questa operazione non può essere annullata.</p>

                <div style={{display: "flex", justifyContent: "center", gap: 12, marginTop: 18}}>
                    <button className="bgm-btn" onClick={onConfirm}>
                        Elimina
                    </button>

                    {/* 🔥 Annulla in rosso */}
                    <button
                        className="bgm-btn"
                        style={{
                            background: "#b84040",
                            border: "1px solid #b84040"
                        }}
                        onClick={onCancel}
                    >
                        Annulla
                    </button>
                </div>
            </div>
        </div>
    );
}

/* ----------------------------------------------
   FIELD INPUT  (UNICA MODIFICA FATTA)
---------------------------------------------- */
function Field({ id, label, hint, info, ...props }) {
    return (
        <div style={{ display: 'grid', gap: 6 }}>
            <label className="bgm-label" htmlFor={id} style={{ display: "flex", alignItems: "center", gap: 6 }}>
                {label}

                {/* ICONA INFO */}
                <span
                    style={{
                        width: 18,
                        height: 18,
                        display: "inline-flex",
                        alignItems: "center",
                        justifyContent: "center",
                        borderRadius: "50%",
                        background: "#1f3247",
                        color: "#d2e4f7",
                        cursor: "pointer",
                        fontSize: 12,
                        position: "relative"
                    }}
                >
                    i

                    {/* TOOLTIP */}
                    <span
                        style={{
                            visibility: "hidden",
                            opacity: 0,
                            transition: "opacity .2s",
                            position: "absolute",
                            bottom: "125%",
                            left: "50%",
                            transform: "translateX(-50%)",
                            background: "#0d1e2f",
                            color: "#fff",
                            padding: "8px 10px",
                            borderRadius: 6,
                            width: 220,
                            fontSize: 12,
                            zIndex: 1000,
                            textAlign: "center",
                            boxShadow: "0 4px 18px rgba(0,0,0,0.45)",
                        }}
                        className="tooltip-text"
                    >
                        {info}
                    </span>
                </span>
            </label>

            <style>
                {`
                    .bgm-label span:hover .tooltip-text {
                        visibility: visible !important;
                        opacity: 1 !important;
                    }
                `}
            </style>

            <input
                id={id}
                className="bgm-input"
                placeholder={hint || ""}
                {...props}
            />
        </div>
    );
}

/* ----------------------------------------------
    TABLE HEADER SORT
---------------------------------------------- */
function Th({label, active, dir, onClick}) {
    return (
        <th
            onClick={onClick}
            style={{cursor: 'pointer', whiteSpace: 'nowrap'}}
        >
            {label} {active ? (dir === 'asc' ? '↑' : '↓') : ''}
        </th>
    );
}

/* ----------------------------------------------
    FORMATTER
---------------------------------------------- */
function formatMoney(v) {
    return (
        (Number(v) || 0).toLocaleString(undefined, {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
        }) + ' €'
    );
}

/* ----------------------------------------------
    MAIN COMPONENT
---------------------------------------------- */
export default function AdminSecurities() {

    const [list, setList] = useState([]);
    const [filter, setFilter] = useState('');

    const [sortKey, setSortKey] = useState('ticker');
    const [sortDir, setSortDir] = useState('asc');

    const [saving, setSaving] = useState(false);
    const [loading, setLoading] = useState(true);
    const [formError, setFormError] = useState('');

    // Form nuovo titolo
    const [form, setForm] = useState({
        ticker: '',
        nome: '',
        settore: '',
        prezzo: 10,
        esg: 70,
        rendimentoMensile: 0.01,
        volatilitaMensile: 0.05,
    });

    // Popup delete
    const [deletePopup, setDeletePopup] = useState({show: false, id: null});

    // Inline editing
    const [editRow, setEditRow] = useState(null);
    const [editData, setEditData] = useState({});

    const updateField = (k, v) => {
        setFormError('');
        setForm(p => ({...p, [k]: v}));
    };

    const validTicker = t =>
        /^[A-Za-z0-9.\-_]{1,15}$/.test(String(t || '').trim());

    const resetForm = () => {
        setForm({
            ticker: '',
            nome: '',
            settore: '',
            prezzo: 10,
            esg: 70,
            rendimentoMensile: 0.01,
            volatilitaMensile: 0.05,
        });
        setFormError('');
    };

    /* ----------------------------
       LOAD LISTA
    ---------------------------- */
    async function load() {
        setLoading(true);
        try {
            const items = await Assets.list(true);
            setList(Array.isArray(items) ? items : []);
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        load();
    }, []);

    /* ----------------------------
       SALVA NUOVO TITOLO
    ---------------------------- */
    async function handleSaveNew() {
        const payload = {
            ticker: form.ticker.trim(),
            nome: form.nome.trim(),
            settore: form.settore.trim(),
            prezzo: Number(form.prezzo),
            esg: Number(form.esg),
            rendimentoMensile: Number(form.rendimentoMensile),
            volatilitaMensile: Number(form.volatilitaMensile),
            source: 'MANUAL',
            active: true,
        };

        if (!validTicker(payload.ticker))
            return setFormError('Ticker non valido.');
        if (!payload.nome)
            return setFormError('Il nome è obbligatorio.');

        const exists = list.some(
            a => a.ticker.toUpperCase() === payload.ticker.toUpperCase()
        );
        if (exists) return setFormError('Ticker già esistente.');

        setSaving(true);
        try {
            const created = await Admin.createSec(payload);
            if (created?.id) {
                setList(prev => [created, ...prev]);
            } else {
                await load();
            }
            resetForm();
        } catch (e) {
            const msg = String(e?.message || '').toLowerCase();
            if (msg.includes('403')) {
                setFormError('Non sei autorizzato a creare titoli.');
            } else {
                setFormError('Errore durante il salvataggio.');
            }
        } finally {
            setSaving(false);
        }
    }

    /* ----------------------------
        EDIT INLINE
    ---------------------------- */
    function startEdit(a) {
        setEditRow(a.id);
        setEditData({
            ticker: a.ticker,
            nome: a.nome,
            settore: a.settore,
            prezzo: a.prezzo,
            esg: a.esg,
            rendimentoMensile: a.rendimentoMensile,
            volatilitaMensile: a.volatilitaMensile,
        });
    }

    async function saveInline(id) {
        setSaving(true);
        setFormError('');

        try {
            const {ticker, ...changes} = editData; // 🔥 ticker NON modificabile
            const updated = await Admin.updateSec(id, {id, ...changes});

            setList(prev =>
                prev.map(a => (a.id === id ? {...a, ...updated} : a))
            );

            setEditRow(null);
        } catch {
            setFormError('Errore durante la modifica.');
        } finally {
            setSaving(false);
        }
    }

    /* ----------------------------
       DELETE POPUP
    ---------------------------- */
    function remove(id) {
        setDeletePopup({show: true, id});
    }

    async function confirmDelete() {
        const id = deletePopup.id;
        setSaving(true);
        try {
            await Admin.deleteSec(id);
            setList(prev => prev.filter(a => a.id !== id));
        } catch {
            alert("Errore durante l'eliminazione.");
        } finally {
            setSaving(false);
            setDeletePopup({show: false, id: null});
        }
    }

    /* ----------------------------
       SORT + FILTER
    ---------------------------- */
    function toggleSort(key) {
        if (key === sortKey) setSortDir(d => (d === 'asc' ? 'desc' : 'asc'));
        else {
            setSortKey(key);
            setSortDir('asc');
        }
    }

    const filtered = useMemo(() => {
        const q = filter.trim().toLowerCase();
        let arr = list;
        if (q) {
            arr = arr.filter(
                a =>
                    a.ticker.toLowerCase().includes(q) ||
                    a.nome.toLowerCase().includes(q) ||
                    (a.settore || '').toLowerCase().includes(q)
            );
        }

        arr = [...arr].sort((a, b) => {
            const dir = sortDir === 'asc' ? 1 : -1;
            const A = a[sortKey];
            const B = b[sortKey];
            if (A < B) return -1 * dir;
            if (A > B) return 1 * dir;
            return 0;
        });

        return arr;
    }, [list, filter, sortKey, sortDir]);

    const totalAssets = filtered.length;
    const avgESG =
        filtered.reduce((acc, a) => acc + Number(a.esg || 0), 0) /
        (filtered.length || 1);
    const avgVol =
        filtered.reduce(
            (acc, a) => acc + Number(a.volatilitaMensile || 0),
            0
        ) / (filtered.length || 1);

    /* ----------------------------
        RENDER
    ---------------------------- */
    return (
        <div className="bgm-container" style={{padding: 16}}>
            <div className="bgm-card" style={{display: 'grid', gap: 14}}>

                <h3 style={{margin: 0}}>Area Admin – Catalogo Titoli</h3>

                {/* KPI */}
                {!loading && (
                    <div
                        style={{
                            display: 'grid',
                            gap: 12,
                            gridTemplateColumns:
                                'repeat(auto-fit, minmax(260px, 1fr))',
                        }}
                    >
                        <SimBox
                            title="Titoli"
                            value={totalAssets}
                            color="#60a5fa"
                        />
                        <SimBox
                            title="ESG medio"
                            value={avgESG.toFixed(1)}
                            color="var(--bgm-gold)"
                        />
                        <SimBox
                            title="Volatilità media"
                            value={(avgVol * 100).toFixed(2) + '%'}
                            color="#fbbf24"
                        />
                    </div>
                )}

                {/* FORM NUOVO */}
                <div className="bgm-card" style={{display: 'grid', gap: 10}}>
                    <h4 style={{margin: 0}}>Nuovo titolo</h4>

                    <div
                        style={{
                            display: 'grid',
                            gridTemplateColumns:
                                '2fr 2fr 1fr 1fr 1fr 1fr 1fr',
                            gap: 10,
                            alignItems: 'end',
                        }}
                    >
                        <Field
                            id="ticker"
                            label="Ticker"
                            hint="es. AAPL"
                            info="Codice breve del titolo (es. AAPL per Apple)."
                            value={form.ticker}
                            onChange={e => updateField('ticker', e.target.value)}
                        />

                        <Field
                            id="nome"
                            label="Nome"
                            hint="es. Apple Inc."
                            info="Nome completo del titolo (es. Apple Inc.)."
                            value={form.nome}
                            onChange={e => updateField('nome', e.target.value)}
                        />

                        <Field
                            id="settore"
                            label="Settore"
                            hint="es. Tecnologia"
                            info="Categoria economica del titolo (es. Tecnologia)."
                            value={form.settore}
                            onChange={e => updateField('settore', e.target.value)}
                        />

                        <Field
                            id="prezzo"
                            label="Prezzo"
                            hint="180.00"
                            type="number"
                            step="0.01"
                            info="Prezzo di una singola unità del titolo (es. 180.00)."
                            value={form.prezzo}
                            onChange={e => updateField('prezzo', e.target.value)}
                        />

                        <Field
                            id="esg"
                            label="ESG"
                            hint="0 - 100"
                            type="number"
                            info="Valutazione di sostenibilità da 0 (bassa) a 100 (alta)."
                            value={form.esg}
                            onChange={e => updateField('esg', e.target.value)}
                        />

                        <Field
                            id="r"
                            label="r"
                            hint="0.012"
                            type="number"
                            step="0.001"
                            info="Rendimento medio mensile espresso in decimale (es. 0.012 = 1.2%)."
                            value={form.rendimentoMensile}
                            onChange={e => updateField('rendimentoMensile', e.target.value)}
                        />

                        <Field
                            id="sigma"
                            label="σ"
                            hint="0.045"
                            type="number"
                            step="0.001"
                            info="Volatilità media mensile espressa in decimale (es. 0.045 = 4.5%)."
                            value={form.volatilitaMensile}
                            onChange={e => updateField('volatilitaMensile', e.target.value)}
                        />
                    </div>

                    <div style={{display: 'flex', gap: 10}}>
                        <button
                            className="bgm-btn"
                            type="button"
                            onClick={handleSaveNew}
                            disabled={saving}
                        >
                            {saving ? 'Salvataggio…' : 'Salva titolo'}
                        </button>
                    </div>

                    {formError && (
                        <div style={{color: '#ff8a8a'}}>{formError}</div>
                    )}
                </div>

                {/* FILTRI */}
                <div style={{display: 'flex', gap: 8}}>
                    <input
                        className="bgm-input"
                        placeholder="Cerca titolo…"
                        value={filter}
                        onChange={e => setFilter(e.target.value)}
                        style={{minWidth: 260}}
                    />
                    <button
                        className="bgm-btn"
                        onClick={load}
                        disabled={loading}
                    >
                        Aggiorna
                    </button>
                </div>

                {/* TABELLA */}
                <div style={{overflowX: 'auto'}}>
                    <table
                        className="table"
                        style={{marginTop: 12, minWidth: 900}}
                    >
                        <thead>
                        <tr>
                            <Th
                                label="Ticker"
                                active={sortKey === 'ticker'}
                                dir={sortDir}
                                onClick={() => toggleSort('ticker')}
                            />
                            <Th
                                label="Nome"
                                active={sortKey === 'nome'}
                                dir={sortDir}
                                onClick={() => toggleSort('nome')}
                            />
                            <th>Settore</th>
                            <Th
                                label="Prezzo"
                                active={sortKey === 'prezzo'}
                                dir={sortDir}
                                onClick={() => toggleSort('prezzo')}
                            />
                            <Th
                                label="ESG"
                                active={sortKey === 'esg'}
                                dir={sortDir}
                                onClick={() => toggleSort('esg')}
                            />
                            <th>r</th>
                            <th>σ</th>
                            <th></th>
                        </tr>
                        </thead>

                        <tbody>
                        {loading ? (
                            <tr>
                                <td colSpan={8} style={{padding: 20}}>
                                    <Spinner/>
                                </td>
                            </tr>
                        ) : filtered.length === 0 ? (
                            <tr>
                                <td
                                    colSpan={8}
                                    style={{padding: 20, opacity: 0.7}}
                                >
                                    Nessun titolo.
                                </td>
                            </tr>
                        ) : (
                            filtered.map(a => (
                                <tr key={a.id}>
                                    {editRow === a.id ? (
                                        <>
                                            {/* TICKER NON MODIFICABILE */}
                                            <td>
                                                <input
                                                    className="bgm-input"
                                                    value={editData.ticker}
                                                    disabled
                                                    style={{
                                                        opacity: 0.6,
                                                        background: "#0d2237"
                                                    }}
                                                />
                                            </td>

                                            <td>
                                                <input
                                                    className="bgm-input"
                                                    value={editData.nome}
                                                    onChange={e =>
                                                        setEditData({
                                                            ...editData,
                                                            nome:
                                                            e.target.value,
                                                        })
                                                    }
                                                />
                                            </td>

                                            <td>
                                                <input
                                                    className="bgm-input"
                                                    value={editData.settore}
                                                    onChange={e =>
                                                        setEditData({
                                                            ...editData,
                                                            settore:
                                                            e.target
                                                                .value,
                                                        })
                                                    }
                                                />
                                            </td>

                                            <td>
                                                <input
                                                    className="bgm-input"
                                                    type="number"
                                                    value={editData.prezzo}
                                                    onChange={e =>
                                                        setEditData({
                                                            ...editData,
                                                            prezzo: Number(
                                                                e.target
                                                                    .value
                                                            ),
                                                        })
                                                    }
                                                />
                                            </td>

                                            <td>
                                                <input
                                                    className="bgm-input"
                                                    type="number"
                                                    value={editData.esg}
                                                    onChange={e =>
                                                        setEditData({
                                                            ...editData,
                                                            esg: Number(
                                                                e.target
                                                                    .value
                                                            ),
                                                        })
                                                    }
                                                />
                                            </td>

                                            <td>
                                                <input
                                                    className="bgm-input"
                                                    type="number"
                                                    value={
                                                        editData.rendimentoMensile
                                                    }
                                                    onChange={e =>
                                                        setEditData({
                                                            ...editData,
                                                            rendimentoMensile:
                                                                Number(
                                                                    e.target
                                                                        .value
                                                                ),
                                                        })
                                                    }
                                                />
                                            </td>

                                            <td>
                                                <input
                                                    className="bgm-input"
                                                    type="number"
                                                    value={
                                                        editData.volatilitaMensile
                                                    }
                                                    onChange={e =>
                                                        setEditData({
                                                            ...editData,
                                                            volatilitaMensile:
                                                                Number(
                                                                    e.target
                                                                        .value
                                                                ),
                                                        })
                                                    }
                                                />
                                            </td>

                                            <td>
                                                <button
                                                    className="bgm-btn"
                                                    onClick={() =>
                                                        saveInline(a.id)
                                                    }
                                                >
                                                    Salva
                                                </button>
                                                <button
                                                    className="bgm-btn"
                                                    onClick={() =>
                                                        setEditRow(null)
                                                    }
                                                >
                                                    Annulla
                                                </button>
                                            </td>
                                        </>
                                    ) : (
                                        <>
                                            <td>{a.ticker}</td>
                                            <td>{a.nome}</td>
                                            <td>{a.settore}</td>

                                            <td>{formatMoney(a.prezzo)}</td>
                                            <td>{a.esg}</td>

                                            <td>
                                                {(a.rendimentoMensile * 100).toFixed(2)}%
                                            </td>

                                            <td>
                                                {(a.volatilitaMensile * 100).toFixed(2)}%
                                            </td>

                                            <td>
                                                <div
                                                    style={{
                                                        display: 'flex',
                                                        gap: 6,
                                                        flexWrap: 'wrap',
                                                    }}
                                                >
                                                    <button
                                                        className="bgm-btn"
                                                        onClick={() =>
                                                            startEdit(a)
                                                        }
                                                    >
                                                        Modifica
                                                    </button>

                                                    <button
                                                        className="bgm-btn"
                                                        onClick={() =>
                                                            remove(a.id)
                                                        }
                                                    >
                                                        Elimina
                                                    </button>
                                                </div>
                                            </td>
                                        </>
                                    )}
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* POPUP ELIMINAZIONE */}
            <DeletePopup
                show={deletePopup.show}
                onCancel={() =>
                    setDeletePopup({show: false, id: null})
                }
                onConfirm={confirmDelete}
            />
        </div>
    );
}