package com.recommendation.engine;

import java.util.Map;

/**
 * Mathematical similarity functions: Cosine and Pearson Correlation.
 */
public class SimilarityEngine {

    public static double cosineSimilarity(Map<Integer, Float> vecA, Map<Integer, Float> vecB) {
        if (vecA == null || vecB == null || vecA.isEmpty() || vecB.isEmpty()) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (Map.Entry<Integer, Float> entry : vecA.entrySet()) {
            int key = entry.getKey();
            float valA = entry.getValue();
            normA += valA * valA;
            if (vecB.containsKey(key)) {
                dotProduct += valA * vecB.get(key);
            }
        }

        for (float valB : vecB.values()) {
            normB += valB * valB;
        }

        if (normA == 0.0 || normB == 0.0) return 0.0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
