package me.tatarka.inject.test

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isSameAs
import assertk.assertions.isTrue
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.test.different.DifferentPackageFoo
import kotlin.test.Test

class ProvidesFoo(val bar: ProvidesBar? = null)

@Inject class ProvidesBar

@Component abstract class ProvidesFunctionComponent {
    var providesCalled = false

    abstract val foo: ProvidesFoo

    @Provides
    fun foo() = ProvidesFoo().also { providesCalled = true }
}

@Component abstract class ProvidesFunctionArgComponent {
    var providesCalled = false

    abstract val foo: ProvidesFoo

    @Provides
    fun foo(bar: ProvidesBar) = ProvidesFoo(bar).also { providesCalled = true }
}

@Component abstract class ProvidesValComponent {
    var providesCalled = false

    abstract val foo: ProvidesFoo

    val provideFoo
        @Provides get() = ProvidesFoo().also { providesCalled = true }
}

@Component abstract class ProvidesExtensionFunctionComponent {
    var providesCalled = false

    abstract val foo: ProvidesFoo

    @Provides
    fun ProvidesBar.provideFoo() = ProvidesFoo(this).also { providesCalled = true }
}

@Component abstract class ProvidesExtensionValComponent {
    var providesCalled = false

    abstract val foo: ProvidesFoo

    val ProvidesBar.provideFoo
        @Provides get() = ProvidesFoo(this).also { providesCalled = true }
}

@Component abstract class ProvidesValConstructorComponent(@get:Provides val provideFoo: ProvidesFoo) {
    abstract val foo: ProvidesFoo
}

class Foo1
class Foo2

@Inject class Foo3 : IFoo {
    override fun equals(other: Any?) = other is Foo

    override fun hashCode() = 0
}

@Component abstract class ProvidesOverloadsComponent {
    abstract val foo1: Foo1
    abstract val foo2: Foo2
    abstract val foo3: Foo3
    abstract val foo4: IFoo

    @Provides
    fun foo() = Foo1()

    @Provides
    fun foo(bar: ProvidesBar) = Foo2()

    val foo
        @Provides get() = Foo3()

    val Foo3.foo: IFoo
        @Provides get() = this
}

class IntFoo(val int: Int)

class IntArrayFoo(val intArray: IntArray)

class StringArrayFoo(val stringArray: Array<String>)

@Inject class BasicTypes(
    val boolean: Boolean,
    val byte: Byte,
    val char: Char,
    val short: Short,
    val int: Int,
    val long: Long,
    val float: Float,
    val double: Double,
    val string: String,
    val booleanArray: BooleanArray,
    val stringArray: Array<String>,
    val intFoo: IntFoo,
    val intArrayFoo: IntArrayFoo,
    val stringArrayFoo: StringArrayFoo
)

@Component abstract class ProvidesBasicTypes {
    abstract val basicType: BasicTypes

    val boolean
        @Provides get() = true

    val byte
        @Provides get() = 1.toByte()

    val char
        @Provides get() = 'a'

    val short
        @Provides get() = 2.toShort()

    val int
        @Provides get() = 3

    val long
        @Provides get() = 4L

    val float
        @Provides get() = 5f

    val double
        @Provides get() = 6.0

    val string
        @Provides get() = "b"

    val booleanArray
        @Provides get() = booleanArrayOf(true)

    val stringArray
        @Provides get() = arrayOf("c")

    val intArray
        @Provides get() = intArrayOf(7)

    val Int.bind
        @Provides get() = IntFoo(this)

    val IntArray.bind
        @Provides get() = IntArrayFoo(this)

    val Array<String>.bind
        @Provides get() = StringArrayFoo(this)
}

@Inject class ValConstructorBasicTypes(
    val boolean: Boolean,
    val string: String
)

@Component abstract class ProvidesValConstructorBasicTypes(
    @get:Provides val boolean: Boolean,
    @get:Provides val string: String
) {
    abstract val basicType: ValConstructorBasicTypes
}

@Component abstract class ProvidesInnerClassComponent {
    abstract val fooFactory: DifferentPackageFoo.Factory
}

class ProvidesTest {

    @Test fun generates_a_component_that_provides_a_dep_from_a_function() {
        val component = ProvidesFunctionComponent::class.create()

        component.foo
        assertThat(component.providesCalled).isTrue()
    }

    @Test fun generates_a_component_that_provides_a_dep_from_a_function_with_arg() {
        val component = ProvidesFunctionArgComponent::class.create()
        val foo: ProvidesFoo = component.foo

        assertThat(foo.bar).isNotNull()
        assertThat(component.providesCalled).isTrue()
    }

    @Test fun generates_a_component_that_provides_a_dep_from_a_val() {
        val component = ProvidesValComponent::class.create()

        component.foo
        assertThat(component.providesCalled).isTrue()
    }

    @Test fun generates_a_component_that_provides_a_deb_from_an_extension_function() {
        val component = ProvidesExtensionFunctionComponent::class.create()

        assertThat(component.foo.bar).isNotNull()
        assertThat(component.providesCalled).isTrue()
    }

    @Test fun generates_a_component_that_provides_a_deb_from_an_extension_val() {
        val component = ProvidesExtensionValComponent::class.create()

        assertThat(component.foo.bar).isNotNull()
        assertThat(component.providesCalled).isTrue()
    }

    @Test fun generates_a_component_that_provides_a_dep_from_a_constructor_val() {
        val foo = ProvidesFoo()
        val component = ProvidesValConstructorComponent::class.create(foo)

        assertThat(component.foo).isSameAs(foo)
    }

    @Test fun generates_a_component_that_provides_from_functions_with_the_same_name() {
        val component = ProvidesOverloadsComponent::class.create()

        assertThat(component.foo1).isNotNull()
        assertThat(component.foo2).isNotNull()
        assertThat(component.foo3).isNotNull()
        assertThat(component.foo4).isNotNull()
    }

    @Test fun generates_a_component_that_provides_basic_types() {
        val component = ProvidesBasicTypes::class.create()

        assertThat(component.basicType.boolean).isTrue()
        assertThat(component.basicType.byte).isEqualTo(1.toByte())
        assertThat(component.basicType.char).isEqualTo('a')
        assertThat(component.basicType.short).isEqualTo(2.toShort())
        assertThat(component.basicType.int).isEqualTo(3)
        assertThat(component.basicType.long).isEqualTo(4L)
        assertThat(component.basicType.float).isEqualTo(5f)
        assertThat(component.basicType.double).isEqualTo(6.0)
        assertThat(component.basicType.string).isEqualTo("b")
        assertThat(component.basicType.booleanArray.toTypedArray()).containsExactly(true)
        assertThat(component.basicType.stringArray).containsExactly("c")
        assertThat(component.basicType.intFoo.int).isEqualTo(3)
        assertThat(component.basicType.intArrayFoo.intArray.toTypedArray()).containsExactly(7)
        assertThat(component.basicType.stringArrayFoo.stringArray).containsExactly("c")
    }

    @Test fun generates_a_component_that_provides_val_constructor_basic_types() {
        val component = ProvidesValConstructorBasicTypes::class.create(
            true,
            "a"
        )

        assertThat(component.basicType.boolean).isTrue()
        assertThat(component.basicType.string).isEqualTo("a")
    }

    @Test fun generates_a_component_that_references_an_inner_class() {
        val component = ProvidesInnerClassComponent::class.create()

        assertThat(component.fooFactory).isNotNull()
    }
}