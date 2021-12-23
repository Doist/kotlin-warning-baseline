package com.doist.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class RemoveKotlinWarningBaselineTask : DefaultTask() {
    @get:InputFiles
    abstract val baselineFiles: ListProperty<File>

    @TaskAction
    fun remove() = baselineFiles.get().forEach(File::delete)
}
