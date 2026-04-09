package com.manish.mindora.presentation.util

private val WORD_SPLIT_REGEX = "\\s+".toRegex()

fun countWords(text: String): Int {
    val t = text.trim()
    if (t.isEmpty()) return 0
    return t.split(WORD_SPLIT_REGEX).count { it.isNotEmpty() }
}
