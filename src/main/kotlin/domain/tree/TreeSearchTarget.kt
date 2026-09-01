package domain.tree

interface TreeSearchTarget {

    fun insert(id: String): TreeSearchTarget

    fun searchWithStepCount(targetId: String): Pair<Boolean, Int>
}
