
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.*;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Matheus Sereno e Caio
 */
public class Ia {

    public static Board board;
    public static Triple bestMovement = new Triple(-1,-1,-1);
    public static long timeByTurn = 2;
    public static SimpleStringProperty depthProperty = new SimpleStringProperty();

    // Weight of each heuristic attribute
    public int numberOfPieces;
    public int numberOfMorris;
    public int numberOfBlockedPieces;
    public int numberOfDoubledPieces;
    public int numberOfTriplePieces;
    public int numberOfAdjacentPieces;
    public int winningConfiguration;
    public int pieceMobility;
    public int blockSpot;

    // Class constructors
    public Ia(Board boardInstantiated){
        board = boardInstantiated;
    }

    public Ia() {}

    public static void setTimeByTurn(long t){
        timeByTurn = t;
    }

    // Função não está contando moinhos adjacentes!!!!!!!!!!!!!!!!!!!!!! Está contando número de moinhos! checar se moinhos tem casas em comum! Falta refazer
    private int countAdjacentMorris (Board board, int player){
        int[] morrisCount = new int[24];
        for (int i = 0; i < 24; i++) morrisCount[i] = 0;

        for (int m = 0; m < Board.TRIOS.length; m++){
            int count = 0;
            for (int j = 0; j < Board.TRIOS[m].length; j++){
                if (board.getOccupant(Board.TRIOS[m][j]) == player)  count++;
            }

            if (count == 3){
                for (int j = 0; j < Board.TRIOS[m].length; j++){
                    morrisCount[Board.TRIOS[m][j]]++;
                }
            }
        }

        int answer = 0;
        for (int position = 0; position < 24; position++){
            if (morrisCount[position] > 1){
                answer++;
            }
        }

        return answer;
    }

    private int adjacentMorrisDifference (Board board){
        return countAdjacentMorris(board, Board.WHITE) - countAdjacentMorris(board, Board.BLACK);
    }

    private int countPieceMobility(Board board, int player){
        int count = 0;

        for (int position = 0; position < 24; position++){
            if (board.getOccupant(position) == player){
                for (int j = 0; j < Board.NEIGHBORHOOD[position].length; j++){
                    if (board.getOccupant(Board.NEIGHBORHOOD[position][j]) == Board.EMPTY)
                        count++;
                }
            }
        }
        return count;
    }

    private int pieceMobilityDifference(Board board){
        return countPieceMobility(board, Board.WHITE) - countPieceMobility(board, Board.BLACK);
    }

    private int countBlockSpot(Board board, int player){
        int count = 0;
        int opponent;
        if(player == Board.WHITE) opponent = Board.BLACK;
        else opponent = Board.WHITE;
        for (int position = 0; position < 24; position++){
            if (board.getOccupant(position) == player){
                for (int j = 0; j < Board.NEIGHBORHOOD[position].length; j++){
                    if (board.getOccupant(Board.NEIGHBORHOOD[position][j]) == opponent)
                        count++;
                }
            }
        }
        return count;
    }

    private int blockSpotDifference(Board board){
        return countBlockSpot(board, Board.WHITE) - countBlockSpot(board, Board.BLACK);
    }

    public static int countTriplePieces (Board board, int player){
        int count = 0;

        for (int position = 0; position < 24; position++){
            if(board.getOccupant(position) == player){
                switch(Board.NEIGHBORHOOD[position].length){
                    case 2:
                        if(board.getOccupant(Board.NEIGHBORHOOD[position][0]) == player && board.getOccupant(Board.NEIGHBORHOOD[position][1]) == player) count++;
                        break;
                    case 3:
                        if(board.getOccupant(Board.NEIGHBORHOOD[position][0]) == player && board.getOccupant(Board.NEIGHBORHOOD[position][2]) == player) count++;
                        if(board.getOccupant(Board.NEIGHBORHOOD[position][1]) == player && board.getOccupant(Board.NEIGHBORHOOD[position][2]) == player) count++;
                        break;
                    case 4:
                        if(board.getOccupant(Board.NEIGHBORHOOD[position][0]) == player && board.getOccupant(Board.NEIGHBORHOOD[position][2]) == player) count++;
                        if(board.getOccupant(Board.NEIGHBORHOOD[position][1]) == player && board.getOccupant(Board.NEIGHBORHOOD[position][2]) == player) count++;
                        if(board.getOccupant(Board.NEIGHBORHOOD[position][0]) == player && board.getOccupant(Board.NEIGHBORHOOD[position][3]) == player) count++;
                        if(board.getOccupant(Board.NEIGHBORHOOD[position][1]) == player && board.getOccupant(Board.NEIGHBORHOOD[position][3]) == player) count++;
                        break;
                    default:
                        break;
                }
            }
        }

        return count;
    }

