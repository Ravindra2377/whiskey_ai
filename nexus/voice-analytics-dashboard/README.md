# Voice Analytics Dashboard

A lightweight dashboard for the Nexus voice assistant. It consumes the JSON feed exposed by

```
java -jar nexus/target/nexus-1.0.0.jar analytics --voice --server
```

and renders intent, error, and confirmation telemetry.

## Quick start

```bash
cd nexus/voice-analytics-dashboard
npm install
npm run start
```

Then open <http://localhost:3000>. You can change the API base URL at the top of the page to match the port you chose for `--server` (defaults to `8088`).

## Without Node.js

You can also serve the static assets with any HTTP server:

```bash
cd nexus/voice-analytics-dashboard
python -m http.server 3000
```

Open `http://localhost:3000` and ensure the “API Base URL” input targets your analytics endpoint.

## Project structure

- `index.html` – Dashboard markup and client-side logic
- `styles.css` – Minimal styling
- `package.json` – Development server (lite-server)

## Troubleshooting

- Ensure `analytics --voice --server` is running and reachable (defaults to `http://localhost:8088`).
- If the dashboard shows “Failed to load metrics”, inspect the browser console for CORS or network errors. The CLI server serves CORS-friendly responses out of the box.
- Voice analytics require `nexus.db.enabled=true` with the PostgreSQL datasource configured.
