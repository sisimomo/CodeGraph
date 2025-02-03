# CodeGraph

![Build](https://github.com/sisimomo/CodeGraph/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/26453-codegraph.svg)](https://plugins.jetbrains.com/plugin/26453-codegraph)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/26453-codegraph.svg)](https://plugins.jetbrains.com/plugin/26453-codegraph)

<!-- Plugin description -->

This IntelliJ plugin generates an **interactive dependency graph** for a selected Java file, allowing users to **filter
dependencies** based on a specified package and **concatenate selected files** into the clipboard. The plugin provides a
visual representation of the dependencies, enabling users to explore and manipulate them interactively.

## Usage

1. **Open a Java file** in IntelliJ IDEA.
2. **Invoke the plugin** using the configured shortcut.
3. **Enter an optional package filter** in the prompt.
4. The **dependency graph is displayed** in a floating window.
5. **Interact with the graph**:

- Click nodes to enable/disable them to include/exclude files from concatenation.

6. Click **“Concatenate to Clipboard”** to copy the selected files’ content.
7. **Paste the concatenated content** where needed (ChatGPT, Claude, Ollama, etc.).

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
- **Java 17+**
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

---

## License

This project is licensed under the **MIT License**. See [LICENSE](LICENSE) for details.

---

## Contact

For issues or feature requests, open an issue on [GitHub Issues](https://github.com/sisimomo/CodeGraph/issues).
