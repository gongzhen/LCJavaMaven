package leetcode.dfs.easy;

import helper.PrintUtils;
import helper.QuadrupleFunction;
import helper.gzlinkedlist.GZLinkedList;
import helper.gzstack.GZStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LC1971 {

    public boolean validPath(int n, int[][] edges, int start, int end) {
        if (edges == null || edges.length == 0 || edges[0].length == 0) {
            return false;
        }
        Map<Integer, List<Integer>> graph = new ConcurrentHashMap<>();
        for (int[] edge : edges) {
            if (!graph.containsKey(edge[0])) {
                graph.put(edge[0], new ArrayList<>());
            }
            graph.get(edge[0]).add(edge[1]);

            if (!graph.containsKey(edge[1])) {
                graph.put(edge[1], new ArrayList<>());
            }
            graph.get(edge[1]).add(edge[0]);

//            List<Integer> vertex = graph.getOrDefault(edge[0], new ArrayList<>());
//            vertex.add(edge[1]);
//            graph.put(edge[0], vertex);
        }

        boolean[] visited = new boolean[n];

        GZLinkedList<Integer> queue = new GZLinkedList<>();
        queue.offer(start);
        visited[start] = true;

        while (!queue.isEmpty()) {
            Integer _vertex = queue.poll();
            if (_vertex == end) {
                return true;
            }
            for(int node : graph.get(_vertex)) {
                if (visited[node] == false) {
                    queue.offer(node);
                    visited[node] = true;
                }
            }
        }
        return false;
    }

    public boolean validPathDFS(int n, int[][] edges, int start, int end) {
        if (edges == null || edges.length == 0 || edges[0].length == 0) {
            return true;
        }

        Map<Integer, List<Integer>> graph = new ConcurrentHashMap<>();

        for(int[] edge : edges) {
            if (!graph.containsKey(edge[0])) {
                graph.put(edge[0], new ArrayList<>());
            }
            graph.get(edge[0]).add(edge[1]);
            if (!graph.containsKey(edge[1])) {
                graph.put(edge[1], new ArrayList<>());
            }
            graph.get(edge[1]).add(edge[0]);
        }

        GZStack<Integer> stack = new GZStack<>();
        stack.push(start);
        boolean[] visited = new boolean[n];

        while (!stack.isEmpty()) {
            Integer top = stack.pop();
            if (top == end) {
                return true;
            }
            for(int node : graph.get(top)) {
                if (!visited[node]) {
                    stack.push(node);
                    visited[node] = true;
                }
            }
        }
        return false;
    }


    public static void main(String[] args) {
        LC1971 lc1971 = new LC1971();

        QuadrupleFunction<Integer, int[][], Integer, Integer, Boolean> triFunction = (Integer n, int[][] edges, Integer start, Integer end) -> lc1971.validPath(n, edges, start, end);
        boolean result = triFunction.apply(3, new int[][]{{0,1},{1,2},{2,0}}, 0, 2);
        PrintUtils.printString("res: " + result);

        result = triFunction.apply(6, new int[][]{{0,1},{0,2},{3,5},{5,4},{4,3}}, 0, 5);
        PrintUtils.printString("res: " + result);

        QuadrupleFunction<Integer, int[][], Integer, Integer, Boolean> triFunctionDFS = (Integer n, int[][] edges, Integer start, Integer end) -> lc1971.validPathDFS(n, edges, start, end);
        result = triFunctionDFS.apply(3, new int[][]{{0,1},{1,2},{2,0}}, 0, 2);
        PrintUtils.printString("res: " + result);

        result = triFunctionDFS.apply(6, new int[][]{{0,1},{0,2},{3,5},{5,4},{4,3}}, 0, 5);
        PrintUtils.printString("res: " + result);

    }
}
