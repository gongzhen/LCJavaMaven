package helper;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PrintUtils {

    public static void printLine() {
        System.out.println("-------------------------------------------");
    }

    public static void printOpenBrackets() {
        System.out.println("[");
    }

    public static void printCloseBrackets() {
        System.out.println("]");
    }

    public static int getLineNumber() {
        return Thread.currentThread().getStackTrace()[2].getLineNumber();
    }

    public static void printBinaryString(int x) {
        printString(Integer.toBinaryString(x));
    }

    public static void printBool(boolean x) {
        if (x == true) {
            printString("true");
        } else {
            printString("false");
        }
    }

    public static void printString(String arg, Object o1, Object o2) {
        System.out.println(arg + ", " + o1.toString() + ", " + o2.toString());
    }

    public static void printString(String arg, Object object) {
        System.out.println(arg + object.toString());
    }

    public static void printString(String arg) {
        System.out.println(arg);
    }

    public static void printTreeNodeList(List<TreeNode> list) {
        printStringWithoutNewLine("[");
        for(TreeNode n : list) {
            printStringWithoutNewLine("n:" + n.val + ", ");
        }
        printStringWithoutNewLine("]");
    }

    public static void printDoubleArray(double[] list) {
        if(list == null) {
            return;
        }
        printStringWithoutNewLine("[");
        int i = 0;
        for(double n : list) {
            printStringWithoutNewLine("[" + i + "]" + n + ", ");
            i++;
        }
        printStringWithoutNewLine("]\n");
    }

    public static void printArray(int[] list) {
        if(list == null) {
            return;
        }
        printStringWithoutNewLine("[");
        int i = 0;
        for(int n : list) {
            printStringWithoutNewLine("[" + i + "]" + n + ", ");
            i++;
        }
        printStringWithoutNewLine("]\n");
    }

    public static void printMapArray(Map<Integer, Integer>[] maps) {
        int idx = 0;
        for(Map<Integer, Integer> map : maps) {
            printOpenBrackets();
            printString("[" + idx + "]");
            printMapKeyIntegerValueInteger(map);
            idx++;
            printCloseBrackets();
        }
    }

    public static void printHashSetOfLinkedListInteger(Set<List<Integer>> set) {
        if (set.isEmpty() == true) {
            return;
        }
        for(List<Integer> list : set) {
            PrintUtils.printListInteger(list);
        }
    }

    public static void printStringArray(String[] list) {
        if(list == null) {
            return;
        }
        printStringWithoutNewLine("[");
        int i = 0;
        for(String n : list) {
            printStringWithoutNewLine("[" + i + "]" + n + ", ");
            i++;
        }
        printStringWithoutNewLine("]\n");
    }


    public static void printListString(List<String> list) {
        for(String s: list) {
            printStringWithoutNewLine(s + ", ");
        }
        printStringWithoutNewLine("\n");
    }

    public static void printListInteger(List<Integer> list) {
        printStringWithoutNewLine("[");
        for(Integer s: list) {
            printStringWithoutNewLine(s + ", ");
        }
        printString("]");
        printStringWithoutNewLine("\n");
    }

    public static void printListStringWithLine(List<String> list) {
        for(String s: list) {
            printString(s + ", ");
        }
        printStringWithoutNewLine("\n");
    }

    public static void printMatrixInt(int[][] matrix) {
        int i = 0;
        for(int[] s: matrix) {
            int j = 0;
            for(int n: s) {
                printStringWithoutNewLine( "[" + i + "]" + "[" + (j++) + "]" + n + ", ");
            }
            System.out.println();
            i++;
        }
    }

    public static void printMatrixBool(boolean[][] matrix) {
        int i = 0;
        for(boolean[] s: matrix) {
            int j = 0;
            for(boolean n: s) {
                printStringWithoutNewLine( "[" + i + "]" + "[" + (j++) + "]" + n + ", ");
            }
            System.out.println();
            i++;
        }
    }

    public static void printMatrixChar(char[][] matrix) {
        int i = 0;
        for(char[] s: matrix) {
            int j = 0;
            for(char n: s) {
                printStringWithoutNewLine( "[" + i + "]" + "[" + (j++) + "]" + n + ", ");
            }
            System.out.println();
            i++;
        }
    }

    public static void printStringWithoutNewLine(String arg) {
        System.out.print(arg);
    }

    public static void printMapKeyStringValueString(Map<String, String> map) {
        for(String key:map.keySet()) {
            String value = map.get(key);
            printString("key:" + key + ": [" + value + "]");
        }
    }

    public static void printMapKeyIntegerValueInteger(Map<Integer, Integer> map) {
        for(Integer key:map.keySet()) {
            Integer value = map.get(key);
            printString("key:" + key + ": [" + value + "]");
        }
    }

    public static void printMapKeyStringValueInteger(Map<String, Integer> map) {
        for(String key:map.keySet()) {
            Integer value = map.get(key);
            printString("key:" + key + ": [" + value + "]");
        }
    }

    public static void printListOfMapEntry(List<Map.Entry<String,Integer>> list) {
        if (list == null) {
            return;
        }
        for(Map.Entry<String,Integer> entry : list) {
            printString("key:" + entry.getKey() + ", value:" + entry.getValue());
        }
    }

    public static void printMapKeyStringValueList(Map<String, List<String>> map) {
        for(String key:map.keySet()) {
            List<String> list = map.get(key);
            printStringWithoutNewLine("key:" + key + ": [");
            for(String n : list) {
                printStringWithoutNewLine(n + ", ");
            }
            printString("]");
        }
    }

    public static void printListOfListInteger(List<List<Integer>> listOflist) {
        if (listOflist == null || listOflist.isEmpty()) {
            return;
        }
        for(List<Integer> list: listOflist){
            printStringWithoutNewLine("[");
            for(int n : list) {
                printStringWithoutNewLine(n + ", ");
            }
            printStringWithoutNewLine("]\n");
        }
    }

    public static void printListOfListString(List<List<String>> listOflist) {
        for(List<String> list: listOflist){
            printStringWithoutNewLine("[");
            for(String n : list) {
                printStringWithoutNewLine(n + ", ");
            }
            printStringWithoutNewLine("]\n");
        }
    }

}

