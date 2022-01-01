package leetcode.dfs.easy;

import helper.gzlinkedlist.GZLinkedList;

import java.util.Queue;

public class LC730 {

    /**
     * Description
     * You are starving and you want to eat food as quickly as possible. You want to find the shortest path to arrive at any food cell.
     *
     * You are given an m x n character matrix, grid, of these different types of cells:
     *
     * '*' is your location. There is exactly one '*' cell.
     * '#' is a food cell. There may be multiple food cells.
     * 'O' is free space, and you can travel through these cells.
     * 'X' is an obstacle, and you cannot travel through these cells.
     * You can travel to any adjacent cell north, east, south, or west of your current location if there is not an obstacle.
     *
     * Return the length of the shortest path for you to reach any food cell. If there is no path for you to reach food, return -1.
     *
     */
    public int getFood(char[][] grid) {
        int row = grid.length;
        int col = grid[0].length;

        GZLinkedList<int[]> queue = new GZLinkedList<>();
        for(int i = 0; i < row; i++) {
            for(int j = 0; j < col; j++) {
                if (grid[i][j] == '*') {
                    queue.offer(new int[]{i, j});
                }
            }
        }
        int[][] DIR = new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        int count = 0;

        while(!queue.isEmpty()) {
            int size = queue.length();
            count++;
            for(int i = 0; i < size; i++) {
                int[] pair = queue.poll();
                int x = pair[0];
                int y = pair[1];

                for (int[] dir : DIR) {
                    int nx = x + dir[0];
                    int ny = y + dir[1];

                    if (nx >= row || nx < 0 || ny >= col || ny < 0 || grid[nx][ny] == 'X') {
                        continue;
                    }

                    if (grid[nx][ny] == '#') {
                        return count;
                    }

                    grid[nx][ny] = 'X';
                    queue.offer(new int[]{nx, ny});
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {

    }
}
