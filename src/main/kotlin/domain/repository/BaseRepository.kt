package domain.repository

interface BaseRepository<T, ID> {
    suspend fun getAll(): List<T>
    suspend fun getById(id: ID): T?
    suspend fun create(item: T): T
    suspend fun update(item: T): T
    suspend fun delete(id: ID)
}
