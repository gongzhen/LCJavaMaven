package helper.matrix;

import helper.PrintUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Array;

public class LeetCodeMatrix<T> {
    private final T[][] matrix;
    private final boolean isDebug;
    private final int row;
    private final int col;

    public LeetCodeMatrix(T[][] matrix, int row, int col, boolean isDebug) {
        this.isDebug = isDebug;
        this.row = row;
        this.col = col;
        this.matrix = matrix.clone();
    }

    public static LeetCodeMatrix.BuilderImpl builder() {
        return new LeetCodeMatrix.BuilderImpl();
    }

    public final T[][] getMatrix() {
        return this.matrix;
    }

    public void printMatrix() {
        for(int i = 0; i < this.row; i++) {
            for(int j = 0; j < this.col; j++) {
                PrintUtils.printStringWithoutNewLine(this.matrix[i][j] + ", ");
            }
            PrintUtils.printString("");
        }
    }

    public interface Builder<T> {
        LeetCodeMatrix.Builder setRow(int row);

        LeetCodeMatrix.Builder setColumn(int column);

        LeetCodeMatrix.Builder setDebug(boolean isDebug);

        LeetCodeMatrix build();
    }

    private static final class BuilderImpl<T> implements LeetCodeMatrix.Builder<T> {
        private @Nullable T[][] matrix;
        private boolean isDebug;
        private int row;
        private int col;

        public Builder setMatrix(T[][] matrix) {
            this.matrix = matrix;
            return this;
        }

        @Override
        public Builder setRow(int row) {
            this.row = row;
            return this;
        }

        @Override
        public Builder setColumn(int column) {
            this.col = column;
            return this;
        }

        @Override
        public Builder setDebug(boolean isDebug) {
            this.isDebug = isDebug;
            return this;
        }

        @Override
        public LeetCodeMatrix build() {

            for(int i = 0; i < this.row; i++) {
                this.matrix[i] = (T[])new Object[this.col];
            }
            return new LeetCodeMatrix(this.matrix, this.row, this.col, this.isDebug);
        }
    }
}
