package com.velichkomarija.recognizesymbolapp.utils

class Result(probs: FloatArray, timeCost: Long) {
    val numberOfClass: Int
    val probabilityValue: Float
    val timeCostValue: Long = timeCost

    init {
        numberOfClass = argmax(probs)
        probabilityValue = probs[numberOfClass]
    }

    fun argmax(probs: FloatArray): Int {
        var maxIdx = -1
        var maxProb = 0.0f

        for (i in probs.indices) {
            if (probs[i] > maxProb) {
                maxProb = probs[i]
                maxIdx = i;
            }
        }
        return maxIdx
    }

}