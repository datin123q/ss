package application;

import java.io.InputStream;
import java.util.Random;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import application.Board.Cell;

public class BattleshipMain extends Application {

    private boolean running = false;
    private Board enemyBoard, playerBoard;

    private int shipsToPlace = 5;

    private boolean enemyTurn = false;

    private Random random = new Random();

    private Parent createContent() {
    	
    	InputStream input = getClass().getResourceAsStream("/images/back.png");
		 
        Image image = new Image(input);
        ImageView imageView = new ImageView(image);
        Pane root = new Pane(imageView);
        root.setPrefSize(1000, 600);
        Media media = new Media(getClass().getResource("/sound/sound.mp3").toString());
    	MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0.7);
        mediaPlayer.setAutoPlay(true);
//        Pane root = new Pane();
//        root.setPrefSize(1000, 600);
        InputStream input1 = getClass().getResourceAsStream("/images/unmute.png");
		 
        Image image1 = new Image(input1);
        ImageView imageView1 = new ImageView(image1);
        InputStream input2 = getClass().getResourceAsStream("/images/mute.png");
		 
        Image image2 = new Image(input2);
        ImageView imageView2 = new ImageView(image2);
        
        
        
        Button but1 = new Button();
        but1.setShape(new Circle(15));
        but1.setMinSize(30, 30);
        but1.setMaxSize(30, 30);
        but1.setGraphic(imageView1 );
        but1.setLayoutX(0);
        but1.setLayoutY(0);
        root.getChildren().addAll(but1);
        but1.setOnAction(new EventHandler<ActionEvent>() {
        	int k=0;
        	 
            @Override
            public void handle(ActionEvent event) {
            	k++;
            	if(k%2==1) {mediaPlayer.stop(); but1.setGraphic(imageView2 );}
				else {mediaPlayer.play(); but1.setGraphic(imageView1 );}

            }
        });

        enemyBoard = new Board(true, event -> {
            if (!running)
                return;

            Cell cell = (Cell) event.getSource();
            if (cell.wasShot)
                return;

            enemyTurn = !cell.shoot();

            if (enemyBoard.ships == 0) {
                System.out.println("YOU WIN");
                System.exit(0);
            }

            if (enemyTurn)
                enemyMove();
        });

        playerBoard = new Board(false, event -> {
            if (running)
                return;

            Cell cell = (Cell) event.getSource();
            if (playerBoard.placeShip(new Ship(shipsToPlace, event.getButton() == MouseButton.PRIMARY), cell.x, cell.y)) {
                if (--shipsToPlace == 0) {
                    startGame();
                }
            }
        });

        HBox vbox = new HBox(100, playerBoard, enemyBoard);
        vbox.setAlignment(Pos.CENTER);

       vbox.setLayoutX(90);
       vbox.setLayoutY(125);
       root.getChildren().add(vbox);

        return root;
    }

    private void enemyMove() {
        while (enemyTurn) {
            int x = random.nextInt(10);
            int y = random.nextInt(10);

            Cell cell = playerBoard.getCell(x, y);
            if (cell.wasShot)
                continue;

            enemyTurn = cell.shoot();

            if (playerBoard.ships == 0) {
                System.out.println("YOU LOSE");
                System.exit(0);
            }
        }
    }

    private void startGame() {
        // place enemy ships
        int type = 5;

        while (type > 0) {
            int x = random.nextInt(10);
            int y = random.nextInt(10);

            if (enemyBoard.placeShip(new Ship(type, Math.random() < 0.5), x, y)) {
                type--;
            }
        }

        running = true;
    }

    @Override
    public void start(Stage primaryStage)  {
        Scene scene = new Scene(createContent());
        primaryStage.setTitle("Battleship");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void run( Stage primaryStage) {

    	createContent();
    	enemyMove() ;
     	start(primaryStage);
    }
}
