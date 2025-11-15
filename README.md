# Banca BGM — ESG Investments (Full-Stack)

Prototipo didattico per simulazione e gestione portafogli con indicatori ESG, architettura REST API-based e frontend React in brand Banca BGM.
### ⚠️ Attenzione / Disclaimer
Tutti i dati presenti nel progetto sono fittizi e utilizzati a scopo didattico.
“Banca BGM” non esiste: è un nome inventato per il project work.

## 👀 Anteprima
- Login
- Home
- Dashboard Investimenti (Creazione portafoglio / Inserimento Assets)
- Simulazione scenari (neutro / ottimistico / pessimistico)
- Admin – Catalogo titoli

## ✨ Funzionalità chiave
- Autenticazione “light” con sessione HTTP (cookie JSESSIONID), login/logout e “utente corrente”
- Catalogo titoli/asset con punteggio ESG e parametri mensili (rendimento, volatilità)
- Portafogli & posizioni (create, list, add/remove)
- Simulazione multi-scenario (Metodo Montecarlo) su orizzonte in mesi, con ESG aggregato
- Area Admin per CRUD titoli (create/soft-delete/modify), oggi dati manuali → roadmap: provider esterni
- OpenAPI/Swagger UI integrato per esplorare le API
- H2 embedded per sviluppo (schema+seed) → roadmap: SQL Server (on-prem)
- MapStruct per mappare Entity ⇄ Model

## 🧱 Architettura
```bash
  bgm-investments-esg/
  ├─ bgm-investments-esg-backend/   # Spring Boot 3, REST API
  ├─ bgm-investments-esg-doc/   # javadoc, ER Schema, UML Schema
  └─ bgm-investments-esg-frontend/  # React (Vite)
```

## 🛠️ Stack tecnico
### Backend
- Java 17 • Spring Boot 3 (Web, Security, Data JPA)
- H2 database (dev), JPA/Hibernate
- MapStruct (Entity ⇄ Model)
- springdoc-openapi (Swagger UI)
- BCrypt (password hash)
### Frontend
- React 18 • Vite
- React Router
- CSS custom con palette BGM
### Branding
- Blu Scuro `#0C233C` (primario/sfondo)
- Oro `#C6A45F` (accenti)
- Blu Medio `#5B85B5` (testi secondari/linee)
- Logo: `bgm-investments-esg-frontend/public/logo-bgm.png`

## 🚀 Avvio rapido
### Prerequisiti
- Java 17, Maven
- Node.js 18+ / npm
### Backend
```bash
  cd bgm-investments-esg/bgm-investments-esg-backend
  mvn spring-boot:run
  # API base: http://localhost:8080/bgm-investments-esg-backend/
  # Swagger UI: http://localhost:8080/swagger-ui/index.html
  # H2 console: http://localhost:8080/h2-console
  # JDBC URL: jdbc:h2:file:./target/esgdb   user: sa  pass: (vuota)
```
### Frontend
```bash
  cd ../bgm-investments-esg-frontend
  npm i
  npm run dev
  # FE base: http://localhost:5173/bgm-investments-esg-frontend/
```

## 🔐 Credenziali demo
- Utente: `mario.rossi@bgm.it` / `password` (ROLE: `USER`)
- Admin: `admin@bgm.it` / `password` (ROLE: `ADMIN`)

## 🗺️ Roadmap
- Sezione autogenerazione portafoglio da capitale e vincoli iniziali (es. ESG > X)
- SSO (Keycloak / OAuth2) al posto della sessione locale
- Provider mercati/ESG esterni (refresh automatico catalogo titoli)
- DB enterprise (SQL Server)