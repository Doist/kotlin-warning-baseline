package com.doist.gradle.task

import com.doist.gradle.convertor.PathSeparatorConvertor
import com.doist.gradle.ext.filterByBaseline
import com.doist.gradle.ext.readSetOfLines
import com.doist.gradle.ext.readWarningLines
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class CheckKotlinWarningBaselineTask : DefaultTask() {
    @get:InputFiles
    abstract val warningFiles: ListProperty<File>

    @get:InputFiles
    abstract val baselineFiles: ListProperty<File>

    @get:Input
    abstract val pathConvertor: Property<PathSeparatorConvertor>

    @TaskAction
    fun write() {
        val pathConvertor = pathConvertor.get()
        val warningSet = warningFiles.get().readSetOfLines()
        val diff = baselineFiles.get().flatMap { baselineFile ->
            val baseline = baselineFile.readWarningLines()
            val current = warningSet.filterByBaseline(baselineFile)
            (current - baseline).map {
                val parentFile = baselineFile.parentFile
                pathConvertor.toPlatform(it).toString()
                    .replaceFirst(parentFile.name, parentFile.absolutePath)
            }
        }
        if (diff.isNotEmpty()) {
            val text = diff.joinToString(
                prefix = "Found ${diff.size} warnings behind baseline:\n",
                separator = "\n"
            )
            throw GradleException(text)
        }
    }
}
