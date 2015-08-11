package com.nikesh.countdowntimer;

import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.applet.*;

public class CountDownTimer extends JFrame implements ActionListener, Runnable {

    // Stores the current minute and second.
    private int currentMinute, currentSecond;

    // Panels to group the UI components.
    private JPanel bottomPanel, topPanel, main = new JPanel(new BorderLayout());

    /**
     * UI COMPONENTS *
     */
    private JTextField minutes, seconds;
    private JButton button;
    private JPopupMenu popupmenu = new JPopupMenu();
    private JMenu sounds;
    private JMenuItem sound1, sound2, sound3, exit, stopSound;

    // Flag for an error
    private boolean error = false;

    // Thread to run the timer.
    private Thread t;

    // Variable to store the running state of thread.
    private boolean isRunning = false;

    // Stores the selected audio for the timer's timeout sound.
    private AudioClip audio;

    /**
     * No-args constructor
     */
    public CountDownTimer() {
        init();
    }

    /**
     * Initializes the UI components, adds listeners, places the interface
     */
    private void init() {
        //JDialog.setDefaultLookAndFeelDecorated(false);
        setTitle("Timer");
        try {
            audio = Applet.newAudioClip(this.getClass().getResource("Ed Sheeran.wav"));
        } catch (Exception ex) {
            Logger.getLogger(CountDownTimer.class.getName()).log(Level.SEVERE, null, ex);
        }
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        button = new JButton("Start");
        button.addActionListener(this);

        Font font = new Font("Calibri", Font.ITALIC, 20);

        minutes = new JTextField("00", 2);
        minutes.setFont(font);
        minutes.setHorizontalAlignment(JTextField.CENTER);
        minutes.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                minutes.setText("");

            }
        });
        seconds = new JTextField("00", 2);
        seconds.setFont(font);
        seconds.setHorizontalAlignment(SwingConstants.CENTER);
        seconds.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                seconds.setText("");
                if (e.isPopupTrigger()) {
                    popupmenu.show(e.getComponent(), e.getX(), e.getY());
                }

            }
        });

        exit = new JMenuItem("Exit", KeyEvent.VK_X);
        exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        sounds = new JMenu("Sounds");
        sound1 = new JMenuItem("Ed Sheeran", KeyEvent.VK_D);
        sound2 = new JMenuItem("Coldplay", KeyEvent.VK_C);
        sound3 = new JMenuItem("Dream Theater", KeyEvent.VK_E);
        sound2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                audio.stop();
                audio = Applet.newAudioClip(this.getClass().getResource("Coldplay.wav"));
            }
        });
        sound3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                audio.stop();
                audio = Applet.newAudioClip(this.getClass().getResource("Dream Theater.wav"));
            }
        });
        stopSound = new JMenuItem("Stop Sound", KeyEvent.VK_T);
        stopSound.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                audio.stop();
            }
        });
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                audio.stop();
            }
        });

        sounds.add(sound1);
        sounds.add(sound2);
        sounds.add(sound3);

        popupmenu.add(sounds);
        popupmenu.add(stopSound);
        popupmenu.add(exit);

        topPanel.add(minutes);
        topPanel.add(new JLabel(":"));
        topPanel.add(seconds);

        bottomPanel.add(button);

        main.add(topPanel, BorderLayout.CENTER);
        main.add(bottomPanel, BorderLayout.SOUTH);

        main.setComponentPopupMenu(popupmenu);
        add(main, BorderLayout.CENTER);
        setCloseOperation();

        setSize(200, 120);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int) (d.getWidth() - 200), 0);
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        new CountDownTimer();
    }

    private void setCloseOperation() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE | JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Overriding the method that gets called when even event occurs.
     * @param e Event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == button) {
            if (button.getText().equalsIgnoreCase("Start")) {
                audio.stop();
                checkNumbers();                // perform check if numbers are valid
                if (!error) {
                    // Start the thread
                    // change button text
                    // disable textfield
                    this.isRunning = true;
                    t = new Thread(this);
                    t.start();
                    button.setText("Stop");
                    minutes.setEditable(false);
                    seconds.setEditable(false);
                }
            } else if (button.getText().equalsIgnoreCase("Stop")) {
                // kill the thread
                // change button text to start
                // enable textfield and set default value
                stopThread();
            }
        }
    }

    /**
     * Checks whether the number entered by the user in the field is valid or not.
     */
    private void checkNumbers() {
        try {
            int minute = Integer.parseInt(this.minutes.getText());
            int second = Integer.parseInt(this.seconds.getText());
            System.out.println("Minutes:  " + minute);
            System.out.println("Seconds:  " + second);
            this.currentMinute = minute;
            this.currentSecond = second;
            if (minute < 0 || minute >= 60 || second < 0 || second >= 60) {
                throw new Exception();
            }
            if (minute == 0 && second == 0) {
                throw new Exception();
            }
            error = false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid number", "Error", JOptionPane.ERROR_MESSAGE);
            error = true;
        }
    }

    /**
     * Thread that starts the countdown
     */
    @Override
    public void run() {
        while (isRunning) {
            try {
                Thread.sleep(1000);
                System.out.println("Inisde Thread.");

                if (this.currentSecond == 0 && this.currentMinute >= 1) {
                    this.currentMinute = this.currentMinute - 1;
                    if (this.currentMinute < 0) {
                        this.currentMinute = 0;
                    }
                    this.currentSecond = 59;
                } else {
                    this.currentSecond = this.currentSecond - 1;
                }

                if (this.currentMinute <= 0 && this.currentSecond <= 0) {
                    stopThread();
                    audio.loop();
                    // play sound
                }
                if (this.currentSecond <= 9) {
                    seconds.setText("0" + this.currentSecond);
                } else {
                    seconds.setText(this.currentSecond + "");
                }
                if (this.currentMinute <= 9) {
                    minutes.setText("0" + this.currentMinute);
                } else {
                    minutes.setText(this.currentMinute + "");
                }

                // starts countdown
            } catch (InterruptedException ex) {
                Logger.getLogger(CountDownTimer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (!isRunning) {
            minutes.setText("00");
            seconds.setText("00");
        }
    }

    /**
     * Method that helps to stop the thread.
     */
    private void stopThread() {
        this.isRunning = false;
        button.setText("Start");
        minutes.setEditable(true);
        seconds.setEditable(true);
        minutes.setText("00");
        seconds.setText("00");
    }

}
