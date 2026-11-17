package com.recommendation;

import com.recommendation.dsa.SparseMatrix;
import com.recommendation.dsa.TopKHeap;
import com.recommendation.engine.ItemCollaborativeFilter;
import com.recommendation.engine.SimilarityEngine;
import com.recommendation.engine.UserCollaborativeFilter;

import java.util.*;

public class RecommendationTest {
    public static void main(String[] args) {
        testCosineSimilarityIdentical();
        testCosineSimilarityOrthogonal();
        testTopKHeapOrdering();
        testUserCollaborativeFilteringRecommendations();
        testItemCollaborativeFilteringRecommendations();
        System.out.println("ALL 5 RECOMMENDATION ENGINE TESTS PASSED SUCCESSFULLY!");
    }

    private static void testCosineSimilarityIdentical() {
        Map<Integer, Float> a = Map.of(1, 5f, 2, 4f);
        Map<Integer, Float> b = Map.of(1, 5f, 2, 4f);
        double sim = SimilarityEngine.cosineSimilarity(a, b);
        assert Math.abs(sim - 1.0) < 1e-5 : "Expected 1.0 for identical vectors";
    }

    private static void testCosineSimilarityOrthogonal() {
        Map<Integer, Float> a = Map.of(1, 5f);
        Map<Integer, Float> b = Map.of(2, 5f);
        double sim = SimilarityEngine.cosineSimilarity(a, b);
        assert sim == 0.0 : "Expected 0.0 for disjoint vectors";
    }

    private static void testTopKHeapOrdering() {
        TopKHeap heap = new TopKHeap(3);
        heap.offer(101, 2.5f);
        heap.offer(102, 4.8f);
        heap.offer(103, 1.2f);
        heap.offer(104, 3.9f);

        List<TopKHeap.ScoredItem> top = heap.getTopKDescending();
        assert top.size() == 3 : "Expected 3 items in top-3 heap";
        assert top.get(0).itemId == 102 && top.get(0).score == 4.8f;
        assert top.get(1).itemId == 104 && top.get(1).score == 3.9f;
        assert top.get(2).itemId == 101 && top.get(2).score == 2.5f;
    }

    private static void testUserCollaborativeFilteringRecommendations() {
        SparseMatrix matrix = new SparseMatrix();
        matrix.insert(1, 10, 5f);
        matrix.insert(1, 20, 4f);

        matrix.insert(2, 10, 5f);
        matrix.insert(2, 30, 4.5f);

        UserCollaborativeFilter ucf = new UserCollaborativeFilter(matrix);
        List<TopKHeap.ScoredItem> recs = ucf.recommend(1, 3, 2);
        assert !recs.isEmpty() : "Expected recommendation for user 1";
        assert recs.get(0).itemId == 30 : "Expected item 30 recommended from user 2";
    }

    private static void testItemCollaborativeFilteringRecommendations() {
        SparseMatrix matrix = new SparseMatrix();
        matrix.insert(1, 10, 5f);
        matrix.insert(1, 20, 5f);

        matrix.insert(2, 10, 4f);
        matrix.insert(2, 20, 4f);

        // User 3 only likes item 10, should recommend item 20
        matrix.insert(3, 10, 5f);

        ItemCollaborativeFilter icf = new ItemCollaborativeFilter(matrix);
        List<TopKHeap.ScoredItem> recs = icf.recommend(3, 2);
        assert !recs.isEmpty() : "Expected recommendation for user 3";
        assert recs.get(0).itemId == 20 : "Expected item 20 recommended via item-item similarity";
    }
}
