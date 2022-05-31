package com.doist.gradle.convertor

import org.apache.tools.ant.taskdefs.condition.Os

private const val UNIX_SEPARATOR = "/"

fun PathSeparatorConvertor() = when {
    Os.isFamily(Os.FAMILY_WINDOWS) -> WindowsToUnixPathSeparatorConvertor()
    else -> KeepAsIsPathSeparatorConvertor()
}

interface PathSeparatorConvertor {
    fun toUnix(text: CharSequence): CharSequence
}

private class WindowsToUnixPathSeparatorConvertor : PathSeparatorConvertor {
    // Finds each "\" before ".kt" if they're separated by letters/digits/underscores.
    //
    // E.g. only the first two "\" characters will be found in
    // "w: C:\work\Composer.kt: (1, 1): \Deprecated\ in \Java\" line. The rest are behind ".kt".
    private val findWindowsSeparatorsRegex = """(?<=(\w|:))?\\(?=(\w|\\)*\.kt)""".toRegex()

    override fun toUnix(text: CharSequence) =
        text.replace(findWindowsSeparatorsRegex, UNIX_SEPARATOR)
}

private class KeepAsIsPathSeparatorConvertor : PathSeparatorConvertor {
    override fun toUnix(text: CharSequence) = text
}
