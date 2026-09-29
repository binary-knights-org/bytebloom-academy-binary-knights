//package domain.usecase.crud.vehicle
//
//import domain.model.RegionalZone
//import domain.model.Warehouse
//import domain.model.input.CreateVehicleInput
//import domain.repository.VehicleRepository
//import domain.validator.vehicle.CreateVehicleValidator
//import domain.validator.ValidationResult // افترضنا وجود كائن نتيجة التحقق
//import domain.validator.vehicle.VehicleValidationError
//import io.mockk.coEvery
//import io.mockk.coVerify
//import io.mockk.every
//import io.mockk.mockk
//import kotlinx.coroutines.test.runTest
//import kotlin.test.Test
//import kotlin.test.assertTrue
//
//class CreateVehicleUseCaseTest {
//
//    // 1. إنشاء الاعتمادات الوهمية (Mocks)
//    private val vehicleRepository = mockk<VehicleRepository>()
//    private val validator = mockk<CreateVehicleValidator>()
//
//    // 2. إحقان الاعتمادات في الكلاس المراد اختباره
//    private val useCase = CreateVehicleUseCase(vehicleRepository, validator)
//
//    private val origin = Warehouse(
//        id = "WH-1",
//        name = "Origin Warehouse",
//        regionalZone = RegionalZone.NORTH,
//        latitude = 31.95,
//        longitude = 35.91
//    )
//
//    @Test
//    fun `should create vehicle successfully`() = runTest {
//        // Given
//        val input = CreateVehicleInput(
//            id = "TRK-101",
//            maxCapacityKg = 7046.91,
//            costPerKm = 3.15,
//            currentHub = origin
//        )
//        // تحديد سلوك الـ Validator والـ Repository كلاهما بالنجاح
//        every { validator.validate(input) } returns ValidationResult.Valid
//        coEvery { vehicleRepository.create(any()) } returns true
//
//        // When
//        val result = useCase(input)
//
//        // Then
//        assertTrue(result.isSuccess)
//        coVerify(exactly = 1) { vehicleRepository.create(any()) }
//    }
//
//    @Test
//    fun `should fail when vehicle creation fails in repository`() = runTest {
//        // Given
//        val input = CreateVehicleInput(
//            id = "TRK-101",
//            maxCapacityKg = 4500.91,
//            costPerKm = 5.15,
//            currentHub = origin
//        )
//        every { validator.validate(input) } returns ValidationResult.Valid
//        coEvery { vehicleRepository.create(any()) } returns false
//
//        // When
//        val result = useCase(input)
//
//        // Then
//        assertTrue(result.isFailure)
//        coVerify(exactly = 1) { vehicleRepository.create(any()) }
//    }
//
//    @Test
//    fun `should fail validation when maxCapacityKg is not positive`() = runTest {
//        // Given
//        val input = CreateVehicleInput(
//            id = "TRK-101",
//            maxCapacityKg = 0.0,
//            costPerKm = 3.15,
//            currentHub = origin
//        )
//        // تحديد سلوك الـ Validator بالفشل
//        every { validator.validate(input) } returns ValidationResult.Invalid(listOf(VehicleValidationError.InvalidCapacity))
//
//        // When
//        val result = useCase(input)
//
//        // Then
//        assertTrue(result.isFailure)
//
//        // التحقق من أن المستودع لم يُستدَعَ إطلاقاً لأن التحقق فشل قبله
//        coVerify(exactly = 0) { vehicleRepository.create(any()) }
//    }
//
//    @Test
//    fun `should fail validation when costPerKm is not positive`() = runTest {
//        // Given
//        val input = CreateVehicleInput(
//            id = "TRK-101",
//            maxCapacityKg = 7046.91,
//            costPerKm = -1.0,
//            currentHub = origin
//        )
//        every { validator.validate(input) } returns ValidationResult.Invalid(listOf(VehicleValidationError.InvalidCost))
//
//        // When
//        val result = useCase(input)
//
//        // Then
//        assertTrue(result.isFailure)
//        coVerify(exactly = 0) { vehicleRepository.create(any()) }
//    }
//}