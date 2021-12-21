package com.doist.gradle.spec

import org.gradle.api.Task
import org.gradle.api.specs.Spec

class TaskInGraphSpec(private val task: Task) : Spec<Task> {
    override fun isSatisfiedBy(element: Task) = element.project.gradle.taskGraph.hasTask(task)
}
