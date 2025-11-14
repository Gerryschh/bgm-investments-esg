import {useEffect, useMemo, useState} from 'react';
import {Portfolios} from '../api/client';
import Spinner from '../components/Spinner';
import SimBox from '../components/SimBox';
import DonutChart from '../components/DonutChart';

// calcolo ESG medio ponderato per valore (quantità * prezzo); fallback a media semplice
function computeWeightedEsg(positions) {
    if (!positions || positions.length === 0) return {esgAvg: 0, totalValue: 0};

    let totalValue = 0;
    let weightedSum = 0;
    let havePrices = false;

    for (const p of positions) {
        const q = Number(p.quantita || 0);
        const price = Number(p.asset?.prezzo ?? 0);
        const esg = Number(p.asset?.esg ?? 0);

        const value = q * (isFinite(price) ? price : 0);
        if (value > 0) havePrices = true;

        totalValue += value;
        weightedSum += esg * (value || 1); // se manca prezzo, 1 evita di azzerare il contributo
    }

    if (havePrices && totalValue > 0) {
        return {esgAvg: weightedSum / totalValue, totalValue};
    }

    // fallback: media semplice
    const simpleAvg =
        positions.reduce((acc, p) => acc + Number(p.asset?.esg ?? 0), 0) /
        positions.length;
    return {esgAvg: simpleAvg || 0, totalValue};
}

