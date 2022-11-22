package it.unibo.oop.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {
    
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");

    /**
     * Builds a new CGUI.
     */
    public ConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(this.display);
        panel.add(this.up);
        panel.add(this.down);
        panel.add(this.stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        final Agent counter = new Agent();
        new Thread(counter).start();
        up.addActionListener(u -> counter.startCountUp());
        down.addActionListener(d -> counter.startCountDown());
        stop.addActionListener(s -> { 
            counter.stopCounting();
            this.up.setEnabled(false);
            this.down.setEnabled(false);
            this.stop.setEnabled(false);
        });
    }

    /**
     * Integrated class for counter.
     */
    private class Agent implements Runnable {
        private volatile boolean stop;
        private volatile boolean start;
        private volatile boolean up;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextText));
                    if (start) {
                        counter = this.up ? this.counter + 1 : this.counter - 1;
                    }
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace(); // NOPDM
                }
            }
        }

        /**
         * Function to stop counting.
         */
        public void stopCounting() {
            this.stop = true;
        }

        /**
         * Fuction to start counting up.
         */
        public void startCountUp() {
            this.start = true;
            this.up = true;
        }

        /**
         * Fuction to start counting down.
         */
        public void startCountDown() {
            this.start = true;
            this.up = false;
        }
    }
}
