import {Activity, BarChart3, Leaf, Network, ScatterChart, TrendingUp} from 'lucide-react';


export default function Dashboard() {
    return (
        <div className="bgm-container" style={{padding: 24, display: 'grid', gap: 16}}>
            <div className="bgm-card" style={{padding: 24}}>
                <h2 style={{marginTop: 0}}>Benvenuto nella piattaforma BGM Investments ESG</h2>
                <p style={{opacity: 0.85}}>
                    Questo strumento ti aiuta a <b>gestire e simulare portafogli di investimento</b> in base a criteri
                    di
                    <b> sostenibilità (ESG)</b>, rendimento e rischio.
                    <br/>
                    Anche se non hai conoscenze finanziarie, potrai capire in modo semplice come i tuoi investimenti si
                    comportano
                    nel tempo.
                </p>
            </div>

            <div
                className="bgm-card"
                style={{
                    display: 'grid',
                    gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))',
                    gap: 16,
                    padding: 24,
                }}
            >
                <InfoCard
                    icon={<Leaf size={30} color="var(--bgm-gold)"/>}
                    title="Cosa significa ESG"
                    text="ESG sta per Environment, Social, Governance: indica quanto un'azienda è sostenibile dal punto di vista ambientale e sociale. Un punteggio alto è un buon segno."
                />

                <InfoCard
                    icon={<TrendingUp size={30} color="#60a5fa"/>}
                    title="Rendimento atteso"
                    text="È la crescita media stimata del valore del tuo portafoglio nel tempo. Non è garantita, ma serve per capire il potenziale di guadagno."
                />

                <InfoCard
                    icon={<Activity size={30} color="#7bd389"/>}
                    title="Volatilità"
                    text="Misura quanto un investimento può variare nel tempo. Più è alta, maggiore è il rischio ma anche il potenziale guadagno."
                />

                <InfoCard
                    icon={<ScatterChart size={30} color="#60a5fa"/>}
                    title="Simulazione Monte Carlo"
                    text="Per ogni portafoglio vengono generati migliaia di scenari di mercato basati su estrazioni casuali, producendo una distribuzione realistica dei possibili rendimenti futuri."
                />

                <InfoCard
                    icon={<Network size={30} color="#a78bfa"/>}
                    title="Diversificazione"
                    text="Un portafoglio distribuito tra asset diversi tende a essere più stabile: la diversificazione riduce l’impatto negativo dei singoli titoli più volatili."
                />

                <InfoCard
                    icon={<BarChart3 size={30} color="#f87171"/>}
                    title="Scenari di rendimento"
                    text="Gli scenari pessimistico, neutro e ottimistico rappresentano rispettivamente il 5°, 50° e 95° percentile della distribuzione simulata di risultati."
                />

            </div>

            <div className="bgm-card" style={{padding: 24}}>
                <h3>Come usare la piattaforma</h3>
                <ul style={{opacity: 0.85, lineHeight: 1.7}}>
                    <li>
                        Vai in <b>Dashboard Investimenti</b> per creare e visualizzare i tuoi portafogli.
                    </li>
                    <li>
                        All’interno di ogni portafoglio, puoi <b>aggiungere asset</b> (azioni, fondi, ETF…) e vedere
                        grafici di
                        composizione.
                    </li>
                    <li>
                        Esegui una <b>simulazione ESG</b> per confrontare gli scenari di rendimento e sostenibilità.
                    </li>
                </ul>
            </div>
        </div>
    );
}

function InfoCard({icon, title, text}) {
    return (
        <div style={{border: '1px solid #222', borderRadius: 10, padding: 16, background: 'rgba(255,255,255,0.03)'}}>
            <div style={{display: 'flex', alignItems: 'center', gap: 12}}>
                {icon}
                <h4 style={{margin: 0}}>{title}</h4>
            </div>
            <p style={{marginTop: 8, opacity: 0.8}}>{text}</p>
        </div>
    );
}
