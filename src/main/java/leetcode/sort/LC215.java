package leetcode.sort;

import helper.PrintUtils;
import java.util.function.*;

public class LC215 {

    public int findKthLargest(int[] nums, int k) {
        if (nums == null || nums.length == 0) {
            throw new IllegalArgumentException();
        }

        int pivot = partition(nums, 0, nums.length - 1);
        k = nums.length - k;
        while (pivot != k) {
            if (pivot > k) {
                pivot = partition(nums, 0, pivot - 1);
            } else {
                pivot = partition(nums, pivot + 1, nums.length -1);
            }
        }
        return nums[pivot];
    }

    private int partition(int[] nums, int start, int end) {
        if (start == end) {
            return start;
        }

        // int r =(int)(Math.random() * (end - start + 1)) + start;
        BiFunction<Integer, Integer, Integer> randomFunc = (startIdx, endIdx) -> {
            return (int)(Math.random()) * (endIdx - startIdx + 1) + startIdx;
        };
        int r = getRandom(start, end, randomFunc);
        int pivot = nums[r];
        swap(nums, r, end);// move pivot to end.
        int less = start - 1;
        for(int i = start; i < end; i++) {
            if (nums[i] < pivot) {
                swap(nums, i, ++less);
            }
        }
        swap(nums, end, ++less);
        return less;
    }

    private int getRandom(int start, int end, BiFunction<Integer, Integer, Integer> biFunction) {
        return biFunction.apply(start, end);
    }


    private void swap(int[] nums, int i1, int i2) {
        int temp = nums[i1];
        nums[i1] = nums[i2];
        nums[i2] = temp;
    }

    public static void main(String[] args) {
        Supplier<LC215> lc215Supplier = () -> new LC215();

        BiFunction<int[], Integer, Integer> triFunction = (int[] nums, Integer k) -> lc215Supplier.get().findKthLargest(nums, k);

        int res = triFunction.apply(new int[]{3,2,1,5,6,4}, 3);
        PrintUtils.printString("res: " + res);
    }
}
