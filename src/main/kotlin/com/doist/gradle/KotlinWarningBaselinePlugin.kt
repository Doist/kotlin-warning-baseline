package com.doist.gradle

import com.doist.gradle.collector.WarningFileCollector
import com.doist.gradle.convertor.PathSeparatorConvertor
import com.doist.gradle.spec.TaskInGraphSpec
import com.doist.gradle.task.CheckKotlinWarningBaselineTask
import com.doist.gradle.task.RemoveKotlinWarningBaselineTask
import com.doist.gradle.task.WriteKotlinWarningBaselineTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompile
import java.io.File

class KotlinWarningBaselinePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val extension = extensions.create<KotlinWarningBaselineExtension>("kotlinWarningBaseline")
        afterEvaluate { configure(extension) }
    }

    private fun Project.configure(extension: KotlinWarningBaselineExtension) {
        val kotlinExtension = extensions.findByType<KotlinProjectExtension>()
            ?: throw GradleException("Kotlin not configured in project $this.")
        val baselines = kotlinExtension.sourceSets.associate {
            val sourceSetRoot = it.findRootDirectory()
            sourceSetRoot to File(sourceSetRoot, extension.baselineFileName)
        }
        val pathConvertor = PathSeparatorConvertor()

        val kotlinTaskMap = tasks.withType<AbstractKotlinCompile<*>>()
            .filter { task -> extension.skipSpecs.none { it.isSatisfiedBy(task) } }
            .associateWith { File(buildDir, "kotlin-warnings/${it.name}.txt") }
            .onEach { (task, file) ->
                val collector = WarningFileCollector(task, file, pathConvertor, baselines.keys)
                gradle.taskGraph.addTaskExecutionListener(collector)
            }

        val clean = tasks.getByName("clean")

        val check = tasks.create<CheckKotlinWarningBaselineTask>("checkKotlinWarningBaseline") {
            group = "verification"
            description = "Check that all warnings are in warning baseline files."

            warningFiles.set(kotlinTaskMap.values)
            baselineFiles.set(baselines.values)
            this.pathConvertor.set(pathConvertor)

            dependsOn(kotlinTaskMap.keys + clean)
            mustRunAfter(clean)
        }
        val write = tasks.create<WriteKotlinWarningBaselineTask>("writeKotlinWarningBaseline") {
            group = "verification"
            description = "Create or update warning baseline files for each source set."

            warningPostfix.set(extension.warningPostfix)
            warningFiles.set(kotlinTaskMap.values)
            baselineFiles.set(baselines.values)

            dependsOn(kotlinTaskMap.keys + clean)
            mustRunAfter(clean)
        }
        tasks.create<RemoveKotlinWarningBaselineTask>("removeKotlinWarningBaseline") {
            group = "verification"
            description = "Remove all warning baseline files."

            baselineFiles.set(baselines.values)
        }

        tasks.getByName("check").dependsOn(check)

        kotlinTaskMap.keys.forEach { task ->
            task.outputs.doNotCacheIf("Task graph has ${check.name}.", TaskInGraphSpec(check))
            task.outputs.doNotCacheIf("Task graph has ${write.name}.", TaskInGraphSpec(write))
        }
    }

    private fun KotlinSourceSet.findRootDirectory(): File {
        var parent: File? = kotlin.sourceDirectories.firstOrNull()?.parentFile
        while (parent != null && parent.name != name) {
            parent = parent.parentFile
        }
        return parent ?: throw GradleException(
            "Can't find root directory for sources set $name in ${kotlin.sourceDirectories.asPath}"
        )
    }

    private val KotlinWarningBaselineExtension.warningPostfix
        get() = when {
            insertFinalNewline -> "\n"
            else -> ""
        }
}
