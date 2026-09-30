package architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertTrue
import kotlin.test.Test


class CleanArchitectureTest {

    @Test
    fun `domain layer should not depend on data layer`() {
        Konsist.scopeFromProduction()
            .assertArchitecture {
                val domain = Layer("Domain", "domain..")
                val data = Layer("Data", "data..")

                domain.doesNotDependOn(data)
            }

    }

    @Test
    fun `domain layer should use only domain and standard library dependencies`() {
        Konsist.scopeFromProduction()
            .files
            .withPackage("domain..")
            .assertTrue { file ->
                file.imports.all { import ->
                    import.name.startsWith("domain.") ||
                            import.name.startsWith("kotlin.") ||
                            import.name.startsWith("java.")
                }
            }
    }


    @Test
    fun `usecases should have names ending with UseCase`() {
        Konsist.scopeFromProduction()
            .classes()
            .withPackage("domain.usecase..")
            .assertTrue {
                it.name.endsWith("UseCase")
            }
    }

    @Test
    fun `validators should have names ending with Validator`() {
        Konsist.scopeFromProduction()
            .classes()
            .withPackage("domain.validator..")
            .assertTrue {
                it.name.endsWith("Validator")
            }

    }

    @Test
    fun `use cases should have a public operator invoke function`() {
        Konsist.scopeFromProduction()
            .classes()
            .withPackage("domain.usecase..")
            .assertTrue {
                it.functions().any { function ->
                    function.name == "invoke" &&
                            function.hasOperatorModifier &&
                            function.hasPublicOrDefaultModifier
                }
            }
    }

    @Test
    fun `DTO classes should end with Dto and be annotated with @Serializable`() {
        Konsist.scopeFromProduction()
            .classes()
            .withPackage("data.remote.dto..")
            .assertTrue {
                it.name.endsWith("Dto") && it.hasAnnotationWithName("Serializable")
            }
    }


}
