package com.manish.mindora.domain.model

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
    ;

    fun toPreferenceString(): String = name.lowercase()

    companion object {
        fun fromPreferenceString(value: String?): ThemeMode {
            if (value.isNullOrBlank()) return SYSTEM
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: SYSTEM
        }
    }
}
