package fractal.connect4game;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static fractal.connect4game.Main.*;

public class Controller implements Initializable {

	private static final int COLUMNS = 7;
	private static final int ROWS = 6;
	private static final int CIRCLE_DIAMETER=80;
	private String PLAYER_ONE = "Player one";
	private String PLAYER_TWO = "Player two";
	private static final String discColor1 = "#24303E";
	private static final String discColor2 = "#4CAA88";

	private boolean isPlayeroneTurn = true;

	private Disc[][] insertedDiscs=new Disc[ROWS][COLUMNS];		//for structural changes for developers

	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscPane;

	@FXML
	public Label playerNameLabel;

	@FXML
	public TextField player1;

	@FXML
	public TextField player2;

	@FXML
	public Button startButton;

	public void createPlayground(){

		Shape rectangleWithHoles = createGameStructuralGrid();
		rootGridPane.add(rectangleWithHoles,0,1);

		List<Rectangle> rectangleList=createClickableColumns();
		for (Rectangle rectangle:rectangleList) rootGridPane.add(rectangle, 0, 1);

	}

	private Shape createGameStructuralGrid(){
		Shape rectangleWithHoles= new Rectangle((COLUMNS+1)*CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);


		for(int row=0;row<ROWS;row++){
			for(int column=0;column<COLUMNS;column++){
				Circle circle=new Circle();
				circle.setRadius(CIRCLE_DIAMETER/2);
				circle.setCenterX(CIRCLE_DIAMETER/2);
				circle.setCenterY(CIRCLE_DIAMETER/2);
				circle.setSmooth(true);
				circle.setTranslateX(column*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
				circle.setTranslateY(row*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
				rectangleWithHoles=Shape.subtract(rectangleWithHoles,circle);

			}
		}

		rectangleWithHoles.setFill(Color.WHITE);
		return rectangleWithHoles;
	}

	private List<Rectangle> createClickableColumns(){
		List<Rectangle> rectangleList=new ArrayList<Rectangle>();

		for(int col=0;col<COLUMNS;col++){
			Rectangle rectangle=new Rectangle(CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);

			rectangle.setOnMouseEntered(actionEvent->rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(actionEvent->rectangle.setFill(Color.TRANSPARENT));
			final int column = col;
			rectangle.setOnMouseClicked(actionEvent->{
				insertDisc(new Disc(isPlayeroneTurn),column);
			});
			rectangleList.add(rectangle);
		}
		return rectangleList;

	}

	private void insertDisc(Disc disc,int column){
		int row=ROWS-1;
		while (row>=0){
			if(getDiscIfPresent(row,column)==null)
				break;

			row--;
		}
		if(row<0)	return;

		insertedDiscs[row][column]=disc;
		disc.setTranslateX(column*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),disc);
		translateTransition.setToY(row*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
		final int rowNo=row;
		final int colNo=column;
		translateTransition.setOnFinished(actionEvent -> {
			if(gameEnded(rowNo,colNo)){ 	gameOver();return; }
			isPlayeroneTurn=!(isPlayeroneTurn);
			playerNameLabel.setText(isPlayeroneTurn?PLAYER_ONE:PLAYER_TWO);
		});

		translateTransition.play();
		insertedDiscPane.getChildren().add(disc);
	}

	private void gameOver() {
		Alert alert=new Alert(Alert.AlertType.INFORMATION);
		String s = isPlayeroneTurn?PLAYER_ONE:PLAYER_TWO;
		alert.setHeaderText(s + " is the winner.");
		alert.setTitle("Winner winner ,chicken dinner.");
		alert.setContentText("Want to play again?");

		ButtonType yesBtn = new ButtonType(("Yes"));
		ButtonType noBtn = new ButtonType(("No"));
		alert.getButtonTypes().setAll(yesBtn,noBtn);

		Platform.runLater(()->{
			Optional<ButtonType> btnClicked = alert.showAndWait();
			if(btnClicked.isPresent()&&btnClicked.get()==yesBtn){
				//....user has choosen YES so RESET GAME
				resetGame1();
			} else{
				Platform.exit();
				System.exit(0);
			}
		});
	}

	public void resetGame1(){
		insertedDiscPane.getChildren().clear();
		for(int row=0;row<insertedDiscs.length;row++){
			for(int column=0;column<insertedDiscs[row].length;column++)
				insertedDiscs[row][column]=null;
		}
		isPlayeroneTurn=true;
		playerNameLabel.setText(PLAYER_ONE);

		createPlayground();
	}

	private static class Disc extends Circle {
		private final boolean isPlayerOneMove;
		public Disc(boolean isPlayerOneMove){
			this.isPlayerOneMove=isPlayerOneMove;
			setRadius(CIRCLE_DIAMETER/2);
			setFill(isPlayerOneMove? Color.valueOf(discColor1):Color.valueOf(discColor2));
			setCenterX(CIRCLE_DIAMETER/2);
			setCenterY(CIRCLE_DIAMETER/2);
		}
	}

	private boolean gameEnded(int row,int column){
		//vertical points
		List<javafx.geometry.Point2D>verticalPoints = IntStream.rangeClosed(row-3,row+3)
				.mapToObj(r->new Point2D(r,column))
						.collect(Collectors.toList());

		//horizontal points
		List<javafx.geometry.Point2D>horizontalPoints = IntStream.rangeClosed(column-3,column+3)
				.mapToObj(col->new Point2D(row,col))
				.collect(Collectors.toList());

		//Diagonal points
		Point2D startPoint1=new Point2D(row-3,column+3);
		List<javafx.geometry.Point2D>diagonal1Points = IntStream.rangeClosed(0,6)
				.mapToObj(i->startPoint1.add(i,-i))
				.collect(Collectors.toList());

		Point2D startPoint2=new Point2D(row-3,column-3);
		List<javafx.geometry.Point2D>diagonal2Points = IntStream.rangeClosed(0,6)
				.mapToObj(i->startPoint2.add(i,i))
				.collect(Collectors.toList());



		boolean isEnded=checkCombinations(verticalPoints)||checkCombinations(horizontalPoints)||checkCombinations(diagonal1Points)||checkCombinations(diagonal2Points);


		return isEnded;
	}

	private boolean checkCombinations(List<Point2D> points) {
		int chain=0;
		for (Point2D point: points) {
			int rowIndexForArray =(int)point.getX();
			int columnIndexForArray =(int) point.getY();

			Disc disc = getDiscIfPresent(rowIndexForArray,columnIndexForArray);

			if(disc!=null && disc.isPlayerOneMove==isPlayeroneTurn){
				chain++;
				if(chain==4) return true;
			}
			else chain = 0;
		}
		return false;
	}

	private Disc getDiscIfPresent(int row,int column){	//To prevent ArrayIndexOutOfBoundException
		if(row>=ROWS||row<0||column>=COLUMNS||column<0)
			return null;
		return insertedDiscs[row][column];
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

		startButton.setOnAction(ActionEvent -> {
			PLAYER_ONE=player1.getText();
			PLAYER_TWO=player2.getText();
			playerNameLabel.setText(PLAYER_ONE);
		});


	}
}
