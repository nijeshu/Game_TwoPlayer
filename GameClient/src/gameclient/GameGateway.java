
package gameclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javafx.application.Platform;
import javafx.scene.control.TextArea;


public class GameGateway implements game.gameConstants {

    private PrintWriter outputToServer;
    private BufferedReader inputFromServer;
    private TextArea textArea;

    public GameGateway(TextArea textArea) {
        this.textArea = textArea;
        try {
            Socket socket = new Socket("localhost", 8000);
            outputToServer = new PrintWriter(socket.getOutputStream());
            inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (IOException ex) {
            System.out.println("Exception in constructor: "+ex.getMessage());
            Platform.runLater(() -> this.textArea.appendText("Exception in gateway constructor: " + ex.toString() + "\n"));
        }
    }
    
    public void setPlayer(String playerName){
        outputToServer.println(ADD_PLAYER);
        outputToServer.println(playerName);
        outputToServer.flush();
    }

    public void recordStartVote(String playerName) {
        outputToServer.println(RECORD_START_VOTE);
        outputToServer.println(playerName);
        outputToServer.flush();
    }

    public String getNextQuestion() {
        outputToServer.println(GET_NEXT_QUESTION);
        outputToServer.flush();
        String comment = "";
        try {
            comment = inputFromServer.readLine();
        } catch (IOException ex) {
            Platform.runLater(() -> textArea.appendText("Error in getComment: " + ex.toString() + "\n"));
        }
        return comment;
    }

    public String getCorrectAnswer() {
        outputToServer.println(GET_CORRECT_ANSWER);
        outputToServer.flush();
        String comment = "";
        try {
            comment = inputFromServer.readLine();
        } catch (IOException ex) {
            Platform.runLater(() -> textArea.appendText("Error in getComment: " + ex.toString() + "\n"));
        }
        return comment;
    }

    public void recordGuess(String player, String guess) {
        outputToServer.println(RECORD_START_VOTE);
        outputToServer.println(player);
        outputToServer.println(guess);
        outputToServer.flush();
    }

    public boolean isGameOver() {
        outputToServer.println(IS_GAME_OVER);
        outputToServer.flush();
        String comment = "";
        try {
            comment = inputFromServer.readLine();
        } catch (IOException ex) {
            Platform.runLater(() -> textArea.appendText("Error in getComment: " + ex.toString() + "\n"));
        }
        if(comment.equalsIgnoreCase("true")){
            return true;
        }
        else{
            return false;
        }
    }

    public String getLeaderBoard() {
        outputToServer.println(GET_LEADERBOARD);
        outputToServer.flush();
        String comment = "";
        try {
            comment = inputFromServer.readLine();
        } catch (IOException ex) {
            Platform.runLater(() -> textArea.appendText("Error in getComment: " + ex.toString() + "\n"));
        }
        return comment;
    }
}
