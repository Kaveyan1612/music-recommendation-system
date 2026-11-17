package com.recommendation;

import com.recommendation.dsa.SparseMatrix;
import com.recommendation.dsa.TopKHeap;
import com.recommendation.engine.ItemCollaborativeFilter;
import com.recommendation.engine.UserCollaborativeFilter;
import com.recommendation.model.Track;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("   Music Recommendation System — Collaborative Filtering & DSA Engine ");
        System.out.println("======================================================================\n");

        // 1. Curated Catalogue
        Map<Integer, Track> catalog = new HashMap<>();
        catalog.put(1, new Track(1, "Midnight City", "M83", "Synthwave / Electronic"));
        catalog.put(2, new Track(2, "Starboy", "The Weeknd", "R&B / Synthpop"));
        catalog.put(3, new Track(3, "Get Lucky", "Daft Punk", "Funk / Disco"));
        catalog.put(4, new Track(4, "Strobe", "deadmau5", "Progressive House"));
        catalog.put(5, new Track(5, "Clair de Lune", "Flight Facilities", "Indie Electronic"));
        catalog.put(6, new Track(6, "Breathe", "The Prodigy", "Big Beat / Electronic"));
        catalog.put(7, new Track(7, "Redbone", "Childish Gambino", "Funk / Soul"));
        catalog.put(8, new Track(8, "Instant Crush", "Daft Punk", "Electropop"));

        // 2. Build Sparse Interaction Matrix
        SparseMatrix matrix = new SparseMatrix();

        // User 1 (Likes Daft Punk, M83, The Weeknd)
        matrix.insert(1, 1, 5.0f);
        matrix.insert(1, 2, 4.5f);
        matrix.insert(1, 3, 5.0f);

        // User 2 (Likes Electronic / House)
        matrix.insert(2, 1, 4.5f);
        matrix.insert(2, 3, 4.8f);
        matrix.insert(2, 4, 5.0f);
        matrix.insert(2, 8, 4.2f);

        // User 3 (Likes Funk / Soul)
        matrix.insert(3, 3, 4.0f);
        matrix.insert(3, 7, 5.0f);

        // User 4 (New user profile: listened to Midnight City)
        int targetUser = 4;
        matrix.insert(targetUser, 1, 4.8f);

        System.out.println("Target User [ID 4] Historical Interactions:");
        for (int trackId : matrix.getUserRatings(targetUser).keySet()) {
            System.out.println("  • " + catalog.get(trackId));
        }
        System.out.println();

        // 3. User-Based Recommendations
        UserCollaborativeFilter userFilter = new UserCollaborativeFilter(matrix);
        long start = System.nanoTime();
        List<TopKHeap.ScoredItem> userRecs = userFilter.recommend(targetUser, 5, 3);
        long duration = System.nanoTime() - start;

        System.out.printf("--- User-Based Collaborative Filtering (Elapsed: %.2f ms) ---\n", duration / 1_000_000.0);
        for (TopKHeap.ScoredItem item : userRecs) {
            Track t = catalog.get(item.itemId);
            System.out.printf("  ★ Predicted Rating: %.2f/5.00 | %s\n", item.score, t);
        }
        System.out.println();

        // 4. Item-Based Recommendations
        ItemCollaborativeFilter itemFilter = new ItemCollaborativeFilter(matrix);
        start = System.nanoTime();
        List<TopKHeap.ScoredItem> itemRecs = itemFilter.recommend(targetUser, 3);
        duration = System.nanoTime() - start;

        System.out.printf("--- Item-Based Collaborative Filtering (Elapsed: %.2f ms) ---\n", duration / 1_000_000.0);
        for (TopKHeap.ScoredItem item : itemRecs) {
            Track t = catalog.get(item.itemId);
            System.out.printf("  ★ Item Similarity Score: %.2f/5.00 | %s\n", item.score, t);
        }
        System.out.println("\n======================================================================");
    }
}
