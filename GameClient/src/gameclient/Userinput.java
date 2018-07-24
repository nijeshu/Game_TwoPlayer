package gameclient;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Userinput {

    private String mostRecentAnswer;
    private static Lock lock;
    private static Condition playerIn;

    public Userinput() {
        mostRecentAnswer = "";
        lock = new ReentrantLock();
        playerIn = lock.newCondition();
    }

    public String getPlayerAnswer() {
        mostRecentAnswer = null; 
        lock.lock();
        try {
            while (mostRecentAnswer == null) {
                playerIn.await(30, TimeUnit.SECONDS);
                
            }
        } catch (InterruptedException ex) {
        } finally {
            lock.unlock();
        }
        if (mostRecentAnswer==null) {
            return " ";
        }
        return mostRecentAnswer;
    }

    public void setPlayerAnswer(String UserAnswer) {
        if (UserAnswer.equals("s")) {
            lock.lock();
            try {
                mostRecentAnswer = "s";
                playerIn.signal();
            } finally {
                lock.unlock();
            }
        }
            else if (UserAnswer.equals("A")) {
            lock.lock();
            try {
                mostRecentAnswer = "A";
                playerIn.signal();
            } finally {
                lock.unlock();
            }
        } else if (UserAnswer.equals("B")) {
            lock.lock();
            try {
                mostRecentAnswer = "B";
                playerIn.signal();
            } finally {
                lock.unlock();
            }
        } else if (UserAnswer.equals("C")) {
            lock.lock();
            try {
                mostRecentAnswer = "C";
                playerIn.signal();
            } finally {
                lock.unlock();
            }
        } else if (UserAnswer.equals("D")) {
            lock.lock();
            try {
                mostRecentAnswer = "D";
                playerIn.signal();
            } finally {
                lock.unlock();
            }
        } else {
            mostRecentAnswer=null;
        }

    }
}
