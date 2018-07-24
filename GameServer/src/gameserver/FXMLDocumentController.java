package gameserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

public class FXMLDocumentController implements Initializable {

    @FXML
    private TextArea textArea;

    private int clientNo = 0;
    private quizMaster quizmaster;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Starting to initialize.");
        quizmaster = new quizMaster();

        new Thread(() -> {
            try {

                ServerSocket serverSocket = new ServerSocket(8000);
                System.out.println("Started server.");
                while (true) {

                    Socket socket = serverSocket.accept();

                    clientNo++;

                    Platform.runLater(() -> {

                        textArea.appendText("Starting thread for client " + clientNo
                                + " at " + new Date() + '\n');
                    });

                    new Thread(new HandleAClient(socket, quizmaster, textArea)).start();
                }
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }).start();
    }

}

class HandleAClient implements Runnable, game.gameConstants {

    private Socket socket;
    private quizMaster quizmaster;
    private TextArea textArea;
    private String handle;
    private String handle1;

    public HandleAClient(Socket socket, quizMaster quizmaster, TextArea textArea) {
        this.socket = socket;
        this.quizmaster = quizmaster;
        this.textArea = textArea;
    }

    @Override
    public void run() {
        try {

            BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outputToClient = new PrintWriter(socket.getOutputStream());

            while (true) {

                int request = Integer.parseInt(inputFromClient.readLine());
                System.out.println("Got request " + request);
                switch (request) {
                    case ADD_PLAYER: {
                        handle = inputFromClient.readLine();
                        quizmaster.addPlayer(handle);
                        break;
                    }
                    case RECORD_START_VOTE: {
                        String comment = inputFromClient.readLine();
                        quizmaster.recordStartVote(comment);
                        break;
                    }
                    case GET_NEXT_QUESTION: {
                        outputToClient.println(quizmaster.getNextQuestion());
                        outputToClient.flush();
                        break;
                    }
                    case GET_CORRECT_ANSWER: {
                        outputToClient.println(quizmaster.getCorrectAnswer());
                        outputToClient.flush();
                        break;
                    }
                    case RECORD_GUESSS: {
                        handle = inputFromClient.readLine();
                        handle1 = inputFromClient.readLine();
                        quizmaster.recordGuess(handle, handle1);
                        break;
                    }
                    case IS_GAME_OVER: {
                        outputToClient.println(quizmaster.isGameOver());
                        outputToClient.flush();
                        break;
                    }

                    case GET_LEADERBOARD: {
                        quizmaster.getLeaderBoard();

                    }
                }
            }
        } catch (IOException ex) {
            Platform.runLater(() -> textArea.appendText("Exception in client thread: " + ex.toString() + "\n"));
        } catch (InterruptedException ex) {
            Logger.getLogger(HandleAClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
