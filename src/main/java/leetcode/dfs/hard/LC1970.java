package leetcode.dfs.hard;

import helper.PrintUtils;
import helper.gzlinkedlist.GZLinkedList;

public class LC1970 {

    /**
     * any cells in bottom row by starting from any cells in top row.
     * Input: row = 2, col = 2, cells = [[1,1],[2,1],[1,2],[2,2]]
     * convert to cells = [[0,0],[1,0],[0,1],[1,1]]
     * solution:
     * @link https://leetcode.com/problems/last-day-where-you-can-still-cross/discuss/1403907/C%2B%2BJavaPython-Binary-Search-and-BFS-Clean-and-Concise
     */
    public int latestDayToCross(int row, int col, int[][] cells) {
        if (cells == null || cells.length == 0 || cells[0].length == 0) {
            return 0;
        }
        int ans = -1;
        int left = 1;
        int right = cells.length;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            PrintUtils.printString("mid: " + mid);
            if (canWork(row, col, cells, mid)) {
                left = mid + 1;
                ans = mid;
            } else {
                right = mid - 1;
            }
        }
        return ans;
    }

    /**
     * How to check if we can walk in dayAt th day?
     * Firstly, we build the grid in the dayAt th day
     * Then we use BFS to check if we can reach to the
     * any cells in bottom row by starting from any cells in top row.
     * Input: row = 2, col = 2, cells = [[1,1],[2,1],[1,2],[2,2]]
     */
    int[][] DIR = new int[][]{{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
    private boolean canWork(int row, int col, int[][] cells, int dayAt) {
        int[][] grid = new int[row][col];
        for(int day = 0; day < dayAt; day++) {
            int r = cells[day][0]; // 1 -> 1 - 1 = 0
            int c = cells[day][1]; // 1 -> 1 - 1 = 0
            grid[r-1][c-1] = 1;
        }

        GZLinkedList<int[]> queue = new GZLinkedList<>();
        for(int c = 0; c < col; c++) {
            if (grid[0][c] == 0) {
                queue.offer(new int[]{0, c});
                grid[0][c] = 1;
            }
        }

        for (int i = 0; i < row; i++) {
            for(int j = 0; j < col; j++) {
                PrintUtils.printStringWithoutNewLine("[" + i + "][" + j + "]:" + grid[i][j] + ", ");
            }
            PrintUtils.printString("\n");
        }

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int r = curr[0], c = curr[1];
            PrintUtils.printString("r:" + r);
            if (r == row - 1) {
                return true;
            }

            for(int[] dir : DIR) {
                int nr = r + dir[0];
                int nc = c + dir[1];
                if (nr < 0 || nc < 0 || nr >= row || nc >= col || grid[nr][nc] == 1) {
                    continue;
                }
                grid[nr][nc] = 1;
                queue.offer(new int[]{nr, nc});
            }
        }
        return false;
    }

    public static void main(String[] args) {
        LC1970 obj = new LC1970();
        int res = obj.latestDayToCross(2, 2, new int[][]{{1,1},{2,1},{1,2},{2,2}});
        PrintUtils.printString("res:" + res);
    }
}
