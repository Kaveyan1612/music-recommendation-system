package com.recommendation.dsa;

import java.util.*;

/**
 * Memory-efficient sparse matrix storing non-zero user-item listening interactions.
 * Space complexity: O(|E|) where |E| is the number of ratings.
 */
public class SparseMatrix {
    private final Map<Integer, Map<Integer, Float>> userToItems = new HashMap<>();
    private final Map<Integer, Map<Integer, Float>> itemToUsers = new HashMap<>();

    public void insert(int userId, int itemId, float rating) {
        userToItems.computeIfAbsent(userId, k -> new HashMap<>()).put(itemId, rating);
        itemToUsers.computeIfAbsent(itemId, k -> new HashMap<>()).put(userId, rating);
    }

    public Map<Integer, Float> getUserRatings(int userId) {
        return userToItems.getOrDefault(userId, Collections.emptyMap());
    }

    public Map<Integer, Float> getItemRatings(int itemId) {
        return itemToUsers.getOrDefault(itemId, Collections.emptyMap());
    }

    public Set<Integer> getAllUsers() {
        return userToItems.keySet();
    }

    public Set<Integer> getAllItems() {
        return itemToUsers.keySet();
    }

    public boolean hasRated(int userId, int itemId) {
        Map<Integer, Float> ratings = userToItems.get(userId);
        return ratings != null && ratings.containsKey(itemId);
    }
}
