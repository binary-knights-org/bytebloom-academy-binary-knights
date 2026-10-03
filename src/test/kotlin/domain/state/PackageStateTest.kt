package domain.state

import com.google.common.truth.Truth.assertThat
import domain.model.Package
import domain.model.Priority
import domain.model.Warehouse
import domain.model.exception.IllegalStateTransitionException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PackageStateTest {

    private fun createPackage(): Package {
        val origin = mockk<Warehouse> {
            every { id } returns "WH-1"
        }

        val destination = mockk<Warehouse> {
            every { id } returns "WH-2"
        }

        return Package(
            weight = 10.0,
            priority = Priority.STANDARD,
            originHub = origin,
            destinationHub = destination
        )
    }

    @Test
    fun `package should start in created state`() {
        val packageItem = createPackage()

        assertThat(packageItem.getState())
            .isInstanceOf(CreatedState::class.java)
    }

    @Test
    fun `package should move from created to assigned to vehicle`() {
        val packageItem = createPackage()

        packageItem.assignToVehicle()

        assertThat(packageItem.getState())
            .isInstanceOf(AssignedToVehicleState::class.java)
    }

    @Test
    fun `package should move from assigned to in transit`() {
        val packageItem = createPackage()

        packageItem.assignToVehicle()
        packageItem.startTransit()

        assertThat(packageItem.getState())
            .isInstanceOf(InTransitState::class.java)
    }

    @Test
    fun `package should move from in transit to delivered`() {
        val packageItem = createPackage()

        packageItem.assignToVehicle()
        packageItem.startTransit()
        packageItem.markDelivered()

        assertThat(packageItem.getState())
            .isInstanceOf(DeliveredState::class.java)
    }

    @Test
    fun `package should move from in transit to delivery failed`() {
        val packageItem = createPackage()

        packageItem.assignToVehicle()
        packageItem.startTransit()
        packageItem.markFailed()

        assertThat(packageItem.getState())
            .isInstanceOf(DeliveryFailedState::class.java)
    }

    @Test
    fun `package should reject starting transit before assignment`() {
        val packageItem = createPackage()

        assertThrows<IllegalStateTransitionException> {
            packageItem.startTransit()
        }
    }

    @Test
    fun `package should reject delivery before transit`() {
        val packageItem = createPackage()

        assertThrows<IllegalStateTransitionException> {
            packageItem.markDelivered()
        }
    }

    @Test
    fun `package should reject failure before transit`() {
        val packageItem = createPackage()

        assertThrows<IllegalStateTransitionException> {
            packageItem.markFailed()
        }
    }

    @Test
    fun `package should reject assignment while in transit`() {
        val packageItem = createPackage()

        packageItem.assignToVehicle()
        packageItem.startTransit()

        assertThrows<IllegalStateTransitionException> {
            packageItem.assignToVehicle()
        }
    }

    @Test
    fun `package should reject starting transit after delivery`() {
        val packageItem = createPackage()

        packageItem.assignToVehicle()
        packageItem.startTransit()
        packageItem.markDelivered()

        assertThrows<IllegalStateTransitionException> {
            packageItem.startTransit()
        }
    }

    @Test
    fun `package should reject failure after delivery`() {
        val packageItem = createPackage()

        packageItem.assignToVehicle()
        packageItem.startTransit()
        packageItem.markDelivered()

        assertThrows<IllegalStateTransitionException> {
            packageItem.markFailed()
        }
    }

    @Test
    fun `package should reject delivery after failure`() {
        val packageItem = createPackage()

        packageItem.assignToVehicle()
        packageItem.startTransit()
        packageItem.markFailed()

        assertThrows<IllegalStateTransitionException> {
            packageItem.markDelivered()
        }
    }
}
