package leetcode.dfs.medium;

import java.util.LinkedList;
import java.util.Queue;

public class LC1926 {


    public int nearestExit(char[][] maze, int[] entrance) {
        int row = maze.length;
        int col = maze[0].length;

        boolean[][] visited = new boolean[row][col];
        for(int i = 0; i < row; i++) {
            for(int j = 0; j < col; j++) {
                if (maze[i][j] == '+') {
                    visited[i][j] = true;
                }
            }
        }


        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{entrance[0], entrance[1]});
        visited[entrance[0]][entrance[1]] = true;

        int step = 1;
        int[][] DIR = new int[row][col];

        while(!queue.isEmpty()) {
            int size = queue.size();
            for(int i = 0; i < size; i++) {
                int[] top = queue.poll();
                int x = top[0];
                int y = top[1];
                for (int[] dir : DIR) {
                    int nx = x + dir[0];
                    int ny = x + dir[1];

                    if (nx < 0 || nx < y || nx >= row || ny >= col || visited[nx][ny] == true) {
                        continue;
                    }
                    if (nx == 0 || nx == row - 1 || ny == 0 || ny == col - 1 || visited[nx][ny] == true) {
                        return step;
                    }
                    queue.offer(new int[]{nx, ny});
                    visited[nx][ny] = true;
                }
            }
            step++;
        }
        return -1;
    }
    public static void main(String[] args) {

    }
}
