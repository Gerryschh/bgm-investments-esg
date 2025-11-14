import {NavLink} from 'react-router-dom';

export default function Nav({user, onLogout}) {
    const isAdmin = user && user.role === 'ADMIN';

    // provo a ricostruire un nome decente
    const fullName = user?.userName || 'Utente';

    // iniziali per il cerchietto
    const initials = fullName
        .split(' ')
        .filter(Boolean)
        .slice(0, 2)
        .map(s => s[0]?.toUpperCase())
        .join('') || 'U';

    return (
        <header className="nav" role="navigation" aria-label="Main Navigation">
            <div
                style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: 24,
                    width: '100%',
                    flexWrap: 'nowrap',
                }}
            >
                {/* LOGO + NOME BANCA INSIEME */}
                <div style={{display: 'flex', alignItems: 'center', gap: 10}}>
                    <a href="/" aria-label="Homepage BGM">
                        <img src="/bgm-investments-esg-frontend/logo-bgm.png" alt="BGM" style={{height: 80}}/>
                    </a>
                    <a
                        href="/"
                        className="bgm-link"
                        style={{fontWeight: 700, fontSize: 18, textDecoration: 'none'}}
                    >
                        Banca BGM
                    </a>
                </div>

                {/* LINK DI NAVIGAZIONE */}
                {!isAdmin && (
                    <>
                        <NavLink
                            to="/"
                            className={({isActive}) =>
                                `bgm-link ${isActive ? 'active' : ''}`
                            }
                        >
                            Home
                        </NavLink>
                        <NavLink
                            to="/portfolios"
                            className={({isActive}) =>
                                `bgm-link ${isActive ? 'active' : ''}`
                            }
                        >
                            Dashboard Investimenti
                        </NavLink>
                    </>
                )}

                {isAdmin && (
                    <NavLink
                        to="/admin"
                        className={({isActive}) =>
                            `bgm-link ${isActive ? 'active' : ''}`
                        }
                    >
                        Area Admin – Catalogo Titoli
                    </NavLink>
                )}

                {/* SPACER */}
                <div style={{flex: 1}}/>

                {/* UTENTE + LOGOUT */}
                {user && (
                    <div style={{display: 'flex', alignItems: 'center', gap: 12}}>
                        <div
                            style={{
                                width: 32,
                                height: 32,
                                borderRadius: '50%',
                                border: '1px solid var(--border)',
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center',
                                fontSize: 14,
                                fontWeight: 600,
                                background: 'rgba(255,255,255,0.06)',
                            }}
                            aria-hidden
                        >
                            {initials}
                        </div>
                        <div style={{display: 'flex', flexDirection: 'column', lineHeight: 1}}>
                            <span style={{fontSize: 11, opacity: 0.7}}>
                                {user.role === 'ADMIN' ? 'Admin' : 'Utente'}
                            </span>
                            <span
                                style={{
                                    fontSize: 14,
                                    maxWidth: 160,
                                    whiteSpace: 'nowrap',
                                    textOverflow: 'ellipsis',
                                    overflow: 'hidden'
                                }}
                            > {fullName}
                            </span>
                        </div>
                        <button
                            type="button"
                            className="bgm-btn"
                            style={{padding: '6px 14px', fontSize: 13}}
                            onClick={onLogout}
                        >
                            Logout
                        </button>
                    </div>
                )}
            </div>
        </header>
    );
}