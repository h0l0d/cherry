package ru.dkovalev

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

internal class UtilsKtTest {

    @Test
    fun filterNotNullOrEmptyKeys() {
        val map = mapOf(null to 1, "" to 2, "key" to 3)
        assertThat(map.filterNotNullOrEmptyKeys(), `is`(mapOf("key" to 3)))
    }
}