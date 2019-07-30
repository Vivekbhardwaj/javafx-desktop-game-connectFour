package fractal.connect4game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import static java.lang.System.exit;

public class Main extends Application {

    public Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Game.fxml"));
        GridPane rootGridPane = loader.load();
        controller = loader.getController();
        controller.createPlayground();

        MenuBar menuBar = createMenu();
        Pane menuPane= (Pane) rootGridPane.getChildren().get(0);        //get has the index of the child in our fxml reqd pane is the second child
        menuPane.getChildren().add(menuBar);

        primaryStage.setTitle("Connect Four");
        primaryStage.setScene(new Scene(rootGridPane));
        primaryStage.show();
    }

    private MenuBar createMenu(){

        Menu fileMenu = new Menu("File");
        Menu helpMenu = new Menu("Help");

        //file menu
        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(actionEvent -> controller.resetGame1());

        MenuItem resetGame = new MenuItem("Reset Game");
        resetGame.setOnAction(actionEvent -> controller.resetGame1());

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

        MenuItem exitGame = new MenuItem("Exit Game");
        exitGame.setOnAction(actionEvent ->{
            Platform.exit();
            System.exit(0);
        });

        fileMenu.getItems().addAll(newGame,resetGame,separatorMenuItem,exitGame);


        //help menu
        MenuItem aboutGame = new MenuItem("About");
        aboutGame.setOnAction(actionEvent -> alertAboutGame());

        MenuItem developers = new MenuItem("Developer");
        developers.setOnAction(actionEvent -> alertAboutDevelopers());

        SeparatorMenuItem separatorMenuItem1 = new SeparatorMenuItem();

        helpMenu.getItems().addAll(aboutGame,separatorMenuItem1,developers);

        //adding both menu to menubar
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu,helpMenu);

        return menuBar;

    }

    private void alertAboutDevelopers() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Developer");
        alert.setHeaderText("VIVEK BHARDWAJ");
        alert.setContentText("Institute of Engineeirng and Technology\nLucknow");
        alert.show();
    }

    private void alertAboutGame() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Rules");
        alert.setHeaderText("How to play?");
        alert.setContentText("The first player starting Connect Four by \ndropping one of his/her yellow discs into the center\n column of an empty game board. \nThe two players then alternate turns dropping \none of their discs at a time \ninto an unfilled column, until \nthe second player, with red discs, achieves \na diagonal four in a row, and wins \nthe game. For games where the board fills \nup before either player achieves four in a row, \nthen the games are a draw.");
        alert.show();
    }

    public static  void resetGame1() {

        System.out.println("Game Restarted");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
