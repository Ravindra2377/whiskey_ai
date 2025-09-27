# Voice Intent Flow

```mermaid
sequenceDiagram
    participant User
    participant Recorder as AudioRecorder
    participant Whisper as WhisperClient
    participant NL as NaturalLanguageCommand
    participant Context as ConversationContext
    participant CLI as Command Registry
    participant DB as VoiceCommandLogService

    User->>Recorder: Speak "Hey Nexus deploy staging"
    Recorder->>Whisper: Stream PCM chunk
    Whisper-->>Recorder: Transcript delta
    Recorder->>NL: Forward transcript
    NL->>Context: recordUtterance()
    NL->>CLI: execute("run --tag=deploy --name=staging --dry-run")
    CLI-->>NL: exit code
    NL->>Context: rememberLastCommand()
    NL->>DB: record intent + status
    NL-->>User: "Running as dry-run..."
    User->>NL: "yes"
    NL->>Context: consumePendingCommand()
    NL->>CLI: replay pending executor
    CLI-->>NL: exit code
    NL->>DB: record confirmation result
    NL-->>User: "Deployment completed"
```

The flow highlights where repeated commands and confirmations hook in:

- `ConversationContext` stores both pending confirmations and the most recent successful executor, enabling follow-ups such as “repeat that”.
- Each analytics event is persisted with an intent label, making confirmation counts and wake-word misses visible in the CLI and dashboard.
- Error hints are produced before the database write, so remediation advice appears immediately in the live session.