    public static int triplePiecesDifference (Board board){
        return countTriplePieces(board, Board.WHITE) - countTriplePieces(board, Board.BLACK);
    }

    public static int countDoublePieces (Board board, int player){
        int count = 0;
        for (int position = 0; position < 24; position++){
            if(board.getOccupant(position) == player){
                switch(Board.NEIGHBORHOOD[position].length){
                    case 2:
                        if(board.getOccupant(Board.NEIGHBORHOOD[position][0]) == player && board.getOccupant(Board.NEIGHBORHOOD[position][1]) == Board.EMPTY ) count++;
                        if(board.getOccupant(Board.NEIGHBORHOOD[position][1]) == player && board.getOccupant(Board.NEIGHBORHOOD[position][0]) == Board.EMPTY ) count++;
                        break;
                    case 3:
                        if(board.getOccupant(Board.NEIGHBORHOOD[position][0]) == player && board.getOccupant(Board.NEIGHBORHOOD[position][1]) == Board.EMPTY && board.getOccupant(Board.NEIGHBORHOOD[position][2]) == Board.EMPTY ) count++;
                        if(board.getOccupant(Board.NEIGHBORHOOD[position][1]) == player && board.getOccupant(Board.NEIGHBORHOOD[position][0]) == Board.EMPTY && board.getOccupant(Board.NEIGHBORHOOD[position][2]) == Board.EMPTY ) count++;
                        if(board.getOccupant(Board.NEIGHBORHOOD[position][2]) == player && board.getOccupant(Board.NEIGHBORHOOD[position][0]) == Board.EMPTY && board.getOccupant(Board.NEIGHBORHOOD[position][1]) == Board.EMPTY ) count++;
                        break;
                    case 4:
                        if(board.getOccupant(Board.NEIGHBORHOOD[position][0]) == player && board.getOccupant(Board.NEIGHBORHOOD[position][1]) == Board.EMPTY && board.getOccupant(Board.NEIGHBORHOOD[position][2]) == Board.EMPTY && board.getOccupant(Board.NEIGHBORHOOD[position][3]) == Board.EMPTY ) count++;
                        if(board.getOccupant(Board.NEIGHBORHOOD[position][1]) == player && board.getOccupant(Board.NEIGHBORHOOD[position][0]) == Board.EMPTY && board.getOccupant(Board.NEIGHBORHOOD[position][2]) == Board.EMPTY && board.getOccupant(Board.NEIGHBORHOOD[position][3]) == Board.EMPTY ) count++;
                        if(board.getOccupant(Board.NEIGHBORHOOD[position][2]) == player && board.getOccupant(Board.NEIGHBORHOOD[position][0]) == Board.EMPTY && board.getOccupant(Board.NEIGHBORHOOD[position][1]) == Board.EMPTY && board.getOccupant(Board.NEIGHBORHOOD[position][3]) == Board.EMPTY ) count++;
                        if(board.getOccupant(Board.NEIGHBORHOOD[position][3]) == player && board.getOccupant(Board.NEIGHBORHOOD[position][0]) == Board.EMPTY && board.getOccupant(Board.NEIGHBORHOOD[position][1]) == Board.EMPTY && board.getOccupant(Board.NEIGHBORHOOD[position][2]) == Board.EMPTY ) count++;
                        break;
                    default:
                        break;
                }
            }
        }
        return count;
    }

    public static int doublePiecesDifference (Board board){
        return countDoublePieces(board, Board.WHITE) - countDoublePieces(board, Board.BLACK);
    }

    public static int winningConfiguration(Board board){
        System.out.println("***********************************************************Calculando movimentos para ver se alguém perdeu por não ter mais movimentos***********************************************************");
        if (legalMoves(board, Board.BLACK).size() == 0) return 1;
        if (legalMoves(board, Board.WHITE).size() == 0) return -1;
        System.out.println("***********************************************************Terminado***********************************************************");
        if(countPieceNumber(board, Board.WHITE) == 2) return 1;
        if(countPieceNumber(board, Board.BLACK) == 2) return -1;
        return 0;
    }

    public static int countPieceNumber(Board board, int player){
        int count = 0;
        for (int position = 0; position < 24; position++){
            if (board.getOccupant(position) == player) count++;
        }
        return count;
    }

