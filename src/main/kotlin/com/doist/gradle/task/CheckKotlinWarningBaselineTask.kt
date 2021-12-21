package com.doist.gradle.task

import com.doist.gradle.ext.filterByBaseline
import com.doist.gradle.ext.readSetOfLines
import com.doist.gradle.ext.readWarningLines
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class CheckKotlinWarningBaselineTask : DefaultTask() {
    @get:InputFiles
    abstract val warningFiles: ListProperty<File>

    @get:InputFiles
    abstract val baselineFiles: ListProperty<File>

    @TaskAction
    fun write() {
        val warningSet = warningFiles.get().readSetOfLines()
        val diff = baselineFiles.get().flatMap { baselineFile ->
            val baseline = baselineFile.readWarningLines()
            val current = warningSet.filterByBaseline(baselineFile)
            (current - baseline).map {
                val parentFile = baselineFile.parentFile
                it.replaceFirst(parentFile.name, parentFile.absolutePath)
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
