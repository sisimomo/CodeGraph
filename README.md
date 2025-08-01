# CodeGraph

![Build](https://github.com/sisimomo/CodeGraph/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/26453-codegraph.svg)](https://plugins.jetbrains.com/plugin/26453-codegraph)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/26453-codegraph.svg)](https://plugins.jetbrains.com/plugin/26453-codegraph)

<!-- Plugin description -->

This IntelliJ plugin generates an **interactive dependency graph** for selected Java or Kotlin files, allowing users to
**filter dependencies** based on a specified package and **concatenate selected files** into the clipboard. The plugin
provides a visual representation of the dependencies, enabling users to explore and manipulate them interactively.

## Usage

1. **Select one or multiple Java or Kotlin files** in the IntelliJ IDEA project side panel.
2. **Invoke the plugin** using the configured shortcut (default <kbd>Ctrl</kbd> + <kbd>Alt</kbd> + <kbd>G</kbd>).
    - Or use the Command Palette (Find Action, <kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>A</kbd>) then type <kbd>
      CodeGraph - Show Dependencies</kbd>.
3. **Enter an optional package filter** in the prompt.
4. The **dependency graph is displayed** in a floating window.
5. **Interact with the graph**:
    - Click nodes to enable/disable them to include/exclude files from concatenation.
6. **Choose how to share selected files:**
    - **If your AI agent (e.g., Claude Code, GitHub Copilot Agent, Cursor, etc.) already has access to your files:**
        - Click **“Copy AI Agent Context Prompt”** to copy a list of the selected files' paths. Paste this list into
          your AI tool to provide context.
    - **If your AI agent does NOT have access to your files:**
        - Click **“Concatenate to Clipboard”** to copy the full content of the selected files. Paste this content into
          your AI tool to provide full context.
7. **Paste the concatenated content or prompt** where needed (ChatGPT, Claude, Ollama, etc.).

<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "CodeGraph"</kbd> >
  <kbd>Install</kbd>

- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/26453-codegraph) and install it by clicking
  the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/26453-codegraph/versions) from
  JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/sisimomo/CodeGraph/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

---

## Development & Contribution

### Prerequisites

- **IntelliJ IDEA Plugin SDK**
- **Java 21+**
- **Gradle (for build automation)**

### Building the Plugin

1. Clone the repository:
   ```sh
   git clone https://github.com/sisimomo/CodeGraph.git
   cd CodeGraph
   ```
2. Build the project:
   ```sh
   ./gradlew buildPlugin
   ```
3. The generated plugin `.jar` file will be in `build/distributions/`.

### Running in IntelliJ

- Open the project in IntelliJ IDEA.
- Run the plugin using **Gradle runIde**:
   ```sh
   ./gradlew runIde
   ```

### Contributing

- Fork the repository and create a new branch.
- Submit a pull request with detailed explanations.

### Release Process

To release a new version of CodeGraph:

1. **Update the version:**
    - Change the `pluginVersion` property in the `gradle.properties` file to the desired new version.
2. **Update the changelog:**
    - List all changes for the new version under the `## [Unreleased]` section in `CHANGELOG.md`.
3. **Merge to main:**
    - Merge your changes into the `main` branch.
4. **Draft release:**
    - A pipeline will automatically create a draft release on GitHub.
5. **Publish the release:**
    - Publish the draft release on GitHub. This will trigger a CI pipeline that:
        - Publishes the plugin to the JetBrains Marketplace.
        - Automatically updates the `CHANGELOG.md` file.

No manual upload to the Marketplace is required; the process is fully automated after publishing the GitHub release
draft.

---

## License

This project is licensed under the **MIT License**. See [LICENSE](LICENSE) for details.

---

## Contact

For issues or feature requests, open an issue on [GitHub Issues](https://github.com/sisimomo/CodeGraph/issues).
