<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# CodeGraph Changelog

## [Unreleased]

### Fixed

- Explicitly declare compatibility with Kotlin K2 mode by adding to resolve plugin incompatibility warning.

## [0.2.0] - 2025-06-29

### Added

- Support for selecting and analyzing Kotlin files in addition to Java files.
- New "Copy AI Agent Context Prompt" action to copy relative file paths instead of file content.
- File paths in clipboard content are now shown as project-relative instead of just filenames.

### Changed

- Dependency collection is now more intelligent: instead of relying only on import statements, the plugin analyzes
  actual code references within Java and Kotlin files using PSI traversal. This results in a more accurate and
  comprehensive dependency graph that reflects better file relationships and usages.

## [0.1.0] - 2025-02-19

### Added

- Support for selecting multiple Java files in IntelliJ IDEA project panel.
- New keyboard shortcut: <kbd>Ctrl</kbd> + <kbd>Alt</kbd> + <kbd>G</kbd> to invoke the plugin. Instead of <kbd>
  Ctrl</kbd> + <kbd>Alt</kbd> + <kbd>D</kbd>.
- Enhanced UI with root files highlighted in orange.
- Introduce French translations.

### Fixed

- Enhanced plugin Usage instructions in `README.md`.

## [0.0.1]

### Added

- Initial version
- Initial scaffold created
  from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intelliJ-platform-plugin-template)

[Unreleased]: https://github.com/sisimomo/CodeGraph/compare/v0.2.0...HEAD

[0.2.0]: https://github.com/sisimomo/CodeGraph/compare/v0.1.0...v0.2.0

[0.1.0]: https://github.com/sisimomo/CodeGraph/compare/v0.0.1...v0.1.0

[0.0.1]: https://github.com/sisimomo/CodeGraph/commits/v0.0.1
