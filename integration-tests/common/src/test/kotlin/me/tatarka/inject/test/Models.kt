package me.tatarka.inject.test

import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.Scope
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER

interface IFoo

interface IBar

var fooConstructorCount = 0

@Inject class Foo : IFoo {
    init {
        fooConstructorCount++
    }

    override fun equals(other: Any?) = other is Foo

    override fun hashCode() = 0
}

@Inject data class Bar(val foo: Foo) : IFoo

@Inject data class BarImpl(val foo: IFoo) : IBar

@Inject data class BarImpl2(val foo: IFoo) : IFoo

@Inject class Baz : IFoo

@Inject data class Bar2(val bar: Bar) : IFoo

@Inject data class Bar3(val bar: Bar) : IFoo

@Inject internal class InternalFoo

class InternalBarConstructor @Inject internal constructor(internal val foo: InternalFoo)

class NamedFoo(val name: String)

interface INamedBar {
    val name: String
}

class NamedBar(override val name: String) : INamedBar

@Scope
@Target(CLASS, FUNCTION, PROPERTY_GETTER)
annotation class CustomScope

var customScopeBarConstructorCount = 0

@CustomScope @Inject class CustomScopeBar {
    init {
        customScopeBarConstructorCount++
    }
}