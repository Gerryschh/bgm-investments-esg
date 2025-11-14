import {useState} from 'react';
import {Auth} from '../api/client';

export default function Login({onLogged}) {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [err, setErr] = useState('');
    const [loading, setLoading] = useState(false);

    async function submit(e) {
        e.preventDefault();
        setErr('');
        setLoading(true);
        try {
            const u = await Auth.login(email, password);
            // se il backend ritorna un oggetto utente con id
            if (!u || !u.id) throw new Error('Login fallito');
            onLogged && onLogged(u);
        } catch (ex) {
            console.error('Login error:', ex);
            setErr('Credenziali non valide');
        } finally {
            setLoading(false);
        }
    }

    return (
        <div
            className="bgm-container"
            style={{
                minHeight: '100vh',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                padding: 16,
            }}
        >
            <div style={{maxWidth: 420, width: '100%', textAlign: 'center'}}>
                {/* LOGO CENTRALE */}
                <img
                    src="/bgm-investments-esg-frontend/logo-bgm.png"
                    alt="Banca BGM"
                    style={{height: 150, marginBottom: 32}}
                />

                {/* BOX CON IL FORM */}
                <div className="bgm-card" style={{textAlign: 'left'}}>
                    <h2 style={{marginTop: 0, marginBottom: 16}}>Accesso Area Riservata</h2>

                    <form
                        onSubmit={submit}
                        noValidate
                        style={{display: 'grid', gap: 10}}
                    >
                        <div style={{display: 'grid', gap: 6}}>
                            <label htmlFor="email">Email</label>
                            <input
                                id="email"
                                className="bgm-input"
                                placeholder="Inserisci la tua email"
                                value={email}
                                onChange={e => setEmail(e.target.value)}
                                type="email"
                                required
                            />
                        </div>

                        <div style={{display: 'grid', gap: 6, marginTop: 8}}>
                            <label htmlFor="password">Password</label>
                            <input
                                id="password"
                                className="bgm-input"
                                placeholder="Inserisci la tua password"
                                value={password}
                                onChange={e => setPassword(e.target.value)}
                                type="password"
                                required
                            />
                        </div>

                        <div
                            style={{
                                marginTop: 16,
                                display: 'flex',
                                alignItems: 'center',
                                gap: 10,
                            }}
                        >
                            <button className="bgm-btn" disabled={loading}>
                                {loading ? 'Entrando…' : 'Entra'}
                            </button>
                            {loading && (
                                <div
                                    className="spin"
                                    style={{
                                        width: 18,
                                        height: 18,
                                        border: '3px solid #0002',
                                        borderTopColor: 'var(--bgm-gold)',
                                        borderRadius: '50%',
                                        animation: 'spin 1s linear infinite',
                                    }}
                                />
                            )}
                        </div>

                        {err && (
                            <div style={{marginTop: 12, color: '#ffb4b4'}}>{err}</div>
                        )}
                    </form>
                </div>
            </div>

            <style>{`@keyframes spin{to{transform:rotate(360deg)}}`}</style>
        </div>
    );
}