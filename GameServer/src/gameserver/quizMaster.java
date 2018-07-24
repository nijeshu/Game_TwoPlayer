package gameserver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class quizMaster {

    private List<String> questions;
    private List<String> answers;
    private List<String> players;
    private List<Integer> scores;
    private int startVotes;
    private int currentQuestion;
    private int answersReceived;
    private static Lock lock;
    private static Condition playerIn;
    private static Condition playerAnswer;

    public quizMaster() {
        startVotes = 0;
        currentQuestion = 0;
        answersReceived = 0;
        questions = new ArrayList<>();
        answers = new ArrayList<>();
        players = new ArrayList<>();
        scores = new ArrayList<>();
        lock = new ReentrantLock();
        playerIn = lock.newCondition();
        playerAnswer = lock.newCondition();

        Scanner questionaire = null;
        try {
            questionaire = new Scanner(new File("Questions.txt"));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
        System.out.println("Starting to read questions.");
        while (questionaire.hasNext()) {
            String question = questionaire.nextLine();
            questions.add(question);
        }
        System.out.println("No of questions read= "+ questions.size());
        Scanner answeraire = null;
        try {
            answeraire = new Scanner(new File("Answers.txt"));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
        while (answeraire.hasNext()) {
            String answer = answeraire.nextLine();
            answers.add(answer);
        }
        System.out.println("No of answers read=" + answers.size());
    }

    public void addPlayer(String playerName) {
        players.add(playerName);
        scores.add(0);
    }

    public void recordStartVote(String playerName) {
        startVotes++;
        System.out.println("Recorded start vote.");
        System.out.println("Player's Size"+ players.size());
        System.out.println("StartVote="+ startVotes);
        if (startVotes == players.size()) {
            lock.lock();
            try {
                System.out.println("Signalled!");
                playerIn.signal();
            } finally {
                lock.unlock();
            }
        }
    }

    public String getNextQuestion() throws InterruptedException {
        
        System.out.println("Waiting to serve question.");
        if(currentQuestion > 0 || startVotes < players.size()) {
        lock.lock();
        try {
            playerIn.await();
        } finally {
            lock.unlock();
        }
        }
        String currentQuestionToDisplay = questions.get(currentQuestion);
        return currentQuestionToDisplay;
    }

    public String getCorrectAnswer() throws InterruptedException {
        lock.lock();
        try {
            playerAnswer.await();
        } finally {
            lock.unlock();
        }

        String AnswerToDisplay = answers.get(currentQuestion);
        return AnswerToDisplay;

    }

    public void recordGuess(String player, String guess) throws InterruptedException {
        answersReceived++;

        int finalScore = scores.get(players.indexOf(player));

        if (answers.get(currentQuestion).equals(guess)) {
            finalScore++;
        }

        if (players.indexOf(player) == scores.indexOf(player)) {

            scores.add(players.indexOf(player), finalScore);
            if (answersReceived == players.size()) {
                lock.lock();
            
                currentQuestion++;
                answersReceived = 0;
                try {
                    playerAnswer.signal();
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public boolean isGameOver() {
        System.out.println("In isGameOver. Current question is "+currentQuestion);
        System.out.println("The size of the questions list is "+questions.size());
        if (currentQuestion <= questions.size()-1) {
            return false;
        } else {
            return true;
        }

    }

    public String getLeaderBoard() {
        for (int i = 0; i < players.size(); i++) {
            String BoardDisplay = "Player Name:" + players.get(i) + "||" + "Score:" + scores.get(i);
            return BoardDisplay;
        }
        return " ";
    }
}