    public static int pieceNumberDifference(Board board){
        return countPieceNumber(board, Board.WHITE) - countPieceNumber(board, Board.BLACK);
    }

    public static int countPiecesBlocked(Board board, int player){
        int count = 0;

        for (int position = 0; position < 24; position++){
            if (board.getOccupant(position) == player){
                boolean blocked = true;
                for (int j = 0; j < Board.NEIGHBORHOOD[position].length; j++){
                    if (board.getOccupant(Board.NEIGHBORHOOD[position][j]) == Board.EMPTY){
                        blocked = false;
                        break;
                    }
                }

                if (blocked == true) count ++;
            }
        }
        return count;
    }

    public static int piecesBlockedDifference(Board board){
        return countPiecesBlocked(board, Board.WHITE) - countPiecesBlocked(board, Board.BLACK);
    }

    public static int countMorris(Board board, int player){
        int count = 0;
        for (int i = 0; i < Board.TRIOS.length; i++){
            if(board.getOccupant(Board.TRIOS[i][0]) == player && board.getOccupant(Board.TRIOS[i][1]) == player && board.getOccupant(Board.TRIOS[i][2]) == player ) count++;
        }
        return count;
    }

    public static int morrisDifference(Board board){
        return countMorris(board, Board.WHITE) - countMorris(board, Board.BLACK);
    }

    /* SETTERS */
    public void setNumberOfPieces(int x){
        numberOfPieces = x;
    }

    public void setNumberOfMorris(int x){
        numberOfMorris = x;
    }

    public void setNumberOfBlockedPieces(int x){
        numberOfBlockedPieces = x;
    }

    public void setNumberOfDoubledPieces(int x){
        numberOfDoubledPieces = x;
    }

    public void setNumberOfTriplePieces(int x){
        numberOfTriplePieces = x;
    }

    public void setNumberOfAdjacentPieces(int x){
        numberOfAdjacentPieces = x;
    }

    public void setWinningConfiguration(int x){
        winningConfiguration = x;
    }

    public void setPieceMobility(int pieceMobility) {
        this.pieceMobility = pieceMobility;
    }

    public void setBlockSpot(int blockSpot) {
        this.blockSpot = blockSpot;
    }

    /* GETTERS */
    public int getNumberOfPieces(){
        return numberOfPieces;
    }

    public int getNumberOfMorris(){
        return numberOfMorris;
    }

    public int getNumberOfBlockedPieces(){
        return numberOfBlockedPieces;
    }

    public int getNumberOfDoubledPieces(){
        return numberOfDoubledPieces;
    }

    public int getNumberOfTriplePieces(){
        return numberOfTriplePieces;
    }

    public int getNumberOfAdjacentPieces(){
        return numberOfAdjacentPieces;
    }

    public int getWinningConfiguration(){
        return winningConfiguration;
    }

    public int getPieceMobility() {
        return pieceMobility;
    }

    public int getBlockSpot() {
        return blockSpot;
    }

    public void setHeuristicPhase1(){
        setNumberOfMorris(40);
        setNumberOfBlockedPieces(1);
        setNumberOfPieces(20);
        setNumberOfDoubledPieces(10);
        setNumberOfTriplePieces(5);
        setNumberOfAdjacentPieces(0);
        setWinningConfiguration(0);
        setPieceMobility(10);
        setBlockSpot(2);
    }

    public void setHeuristicPhase2(){
        setNumberOfMorris(45);
        setNumberOfBlockedPieces(10);
        setNumberOfPieces(20);
        setNumberOfDoubledPieces(1);
        setNumberOfTriplePieces(3);
        setNumberOfAdjacentPieces(3);
        setWinningConfiguration(9999);
        setPieceMobility(20);
        setBlockSpot(2);
    }

    public void setHeuristicPhase3(){
        setNumberOfMorris(20);
        setNumberOfBlockedPieces(0);
        setNumberOfPieces(20);
        setNumberOfDoubledPieces(5);
        setNumberOfTriplePieces(5);
        setNumberOfAdjacentPieces(0);
        setWinningConfiguration(9999);
        setPieceMobility(0);
        setBlockSpot(0);
    }

