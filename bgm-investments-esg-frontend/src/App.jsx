import {useEffect, useState} from 'react';
import {BrowserRouter, Navigate, Route, Routes, useLocation} from 'react-router-dom';
import './styles/theme.css';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import DashboardInvestimenti from './pages/DashboardInvestimenti';
import PortfolioDetail from './pages/PortfolioDetail';
import AdminSecurities from './pages/AdminSecurities';
import {Auth} from './api/client';
import Guard from './components/Guard';
import Nav from './components/Nav';

/* --------- OVERLAY SPINNER GLOBALE ---------- */

function SpinnerOverlay({show, text = 'Caricamento...'}) {
    if (!show) return null;
    return (
        <div
            style={{
                position: 'fixed',
                inset: 0,
                background: 'rgba(0,0,0,.25)',
                display: 'grid',
                placeItems: 'center',
                zIndex: 9999,
            }}
        >
            <div
                style={{
                    background: 'var(--card-bg,#111)',
                    color: 'white',
                    padding: 16,
                    borderRadius: 12,
                    boxShadow: '0 8px 30px rgba(0,0,0,.3)',
                    display: 'flex',
                    gap: 12,
                    alignItems: 'center',
                }}
            >
                <div
                    className="spin"
                    aria-hidden
                    style={{
                        width: 22,
                        height: 22,
                        border: '3px solid #fff3',
                        borderTopColor: 'var(--bgm-gold,#d4af37)',
                        borderRadius: '50%',
                        animation: 'spin 1s linear infinite',
                    }}
                />
                <b>{text}</b>
            </div>
        </div>
    );
}

/* --------- FOOTER ---------- */

function Footer() {
    return (
        <footer
            style={{
                marginTop: 'auto',
                padding: '12px 24px',
                borderTop: '1px solid var(--border)',
                fontSize: 13,
                opacity: 0.8,
                textAlign: 'center',
                background: 'rgba(0,0,0,0.25)',
            }}
        >
            © {new Date().getFullYear()} Banca BGM · Piattaforma Investimenti ESG (demo)
        </footer>
    );
}

/* --------- POPUP LOGOUT ---------- */

function LogoutPopup({show, onConfirm}) {
    if (!show) return null;
    return (
        <div
            style={{
                position: 'fixed',
                inset: 0,
                background: 'rgba(0,0,0,.4)',
                display: 'grid',
                placeItems: 'center',
                zIndex: 9999,
            }}
        >
            <div
                style={{
                    background: 'var(--card-bg,#0b1b2b)',
                    padding: 20,
                    borderRadius: 14,
                    boxShadow: '0 10px 40px rgba(0,0,0,0.45)',
                    maxWidth: 360,
                    width: '90%',
                    textAlign: 'center',
                }}
            >
                <h3 style={{marginTop: 0, marginBottom: 8}}>Logout effettuato</h3>
                <p style={{margin: '0 0 18px'}}>
                    Hai effettuato il logout correttamente.
                </p>
                <button className="bgm-btn" onClick={onConfirm}>
                    Torna al login
                </button>
            </div>
        </div>
    );
}

/* --------- APP PRINCIPALE ---------- */

export default function App() {
    const [user, setUser] = useState(null);
    const [bootLoading, setBootLoading] = useState(true);
    const [globalLoading, setGlobalLoading] = useState(false);
    const [showLogoutPopup, setShowLogoutPopup] = useState(false);

    useEffect(() => {
        (async () => {
            const u = await Auth.me(); // ritorna null se 401 / no token
            setUser(u);
            setBootLoading(false);
        })();
    }, []);

    async function handleLogout() {
        setGlobalLoading(true);
        try {
            await Auth.logout();
        } catch (err) {
            console.error('Logout error:', err);
        } finally {
            setGlobalLoading(false);
            setUser(null);
            setShowLogoutPopup(true); // mostro popup, poi porta al login
        }
    }

    // Se admin: mostra solo Admin e redirect automatico
    const AdminOnlyShell = () => (
        <>
            <Nav user={user} onLogout={handleLogout}/>
            <div className="bgm-container" style={{paddingTop: 12}}>
                <Routes>
                    <Route
                        path="/admin"
                        element={
                            <Guard user={user && user.role === 'ADMIN'}>
                                <AdminSecurities setGlobalLoading={setGlobalLoading}/>
                            </Guard>
                        }
                    />
                    {/* qualunque altra rotta → /admin */}
                    <Route path="*" element={<Navigate to="/admin" replace/>}/>
                </Routes>
            </div>
        </>
    );

    const NormalShell = () => {
        const location = useLocation();
        const isLogin = location.pathname === '/login';

        return (
            <>
                {/* Nav nascosta sulla pagina di login */}
                {!isLogin && <Nav user={user} onLogout={handleLogout}/>}

                <div className="bgm-container" style={{paddingTop: isLogin ? 0 : 12}}>
                    <Routes>
                        <Route
                            path="/login"
                            element={
                                <Login
                                    onLogged={u => {
                                        setUser(u);
                                        window.location.href = '/';
                                    }}
                                />
                            }
                        />
                        <Route
                            path="/"
                            element={
                                <Guard user={user}>
                                    <Dashboard user={user}/>
                                </Guard>
                            }
                        />
                        <Route
                            path="/portfolios"
                            element={
                                <Guard user={user}>
                                    <DashboardInvestimenti setGlobalLoading={setGlobalLoading}/>
                                </Guard>
                            }
                        />
                        <Route
                            path="/portfolios/:id"
                            element={
                                <Guard user={user}>
                                    <PortfolioDetail setGlobalLoading={setGlobalLoading}/>
                                </Guard>
                            }
                        />
                        <Route
                            path="/admin"
                            element={
                                <Guard user={user && user.role === 'ADMIN'}>
                                    <AdminSecurities setGlobalLoading={setGlobalLoading}/>
                                </Guard>
                            }
                        />
                        <Route path="*" element={<Navigate to="/" replace/>}/>
                    </Routes>
                </div>
            </>
        );
    };

    if (bootLoading)
        return (
            <div
                className="bgm-container"
                style={{minHeight: '60vh', display: 'grid', placeItems: 'center'}}
            >
                <div
                    className="spin"
                    style={{
                        width: 32,
                        height: 32,
                        border: '4px solid #0002',
                        borderTopColor: 'var(--bgm-gold,#d4af37)',
                        borderRadius: '50%',
                        animation: 'spin 1s linear infinite',
                    }}
                />
                <style>{`@keyframes spin{to{transform:rotate(360deg)}}`}</style>
            </div>
        );

    return (
        <BrowserRouter basename="/bgm-investments-esg-frontend/">
            {/* Layout a colonna per avere footer in fondo */}
            <div style={{minHeight: '100vh', display: 'flex', flexDirection: 'column'}}>
                {user && user.role === 'ADMIN' ? <AdminOnlyShell/> : <NormalShell/>}
                <Footer/>
            </div>

            <SpinnerOverlay show={globalLoading}/>
            <LogoutPopup
                show={showLogoutPopup}
                onConfirm={() => {
                    setShowLogoutPopup(false);
                    window.location.href = '/bgm-investments-esg-frontend/login';
                }}
            />
            <style>{`@keyframes spin{to{transform:rotate(360deg)}}`}</style>
        </BrowserRouter>
    );
}