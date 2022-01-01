package leetcode.dfs.easy;

import helper.gzlinkedlist.GZLinkedList;
import helper.gzqueue.GZQueue;

import java.util.LinkedList;

public class LC1631 {

    // BFS+DP
    public int minimumEffortPath(int[][] heights) {
        int row = heights.length;
        int col = heights[0].length;
        int[][] DIR = new int[][]{{-1, 0}, {1, 0}, {0, 1}, {0, -1}};

        GZLinkedList<int[]> queue = new GZLinkedList<>();
        queue.offer(new int[]{0, 0});

        int[][] dp = new int[row][col];
        for(int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                dp[i][j] = (int)Math.pow(10, 6);
            }
        }
        dp[0][0] = 0;
        while (!queue.isEmpty()) {
            int[] index = queue.poll();
            int x = index[0];
            int y = index[1];
            for(int[] dir : DIR) {
                int nx = x + dir[0];
                int ny = y + dir[1];
                if (nx >= 0 && nx < row && ny >= 0 && ny < col) {
                    int miniEffort = Math.max(dp[x][y], heights[nx][ny] - heights[x][y]);
                    if (miniEffort < dp[nx][ny]) {
                        dp[nx][ny] = miniEffort;
                        queue.offer(new int[]{nx, ny});
                    }
                }
            }
        }

        return dp[row-1][col-1];
    }

    // BFS + BINARY SEARCH
    public int minimumEffortPath2(int[][] heights) {

        return 0;
    }

    public static void main(String[] args) {

    }
}
