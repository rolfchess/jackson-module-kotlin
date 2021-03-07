package com.fasterxml.jackson.module.kotlin.test.github

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class TestGithub425 {
    @Test
    fun `null or empty should map to optional null`() {
        val objectMapper = jacksonObjectMapper()
        assertEquals(CanHazDefaults(optional = null), objectMapper.readValue("{}"))
        assertEquals(CanHazDefaults(optional = null, optionalWithDefault = null), objectMapper.readValue("{\"optionalWithDefault\":null}"))
    }


    @Test
    fun `explicit null should not map to default`() {
        val objectMapper = jacksonObjectMapper()
        assertEquals(
                CanHazDefaults(optional = null),
                objectMapper.readValue("{}"),
                "Empty value should map to default."
        )

        assertEquals(
                CanHazDefaults(optional = null, optionalWithDefault = null),
                objectMapper.readValue("{\"optionalWithDefault\":null}"),
                "Explicit null should map to null"
        )

        // Mapping null to non-optional field shoud fail
        assertThrows<MissingKotlinParameterException>("Should throw a missing parameter exception") {
            objectMapper.readValue<CanHazDefaults>("{\"notOptionalWithDefault\":null}")
        }
    }

    @Test
    fun `null should map to default with nullIsSameAsDefault`() {
        val objectMapper = jacksonObjectMapper().apply {
            registerModules(KotlinModule(nullIsSameAsDefault = true))
        }
        assertEquals(CanHazDefaults(optional = null), objectMapper.readValue("{\"optionalWithDefault\":null}"))
        assertEquals(CanHazDefaults(optional = null), objectMapper.readValue("{\"notOptionalWithDefault\":null}"))
    }

}

private data class CanHazDefaults(
        val optional: String?,
        val optionalWithDefault: String? = "optional",
        val notOptionalWithDefault: String = "nonOptional"
)

private inline fun <reified T : Exception> assertThrows(message: String? = null, block: () -> Unit) {
    try {
        block()
        fail(message)
    } catch (e: Exception) {
        if (e !is T) {
            fail(message)
        }
    }
}