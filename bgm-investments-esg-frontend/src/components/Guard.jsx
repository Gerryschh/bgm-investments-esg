import { Navigate, useLocation } from 'react-router-dom';

export default function Guard({ user, children }) {
    const location = useLocation();

    // la pagina di login non deve essere mai bloccata
    if (location.pathname === '/login') return children;

    // se non loggato → vai a /login con stato di redirect
    if (!user) {
        return <Navigate to="/login" state={{ from: location.pathname }} replace />;
    }

    // se user è boolean (es. user && user.role === 'ADMIN') ed è false → 403 semplice
    if (typeof user === 'boolean' && user === false) {
        return (
            <div className="bgm-container" style={{ padding: 24 }}>
                <div className="bgm-card">
                    <h3 style={{ marginTop: 0 }}>Non autorizzato</h3>
                    <p>Non hai i permessi per accedere a questa sezione.</p>
                </div>
            </div>
        );
    }

    return children;
}