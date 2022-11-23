package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix{
    
    private final int n;

    public MultiThreadedSumMatrix(final int n) {
        if (n < 1) {
            throw new IllegalArgumentException();
        }
        this.n = n;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private int startLine;
        private int lines;
        private double res;

        /**
         * Build a new worker.
         * 
         * @param matrix
         *            the matrix to sum
         * @param startpos
         *            the initial position for this worker
         * @param nelem
         *            the no. of lines to sum up for this worker
         */
        Worker(final double[][] matrix, final int startLine, final int lines) {
            super();
            this.matrix = matrix;
            this.startLine = startLine;
            this.lines = lines;
        }

        @Override
        public void run() {
            System.out.println("Working from line " + this.startLine + " for " + this.lines + " lines"); // NOPMD
            for (int l = this.startLine; l < this.lines + this.startLine && l < matrix.length; l++) {
                for (final double de : matrix[l]) {
                    this.res += de;
                }
            }
        }

        /**
         * Returns the result of summing up the doubles within the line.
         * 
         * @return the sum of every element in the line
         */
        public double getResult() {
            return this.res;
        }
    }

    @Override
    public double sum(double[][] matrix) {
        /*
         * Build a list of workers
         */
        int lineDiv = matrix.length % n + matrix.length / n;
        final List<Worker> workers = new ArrayList<>(this.n);
        for (int start = 0; start < matrix.length; start += lineDiv) {
            workers.add(new Worker(matrix, start, lineDiv));
        }
        /*
         * Start them
         */
        for (final Worker w: workers) {
            w.start();
        }
        /*
         * Wait for every one of them to finish
         */
        double sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        /*
         * Return the sum
         */
        return sum;
    }
}
