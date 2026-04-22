package com.example.pexelsapp.domain.features.bookmarks.search.models

import javax.inject.Inject
import kotlin.math.min

class LevenshteinDistance @Inject constructor() {
    fun calculate(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }

        for (i in 0..s1.length) {
            for (j in 0..s2.length) {
                when {
                    i == 0 -> dp[i][j] = j
                    j == 0 -> dp[i][j] = i
                    else -> {
                        val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                        dp[i][j] = min(
                            min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + cost
                        )
                    }
                }
            }
        }

        return dp[s1.length][s2.length]
    }
}
