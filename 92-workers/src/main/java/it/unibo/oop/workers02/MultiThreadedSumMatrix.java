package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix{
    
    private final int n;

    public MultiThreadedSumMatrix(final int n) {
        this.n = n;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private int startLine;
        private int endLine;
        private double res;

        /**
         * Build a new worker.
         * 
         * @param line
         *            the line to sum
         */
        Worker(final double[][] matrix, final int startLine, final int endLine) {
            super();
            this.matrix = matrix;
            this.startLine = startLine;
            this.endLine = endLine;
        }

        @Override
        public void run() {
            this.endLine -= matrix.length;
            System.out.println("Working from line " + this.startLine + "to " + this.endLine); // NOPMD
            for (int l = this.startLine; l < this.endLine; l++) {
                for (double de : matrix[l]) {
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
        for (int i = 0; i < matrix.length; i += lineDiv) {
            workers.add(new Worker(matrix, i, lineDiv + i));
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
