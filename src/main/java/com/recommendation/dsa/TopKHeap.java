package com.recommendation.dsa;

import java.util.*;

/**
 * Min-Heap Top-K Priority Queue.
 * Keeps only the top K highest scoring candidates in O(N log K) time.
 */
public class TopKHeap {
    public static class ScoredItem implements Comparable<ScoredItem> {
        public final int itemId;
        public final float score;

        public ScoredItem(int itemId, float score) {
            this.itemId = itemId;
            this.score = score;
        }

        @Override
        public int compareTo(ScoredItem other) {
            return Float.compare(this.score, other.score); // Min-heap order
        }
    }

    private final int k;
    private final PriorityQueue<ScoredItem> minHeap;

    public TopKHeap(int k) {
        this.k = k;
        this.minHeap = new PriorityQueue<>(k);
    }

    public void offer(int itemId, float score) {
        if (minHeap.size() < k) {
            minHeap.offer(new ScoredItem(itemId, score));
        } else if (score > minHeap.peek().score) {
            minHeap.poll();
            minHeap.offer(new ScoredItem(itemId, score));
        }
    }

    public List<ScoredItem> getTopKDescending() {
        List<ScoredItem> result = new ArrayList<>(minHeap);
        result.sort(Collections.reverseOrder());
        return result;
    }
}
