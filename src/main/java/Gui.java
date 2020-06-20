/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.FileInputStream;

/**
 *
 * @author Matheus Sereno
 */

public class Gui {

    // Scenes
    public Scene introScene;
    Scene playScene;

    //    Parameters
    double cemitery [] = {700, 50};
    double boardPosX = 100;
    double boardPosY = 100;
    final double origin [] = {50,700};
    final double originSuper [] = {700,700};

    //    Board characteristics
    double lineWidth = 10;
    double radius = 20;
    double buttonSize = 15;
    Color boardColor = Color.SADDLEBROWN;

    //    Information about the pieces of the game
    double positionMatrix[][] = new double[2][24];
    boolean neighborsSelected = false;
    int whitePositions[] = new int [9];
    int blackPositions[] = new int [9];
    int neighborhoodPositions[] = new int[4];
    int neighborhoodSuperPositions[] = new int[24];
    int selectedPiece = -1;

    //    Visual elements of the game
    Button buttons[] = new Button[24];
    Circle whitePieces[] = new Circle[9];
    Circle blackPieces[] = new Circle[9];
    Circle neighbors []= new Circle[4];
    Circle superNeighbors[] = new Circle[24];

    //    Board and IA instantiations
    Board board = new Board();
    Ia ia = new Ia(board);

    //    Class constructor
    public Gui(){
    }

