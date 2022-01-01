package leetcode.dfs.hard;

import helper.PrintUtils;

import java.util.LinkedList;
import java.util.Queue;

public class LC1765 {

    public int[][] highestPeak(int[][] isWater) {
        int row = isWater.length;
        int col = isWater[0].length;
        int height = 0;
        int count = 0;
        int[][] res = new int[row][col];
        int[][] DIR = new int[][]{{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
        Queue<int[]> queue = new LinkedList<>();
        for(int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (isWater[i][j] == 1) {
                    queue.offer(new int[]{i, j});
                    res[i][j] = 0;
                    count++;
                } else {
                    res[i][j] = -1;
                }
            }
        }
        PrintUtils.printMatrixInt(res);

        while (!queue.isEmpty()) {
            int len = queue.size();
            height = height + 1;
            for (int i = 0; i < len; i++) {
                int[] top = queue.poll();
                int x = top[0];
                int y = top[1];
                for(int[] dir : DIR) {
                    int nx = x + dir[0];
                    int ny = y + dir[1];
                    if (nx >= 0 && nx < row && ny >= 0 && ny < col && res[nx][ny] == -1) {
                        res[nx][ny] = height;
                        count++;
                        queue.offer(new int[]{nx, ny});
                    }
                    if (count == row * col) {
                        return res;
                    }
                }
            }
        }
        return res;
    }

    public static void main(String[] args) {
        int[][] isWaters = new int[][]{{0,0,1}, {1,0,0}, {0,0,0}};
        LC1765 obj = new LC1765();
        int[][] res = obj.highestPeak(isWaters);
        PrintUtils.printMatrixInt(res);
    }
}
