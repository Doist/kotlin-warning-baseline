# Kotlin Warning Baseline Gradle Plugin

This plugin adds tasks to control kotlin warnings in the project with the help of baseline or without it. Typical usage of the plugin would be checking that PR doesn't introduce new warnings (for example [github action](.github/workflows/warning-check.yml)) or running it locally to catch new deprecations after dependencies update.

Currently, plugin supports: Kotlin JVM, Kotlin Multiplatform and Android projects.

## Usage

Run:
```shell
./gradlew checkKotlinWarningBaseline
```
and receive error in case of new warnings:
```
FAILURE: Build failed with an exception.
* What went wrong:
Execution failed for task ':checkKotlinWarningBaseline'.
> Found 3 warnings behind baseline:
  .../src/main/kotlin/com/doist/gradle/KotlinWarningBaselinePlugin.kt: (3, 30): 'TaskInGraphSpec' is deprecated. 
  .../src/main/kotlin/com/doist/gradle/KotlinWarningBaselinePlugin.kt: (69, 72): 'TaskInGraphSpec' is deprecated. 
  .../src/main/kotlin/com/doist/gradle/KotlinWarningBaselinePlugin.kt: (70, 72): 'TaskInGraphSpec' is deprecated. 
```

### Notes

- Warnings are differentiated by plugin based on path to `kt` file, warning position in code (line, symbol) and warning message. So if you update code above warning position, it will change position of the warning, and you'll have to update warning baseline.
- When collecting of warnings is running, plugin makes `clean` and temporarily disables Gradle Build Cache for Kotlin compile tasks. So running `writeKotlinWarningBaseline` or `checkKotlinWarningBaseline` leads to **full build**.

## Setup

```kotlin
plugins {
    id("com.doist.gradle.kotlin-warning-baseline") version "1.0.0"
}

// Optional configuration.
kotlinWarningBaseline {
    // Option to change name of warning baseline file. 
    // Default: "warning-baseline.txt" 
    baselineFileName = "..."

    // Option to disable new line at the end of the baseline files.
    // Default: true
    insertFinalNewline = true | false

    // Option to skip some Kotlin compile tasks for collecting of warnings.
    // Default: undefined.
    skipIf { task -> ... } 
}
```

Also see plugin page in [Gradle Plugin Portal](https://plugins.gradle.org/plugin/com.doist.gradle.kotlin-warning-baseline)

## Tasks

- `writeKotlinWarningBaseline` Create or update warning baseline files for each source set in project/module. If there is no warnings in project/module, files won't be created/updated.
- `checkKotlinWarningBaseline` Check that all warnings are in warning baseline files for each source set in project/module.
- `removeKotlinWarningBaseline` Remove all warning baselines files in project/module.

## Release

To release a new version, ensure `CHANGELOG.md` is up-to-date, and push the corresponding tag (e.g., `v1.2.3`). GitHub Actions handles the rest.

## Licence

Released under the [MIT License](https://opensource.org/licenses/MIT).
