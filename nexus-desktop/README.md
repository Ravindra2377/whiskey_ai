# NEXUS Desktop Application (Enterprise Preview)

The `nexus-desktop` module introduces the foundation for the professional
JavaFX desktop experience that will envelop the NEXUS AI full-stack platform.
It delivers the initial Day 1 milestone: a polished desktop shell with
menus, toolbars, navigation, insights, and a welcome workspace that will
host future AI-driven workflows.

## Features delivered today

- **Professional chrome:** menu bar, command toolbar, status/progress area
- **Workspace navigation:** quick access to workspaces and enterprise tasks
- **Welcome surface:** IDE-style landing page with action cards and quick links
- **GPT-4 generation:** the `Generate` button now invokes OpenAI GPT-4 to deliver full project scaffolds written to `generated-apps/`
- **Voice-to-app:** record a spoken brief and Whisper + GPT-4 will produce the matching project blueprint
- **Insight rail:** contextual panes for pipeline, infrastructure, and activity
- **Status telemetry:** live clock, environment indicator, and activity ticker
- **Custom theming:** modern enterprise styling tuned for dark UI environments

## Requirements

- JDK **17** or newer (JavaFX 17 binaries are used)
- One of:
	- Maven 3.8+
	- Gradle 8.5+ (or use the extracted `gradle-8.10` distribution bundled in the repo)

> The Maven build (`pom.xml`) keeps OS-activated profiles for Windows, macOS (Intel &
> Apple Silicon), and Linux. The Gradle build achieves the same coverage via the
> `org.openjfx.javafxplugin`, so choose whichever toolchain is more convenient.

## Building

You can package the desktop shell with either Maven or Gradle.

### Maven

```powershell
mvn -f nexus-desktop/pom.xml package
```

### Gradle

```powershell
& .\gradle-8.10\gradle-8.10\bin\gradle.bat -p nexus-desktop build
```

Both commands produce `nexus-desktop/target/nexus-desktop-0.1.0-SNAPSHOT.jar`
containing the compiled classes and resources.

## Running the desktop shell

To launch the JavaFX application directly:

```powershell
mvn -f nexus-desktop/pom.xml javafx:run
```

or via Gradle:

```powershell
& .\gradle-8.10\gradle-8.10\bin\gradle.bat -p nexus-desktop run
```

Alternatively execute the shaded jar (JavaFX modules must be on the module path):

```powershell
java -jar target/nexus-desktop-0.1.0-SNAPSHOT.jar
```

## GPT-4 application generation

1. Supply an OpenAI API key (GPT-4o/GPT-4 access):
	```powershell
	setx OPENAI_API_KEY "sk-your-key"  # restart the shell after running
	```
	The desktop also prompts for the key on first use and stores it only in-memory for the current session.
2. Launch the desktop app and press **Generate** (or any feature card) on the welcome surface.
3. Describe your application, choose optional stack/tags, and confirm.
4. Generated assets land in `generated-apps/<timestamp>-<project-name>/` with a `GENERATION_SUMMARY.md` audit file.

## Voice-to-app workflow

1. Ensure the same OpenAI key is present (for both Whisper and GPT-4).
2. Click the **Voice** toolbar button and record a short requirement statement.
3. After transcription, the generator pre-fills the dialog with your transcript so you can refine and launch the build.

The activity console and insight rail stream status updates from GPT-4 and Whisper during every run.

### Configuring the CLI bridge

The desktop tries to auto-detect the NEXUS CLI jar (`nexus.jar` or `nexus-1.0.0.jar`) from the
workspace. If the CLI lives elsewhere, open **File → Set NEXUS CLI Location…** and point to the
jar you want to delegate to. The selection is stored in your user preferences and reused on the
next launch. Build the CLI jar with `gradlew :nexus:bootJar` if you need a fresh artifact.

## Next steps (Day 2 and beyond)

1. Wire the existing CLI workflows into the desktop UI panels
2. Surface AI code generation progress within the welcome surface
3. Integrate voice assistant controls and streaming feedback
4. Add environment connection flows for enterprise deployment targets
5. Package with an installer for company-wide distribution

See `docs/integration-hooks.md` for a living plan that maps each desktop action to its
target backend service across upcoming phases.

This module is the anchor for the professional desktop product direction
outlined in the transformation roadmap. Subsequent phases will progressively
activate the AI generation engine, template orchestration, enterprise
integration, and voice-driven workflows.
