package com.swing.test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

interface Observer {
    void update(String message);
}

class Subject {
    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer o) {
        observers.add(o);
    }

    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    public void notifyObservers(String message) {
        for (Observer o : observers) {
            o.update(message);
        }
    }
}

public class ObserverExample {
    public static void main(String[] args) {
        Subject subject = new Subject();

        JFrame frame = new JFrame("Observer Example");
        JButton button = new JButton("Click Me");
        JLabel label = new JLabel("Waiting...");

        // Create an observer
        Observer labelObserver = label::setText;

        // Register the observer
        subject.addObserver(labelObserver);

        // Button click notifies observers
        final AtomicInteger count = new AtomicInteger(0);
        button.addActionListener(e -> subject.notifyObservers(Integer.toString(count.incrementAndGet())));


        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.add(button);
        frame.add(label);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
