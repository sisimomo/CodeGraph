# CodeGraph

![Build](https://github.com/sisimomo/CodeGraph/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/26453-codegraph.svg)](https://plugins.jetbrains.com/plugin/26453-codegraph)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/26453-codegraph.svg)](https://plugins.jetbrains.com/plugin/26453-codegraph)

## Template ToDo list

- [x] Create a new [IntelliJ Platform Plugin Template][template] project.
- [x] Get familiar with the [template documentation][template].
- [x] Adjust the [pluginGroup](./gradle.properties) and [pluginName](./gradle.properties), as well as
  the [id](./src/main/resources/META-INF/plugin.xml) and [sources package](./src/main/kotlin).
- [x] Adjust the plugin description in `README` (see [Tips][docs:plugin-description])
- [x] Review
  the [Legal Agreements](https://plugins.jetbrains.com/docs/marketplace/legal-agreements.html?from=IJPluginTemplate).
- [ ] [Publish a plugin manually](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html?from=IJPluginTemplate)
  for the first time.
- [ ] Set the `MARKETPLACE_ID` in the above README badges. You can obtain it once the plugin is published to JetBrains
  Marketplace.
- [ ] Set the [Plugin Signing](https://plugins.jetbrains.com/docs/intellij/plugin-signing.html?from=IJPluginTemplate)
  related [secrets](https://github.com/JetBrains/intellij-platform-plugin-template#environment-variables).
- [ ] Set
  the [Deployment Token](https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html?from=IJPluginTemplate).
- [ ] Click the <kbd>Watch</kbd> button on the top of the [IntelliJ Platform Plugin Template][template] to be notified
  about releases containing new features and fixes.

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















<!-- Plugin description -->
This Fancy IntelliJ Platform Plugin is going to be your implementation of the brilliant ideas that you have.

This specific section is a source for the [plugin.xml](/src/main/resources/META-INF/plugin.xml) file which will be
extracted by the [Gradle](/build.gradle.kts) during the build process.

To keep everything working, do not remove `<!-- ... -->` sections.
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
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template

[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