    private int evaluationFunction(Board board){
        if (board.numberOfTurns < 18){
            setHeuristicPhase1();
        }else if (board.isWhitePhase3 || board.isBlackPhase3){
            setHeuristicPhase3();
        }else{
            setHeuristicPhase2();
        }

        int f1 = getNumberOfPieces() * pieceNumberDifference(board);
        int f2 = getNumberOfMorris() * morrisDifference(board);
        int f3 = getNumberOfBlockedPieces() * piecesBlockedDifference(board);
        int f4 = getNumberOfDoubledPieces() * doublePiecesDifference(board);
        int f5 = getNumberOfTriplePieces() * triplePiecesDifference(board);
        int f6 = getNumberOfAdjacentPieces() * adjacentMorrisDifference(board);
        int f7;
        if(board.numberOfTurns < 18)
            f7 = 0;
        else
            f7 = getWinningConfiguration() * winningConfiguration(board);
        int f8 = getPieceMobility() * pieceMobilityDifference(board);
//        int f9 = getBlockSpot() * blockSpotDifference(board);

        return f1 + f2 + f3 + f4 + f5 + f6 + f7 + f8;
    }

    public static boolean isMorris (Board board, int position){
        if(board.getOccupant(position) == Board.EMPTY) return false;
        for (int i = 0; i < Board.TRIOS.length; i++){
            boolean isPositionInsideMorris = false;
            boolean isSameColor = true;

            for (int j = 0; j < Board.TRIOS[i].length; j++){

                if (Board.TRIOS[i][j] == position){
                    isPositionInsideMorris = true;
                }

                if (board.getOccupant(Board.TRIOS[i][j]) != board.getOccupant(position)){
                    isSameColor = false;
                    break;
                }
            }

            if (isSameColor == true && isPositionInsideMorris == true)
                return true;
        }
        return false;
    }

    public static ArrayList< Triple > legalMoves (Board board, int player){
        if (board.numberOfTurns < 18){
            return legalMovesPhase1(board, player);
        }else if (board.isWhitePhase3 || board.isBlackPhase3){
            return legalMovesPhase3(board, player);
        }
        return legalMovesPhase2(board, player);
    }

    public static ArrayList< Triple > legalMovesPhase1 (Board board, int player){
        ArrayList< Triple > moves = new ArrayList<>();

        for (int position = 0; position < 24; position++){
            if (board.getOccupant(position) == Board.EMPTY){

                board.gameMatrix[position] = player;

                // Checking if we would need to remove a piece
                if (isMorris(board, position)){

                    Iterator<Integer> allMorris;
                    Iterator<Integer> removeIterator;
                    if(player == Board.WHITE){
                        allMorris = board.blackPieces.iterator();
                        removeIterator = board.blackPieces.iterator();
                    }else{
                        allMorris = board.whitePieces.iterator();
                        removeIterator = board.whitePieces.iterator();
                    }
                    boolean areAllPiecesInMorris = true;

                    while (allMorris.hasNext()){
                        int piece = allMorris.next();
                        if (!isMorris(board, piece)) areAllPiecesInMorris = false;
                    }

                    while (removeIterator.hasNext()){
                        int positionToRemove = removeIterator.next();
                        if (isMorris(board, player)){
                            if(areAllPiecesInMorris)
                                moves.add( new Triple(position,position,positionToRemove) );
                        }else {
                            moves.add( new Triple(position,position,positionToRemove) );
                        }
                    }

                }else {
                    moves.add( new Triple(position, position, 24) );
                    board.gameMatrix[position] = Board.EMPTY;
                }
            }
        }
        System.out.println(player);
        System.out.println(board);
        Iterator<Triple> itMove = moves.iterator();
        while(itMove.hasNext()){
            Triple currentMove = itMove.next();
            System.out.println(currentMove);
        }
        return moves;
    }

