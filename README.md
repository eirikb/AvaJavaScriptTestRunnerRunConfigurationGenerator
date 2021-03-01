# AVA Test Run Configuration Generator

![Build](https://github.com/eirikb/AvaJavaScriptTestRunnerRunConfigurationGenerator/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/13835.svg)](https://plugins.jetbrains.com/plugin/13835)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/13835.svg)](https://plugins.jetbrains.com/plugin/13835)


<!-- Plugin description -->
<h1>Generates a run configuration for AVA JavaScript test runner</h1>
<p>
  This is a workaround while we wait for proper AVA support, see
  <a href="https://youtrack.jetbrains.com/issue/WEB-21788">https://youtrack.jetbrains.com/issue/WEB-21788</a>.<br/>
</p>
<h3>Usage</h3>
<p>
  Place caret inside a test and press <kbd>ctrl</kbd> + <kbd>alt</kbd> + <kbd>shift</kbd> + <kbd>a</kbd>. <br/>
  This should generate and start a new run configuration. <br/>
  If caret is outside of a test it should generate and start a run configuration for the file. <br/>
</p>
<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "
  AVA Test Run Configuration Generator Changelog
  "</kbd> >
  <kbd>Install Plugin</kbd>

- Manually:

  Download
  the [latest release](https://github.com/eirikb/AvaJavaScriptTestRunnerRunConfigurationGenerator/releases/latest) and
  install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