    // Initializing board screen
    public void setUpIntro(Stage stage) throws Exception{

        // Background image
        FileInputStream file = new FileInputStream("src/main/resources/img/game.jpg");
        Image image = new Image(file);
        ImageView background = new ImageView(image);

        // Title of screen
        Text text = new Text(20,50,"Trabalho de IA\nNine Men's Morris");
        text.setWrappingWidth(1000);
        text.setFont(Font.font("Ubuntu Light", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 50));
        text.setTextOrigin(VPos.TOP);
        text.setTextAlignment(TextAlignment.JUSTIFY);
        text.getStyleClass().add("emphasized-text");

        // Play button
        Button play = new Button();
        play.setText("Play");
        play.setLayoutX(640);
        play.setLayoutY(340);

        // Click action for the button
        play.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                stage.setScene(playScene);
            }
        });

        // Grouping elements into Container
        Group content = new Group(text, play);
        Group scene = new Group(background, content);

        // Adding Container to Scene
        introScene = new Scene(scene, 1200, 800);
        // "src/main/resources/trilha.css"
        introScene.getStylesheets().add("trilha.css");
    }

    private boolean movePhase1(boolean turn, int destination){
        if(turn){
            if(board.addPiece(destination, Board.WHITE)){
                System.out.println("Todas as peças já foram colocadas");
                return true;
            }else{
                whitePositions[board.getNumberOfTurns()/2] = destination;
                whitePieces[board.getNumberOfTurns()/2].setLayoutX(positionMatrix[0][destination]);
                whitePieces[board.getNumberOfTurns()/2].setLayoutY(positionMatrix[1][destination]);
            }
        }else{
            Triple iaMovement = ia.bestMovement(board, Board.BLACK);
            destination = iaMovement.destination;

            if (board.addPiece(iaMovement.destination, Board.BLACK)){
                System.out.println("Todas as peças pretas já foram colocadas");
                return true;
            }
            blackPositions[board.getNumberOfTurns()/2] = destination;
            blackPieces[board.getNumberOfTurns()/2].setLayoutX(positionMatrix[0][destination]);
            blackPieces[board.getNumberOfTurns()/2].setLayoutY(positionMatrix[1][destination]);

            if (board.hasHappenedMorris(false)){
                int excludePosition = iaMovement.positionToRemove;
                if(board.removePiece(excludePosition, Board.BLACK)){
                    System.out.println("Não foi possível excluir peça branca na posição " + iaMovement.positionToRemove + " .");
                    return true;
                }

                int removedPieceId = findPositionFromID(Board.WHITE, excludePosition);
                whitePieces[removedPieceId].setLayoutX(cemitery[0]);
                whitePieces[removedPieceId].setLayoutY(cemitery[1]);
                whitePositions[removedPieceId] = -1;
                cemitery[1] += 50;

                board.isExcludeMode = false;
                board.isOver(checkIsStuck(board.turn));
                board.excludeProperty.set("A IA é muito forte! Cuidado...");

            }
            board.changeTurn();
        }
        return false;
    }

    // Método não move realmente a peça. Somente desenha as vizinhanças
    private void movePiecePhase3(boolean turn, int pieceId){
        if(!neighborsSelected){ // SELECTING NEIGHBORHOOD
            if(turn){
                for(int i = 0; i< 24; i++){
                    if( board.getOccupant(i) == Board.EMPTY){
                        neighborhoodSuperPositions[i] = i;
                        superNeighbors[i].setLayoutX(positionMatrix[0][i]);
                        superNeighbors[i].setLayoutY(positionMatrix[1][i]);
                    }else{
                        neighborhoodSuperPositions[i] = -1;
                        superNeighbors[i].setLayoutX(originSuper[0]);
                        superNeighbors[i].setLayoutY(originSuper[1]);
                    }
                }
            }else{
                for(int i = 0; i< 24; i++){
                    if(board.getOccupant(i) == Board.EMPTY){
                        neighborhoodSuperPositions[i] = i;
                        superNeighbors[i].setLayoutX(positionMatrix[0][i]);
                        superNeighbors[i].setLayoutY(positionMatrix[1][i]);
                    }else{
                        neighborhoodSuperPositions[i] = -1;
                        superNeighbors[i].setLayoutX(originSuper[0]);
                        superNeighbors[i].setLayoutY(originSuper[1]);
                    }
                }
            }
            selectedPiece = pieceId;
            neighborsSelected = true;
        }else{
            if(pieceId == selectedPiece){
                for(int j = 0; j<24; j++){
                    superNeighbors[j].setLayoutX(originSuper[0]);
                    superNeighbors[j].setLayoutY(originSuper[1]);
                }
                neighborsSelected = false;
                selectedPiece = -1;
            }
        }
    }

    // Dividir métodos em jogar IA e selecionar vizinhança para as brancas
    private void movePiecePhase2(boolean turn, int pieceId){
        if(board.isWhitePhase3 && turn){
            movePiecePhase3(true, pieceId);
            return;
        }
        if(!neighborsSelected){
            int counter = 0;
            if(turn){
                for(int i = 0; i< Board.NEIGHBORHOOD[whitePositions[pieceId]].length; i++){
                    if(board.getOccupant(Board.NEIGHBORHOOD[whitePositions[pieceId]][i]) == Board.EMPTY){
                        neighborhoodPositions[i] = Board.NEIGHBORHOOD[whitePositions[pieceId]][i];
                        neighbors[i].setLayoutX(positionMatrix[0][Board.NEIGHBORHOOD[whitePositions[pieceId]][i]]);
                        neighbors[i].setLayoutY(positionMatrix[1][Board.NEIGHBORHOOD[whitePositions[pieceId]][i]]);
                    }else{
                        counter++;
                        neighborhoodPositions[i] = -1;
                        neighbors[i].setLayoutX(origin[0]);
                        neighbors[i].setLayoutY(origin[1]);
                    }
                }
                if(counter != Board.NEIGHBORHOOD[whitePositions[pieceId]].length){
                    selectedPiece = pieceId;
                    neighborsSelected = true;
                }
            }else{
                Triple iaMovement = ia.bestMovement(board, Board.BLACK);
                int position = iaMovement.currentPosition;
                int destination = iaMovement.destination;
                int positionToExclude = iaMovement.positionToRemove;


                board.movePiece(position, destination, false);
                int iaPieceId = findPositionFromID(Board.BLACK, position);
                blackPieces[iaPieceId].setLayoutX(positionMatrix[0][destination]);
                blackPieces[iaPieceId].setLayoutY(positionMatrix[1][destination]);
                blackPositions[iaPieceId] = destination;

                if(!board.hasHappenedMorris(true)){
                    checkIsStuck(board.turn);
                    board.changeTurn();

                }else {
                    int pieceToExcludeId = findPositionFromID(Board.WHITE, positionToExclude);

                    if(!board.removePiece(whitePositions[pieceToExcludeId], Board.BLACK)){
                        whitePositions[pieceToExcludeId] = -1;
                    }else{
                        board.excludeProperty.set("Não foi possível remover a peca " + pieceToExcludeId + " [ERRO DA IA]");
                        return;
                    }
                    whitePieces[pieceToExcludeId].setLayoutX(cemitery[0]);
                    whitePieces[pieceToExcludeId].setLayoutY(cemitery[1]);
                    cemitery[1] += 50;
                    board.isExcludeMode = false;
                    board.isOver(checkIsStuck(board.turn));
                    board.excludeProperty.set("A IA é muito forte! Cuidado...");
                    board.changeTurn();

                }
            }
        }else{
            if(pieceId == selectedPiece){
                for(int j = 0; j<4; j++){
                    neighbors[j].setLayoutX(origin[0]);
                    neighbors[j].setLayoutY(origin[1]);
                }
                neighborsSelected = false;
                selectedPiece = -1;
            }

        }
    }

    public boolean checkIsStuck(boolean turn){
        if(turn){
            for(int j = 0; j < 9 ; j++){
                if( blackPositions[j] == -1 )
                    continue;
                for(int i = 0; i< Board.NEIGHBORHOOD[blackPositions[j]].length;i++){
                    if(board.getOccupant(Board.NEIGHBORHOOD[blackPositions[j]][i]) == Board.EMPTY)
                        return false;
                }
            }
        }else{
            for(int j = 0; j < 9 ; j++){
                if(whitePositions[j] == -1)
                    continue;
                for(int i = 0; i< Board.NEIGHBORHOOD[whitePositions[j]].length ;i++){
                    if(board.getOccupant(Board.NEIGHBORHOOD[whitePositions[j]][i]) == Board.EMPTY)
                        return false;
                }
            }
        }
        return true;
    }

    public void setUpPlay(){

        // Play again button
        Button playAgain = new Button("Jogar Novamente");
        playAgain.getStylesheets().add("trilha.css");
        playAgain.setVisible(false);

        // Event when button is clicked
        playAgain.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                startAgain();
            }
        });

        // Drawing Board
        Rectangle exterior = new Rectangle(500,500, Color.TRANSPARENT);
        exterior.setX(boardPosX);
        exterior.setY(boardPosY);
        exterior.setStroke(boardColor);
        exterior.setStrokeWidth(lineWidth);

        Rectangle middle = new Rectangle(300, 300, Color.TRANSPARENT);
        middle.setStroke(boardColor);
        middle.setStrokeWidth(lineWidth);
        middle.setX(boardPosX + exterior.getWidth()/2 - middle.getWidth()/2);
        middle.setY(boardPosY + exterior.getHeight()/2 - middle.getHeight()/2);

        Rectangle interior = new Rectangle(100, 100, Color.TRANSPARENT);
        interior.setStroke(boardColor);
        interior.setStrokeWidth(lineWidth);
        interior.setX(boardPosX + exterior.getWidth()/2 - interior.getWidth()/2);
        interior.setY(boardPosY + exterior.getHeight()/2 - interior.getHeight()/2);

        Line middleLines[] = new Line[4];
        middleLines[0] = new Line(boardPosX+exterior.getWidth()/2,boardPosY, boardPosX+exterior.getWidth()/2, boardPosY+ (exterior.getWidth()-interior.getWidth())/2);
        middleLines[0].setStrokeWidth(lineWidth);
        middleLines[0].setStroke(boardColor);

        middleLines[1] = new Line(boardPosX, boardPosY + exterior.getHeight()/2, boardPosX + (exterior.getWidth()-interior.getWidth())/2, boardPosY + exterior.getHeight()/2);
        middleLines[1].setStrokeWidth(lineWidth);
        middleLines[1].setStroke(boardColor);

        middleLines[2] = new Line(boardPosX+exterior.getWidth()/2,boardPosY+exterior.getHeight(), boardPosX+exterior.getWidth()/2, boardPosY + exterior.getHeight()- (exterior.getWidth()-interior.getWidth())/2);
        middleLines[2].setStrokeWidth(lineWidth);
        middleLines[2].setStroke(boardColor);

        middleLines[3] = new Line(boardPosX + exterior.getWidth(), boardPosY + exterior.getHeight()/2, boardPosX + exterior.getWidth() - (exterior.getWidth()-interior.getWidth())/2, boardPosY + exterior.getHeight()/2);
        middleLines[3].setStrokeWidth(lineWidth);
        middleLines[3].setStroke(boardColor);

        // Buttons
        for(int i = 0; i<24; i++){
            buttons[i] = new Button();
            buttons[i].setId( Integer.toString(i) );
            buttons[i].setStyle("-fx-background-radius: 5em; " +
                    "-fx-min-width: 30px; " +
                    "-fx-min-height: 30px; " +
                    "-fx-max-width: 30px; " +
                    "-fx-max-height: 30px;"+
                    "-fx-background-color: rgb(30,30,30)");
        }

        // Positioning buttons
        for(int i = 0; i<3; i++){
            buttons[i].setLayoutX(boardPosX + i*exterior.getWidth()/2 - buttonSize);
            buttons[i].setLayoutY(boardPosY-buttonSize);
            positionMatrix[0][i] = boardPosX + i*exterior.getWidth()/2;
            positionMatrix[1][i] = boardPosY;
        }


        for(int i = 3; i<6; i++){
            buttons[i].setLayoutX(boardPosX + (exterior.getWidth()-middle.getWidth())/2 + (i-3)*middle.getWidth()/2 - buttonSize);
            buttons[i].setLayoutY(boardPosY + (exterior.getHeight()-middle.getHeight())/2 -buttonSize);
            positionMatrix[0][i] = boardPosX + (exterior.getWidth()-middle.getWidth())/2 + (i-3)*middle.getWidth()/2;
            positionMatrix[1][i] = boardPosY + (exterior.getHeight()-middle.getHeight())/2;
        }

        for(int i = 6; i<9; i++){
            buttons[i].setLayoutX(boardPosX + (exterior.getWidth()-interior.getWidth())/2 + (i-6)*interior.getWidth()/2 - buttonSize);
            buttons[i].setLayoutY( boardPosY + (exterior.getHeight()-interior.getHeight())/2 -buttonSize);
            positionMatrix[0][i] = boardPosX + (exterior.getWidth()-interior.getWidth())/2 + (i-6)*interior.getWidth()/2;
            positionMatrix[1][i] = boardPosY + (exterior.getHeight()-interior.getHeight())/2;
        }

        for(int i = 9; i<12; i++){
            buttons[i].setLayoutX( boardPosX + (i-9)*interior.getWidth() - buttonSize);
            buttons[i].setLayoutY( boardPosY + (exterior.getHeight())/2 -buttonSize);
            positionMatrix[0][i] = boardPosX + (i-9)*interior.getWidth();
            positionMatrix[1][i] = boardPosY + (exterior.getHeight())/2;
        }

        for(int i = 12; i<15; i++){
            buttons[i].setLayoutX( boardPosX + 3* interior.getWidth() + (i-12)* interior.getWidth() - buttonSize);
            buttons[i].setLayoutY( boardPosY + (exterior.getHeight())/2 -buttonSize);
            positionMatrix[0][i] = boardPosX + 3* interior.getWidth() + (i-12)* interior.getWidth();
            positionMatrix[1][i] = boardPosY + (exterior.getHeight())/2;
        }


        for(int i = 15; i<18; i++){
            buttons[i].setLayoutX( boardPosX + (exterior.getWidth()-interior.getWidth())/2 + (i-15)*interior.getWidth()/2 - buttonSize);
            buttons[i].setLayoutY( boardPosY + exterior.getHeight() -(exterior.getHeight()-interior.getHeight())/2 -buttonSize);
            positionMatrix[0][i] = boardPosX + (exterior.getWidth()-interior.getWidth())/2 + (i-15)*interior.getWidth()/2;
            positionMatrix[1][i] = boardPosY + exterior.getHeight() -(exterior.getHeight()-interior.getHeight())/2;
        }

        for(int i = 18; i<21; i++){
            buttons[i].setLayoutX( boardPosX + (exterior.getWidth()-middle.getWidth())/2 + (i-18)*middle.getWidth()/2 - buttonSize);
            buttons[i].setLayoutY( boardPosY + exterior.getHeight() - (exterior.getHeight()-middle.getHeight())/2 - buttonSize);
            positionMatrix[0][i] = boardPosX + (exterior.getWidth()-middle.getWidth())/2 + (i-18)*middle.getWidth()/2;
            positionMatrix[1][i] = boardPosY + exterior.getHeight() - (exterior.getHeight()-middle.getHeight())/2;
        }

        for(int i = 21; i<24; i++){
            buttons[i].setLayoutX( boardPosX + (i-21)*exterior.getWidth()/2 - buttonSize);
            buttons[i].setLayoutY( boardPosY + exterior.getHeight() - buttonSize);
            positionMatrix[0][i] = boardPosX + (i-21)*exterior.getWidth()/2;
            positionMatrix[1][i] = boardPosY + exterior.getHeight();
        }

        // Buttons actions
        for(int i = 0; i<24; i++){
            buttons[i].setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    String partes [] =  event.getSource().toString().split("[=,]");
                    int pressedButton = Integer.parseInt(partes[1]);
                    if(board.numberOfTurns > 18 || board.isExcludeMode || board.getOccupant(pressedButton)!= Board.EMPTY) return;
                    movePhase1(board.turn, pressedButton);
                    if(board.hasHappenedMorris(false))
                        System.out.println("Ocorreu uma triade");
                    else
                        board.changeTurn();
                }
            });
        }

        // White Pieces
        for(int i = 0; i<9; i++){
            whitePieces[i] = new Circle(radius, Color.WHITE);
            whitePieces[i].setStrokeWidth(lineWidth);
            whitePieces[i].setLayoutX(boardPosX + 65*i);
            whitePieces[i].setLayoutY(boardPosY - 50);
            whitePieces[i].setId( Integer.toString(i) );
        }

        // Black Piece
        for(int i = 0; i<9; i++){
            blackPieces[i] = new Circle(radius, Color.BLACK);
            blackPieces[i].setStrokeWidth(lineWidth);
            blackPieces[i].setLayoutX(boardPosX + 65*i);
            blackPieces[i].setLayoutY(boardPosY +exterior.getHeight() + 50);
            blackPieces[i].setId( Integer.toString(i) );

        }

        // Setting actions for white pieces
        for(int i = 0; i<9; i++){
            whitePieces[i].setOnMouseClicked(new EventHandler<MouseEvent>() {

                // Estilo de saída do event.getSource.toString(): ''Button[id=22, styleClass=button]''
                @Override
                public void handle(MouseEvent event) {
                    String partes [] =  event.getSource().toString().split("[=,]");
                    int piecePressedId = Integer.parseInt(partes[1]);
                    if(whitePositions[piecePressedId] == -1)
                        return;
                    if(board.turn && board.numberOfTurns > 18 && !board.isExcludeMode)
                        movePiecePhase2(true, piecePressedId);
                    if(board.isExcludeMode && !board.turn){
                        if(! board.removePiece(blackPositions[piecePressedId], Board.WHITE)){
                            blackPositions[piecePressedId] = -1;
                        }else{
                            board.excludeProperty.set("Não foi possível remover a peca " + piecePressedId+ " [ERRO DE LÓGICA]");
                            return;
                        }
                        whitePieces[piecePressedId].setLayoutX(cemitery[0]);
                        whitePieces[piecePressedId].setLayoutY(cemitery[1]);
                        cemitery[1] += 50;
                        board.isExcludeMode = false;
                        board.isOver(checkIsStuck(board.turn));
                        board.excludeProperty.set("A IA é muito forte! Cuidado...");
                        board.changeTurn();
                    }

                }
            });
        }

        // Set actions for black pieces
        for(int i = 0; i<9; i++){
            blackPieces[i].setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    String partes [] =  event.getSource().toString().split("[=,]");
                    int piecePressedId = Integer.parseInt(partes[1]);
                    if(blackPositions[piecePressedId] == -1)
                        return;
                    if(!board.turn && board.numberOfTurns > 18 && !board.isExcludeMode)
                        movePiecePhase2(false, piecePressedId);
                    if(board.isExcludeMode && board.turn){
                        if(!board.removePiece(blackPositions[piecePressedId], Board.WHITE)){
                            blackPositions[piecePressedId] = -1;
                        }else{
                            board.excludeProperty.set("Não foi possível remover a peca " + piecePressedId+ " [ERRO DE LÓGICA]");
                            return;
                        }
                        blackPieces[piecePressedId].setLayoutX(cemitery[0]);
                        blackPieces[piecePressedId].setLayoutY(cemitery[1]);
                        cemitery[1] += 50;
                        board.isExcludeMode = false;
                        board.isOver(checkIsStuck(board.turn));
                        board.excludeProperty.set("A IA é muito forte! Cuidado...");
                        board.changeTurn();
                    }
                }

            });
        }

        // Circles to representing possible moves in Phase 2 - Neighbors
        for(int i = 0; i < 4;i++){
            neighbors[i] = new Circle(1.5*radius, Color.TRANSPARENT);
            neighbors[i].setStrokeWidth(lineWidth);
            neighbors[i].setStroke(Color.RED);
            neighbors[i].setLayoutX(origin[0]);
            neighbors[i].setLayoutY(origin[1]);
            neighbors[i].setId(Integer.toString(i));
        }

        // Setting actions for neighbors
        for(int i = 0; i<4; i++){
            neighbors[i].setOnMouseClicked(new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent event) {
                    if(neighborsSelected){
                        String partes [] =  event.getSource().toString().split("[=,]");
                        int neighborPressedId = Integer.parseInt(partes[1]);
                        int destination = neighborhoodPositions[neighborPressedId];
                        if(board.turn && destination!=-1){
                            for(int i = 0; i<4; i++){
                                neighbors[i].setLayoutX(origin[0]);
                                neighbors[i].setLayoutY(origin[1]);
                                neighborhoodPositions[i] = -1;
                            }
                            board.movePiece(whitePositions[selectedPiece],destination, true );
                            whitePieces[selectedPiece].setLayoutX(positionMatrix[0][destination]);
                            whitePieces[selectedPiece].setLayoutY(positionMatrix[1][destination]);
                            whitePositions[selectedPiece] = destination;
                            if(!board.hasHappenedMorris(false)){
                                board.isOver(checkIsStuck(board.turn));
                                board.changeTurn();
                            }
                            neighborsSelected = !neighborsSelected;
                            selectedPiece = -1;
                            return;
                        }
                        if(!board.turn && destination!=-1){
                            for(int i = 0; i<4; i++){
                                neighbors[i].setLayoutX(origin[0]);
                                neighbors[i].setLayoutY(origin[1]);
                                neighborhoodPositions[i] = -1;
                            }
                            board.movePiece(blackPositions[selectedPiece],destination, false );
                            blackPieces[selectedPiece].setLayoutX(positionMatrix[0][destination]);
                            blackPieces[selectedPiece].setLayoutY(positionMatrix[1][destination]);
                            blackPositions[selectedPiece] = destination;
                            if(!board.hasHappenedMorris(false)){
                                checkIsStuck(board.turn);
                                board.changeTurn();
                            }
                            neighborsSelected = !neighborsSelected;
                            selectedPiece = -1;
                            return;
                        }
                    }
                }
            });
        }

        // Circles representing possible moves in Phase 3 - super neighbors
        for(int i = 0; i < 24;i++){
            superNeighbors[i] = new Circle(1.5*radius, Color.TRANSPARENT);
            superNeighbors[i].setStrokeWidth(lineWidth);
            superNeighbors[i].setStroke(Color.GOLD);
            superNeighbors[i].setLayoutX(originSuper[0]);
            superNeighbors[i].setLayoutY(originSuper[1]);
            superNeighbors[i].setId(Integer.toString(i));
        }

        // Acions of super neighbors
        for(int i = 0; i<24; i++){
            superNeighbors[i].setOnMouseClicked(new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent event) {
                    if(neighborsSelected){
                        String partes [] =  event.getSource().toString().split("[=,]");
                        int neighborPressedId = Integer.parseInt(partes[1]);
                        int destination = neighborhoodSuperPositions[neighborPressedId];
                        if(board.turn && destination!=-1){
                            for(int i = 0; i<24; i++){
                                superNeighbors[i].setLayoutX(originSuper[0]);
                                superNeighbors[i].setLayoutY(originSuper[1]);
                                neighborhoodSuperPositions[i] = -1;
                            }
                            board.movePiece(whitePositions[selectedPiece],destination, true );
                            whitePieces[selectedPiece].setLayoutX(positionMatrix[0][destination]);
                            whitePieces[selectedPiece].setLayoutY(positionMatrix[1][destination]);
                            whitePositions[selectedPiece] = destination;
                            if(!board.hasHappenedMorris(false)){
                                board.isOver(checkIsStuck(board.turn));
                                board.changeTurn();
                            }
                            neighborsSelected = !neighborsSelected;
                            selectedPiece = -1;
                            return;
                        }
                        if(!board.turn && destination!=-1){
                            for(int i = 0; i<24; i++){
                                superNeighbors[i].setLayoutX(originSuper[0]);
                                superNeighbors[i].setLayoutY(originSuper[1]);
                                neighborhoodSuperPositions[i] = -1;
                            }
                            board.movePiece(blackPositions[selectedPiece],destination, true );
                            blackPieces[selectedPiece].setLayoutX(positionMatrix[0][destination]);
                            blackPieces[selectedPiece].setLayoutY(positionMatrix[1][destination]);
                            blackPositions[selectedPiece] = destination;
                            if(!board.hasHappenedMorris(false)){
                                if(board.isOver(checkIsStuck(board.turn)))
                                    playAgain.setVisible(true);
                                board.changeTurn();
                            }
                            neighborsSelected = !neighborsSelected;
                            selectedPiece = -1;
                        }
                    }
                }
            });
        }

        // Button to make IA choose button
        Button playIA = new Button("Jogar IA");
        playIA.getStylesheets().add("trilha.css");
        playIA.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                if(!board.turn){
                    if(board.numberOfTurns < 18)
                        movePhase1(false, -1);
                    else
                        movePiecePhase2(false, -1);
                }
            }
        });

        // UI to choose how deep the IA is going to work
        Label options = new Label("Profundida de busca da IA: ");
        ObservableList num = FXCollections.observableArrayList(
                "1","2","3","4","5","6","7","8"
        );
        ChoiceBox deepness = new ChoiceBox(num);
        deepness.getSelectionModel().selectFirst();

        Ia.depthProperty.bind(deepness.getSelectionModel().selectedItemProperty());
        HBox infoIA = new HBox(options, deepness);

        // Grouping components of the UI
        Group frame = new Group(exterior, middle, interior,middleLines[0],middleLines[1],middleLines[2], middleLines[3]);
        Group boardSpots = new Group(buttons[0],buttons[1], buttons[2], buttons[3], buttons[4], buttons[5], buttons[6],buttons[7], buttons[8], buttons[9], buttons[10], buttons[11], buttons[12], buttons[13],buttons[14], buttons[15], buttons[16], buttons[17] ,buttons[18],buttons[19], buttons[20], buttons[21], buttons[22], buttons[23]);
        Group blackPiecesUI = new Group(whitePieces[0], whitePieces[1], whitePieces[2], whitePieces[3], whitePieces[4], whitePieces[5], whitePieces[6], whitePieces[7], whitePieces[8]);
        Group whitePiecesUI = new Group(blackPieces[0], blackPieces[1], blackPieces[2], blackPieces[3], blackPieces[4], blackPieces[5], blackPieces[6], blackPieces[7], blackPieces[8]);
        Group neighborsUI = new Group(neighbors[0], neighbors[1], neighbors[2], neighbors[3]);
        Group superNeighborsUI = new Group (superNeighbors[0], superNeighbors[1], superNeighbors[2], superNeighbors[3], superNeighbors[4], superNeighbors[5], superNeighbors[6], superNeighbors[7], superNeighbors[8], superNeighbors[9], superNeighbors[10], superNeighbors[11], superNeighbors[12], superNeighbors[13], superNeighbors[14], superNeighbors[15], superNeighbors[16], superNeighbors[17], superNeighbors[18], superNeighbors[19], superNeighbors[20], superNeighbors[21], superNeighbors[22], superNeighbors[23] );

        Text title = new Text("Info do jogo");
        title.setWrappingWidth(350);
        title.setFont(Font.font("Ubuntu Light", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 10));
        title.setTextOrigin(VPos.TOP);
        title.setTextAlignment(TextAlignment.JUSTIFY);
        title.getStyleClass().add("emphasized-text");

        Text turn = new Text();
        Text exclude = new Text();
        exclude.setWrappingWidth(350);
        Text win = new Text();
        Text rounds = new Text();

        turn.textProperty().bindBidirectional(board.turnProperty);
        exclude.textProperty().bindBidirectional(board.excludeProperty);
        win.textProperty().bindBidirectional(board.winProperty);
        rounds.textProperty().bindBidirectional(board.turnsProperty);

        win.setWrappingWidth(350);

        VBox messagesUI = new VBox(title,turn,exclude,win,rounds,playIA, infoIA,playAgain);
        messagesUI.setLayoutX(800);
        messagesUI.setLayoutY(30);
        messagesUI.setSpacing(10);

        Group boardUI = new Group(frame, boardSpots, blackPiecesUI, whitePiecesUI, neighborsUI, messagesUI, superNeighborsUI);

        playScene = new Scene(boardUI, 1200, 800);
        playScene.getStylesheets().add("trilha.css");
        playScene.setFill(Color.BISQUE);
    }

    public int findPositionFromID(int player, int position){
        for (int i = 0; i < 9; i++){
            if (player == Board.WHITE){
                if (whitePositions[i] == position)
                    return i;
            }else {
                if (blackPositions[i] == position)
                    return i;
            }
        }

        return -1;
    }

    void startAgain(){
        for(int i = 0; i<9; i++){
            whitePieces[i].setLayoutX(boardPosX + 65*i);
            whitePieces[i].setLayoutY(boardPosY - 50);
        }

        for(int i = 0; i<9; i++){
            blackPieces[i].setLayoutX(boardPosX + 65*i);
            blackPieces[i].setLayoutY(boardPosY + 500 + 50);
        }
        board.restart();
    }
}