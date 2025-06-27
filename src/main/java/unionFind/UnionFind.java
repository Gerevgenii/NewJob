package unionFind;

/**
 * This class is used to create a connection between lines.
 */
public class UnionFind {
    private final int[] parent;
    private final int[] rank;

    /**
     * A constructor that initializes the number of objects that need to be linked.
     * @param n Number of required objects
     */
    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
    }

    /**
     * Searches for the root for the current object.
     * @param x the object whose root we need to find
     * @return the root of the desired object
     */
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    /**
     * Creates a connection between two objects without erasing their old values.
     *
     * @param first int to combine
     * @param second int to combine
     */
    public void union(int first, int second) {
        final int rootFirst = find(first);
        final int rootSecond = find(second);
        if (rootFirst == rootSecond) {
            return;
        }
        if (rank[rootFirst] < rank[rootSecond]) {
            parent[rootFirst] = rootSecond;
        } else if (rank[rootFirst] > rank[rootSecond]) {
            parent[rootSecond] = rootFirst;
        } else {
            parent[rootSecond] = rootFirst;
            rank[rootFirst]++;
        }
    }
}