    public static ArrayList< Triple > legalMovesPhase2 (Board board, int player){
        ArrayList< Triple > moves = new ArrayList<>();

        SortedSet<Integer> whitePiecesPositions = new TreeSet<>(board.whitePieces);
        SortedSet<Integer> blackPiecesPositions = new TreeSet<>(board.blackPieces);

        Iterator<Integer> iterator;
        if(player == Board.WHITE)
            iterator = whitePiecesPositions.iterator();
        else
            iterator = blackPiecesPositions.iterator();

        while (iterator.hasNext()){
            int position = iterator.next();

            for (int j = 0; j < Board.NEIGHBORHOOD[position].length; j++){
                int newPosition = Board.NEIGHBORHOOD[position][j];

                if (board.getOccupant(newPosition) == Board.EMPTY){

                    // Making the move in the board's matrix
                    board.gameMatrix[position] = Board.EMPTY;
                    board.gameMatrix[newPosition] = player;

                    // Checking if we need to remove any piece from the opponent
                    if (isMorris(board, newPosition)){
                        Iterator<Integer> allMorris;
                        Iterator<Integer> removeIterator;
                        if(player == Board.WHITE){
                            allMorris = board.blackPieces.iterator();
                            removeIterator = board.blackPieces.iterator();
                        }else{
                            allMorris = board.whitePieces.iterator();
                            removeIterator = board.whitePieces.iterator();
                        }
                        boolean areAllPiecesInMorris = true;

                        while (allMorris.hasNext()){
                            int piece = allMorris.next();
                            if (!isMorris(board, piece)) areAllPiecesInMorris = false;
                        }

                        while (removeIterator.hasNext()){
                            int positionToRemove = removeIterator.next();
                            if (isMorris(board, player)){
                                if(areAllPiecesInMorris)
                                    moves.add( new Triple(position,position,positionToRemove) );
                            }else {
                                moves.add( new Triple(position,position,positionToRemove) );
                            }
                        }

                    }else {
                        moves.add( new Triple(position, newPosition, 24) );
                    }


                    // Undoing move in the board's matrix
                    board.gameMatrix[position] = player;
                    board.gameMatrix[newPosition] = Board.EMPTY;
                }
            }

        }
        return moves;
    }

    public static ArrayList< Triple > legalMovesPhase3 (Board board, int player){

        if (board.isWhitePhase3 == false || board.isBlackPhase3 == false)
            return legalMovesPhase2(board, player);

        ArrayList< Triple > moves = new ArrayList<>();


        ArrayList<Integer> emptyPositions = new ArrayList<>();
        for (int position = 0; position < 24; position++){
            if (board.getOccupant(position) == Board.EMPTY){
                emptyPositions.add(position);
            }
        }

        Iterator<Integer> iterator;
        if(player == Board.WHITE)
            iterator = board.whitePieces.iterator();
        else
            iterator = board.blackPieces.iterator();

        while (iterator.hasNext()){
            int position = iterator.next();

            Iterator<Integer> emptyPositionsIterator = emptyPositions.iterator();
            while (emptyPositionsIterator.hasNext()){
                int newPosition = emptyPositionsIterator.next();

                // Making the move in the board's matrix
                board.gameMatrix[position] = Board.EMPTY;
                board.gameMatrix[newPosition] = player;

                if (isMorris(board, newPosition)){

                    Iterator<Integer> allMorris;
                    Iterator<Integer> removeIterator;
                    if(player == Board.WHITE){
                        allMorris = board.blackPieces.iterator();
                        removeIterator = board.blackPieces.iterator();
                    }else{
                        allMorris = board.whitePieces.iterator();
                        removeIterator = board.whitePieces.iterator();
                    }
                    boolean areAllPiecesInMorris = true;

                    while (allMorris.hasNext()){
                        int piece = allMorris.next();
                        if (!isMorris(board, piece)) areAllPiecesInMorris = false;
                    }

                    while (removeIterator.hasNext()){
                        int positionToRemove = removeIterator.next();
                        if (isMorris(board, player)){
                            if(areAllPiecesInMorris)
                                moves.add( new Triple(position,position,positionToRemove) );
                        }else {
                            moves.add( new Triple(position,position,positionToRemove) );
                        }
                    }
                }else {
                    moves.add( new Triple(position, newPosition, 24) );
                }

                // Undoing move in the board's matrix
                board.gameMatrix[position] = player;
                board.gameMatrix[newPosition] = Board.EMPTY;

            }
        }
        return moves;
    }

