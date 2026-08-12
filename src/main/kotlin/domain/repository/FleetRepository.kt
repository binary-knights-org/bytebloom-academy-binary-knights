package domain.repository

import data.dataholder.FleetRaw

interface FleetRepository {
    fun getAllFleets(): List<FleetRaw>
}
