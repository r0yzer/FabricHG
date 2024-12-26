package de.royzer.fabrichg.util

import kotlin.random.Random

// chat gpt digger
class WeightedCollection<T> {
    private val items = mutableListOf<Pair<T, Double>>()
    private var totalWeight = 0.0

    fun add(item: T, weight: Double) {
        require(weight > 0) { "Weight must be greater than zero" }
        items.add(item to weight)
        totalWeight += weight
    }

    fun get(): T? {
        if (items.isEmpty()) return null

        var randomValue = Random.nextDouble(0.0, totalWeight)
        for ((item, weight) in items) {
            randomValue -= weight
            if (randomValue <= 0) {
                return item
            }
        }
        return null
    }
}