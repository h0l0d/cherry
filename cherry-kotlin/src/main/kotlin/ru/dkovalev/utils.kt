package ru.dkovalev

@Suppress("UNCHECKED_CAST")
fun <V> Map<String?, V>.filterNotNullOrEmptyKeys(): Map<String, V> {
    return filterKeys { k -> !k.isNullOrEmpty() } as Map<String, V>
}
