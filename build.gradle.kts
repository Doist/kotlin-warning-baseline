plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("maven-publish")
    id("com.gradle.plugin-publish").version("0.16.0")
    id("com.doist.gradle.kotlin-warning-baseline").version("+")
}

repositories {
    mavenCentral()
}
group = "com.doist.gradle"
version = property("version") as String

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin")
}

val pluginName = "KotlinWarningBaseline"

gradlePlugin {
    plugins.register(pluginName) {
        id = "${project.group}.${project.name}"
        implementationClass = "com.doist.gradle.KotlinWarningBaselinePlugin"
    }
    isAutomatedPublishing = true
}

pluginBundle {
    website = "https://github.com/Doist/kotlin-warning-baseline"
    vcsUrl = "https://github.com/Doist/kotlin-warning-baseline.git"

    plugins.getByName(pluginName) {
        displayName = "Kotlin Warning Baseline Plugin"
        description = "This plugin adds tasks to control kotlin warnings in the project with the help of baseline or without it"
        tags = listOf(
            "analysis",
            "baseline",
            "check",
            "code quality",
            "kotlin",
            "verification",
            "warnings"
        )
    }

    mavenCoordinates {
        groupId = project.group.toString()
        artifactId = project.name
        version = project.version.toString()
    }
}

tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
}
