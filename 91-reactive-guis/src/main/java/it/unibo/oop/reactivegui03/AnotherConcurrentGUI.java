package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {
        
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
    public AnotherConcurrentGUI() {
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
        final AgentC counter = new AgentC();
        final AgentT timer = new AgentT();
        new Thread(counter).start();
        new Thread(timer).start();
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
    private class AgentC implements Runnable {
        private volatile boolean stop;
        private volatile boolean start;
        private volatile boolean up;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    if (start) {
                        counter = this.up == true ? this.counter + 1 : this.counter - 1;
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

    /**
     * Integrated class for timer.
     */
    private class AgentT implements Runnable {
        private volatile boolean stop;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    Thread.sleep(10_000);
                    stopTimer();
                } catch (InterruptedException ex) {
                    ex.printStackTrace(); // NOPDM
                }
            }
        }

        /**
         * Function to stop counting.
         */
        public void stopTimer() {
            this.stop = true;
            AnotherConcurrentGUI.this.stop.doClick();
        }
    }
}
