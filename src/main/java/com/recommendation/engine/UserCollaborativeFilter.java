package com.recommendation.engine;

import com.recommendation.dsa.SparseMatrix;
import com.recommendation.dsa.TopKHeap;
import java.util.*;

/**
 * User-Based Collaborative Filtering using K-Nearest Neighbors (KNN).
 */
public class UserCollaborativeFilter {
    private final SparseMatrix matrix;

    public UserCollaborativeFilter(SparseMatrix matrix) {
        this.matrix = matrix;
    }

    public List<TopKHeap.ScoredItem> recommend(int targetUser, int kNeighbors, int topN) {
        Map<Integer, Float> targetRatings = matrix.getUserRatings(targetUser);
        if (targetRatings.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. Find K most similar users
        PriorityQueue<UserSim> neighborHeap = new PriorityQueue<>(kNeighbors);
        for (int otherUser : matrix.getAllUsers()) {
            if (otherUser == targetUser) continue;
            Map<Integer, Float> otherRatings = matrix.getUserRatings(otherUser);
            double sim = SimilarityEngine.cosineSimilarity(targetRatings, otherRatings);
            if (sim > 0.05) {
                if (neighborHeap.size() < kNeighbors) {
                    neighborHeap.offer(new UserSim(otherUser, (float) sim));
                } else if (sim > neighborHeap.peek().sim) {
                    neighborHeap.poll();
                    neighborHeap.offer(new UserSim(otherUser, (float) sim));
                }
            }
        }

        // 2. Score candidate items not yet listened to by targetUser
        Map<Integer, Float> scoreNumerator = new HashMap<>();
        Map<Integer, Float> scoreDenominator = new HashMap<>();

        for (UserSim neighbor : neighborHeap) {
            Map<Integer, Float> neighborRatings = matrix.getUserRatings(neighbor.userId);
            for (Map.Entry<Integer, Float> entry : neighborRatings.entrySet()) {
                int itemId = entry.getKey();
                float rating = entry.getValue();

                if (!targetRatings.containsKey(itemId)) {
                    scoreNumerator.put(itemId, scoreNumerator.getOrDefault(itemId, 0f) + neighbor.sim * rating);
                    scoreDenominator.put(itemId, scoreDenominator.getOrDefault(itemId, 0f) + neighbor.sim);
                }
            }
        }

        // 3. Extract Top N via Min-Heap
        TopKHeap topKHeap = new TopKHeap(topN);
        for (Map.Entry<Integer, Float> entry : scoreNumerator.entrySet()) {
            int itemId = entry.getKey();
            float denom = scoreDenominator.get(itemId);
            float predictedRating = (denom > 0) ? (entry.getValue() / denom) : 0f;
            topKHeap.offer(itemId, predictedRating);
        }

        return topKHeap.getTopKDescending();
    }

    private static class UserSim implements Comparable<UserSim> {
        final int userId;
        final float sim;

        UserSim(int userId, float sim) {
            this.userId = userId;
            this.sim = sim;
        }

        @Override
        public int compareTo(UserSim o) {
            return Float.compare(this.sim, o.sim); // Min-heap
        }
    }
}
