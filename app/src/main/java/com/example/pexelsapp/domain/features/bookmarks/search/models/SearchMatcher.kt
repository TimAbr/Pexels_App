package com.example.pexelsapp.domain.features.bookmarks.search.models

import javax.inject.Inject

class SearchMatcher @Inject constructor(
    private val levenshteinDistance: LevenshteinDistance
) {

    fun match(query: String, text: String): Double {
        val queryWords = tokenize(query)
        if (queryWords.isEmpty()) return 1.0

        val textWords = tokenize(text)
        if (textWords.isEmpty()) return 0.0

        var totalScore = 0.0
        var matchedQueryWords = 0

        for (qWord in queryWords) {
            var bestWordScore = 0.0

            for (tWord in textWords) {
                val score = matchWord(qWord, tWord)
                if (score > bestWordScore) {
                    bestWordScore = score
                }
            }

            if (bestWordScore >= MIN_SIMILARITY_SCORE) {
                totalScore += bestWordScore
                matchedQueryWords++
            } else {
                return 0.0
            }
        }

        return if (matchedQueryWords == queryWords.size) {
            totalScore / queryWords.size
        } else {
            0.0
        }
    }

    private fun tokenize(text: String): List<String> {
        return text.lowercase()
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }
    }

    private fun matchWord(queryWord: String, targetWord: String): Double {
        val queryLength = queryWord.length
        if (queryLength == 0) return 1.0

        val targetPrefix = if (targetWord.length > queryLength) {
            targetWord.substring(0, queryLength)
        } else {
            targetWord
        }

        val errors = levenshteinDistance.calculate(queryWord, targetPrefix)
        val maxErrors = when {
            queryLength <= 3 -> 0
            queryLength <= 6 -> 1
            else -> 2
        }

        if (errors > maxErrors) return 0.0

        return (1.0 - (errors.toDouble() / queryLength)).coerceIn(0.0, 1.0)
    }

    companion object {
        const val MIN_SIMILARITY_SCORE = 0.6
    }
}
