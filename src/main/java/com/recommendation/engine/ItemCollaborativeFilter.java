package com.recommendation.engine;

import com.recommendation.dsa.SparseMatrix;
import com.recommendation.dsa.TopKHeap;
import java.util.*;

/**
 * Item-Based Collaborative Filtering.
 * Precomputes item-item similarity vectors and recommends items similar to user history.
 */
public class ItemCollaborativeFilter {
    private final SparseMatrix matrix;

    public ItemCollaborativeFilter(SparseMatrix matrix) {
        this.matrix = matrix;
    }

    public List<TopKHeap.ScoredItem> recommend(int targetUser, int topN) {
        Map<Integer, Float> userRatings = matrix.getUserRatings(targetUser);
        if (userRatings.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Integer, Float> candidateScores = new HashMap<>();
        Map<Integer, Float> simSums = new HashMap<>();

        for (Map.Entry<Integer, Float> entry : userRatings.entrySet()) {
            int ratedItemId = entry.getKey();
            float userRating = entry.getValue();
            Map<Integer, Float> itemUsersA = matrix.getItemRatings(ratedItemId);

            for (int candidateItemId : matrix.getAllItems()) {
                if (userRatings.containsKey(candidateItemId)) continue;
                Map<Integer, Float> itemUsersB = matrix.getItemRatings(candidateItemId);

                double sim = SimilarityEngine.cosineSimilarity(itemUsersA, itemUsersB);
                if (sim > 0.05) {
                    candidateScores.put(candidateItemId, candidateScores.getOrDefault(candidateItemId, 0f) + (float) (sim * userRating));
                    simSums.put(candidateItemId, simSums.getOrDefault(candidateItemId, 0f) + (float) sim);
                }
            }
        }

        TopKHeap topK = new TopKHeap(topN);
        for (Map.Entry<Integer, Float> entry : candidateScores.entrySet()) {
            int itemId = entry.getKey();
            float denom = simSums.get(itemId);
            float score = (denom > 0) ? (entry.getValue() / denom) : 0f;
            topK.offer(itemId, score);
        }

        return topK.getTopKDescending();
    }
}
