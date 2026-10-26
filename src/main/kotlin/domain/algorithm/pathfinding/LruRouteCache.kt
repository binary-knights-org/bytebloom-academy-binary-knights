package domain.algorithm.pathfinding

import domain.model.Warehouse

class LruRouteCache( private val capacity: Int = DEFAULT_CAPACITY
) {
    init {
        if (capacity <= 0) {
            throw IllegalArgumentException(
                "Cache capacity must be greater than zero."
            )
        }
    }

    private class Node(
        val key: RouteKey,
        var path: List<Warehouse>,
        var previous: Node? = null,
        var next: Node? = null
    )

    private val nodes = mutableMapOf<RouteKey, Node>()

    private var head: Node? = null
    private var tail: Node? = null

    @Synchronized
    fun getRoute(key: RouteKey): List<Warehouse>? {
        val node = nodes[key] ?: return null

        moveToHead(node)

        return node.path
    }

    @Synchronized
    fun storeRoute(key: RouteKey, path: List<Warehouse>) {
        val existingNode = nodes[key]

        if (existingNode != null) {
            existingNode.path = path
            moveToHead(existingNode)
            return
        }

        val newNode = Node(key, path)

        nodes[key] = newNode
        addToHead(newNode)

        if (nodes.size > capacity) {
            removeTail()
        }
    }

    private fun moveToHead(node: Node) {
        removeNode(node)
        addToHead(node)
    }

    private fun addToHead(node: Node) {
        node.previous = null
        node.next = head

        head?.previous = node
        head = node

        if (tail == null) {
            tail = node
        }
    }

    private fun removeNode(node: Node) {
        val previousNode = node.previous
        val nextNode = node.next

        if (previousNode == null) {
            head = nextNode
        } else {
            previousNode.next = nextNode
        }

        if (nextNode == null) {
            tail = previousNode
        } else {
            nextNode.previous = previousNode
        }

        node.previous = null
        node.next = null
    }

    private fun removeTail() {
        val leastRecentlyUsed = tail ?: return

        removeNode(leastRecentlyUsed)
        nodes.remove(leastRecentlyUsed.key)
    }

    companion object {
        const val DEFAULT_CAPACITY = 50
    }

}
