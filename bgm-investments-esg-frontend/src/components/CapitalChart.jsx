import {useState} from 'react';
import {CartesianGrid, Legend, Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis,} from 'recharts';

export default function CapitalChart({capitalData = [], returnsData = []}) {
    // mode: 'capital' (euro cumulati) | 'return' (% mensile)
    const [mode, setMode] = useState('capital');

    const data = mode === 'capital' ? capitalData : returnsData;
    const isEmpty = !Array.isArray(data) || data.length === 0;

    return (
        <div>
            <div style={{display: 'flex', gap: 8, marginBottom: 8, flexWrap: 'wrap'}}>
                <button
                    className="bgm-btn"
                    onClick={() => setMode('capital')}
                    disabled={mode === 'capital'}
                    title="Mostra l'andamento del capitale cumulato per scenario"
                >
                    Capitale cumulato
                </button>
                <button
                    className="bgm-btn"
                    onClick={() => setMode('return')}
                    disabled={mode === 'return'}
                    title="Mostra il rendimento (variazione percentuale mensile) per scenario"
                >
                    Rendimento mensile
                </button>
            </div>

            {isEmpty ? (
                <p style={{opacity: 0.8}}>Nessun dato disponibile per questo grafico.</p>
            ) : (
                <ResponsiveContainer width="100%" height={320}>
                    <LineChart data={data}>
                        <CartesianGrid strokeDasharray="3 3"/>
                        <XAxis dataKey="mese"/>
                        <YAxis
                            tickFormatter={(v) =>
                                mode === 'capital'
                                    ? (Number(v) || 0).toLocaleString(undefined, {maximumFractionDigits: 0})
                                    : `${(Number(v) * 100).toFixed(0)}%`
                            }
                        />
                        <Tooltip
                            contentStyle={{background: '#1f1f1f', border: 'none'}}
                            formatter={(v) =>
                                mode === 'capital'
                                    ? [
                                        (Number(v) || 0).toLocaleString(undefined, {
                                            minimumFractionDigits: 2,
                                            maximumFractionDigits: 2,
                                        }) + ' €',
                                        'Capitale',
                                    ]
                                    : [`${(Number(v) * 100).toFixed(2)}%`, 'Rendimento mensile']
                            }
                        />
                        <Legend/>
                        <Line type="monotone" dataKey="ottimistico" name="Ottimistico" stroke="#4ade80" dot={false}/>
                        <Line type="monotone" dataKey="neutro" name="Neutro" stroke="#60a5fa" dot={false}/>
                        <Line type="monotone" dataKey="pessimistico" name="Pessimistico" stroke="#f87171" dot={false}/>
                    </LineChart>
                </ResponsiveContainer>
            )}
        </div>
    );
}