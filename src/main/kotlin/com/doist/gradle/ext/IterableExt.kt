package com.doist.gradle.ext

import java.io.File

fun Iterable<File>.readSetOfLines(): Set<String> = flatMapTo(mutableSetOf()) {
    it.takeIf(File::exists)?.readLines() ?: emptyList()
}

fun Iterable<String>.filterByBaseline(baseline: File): List<String> = filter {
    it.startsWith("${baseline.parentFile.name}/")
}
