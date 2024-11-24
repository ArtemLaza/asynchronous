package ДЗ_3_1;

import java.util.concurrent.RecursiveTask;


// Fork/Join Task для Work Stealing
public class WorkStealing extends RecursiveTask<int[]> {
    private final int[][] matrix;
    private final int startCol;
    private final int endCol;

    public WorkStealing(int[][] matrix, int startCol, int endCol) {
        this.matrix = matrix;
        this.startCol = startCol;
        this.endCol = endCol;
    }

    @Override
    protected int[] compute() {
        int numCols = endCol - startCol;
        if (numCols <= 80) {
            int[] sums = new int[numCols];
            for (int col = startCol; col < endCol; col++) {
                int sum = 0;
                for (int[] row : matrix) {
                    sum += row[col];
                }
                sums[col - startCol] = sum;
            }
            return sums;
        } else {
            int mid = startCol + numCols / 2;
            WorkStealing leftTask = new WorkStealing(matrix, startCol, mid);
            WorkStealing rightTask = new WorkStealing(matrix, mid , endCol);
            invokeAll(leftTask, rightTask);

            int[] leftResult = leftTask.join();
            int[] rightResult = rightTask.join();

            int[] result = new int[numCols];
            System.arraycopy(leftResult, 0, result, 0, leftResult.length);
            System.arraycopy(rightResult, 0, result, leftResult.length, rightResult.length);
            return result;
        }
    }
}
