package com.doist.gradle.collector

import com.doist.gradle.ext.create
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.logging.StandardOutputListener
import org.gradle.api.tasks.TaskState
import java.io.File

class WarningFileCollector(
    private val task: Task,
    private val file: File,
    sourceSets: Set<File>
) : TaskExecutionListener {
    private val prefixSet = sourceSets.mapTo(mutableSetOf()) { "w: ${it.parent}/" }

    private val outputListener = StandardOutputListener { line ->
        for (prefix in prefixSet) {
            if (line.startsWith(prefix)) {
                file.takeIf { !it.exists() }?.create()
                file.appendText("${line.removePrefix(prefix)}\n")
                break
            }
        }
    }

    override fun beforeExecute(task: Task) {
        if (task == this.task) {
            task.logging.addStandardOutputListener(outputListener)
        }
    }

    override fun afterExecute(task: Task, state: TaskState) {
        if (task == this.task) {
            task.logging.removeStandardOutputListener(outputListener)
        }
    }
}
