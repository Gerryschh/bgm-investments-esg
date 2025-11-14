import {useEffect, useMemo, useRef, useState} from 'react';
import {Assets, Portfolios, Simulations} from '../api/client';

import Spinner from '../components/Spinner';
import SimBox from '../components/SimBox';
import DonutChart from '../components/DonutChart';
import CapitalChart from '../components/CapitalChart';

export default function PortfolioDetail() {
    const id = Number(location.pathname.split('/').pop());
    const [positions, setPositions] = useState([]);
    const [catalog, setCatalog] = useState([]);
    const [assetId, setAssetId] = useState(null);
    const [q, setQ] = useState(1);
    const [months, setMonths] = useState(12);
    const [loading, setLoading] = useState(true);
    const [posting, setPosting] = useState(false);
    const [simLoading, setSimLoading] = useState(false);
    const [simReport, setSimReport] = useState(null);
    const abortRef = useRef(null);

    // caricamento iniziale
    async function load() {
        const ctrl = new AbortController();
        abortRef.current = ctrl;
        try {
            const [pos, assets] = await Promise.all([
                Portfolios.positions(id, {signal: ctrl.signal}),
                Assets.list(true, {signal: ctrl.signal}),
            ]);
            setPositions(pos || []);
            setCatalog(assets || []);
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        load();
        return () => abortRef.current?.abort();
    }, []);

    // metriche riepilogo portafoglio
    const metrics = useMemo(() => {
        const totale = positions.reduce(
            (a, p) => a + (Number(p.quantita) || 0) * (Number(p.asset?.prezzo) || 0),
            0
        );
        const esg =
            positions.length > 0
                ? positions.reduce((a, p) => a + (Number(p.asset?.esg) || 0), 0) / positions.length
                : 0;
        const vol =
            positions.length > 0
                ? positions.reduce((a, p) => a + (Number(p.asset?.volatilitaMensile) || 0), 0) /
                positions.length
                : 0;
        return {totale, esg, vol};
    }, [positions]);

    // aggiunta asset
    async function add() {
        if (!assetId || !q || posting) return;
        setPosting(true);
        const a = catalog.find((x) => x.id === assetId);
        const temp = {id: -Date.now(), quantita: q, asset: a};
        setPositions((p) => [temp, ...p]);
        try {
            await Portfolios.addPosition(id, a.id, q);
            const refreshed = await Portfolios.positions(id);
            setPositions(refreshed || []);
        } finally {
            setPosting(false);
        }
    }

    async function remove(pid) {
        const prev = positions;
        setPositions((p) => p.filter((x) => x.id !== pid));
        try {
            await Portfolios.removePosition(id, pid);
        } catch {
            setPositions(prev);
        }
    }

    // simulazione
    async function simulate() {
        setSimLoading(true);
        try {
            const res = await Simulations.run(id, Number(months));
            setSimReport({...(res || {}), _months: Number(months) || 12});
        } finally {
            setSimLoading(false);
        }
    }

    // dati composizione
    const compositionData = useMemo(() => {
        const colors = ['#60a5fa', '#fbbf24', '#7bd389', '#d87cc8', '#f87171', '#93c5fd', '#a78bfa'];
        return positions.map((p, i) => ({
            label: p.asset?.ticker || `Asset ${i + 1}`,
            value: Number(p.quantita) || 0,
            color: colors[i % colors.length],
        }));
    }, [positions]);

    // Serie capitale e rendimento per grafici
    const capitalSeries = useMemo(() => buildCapitalSeries(simReport, metrics.totale, months), [
        simReport,
        metrics.totale,
        months,
    ]);
    const returnsSeries = useMemo(() => buildReturnsSeries(simReport, months), [simReport, months]);

    return (
        <div className="bgm-container" style={{padding: 16}}>
            <div className="bgm-card">
                <h2 style={{marginTop: 0}}>Portafoglio #{id}</h2>

                {/* sezione aggiunta asset */}
                <div style={{display: 'flex', flexWrap: 'wrap', gap: 8, marginBottom: 16}}>
                    <select
                        className="bgm-input"
                        value={assetId || ''}
                        onChange={(e) => setAssetId(Number(e.target.value) || null)}
                        style={{minWidth: 320}}
                        title="Seleziona l'asset da aggiungere al portafoglio"
                    >
                        <option value="">Seleziona un asset…</option>
                        {catalog.map((a) => {
                            const giaPresente = positions.some((p) => p.asset?.id === a.id);
                            return (
                                <option key={a.id} value={a.id} disabled={giaPresente}>
                                    {a.ticker} — {a.nome} | ESG {a.esg ?? '-'} |
                                    Vol {((a.volatilitaMensile || 0) * 100).toFixed(2)}% |
                                    Prezzo {formatMoney(a.prezzo)}
                                    {giaPresente ? ' (già presente)' : ''}
                                </option>
                            );
                        })}
                    </select>
                    <input
                        type="number"
                        className="bgm-input"
                        min="1"
                        value={q}
                        onChange={(e) => setQ(Number(e.target.value) || 1)}
                        style={{width: 100}}
                        title="Quantità di unità da aggiungere"
                    />
                    <button className="bgm-btn" onClick={add} disabled={!assetId || posting}>
                        {posting ? 'Aggiungo…' : 'Aggiungi'}
                    </button>
                </div>

                {/* tabella posizioni */}
                {loading ? (
                    <Spinner/>
                ) : positions.length === 0 ? (
                    <p style={{opacity: 0.7}}>Nessun asset presente in questo portafoglio.</p>
                ) : (
                    <div style={{overflowX: 'auto'}}>
                        <table className="table" style={{minWidth: 760}}>
                            <thead>
                            <tr>
                                <th>Asset</th>
                                <th>Quantità</th>
                                <th>Prezzo</th>
                                <th>Importo totale</th>
                                <th>ESG</th>
                                <th>Volatilità</th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            {positions.map((p) => (
                                <tr key={p.id}>
                                    <td>{p.asset?.ticker}</td>
                                    <td>{p.quantita}</td>
                                    <td>{formatMoney(p.asset?.prezzo)}</td>
                                    <td>{formatMoney((p.asset?.prezzo || 0) * (p.quantita || 0))}</td>
                                    <td>{p.asset?.esg?.toFixed?.(1) ?? '—'}</td>
                                    <td>{((p.asset?.volatilitaMensile || 0) * 100).toFixed(2)}%</td>
                                    <td>
                                        <button className="bgm-btn small" onClick={() => remove(p.id)}>
                                            Rimuovi
                                        </button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}

                {/* composizione + indicatori */}
                <div
                    style={{
                        marginTop: 20,
                        display: 'grid',
                        gap: 16,
                        gridTemplateColumns: 'minmax(320px,1fr) 1fr',
                    }}
                >
                    <div>
                        <h4>Composizione del portafoglio</h4>
                        <DonutChart data={compositionData}/>
                    </div>
                    <div className="bgm-card" style={{display: 'grid', gap: 12, alignContent: 'start'}}>
                        <h4 style={{marginTop: 0}}>Valore & Indicatori</h4>
                        <SimBox
                            title="Importo totale investito"
                            value={formatMoney(metrics.totale)}
                            desc="Somma di (quantità × prezzo) per tutti gli asset"
                            color="#60a5fa"
                        />
                        <SimBox
                            title="ESG medio portafoglio"
                            value={metrics.esg.toFixed(1)}
                            desc="Media dei punteggi ESG degli asset (0–100)"
                            color="var(--bgm-gold)"
                        />
                        <SimBox
                            title="Volatilità media"
                            value={(metrics.vol * 100).toFixed(2) + '%'}
                            desc="Indicatore del rischio medio: più alto = oscillazioni maggiori"
                            color="#fbbf24"
                        />
                    </div>
                </div>

                {/* simulazione */}
                <div style={{display: 'flex', gap: 10, marginTop: 24, flexWrap: 'wrap'}}>
                    <input
                        className="bgm-input"
                        type="number"
                        min="1"
                        value={months}
                        onChange={(e) => setMonths(e.target.value)}
                        style={{maxWidth: 120}}
                        title="Numero di mesi per la simulazione"
                    />
                    <button className="bgm-btn" onClick={simulate} disabled={simLoading}>
                        {simLoading ? 'Simulazione…' : `Simula (${months} mesi)`}
                    </button>
                    {simLoading && <Spinner size={16}/>}
                </div>

                {/* risultati simulazione */}
                {simReport && (
                    <div className="bgm-card" style={{marginTop: 16}}>
                        <h3 style={{marginTop: 0}}>Risultati simulazione</h3>
                        <p style={{opacity: 0.8}}>
                            Scegli se visualizzare il <b>capitale cumulato</b> o il <b>rendimento mensile</b> per i tre
                            scenari.
                        </p>

                        {/* riepilogo scenari */}
                        {capitalSeries?.length > 0 && (
                            <div
                                style={{
                                    display: 'grid',
                                    gap: 12,
                                    gridTemplateColumns: 'repeat(auto-fit,minmax(260px,1fr))',
                                }}
                            >
                                <SimBox
                                    title="Capitale finale – Ottimistico"
                                    value={formatMoney(capitalSeries.at(-1).ottimistico)}
                                    desc="Scenario di mercato positivo"
                                    color="#4ade80"
                                />
                                <SimBox
                                    title="Capitale finale – Neutro"
                                    value={formatMoney(capitalSeries.at(-1).neutro)}
                                    desc="Scenario medio"
                                    color="#60a5fa"
                                />
                                <SimBox
                                    title="Capitale finale – Pessimistico"
                                    value={formatMoney(capitalSeries.at(-1).pessimistico)}
                                    desc="Scenario negativo"
                                    color="#f87171"
                                />
                            </div>
                        )}

                        {/* grafico */}
                        <div style={{marginTop: 20}}>
                            <h4>Andamento nel tempo</h4>
                            <CapitalChart capitalData={capitalSeries || []} returnsData={returnsSeries || []}/>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

/* ==== funzioni helper ==== */
function formatMoney(v) {
    if (!isFinite(v)) return '—';
    return v.toLocaleString(undefined, {minimumFractionDigits: 2, maximumFractionDigits: 2}) + ' €';
}

function normRate(v) {
    if (v == null || isNaN(v)) return 0;
    const num = Number(v);
    return num > 1 ? num / 100 : num;
}

function buildCapitalSeries(simReport, totale, months) {
    if (!simReport) return [];
    const start = Math.max(totale, 0);
    const monthsCount = Number(simReport._months || months || 12);
    const beHist = Array.isArray(simReport.storico) ? simReport.storico : null;
    if (beHist && beHist.length > 0) {
        let co = start, cn = start, cp = start;
        return beHist.map((m, i) => {
            const ro = normRate(m.ottimistico);
            const rn = normRate(m.neutro);
            const rp = normRate(m.pessimistico);
            co *= 1 + ro;
            cn *= 1 + rn;
            cp *= 1 + rp;
            return {mese: m.mese || `Mese ${i + 1}`, ottimistico: co, neutro: cn, pessimistico: cp};
        });
    }
    const rO = normRate(simReport.scenarioOttimistico);
    const rN = normRate(simReport.scenarioNeutro);
    const rP = normRate(simReport.scenarioPessimistico);
    let co = start, cn = start, cp = start;
    return Array.from({length: monthsCount}, (_, i) => {
        co *= 1 + rO;
        cn *= 1 + rN;
        cp *= 1 + rP;
        return {mese: `Mese ${i + 1}`, ottimistico: co, neutro: cn, pessimistico: cp};
    });
}

function buildReturnsSeries(simReport, months) {
    if (!simReport) return [];
    const monthsCount = Number(simReport._months || months || 12);
    const beHist = Array.isArray(simReport.storico) ? simReport.storico : null;
    if (beHist && beHist.length > 0) {
        return beHist.map((m, i) => ({
            mese: m.mese || `Mese ${i + 1}`,
            ottimistico: normRate(m.ottimistico),
            neutro: normRate(m.neutro),
            pessimistico: normRate(m.pessimistico),
        }));
    }
    const rO = normRate(simReport.scenarioOttimistico);
    const rN = normRate(simReport.scenarioNeutro);
    const rP = normRate(simReport.scenarioPessimistico);
    return Array.from({length: monthsCount}, (_, i) => ({
        mese: `Mese ${i + 1}`,
        ottimistico: rO,
        neutro: rN,
        pessimistico: rP,
    }));
}