    private int minimaxMethod(int currentDepth, int maxDepth, int player, int alfa, int beta){

        // Reached the last node of the search tree. Returns evaluation
        if (currentDepth == maxDepth){
            int evaluationCalc = evaluationFunction(board);
            System.out.println(evaluationCalc);
            return evaluationCalc;
        }

        System.out.println("oi");
        // White turn = maximize the evaluation
        if (player == Board.WHITE){
            int bestEvaluation = Integer.MIN_VALUE;

            System.out.println("oi, eu sou o branco");
            ArrayList<Triple> movements = legalMoves(board, player);

            // Make move. Call minimaxMethod recursively. Only call the evaluation method in the end of the depth search
            for (int j = 0; j < movements.size(); j++){

                // Saving the state of the board before the movement
                int numberOfTurns = board.numberOfTurns;
                int noPiecesRemovedCount = board.noPiecesRemovedCount;
                boolean turn = board.turn;

                if (movements.get(j).currentPosition == movements.get(j).destination){
                    board.addPiece(movements.get(j).currentPosition, player);
                }else {
                    board.movePiece(movements.get(j).currentPosition, movements.get(j).destination, true);

                    if (0 <= movements.get(j).positionToRemove && movements.get(j).positionToRemove < 24){
                        board.removePiece(movements.get(j).positionToRemove, Board.WHITE);
                        board.noPiecesRemovedCount = 0;
                    }
                }

                // Processo recursivo de busca
                int evaluation = minimaxMethod(currentDepth+1, maxDepth, Board.BLACK, alfa, beta);

                // Undoing movement made
                if (movements.get(j).currentPosition == movements.get(j).destination ){
                    board.removePiece(movements.get(j).currentPosition, Board.BLACK);
                }else {
                    board.movePiece(movements.get(j).destination, movements.get(j).currentPosition, true);

                    if (0 <= movements.get(j).positionToRemove && movements.get(j).positionToRemove < 24){
                        board.addPiece(movements.get(j).positionToRemove, Board.BLACK);
                    }
                }
                board.numberOfTurns = numberOfTurns;
                board.noPiecesRemovedCount = noPiecesRemovedCount;
                board.turn = turn;

                // Alpha-beta Pruning
                // Poderia usar alfa no if abaixo em vez de maior valor para mudar o melhormovimento?
                if (evaluation > bestEvaluation){
                    bestEvaluation = evaluation;
                    if (currentDepth == 0)
                        bestMovement = new Triple(movements.get(j).currentPosition,movements.get(j).destination,movements.get(j).positionToRemove);
                }

                alfa = Integer.max(alfa, bestEvaluation);

                if (beta <= alfa){
                    break;
                }
            }

            return bestEvaluation;

            // Jogador que minimiza
        }else {
            int worstEvaluation = Integer.MAX_VALUE;
            System.out.println(player);
            ArrayList< Triple >movements = legalMoves(board, player);

            for (int j = 0; j < movements.size(); j++){

                // Saving the state of the board before the movement
                int numberOfTurns = board.numberOfTurns;
                int noPiecesRemovedCount = board.noPiecesRemovedCount;
                boolean turn = board.turn;

                if (movements.get(j).currentPosition == movements.get(j).destination){
                    board.addPiece(movements.get(j).currentPosition, player);
                }else {
                    board.movePiece(movements.get(j).currentPosition, movements.get(j).destination, false);

                    if (0 <= movements.get(j).positionToRemove && movements.get(j).positionToRemove < 24){
                        board.removePiece(movements.get(j).positionToRemove, Board.BLACK);
                    }
                }

                int evaluation = minimaxMethod(currentDepth+1, maxDepth, Board.WHITE, alfa, beta);

                // vis[u] = false;
                if (movements.get(j).currentPosition == movements.get(j).destination){
                    board.removePiece(movements.get(j).currentPosition, Board.WHITE);
                }else {
                    board.movePiece(movements.get(j).destination, movements.get(j).currentPosition, false);

                    if (0 <= movements.get(j).positionToRemove && movements.get(j).positionToRemove < 24){
                        board.addPiece(movements.get(j).positionToRemove, Board.WHITE);
                    }
                }
                board.numberOfTurns = numberOfTurns;
                board.noPiecesRemovedCount = noPiecesRemovedCount;
                board.turn = turn;

                if (evaluation < worstEvaluation){
                    worstEvaluation = evaluation;
                    if (currentDepth == 0)
                        bestMovement = new Triple(movements.get(j).currentPosition,movements.get(j).destination,movements.get(j).positionToRemove);
                }

                beta = Integer.min(beta, worstEvaluation);

                if (beta <= alfa){
                    break;
                }
            }

            return worstEvaluation;
        }
    }

    public Triple bestMovement(Board board, int player){
        Ia.board = board;

        int maxDepth = Integer.parseInt(depthProperty.getValueSafe());
        System.out.println("Max Depth: " + maxDepth);

        minimaxMethod(0, maxDepth, player, Integer.MIN_VALUE, Integer.MAX_VALUE);


        System.out.printf ("Melhor movimento da IA = %d %d %d %c", bestMovement.currentPosition, bestMovement.destination, bestMovement.positionToRemove, 10);

        return bestMovement;
    }

    public static void computeTime(int maxDepth){
        long start,end;

        start = System.currentTimeMillis();

        end = System.currentTimeMillis();

        // DEBUG
        System.out.printf("Tempo gasto na profundidade %d = %dms %c %c", maxDepth, end-start, 10, 10);
    }
}