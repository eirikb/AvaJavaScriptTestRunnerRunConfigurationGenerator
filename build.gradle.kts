import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

plugins {
    id("java")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.intellijPlatform)
    alias(libs.plugins.changelog)
    alias(libs.plugins.kover)
}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create(properties("platformType").get(), properties("platformVersion").get())

        bundledPlugins(properties("platformBundledPlugins").map {
            it.split(',').map(String::trim).filter(String::isNotEmpty)
        })
    }
}

changelog {
    groups.empty()
    repositoryUrl = "https://github.com/eirikb/AvaJavaScriptTestRunnerRunConfigurationGenerator"
}

kover {
    reports {
        total {
            xml {
                onCheck = true
            }
        }
    }
}

intellijPlatform {
    pluginConfiguration {
        name = properties("pluginName")
        version = properties("pluginVersion")

        description = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with(it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }

        val changelog = project.changelog
        changeNotes = properties("pluginVersion").map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        }

        ideaVersion {
            sinceBuild = properties("pluginSinceBuild")
            untilBuild = properties("pluginUntilBuild")
        }
    }

    signing {
        certificateChain = environment("CERTIFICATE_CHAIN")
        privateKey = environment("PRIVATE_KEY")
        password = environment("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = environment("PUBLISH_TOKEN")
        channels = properties("pluginVersion").map { pluginVersion ->
            listOf(pluginVersion.substringAfter('-', "").substringBefore('.').ifEmpty { "default" })
        }
    }

    pluginVerification {
        ides {
            recommended()
        }
    }
}

tasks {
    publishPlugin {
        dependsOn("patchChangelog")
    }
}