export default function DashboardInvestimenti() {
    const [items, setItems] = useState([]); // portafogli base dal BE
    const [rows, setRows] = useState([]); // portafogli con metriche calcolate
    const [name, setName] = useState('');
    const [loading, setLoading] = useState(true);
    const [creating, setCreating] = useState(false);
    const [metricsLoading, setMetricsLoading] = useState(false);

    useEffect(() => {
        (async () => {
            try {
                setLoading(true);
                const data = await Portfolios.list();
                setItems(data || []);
            } finally {
                setLoading(false);
            }
        })();
    }, []);

    // quando cambia la lista, carico le posizioni di ogni portafoglio e calcolo l'ESG medio
    useEffect(() => {
        if (!items?.length) {
            setRows([]);
            return;
        }
        (async () => {
            setMetricsLoading(true);
            try {
                const enriched = await Promise.all(
                    items.map(async (p) => {
                        try {
                            const positions = await Portfolios.positions(p.id);
                            const {esgAvg, totalValue} =
                                computeWeightedEsg(positions);
                            return {...p, positions, esgAvg, totalValue};
                        } catch {
                            return {
                                ...p,
                                positions: [],
                                esgAvg: 0,
                                totalValue: 0,
                            };
                        }
                    })
                );
                setRows(enriched);
            } finally {
                setMetricsLoading(false);
            }
        })();
    }, [items]);

    async function create() {
        if (!name.trim() || creating) return;
        setCreating(true);
        const temp = {
            id: -Date.now(),
            nome: name.trim(),
            createdAt: new Date().toISOString(),
        };
        setItems((prev) => [temp, ...prev]);
        setName('');
        try {
            const created = await Portfolios.create(temp.nome);
            if (created?.id) {
                setItems((prev) =>
                    prev.map((p) => (p.id === temp.id ? created : p))
                );
            } else {
                const data = await Portfolios.list();
                setItems(data || []);
            }
        } finally {
            setCreating(false);
        }
    }

    // ESG medio totale ponderato per valore dei portafogli
    const totalEsg = useMemo(() => {
        if (!rows.length) return 0;
        const totalVal = rows.reduce(
            (acc, r) => acc + (r.totalValue || 0),
            0
        );
        if (totalVal > 0) {
            const sum = rows.reduce(
                (acc, r) =>
                    acc + (r.esgAvg || 0) * (r.totalValue || 0),
                0
            );
            return sum / totalVal;
        }
        // fallback: media semplice delle medie
        return (
            rows.reduce((acc, r) => acc + (r.esgAvg || 0), 0) /
            rows.length
        );
    }, [rows]);

    // palette condivisa
    const colors = ['#60a5fa', '#7bd389', '#fbbf24', '#f87171', '#a78bfa'];

    // 🔹 Donut 1: divisione capitale per portafoglio
    const capitalChartData = useMemo(
        () =>
            rows.map((p, i) => ({
                label: p.nome,
                value: Number(p.totalValue || 0),
                color: colors[i % colors.length],
            })),
        [rows]
    );

    // 🔹 Donut 2: distribuzione ESG per portafoglio
    const esgChartData = useMemo(
        () =>
            rows.map((p, i) => ({
                label: p.nome,
                value: Number(p.esgAvg || 0),
                color: colors[i % colors.length],
            })),
        [rows]
    );

    return (
        <div className="bgm-container" style={{padding: 16}}>
            <div className="bgm-card">
                <h3>Dashboard Investimenti</h3>

                <p style={{opacity: 0.85}}>
                    Qui puoi gestire tutti i tuoi portafogli e avere una panoramica
                    della loro <b>sostenibilità</b> (ESG) e del <b>capitale allocato</b>.
                    L’ESG medio è calcolato come <i>media ponderata per valore</i>{' '}
                    (quantità × prezzo).
                </p>

                {/* creazione */}
                <div
                    style={{
                        display: 'flex',
                        gap: 8,
                        flexWrap: 'wrap',
                        marginBottom: 12,
                    }}
                >
                    <input
                        className="bgm-input"
                        placeholder="Nome portafoglio"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        style={{minWidth: 220}}
                    />
                    <button
                        className="bgm-btn"
                        onClick={create}
                        disabled={!name.trim() || creating}
                    >
                        {creating ? 'Creazione…' : 'Crea portafoglio'}
                    </button>
                </div>

                {/* riepilogo */}
                {!loading && rows.length > 0 && (
                    <div
                        style={{
                            display: 'grid',
                            gap: 12,
                            gridTemplateColumns:
                                'repeat(auto-fit,minmax(260px,1fr))',
                            marginTop: 16,
                        }}
                    >
                        <SimBox
                            title="Totale Portafogli"
                            value={rows.length}
                            desc="Numero totale di portafogli gestiti"
                            color="#60a5fa"
                        />
                        <SimBox
                            title="ESG Medio Totale"
                            value={totalEsg.toFixed(1)}
                            desc="Media ponderata per valore dell’ESG dei tuoi portafogli (0–100)"
                            color="var(--bgm-gold)"
                        />
                    </div>
                )}

                {/* tabella */}
                {loading ? (
                    <div style={{marginTop: 16}}>
                        <Spinner/>
                    </div>
                ) : rows.length === 0 ? (
                    <p style={{opacity: 0.7, marginTop: 16}}>
                        Nessun portafoglio creato.
                    </p>
                ) : (
                    <table
                        className="table"
                        style={{marginTop: 16, minWidth: 760}}
                    >
                        <thead>
                        <tr>
                            <th>Nome</th>
                            <th title="Data di creazione del portafoglio">
                                Creato
                            </th>
                            <th title="Sostenibilità media calcolata sugli asset (ponderata per valore)">
                                ESG medio
                            </th>
                            <th title="Capitale iniziale stimato (somma di quantità × prezzo)">
                                Capitale
                            </th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        {rows.map((p) => (
                            <tr key={p.id}>
                                <td>{p.nome}</td>
                                <td>
                                    {p.createdAt
                                        ? new Date(
                                            p.createdAt
                                        ).toLocaleDateString()
                                        : '—'}
                                </td>
                                <td>
                                    {isFinite(p.esgAvg)
                                        ? p.esgAvg.toFixed(1)
                                        : '—'}
                                </td>
                                <td>{formatMoney(p.totalValue || 0)}</td>
                                <td>
                                    <a
                                        className="bgm-link"
                                        href={`/bgm-investments-esg-frontend/portfolios/${p.id}`}
                                        title="Apri il dettaglio"
                                    >
                                        Apri →
                                    </a>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}

                {/* 📊 Donut charts */}
                {!loading && rows.length > 0 && (
                    <div
                        style={{
                            marginTop: 24,
                            display: 'grid',
                            gap: 24,
                            gridTemplateColumns:
                                'repeat(auto-fit,minmax(280px,1fr))',
                        }}
                    >
                        {/* Donut capitale */}
                        <div>
                            <h4>Divisione del capitale tra portafogli</h4>
                            {metricsLoading ? (
                                <Spinner/>
                            ) : (
                                <DonutChart
                                    data={capitalChartData}
                                    valueFormatter={(v) =>
                                        formatMoney(Number(v) || 0)
                                    }
                                    titleFormatter={(p) => p?.label || ''}
                                />
                            )}
                        </div>

                        {/* Donut ESG */}
                        <div>
                            <h4>Distribuzione ESG per portafoglio</h4>
                            {metricsLoading ? (
                                <Spinner/>
                            ) : (
                                <DonutChart
                                    data={esgChartData}
                                    valueFormatter={(v) =>
                                        `${Number(v || 0).toFixed(1)} ESG`
                                    }
                                    titleFormatter={(p) => p?.label || ''}
                                />
                            )}
                        </div>
                    </div>
                )}

                {/* guida interpretativa */}
                {!loading && rows.length > 0 && (
                    <p
                        style={{
                            marginTop: 16,
                            fontSize: 15,
                            opacity: 0.9,
                        }}
                    >
                        🔍 <b>Come leggere questi grafici:</b> il grafico di
                        sinistra mostra come il tuo capitale è suddiviso tra i
                        diversi portafogli, mentre quello di destra confronta i
                        loro punteggi ESG medi (0 = basso, 100 = alto).
                    </p>
                )}
            </div>
        </div>
    );
}

function formatMoney(v) {
    return (
        (Number(v) || 0).toLocaleString(undefined, {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
        }) + ' €'
    );
}