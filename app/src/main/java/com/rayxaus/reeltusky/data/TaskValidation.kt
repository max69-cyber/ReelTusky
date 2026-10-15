package com.rayxaus.reeltusky.data

/** returns trimmed string, or `null` if it is empty after trim */
fun normalizeTaskTitleOrNull(raw: String): String? = raw.trim().takeIf { it.isNotEmpty() }