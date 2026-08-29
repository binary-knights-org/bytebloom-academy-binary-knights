package domain.usecase

import domain.repository.WarehouseRepository

private const val DEFAULT_AVERAGE_WEIGHT = 0.0

class CalculateAveragePackageWeightUseCase (
    private val warehouseRepository: WarehouseRepository
){
    operator fun invoke(): Double{
       val allPackage = warehouseRepository
            .getAllWarehouses()
            .flatMap { it.cargoQueue }
       val totalWeight =  allPackage.sumOf { it.weight }
       val packageCount = allPackage.size

        val result = if (allPackage.isNotEmpty()){
            totalWeight / packageCount
        } else {
            DEFAULT_AVERAGE_WEIGHT
        }
    return  result

}
}
