package com.doist.gradle.ext

import org.gradle.api.GradleException
import java.io.File

fun File.create() {
    if (!parentFile.exists() && !parentFile.mkdirs()) {
        throw GradleException("Can't create parent file: $this.")
    }
    if (!createNewFile()) {
        throw GradleException("Can't create file: $this.")
    }
}

fun File.readWarningLines() = takeIf(File::exists)
    ?.readLines()
    ?.filterNot { it.isEmpty() || it.startsWith("#") }
    ?: emptyList()
