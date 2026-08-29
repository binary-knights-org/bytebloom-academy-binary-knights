package domain.usecase

import domain.model.tree.HubNode

class TraceHubLineageUseCase {

    operator fun invoke(node: HubNode): List<HubNode> {
        return generateSequence(node) { currentNode ->
            when (currentNode) {
                is HubNode.GlobalHub -> null
                is HubNode.RegionalCenter -> currentNode.parent
                is HubNode.LocalDepot -> currentNode.parent
            }
        }.toList()
    }
}