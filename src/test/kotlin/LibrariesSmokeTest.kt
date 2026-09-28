import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

interface UserRepository {
    fun getName(): String
}

class UserService(
    private val repository: UserRepository
) {
    fun getName(): String {
        return repository.getName()
    }
}

class UserServiceTest : KoinComponent {

    @Test
    fun `should return user name`() {

        // 1. Mock Repository
        val repository = mockk<UserRepository>()

        // 2. Define Mock behavior
        every {
            repository.getName()
        } returns "Ibrahim"

        // 3. Koin Module
        val testModule = module {
            single<UserRepository> {
                repository
            }

            single {
                UserService(get())
            }
        }

        // 4. Start Koin
        startKoin {
            modules(testModule)
        }

        // 5. Get Service from Koin
        val service: UserService by inject()

        // 6. Execute
        val result = service.getName()

        // 7. Assert
        assertThat(result).isEqualTo("Ibrahim")

        // 8. Verify Mock
        verify {
            repository.getName()
        }

        // 9. Stop Koin
        stopKoin()
    }
}
