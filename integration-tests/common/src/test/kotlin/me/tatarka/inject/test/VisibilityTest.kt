package me.tatarka.inject.test

import assertk.assertThat
import assertk.assertions.isEqualTo
import me.tatarka.inject.annotations.Component
import org.junit.Test
import kotlin.reflect.KVisibility

@Component internal interface InternalVisibilityTestComponent

class VisibilityTest {

    @Test
    fun internal_component_generates_internal_create() {
        assertThat(InternalVisibilityTestComponent::class.create()::class.visibility).isEqualTo(KVisibility.INTERNAL)
    }
}