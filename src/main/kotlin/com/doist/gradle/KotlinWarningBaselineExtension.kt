package com.doist.gradle

import org.gradle.api.Task
import org.gradle.api.specs.Spec

open class KotlinWarningBaselineExtension {
    var baselineFileName: String = "warning-baseline.txt"
    var insertFinalNewline: Boolean = true

    internal val skipSpecs = mutableSetOf<Spec<Task>>()

    fun skipIf(spec: Spec<Task>) {
        skipSpecs.add(spec)
    }

    inline fun skipIf(crossinline spec: (Task) -> Boolean) {
        skipIf(Spec<Task> { spec(it) })
    }
}
