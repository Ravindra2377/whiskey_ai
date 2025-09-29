# Desktop ↔ Backend Integration Hooks

This note captures the staged plan for wiring the desktop JavaFX shell to the existing
NEXUS backend services. It balances near-term visibility for stakeholders with the
longer-term goal of converging on a single orchestration engine shared by the CLI,
desktop, and future browser experiences.

## Guiding principles

1. **Reuse the CLI brains.** The CLI already encapsulates service discovery,
   credential management, and action orchestration. The desktop shell should
   invoke those capabilities rather than re-implementing them.
2. **Keep IPC transport swappable.** Start with local process delegation (shelling
   into the CLI) while preparing clear seams for future gRPC/HTTP contracts.
3. **Stream incremental feedback.** UI components publish progress and status updates
   in real time, regardless of the backing transport option.

## Phase 0 – Today’s stubs

- Toolbar and menu actions update `statusText` asynchronously to simulate
  backend work.
- Environment chip and diagnostics button present placeholder feedback with a
  1.5–2.2s delay to mirror real API round trips.
- Navigation events log contextual changes for future data binding.

## Phase 1 – CLI bridge (1–2 sprints)

| Hook | Desktop Trigger | Backend Action | Transport | Notes |
|------|-----------------|----------------|-----------|-------|
| AI Generation | "Generate" toolbar button, welcome cards | OpenAI GPT-4 (chat completions) → write project scaffold | HTTPS (direct OpenAI API) | Streams status to console, persists artefacts under `generated-apps/` |
| Voice Assistant | "Voice" toolbar button | Whisper transcription + GPT-4 generation | HTTPS (OpenAI API) | Voice capture dialog records WAV, routes transcript through generator |
| Environment Connect | "Connect to Company Server" | `java -jar nexus.jar env connect` | Local process | Update environment badge based on exit code |
| Health Check | "Health" toolbar button | `java -jar nexus.jar doctor --json` | Local process | Parse JSON to hydrate diagnostics pane |

Implementation checklist:

- Introduce a `BackendProcessService` that wraps `ProcessBuilder` for the CLI jar. ✅
- Surface cancellable tasks via JavaFX `Service`/`Task` classes.
- Route streamed output to observable buffers bound to the insights panel. ✅
- Add a CLI location picker so non-standard installs can still be targeted. ✅
- Configure GPT-4 and Whisper API clients with secure key prompts. ✅

## Phase 2 – Local services (3–4 sprints)

- Extract reusable orchestration APIs from the CLI into a shared library (`nexus-core`).
- Embed the library within the desktop app for in-process execution where appropriate.
- Expose RESTful endpoints for long-running jobs (generation, migrations) via the existing Spring Boot backend and consume them with a lightweight HTTP client (e.g. `WebClient`).
- Replace process spawning with authenticated HTTP/gRPC calls when the backend is running in enterprise mode.

## Phase 3 – Enterprise deployment (roadmap)

- Support remote execution across environments (dev/staging/prod) with secrets
  managed via the existing company vault integrations.
- Add push notifications (WebSocket or MQTT) to receive backend events without
  polling.
- Bundle the desktop shell with a connection wizard that provisions API tokens
  and stores them securely via the OS keychain.

## Observability & telemetry

- Add a shared event bus so both CLI and desktop publish metrics to the same
  monitoring pipeline (Prometheus + OpenTelemetry traces already in flight).
- Surface failure diagnostics directly in the desktop via structured responses
  from backend services.

## Workstream coordination

- **Desktop team:** focuses on JavaFX `Service` implementations, output consoles,
  and error handling.
- **Backend team:** exposes stable CLI/HTTP interfaces, supplies JSON schemas,
  and provides sample responses for integration tests.
- **DevOps:** ensures packaged CLI binaries are available alongside the desktop
  distribution and updates pipeline scripts to include desktop smoke tests.

With this phased approach, we can deliver immediate demonstrations while paving
the way for deeper convergence with the NEXUS platform stack.
