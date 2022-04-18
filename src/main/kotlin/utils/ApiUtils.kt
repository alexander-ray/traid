package utils

fun Map<String, String>.toQueryString(): String = this.map { "${it.key}=${it.value}" }.joinToString("&")