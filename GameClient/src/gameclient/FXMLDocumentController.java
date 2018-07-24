package gameclient;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;

public class FXMLDocumentController implements Initializable {

    @FXML
    private TextArea questionDisplay;
    @FXML
    private TextArea leaderboardDisplay;

    private Userinput userguess;
    
    private GameGateway gateway;

    public FXMLDocumentController() {
        this.userguess = new Userinput();
    }

    @FXML
    public void StartButton(ActionEvent e) {
        userguess.setPlayerAnswer("s");
    }

    @FXML
    public void OptionA(ActionEvent e) {
        userguess.setPlayerAnswer("A");
    }

    @FXML
    public void OptionB(ActionEvent e) {
        userguess.setPlayerAnswer("B");
    }

    @FXML
    public void OptionC(ActionEvent e) {
        userguess.setPlayerAnswer("C");
    }

    @FXML
    public void OptionD(ActionEvent e) {
        userguess.setPlayerAnswer("D");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gateway = new GameGateway(questionDisplay);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Start Quiz");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter Player's Name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> gateway.setPlayer(name));

        new Thread(new CheckingToStart(gateway, questionDisplay, leaderboardDisplay, userguess)).start();
    }

}

class CheckingToStart implements Runnable, game.gameConstants {

    private GameGateway gateway;
    private TextArea questionDisplay;
    private TextArea leaderboardDisplay;
    private Userinput userguess;

    public CheckingToStart(GameGateway gateway, TextArea questionDisplay, TextArea leaderboardDisplay, Userinput userguess) {
        this.gateway = gateway;
        this.questionDisplay = questionDisplay;
        this.leaderboardDisplay = leaderboardDisplay;
        this.userguess=userguess;
    }

    @Override
    public void run() {
        String startVote = userguess.getPlayerAnswer();
        while (!startVote.equals("s")) {
            startVote = userguess.getPlayerAnswer();
        }
        gateway.recordStartVote(startVote);

        while (gateway.isGameOver() == false) {
            String nextQuestion = gateway.getNextQuestion();
            this.displayQuestion(nextQuestion);
            String playerAnswer = userguess.getPlayerAnswer();
            gateway.recordGuess(playerAnswer, nextQuestion);
            String correct = gateway.getCorrectAnswer();
            this.displayCorrectAnswer(correct);
            String leaders = gateway.getLeaderBoard();
            this.displayLeaderBoard(leaders);
        }
        this.displayGameEndedMessage();
    }

    public void displayQuestion(String nextQuestion) {
        Platform.runLater(() -> questionDisplay.setText(nextQuestion));
    }

    public void displayCorrectAnswer(String Correct) {
        Platform.runLater(() -> questionDisplay.setText(Correct));
    }

    public void displayLeaderBoard(String leaders) {
        Platform.runLater(() -> leaderboardDisplay.setText(leaders));
    }

    public void displayGameEndedMessage() {
        Platform.runLater(() -> questionDisplay.setText("The game is Over!"));
    }
}
