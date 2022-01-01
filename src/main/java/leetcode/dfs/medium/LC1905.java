package leetcode.dfs.medium;

import java.util.LinkedList;
import java.util.Queue;

public class LC1905 {

    public int countSubIslands(int[][] grid1, int[][] grid2) {
        int row = grid1.length;
        int col = grid1[0].length;

        int res = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (grid2[i][j] == 1) {
                    res += bfs(i, j, row, col, grid1, grid2);
                }
            }
        }
        return res;
    }

    int[][] DIR = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    private int bfs(int x, int y, int row, int col, int[][] grid1, int[][] grid2) {
        if (grid1[x][y] == 0) {
            return 0;
        }
        // grid1 and grid2 are both 1
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{x, y});
        grid2[x][y] = 1;
        int res = 0;
        while(!queue.isEmpty()) {
            int[] top = queue.poll();
            int _x = top[0];
            int _y = top[1];
            for (int[] dir : DIR) {
                int nx = _x + dir[0];
                int ny = _y + dir[1];
                if (nx >= 0 && nx < row && ny >= 0 && ny < col && grid2[nx][ny] == 1) {
                    if (grid1[nx][ny] == 0) {
                        res = 0;
                    }
                    grid2[nx][ny] = 0;
                    queue.offer(new int[]{nx, ny});
                }
            }
        }
        return res;
    }

    public static void main(String[] args) {

    }
}
