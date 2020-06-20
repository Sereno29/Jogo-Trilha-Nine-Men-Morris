import java.util.*;

import java.util.SortedSet;
import java.util.TreeSet;
import javafx.beans.property.SimpleStringProperty;

public class Board {

    public SortedSet<Integer> whitePieces, blackPieces;
    public int[] gameMatrix;
    public int numberOfTurns;
    public int noPiecesRemovedCount;
    int[] numberTrios= {0,0}; // {white, black}
    public SortedSet<Integer> whiteTriosPositions, blackTriosPositions;
    public SimpleStringProperty turnProperty;
    public SimpleStringProperty excludeProperty = new SimpleStringProperty("A IA é muito forte! Cuidado...");
    public SimpleStringProperty winProperty = new SimpleStringProperty("Quem será que irá ganhar? ");
    public SimpleStringProperty turnsProperty = new SimpleStringProperty();
    public boolean isWhitePhase3;
    public boolean isBlackPhase3;
    public boolean isExcludeMode;
    public boolean turn; // true = white, false = black
    public final static int EMPTY = 3;
    public final static int WHITE = 0;
    public final static int BLACK = 1;

    static final int[][] TRIOS = {
            {0,1,2}, {3,4,5}, {6,7,8}, {9,10,11}, {12,13,14}, {15,16,17},{18,19,20}, {21,22,23},
            {0,9,21},{3,10,18},{6,11,15}, {1,4,7}, {16,19,22}, {8,12,17}, {5,13,20}, {2,14,23}
    } ;

    public static final int[][] NEIGHBORHOOD =
            {
                    { 1, 9 }, { 0,2,4 }, { 1,14 },
                    { 4,10 }, { 3,5,1,7 }, { 4,13 },
                    { 7,11 }, { 6,8,4 }, { 7,12 },
                    { 21,0,10 }, { 18,3,9,11 }, { 6,15,10 }, { 17,8,13 }, { 5,20,14,12 }, { 2,23,13 },
                    { 11,16 }, { 15,17,19 }, { 12,16 },
                    { 10,19 }, { 18,20,16,22 }, { 13,19 },
                    { 9,22 }, { 23,21,19 }, { 14,22 }
            };

    public Board(){
        gameMatrix = new int[24];
        for (int i = 0; i < 24; i++) gameMatrix[i] = Board.EMPTY;
        numberOfTurns = 0;
        noPiecesRemovedCount = 0;
        whitePieces = new TreeSet<>();
        blackPieces = new TreeSet<>();
        turn = true;
        isExcludeMode = false;
        turnProperty = new SimpleStringProperty("Turno: Brancas jogam");
        turnsProperty.set("Rodadas sem remoção de peças: " + noPiecesRemovedCount);
        whiteTriosPositions = blackTriosPositions = new TreeSet<>();
    }

    public Board(Board t){
        gameMatrix = new int[24];
        System.arraycopy(t.gameMatrix, 0, gameMatrix, 0, 24);
        numberOfTurns = t.numberOfTurns;
        noPiecesRemovedCount = t.noPiecesRemovedCount;
        whitePieces = new TreeSet<>();
        Iterator<Integer>it = t.whitePieces.iterator();
        while (it.hasNext()){
            int aux = it.next();
            whitePieces.add(aux);
        }
        blackPieces = new TreeSet<>();
        it = t.blackPieces.iterator();
        while (it.hasNext()){
            int aux = it.next();
            blackPieces.add(aux);
        }
        turn = t.turn;
        isExcludeMode = t.isExcludeMode;
        turnProperty = t.turnProperty;
        whiteTriosPositions = t.whiteTriosPositions;
        blackTriosPositions = t.blackTriosPositions;
    }

    public int getOccupant(int position){
        return gameMatrix[position];
    }

    public int getNumberOfTurns(){
        return numberOfTurns;
    }

    public void incrementNumberOfTurns(){
        numberOfTurns++;
    }

    public void decrementNumberOfTurns(){
        numberOfTurns--;
    }

    public void incrementNoPieceRemovedCount(){
        noPiecesRemovedCount++;
    }

    public void setIsWhitePhase3(boolean isPhase3){
        isWhitePhase3 = isPhase3;
    }

    public void setIsBlackPhase3(boolean isPhase3){
        isBlackPhase3 =  isPhase3;
    }

    public boolean addPiece(int position, int player){
        if ( gameMatrix[position] != Board.EMPTY ) return true;
        gameMatrix[position] = player;
        if (player == WHITE)
            whitePieces.add(position);
        else
            blackPieces.add(position);
        return false;
    }

    public boolean removePiece(int position, int player ){
        gameMatrix[position] = Board.EMPTY;
        if(player == WHITE)
            blackPieces.remove(position);
        else
            whitePieces.remove(position);
        return false;
    }

    public boolean checkAndRemove(int position, int player){
        if (gameMatrix[position] == Board.EMPTY || gameMatrix[position] == player){
            excludeProperty.set("Posição inválida! Escolha outra");
            return false;
        }else if(player == WHITE && numberTrios[1] != 0){
            if(blackTriosPositions.contains(position)){
                excludeProperty.set("A peça escolhida faz parte de um moinho e não pode ser retirada! Escolha outra");
                return false;
            }
        }else if(player == BLACK && numberTrios[0] != 0){
            if(whiteTriosPositions.contains(position)){
                excludeProperty.set("A peça escolhida faz parte de um moinho e não pode ser retirada! Escolha outra");
                return false;
            }
        }
        return removePiece(position,player);
    }

    public boolean movePiece(int initialPosition , int finalPosition, boolean currentTurn ){
        if(currentTurn){
            if(getOccupant(initialPosition) != WHITE && getOccupant(finalPosition) != EMPTY ) return true;
            gameMatrix[initialPosition] = Board.EMPTY;
            whitePieces.remove(initialPosition);
            gameMatrix[finalPosition] = Board.WHITE;
            whitePieces.add(finalPosition);
        }else{
            if(getOccupant(initialPosition) != BLACK && getOccupant(finalPosition) != EMPTY ) return true;
            gameMatrix[initialPosition] = Board.EMPTY;
            blackPieces.remove(initialPosition);
            gameMatrix[finalPosition] = Board.BLACK;
            blackPieces.add(finalPosition);
        }
        return false;
    }

    public boolean undoMovement(int currentPosition ,int lastPosition, int player, int removedPosition, int noPiecesRemovedCountBefore){
        if(numberOfTurns <= 18){ // Phase 1
            if(!turn){ // Undo movement from white
                whitePieces.remove(currentPosition);
                if(removedPosition > -1 && removedPosition < 24)
                    if(addPiece(lastPosition, BLACK)) return true;
            }else{
                blackPieces.remove(currentPosition);
                if(removedPosition > -1 && removedPosition < 24){
                    if(addPiece(lastPosition, WHITE)) return true;
                }
            }
        }else{ // Phase 2 and 3
            if(!turn){
                if(movePiece(currentPosition, lastPosition, true)) return true;
                if(removedPosition > -1 && removedPosition < 24){
                    if(addPiece(removedPosition, BLACK)) return true;
                    if(blackPieces.size() > 3) setIsBlackPhase3(false);
                }
            }else{
                if(movePiece(currentPosition, lastPosition, false)) return true;
                if(removedPosition > -1 && removedPosition < 24){
                    if(addPiece(removedPosition, WHITE)) return true;
                    if(whitePieces.size() > 3) setIsWhitePhase3(false);
                }
            }
        }
        turn = !turn;
        noPiecesRemovedCount = noPiecesRemovedCountBefore;
        decrementNumberOfTurns();
        hasHappenedMorris(true);
        return false;
    }

    public boolean hasHappenedMorris(boolean undo){
        int morris = 0;
        SortedSet<Integer> aux = new TreeSet<>();
        if(turn){
            Iterator<Integer> it = whiteTriosPositions.iterator();
            while (it.hasNext()){
                int pos = it.next();
                aux.add(pos);
            }

            whiteTriosPositions.clear();

            for(int i = 0,j=0; i< 16; i++){
                if(whitePieces.contains(TRIOS[i][0]) && whitePieces.contains(TRIOS[i][1]) && whitePieces.contains(TRIOS[i][2])){
                    morris++;
                    whiteTriosPositions.add(TRIOS[i][0]);
                    whiteTriosPositions.add(TRIOS[i][1]);
                    whiteTriosPositions.add(TRIOS[i][2]);
                }
            }
            if(morris > numberTrios[0]){
                if(!undo){
                    isExcludeMode = true;
                    noPiecesRemovedCount = -1;
                    excludeProperty.set("Brancas podem remover uma peça preta já que fizeram um moinho");
                }
                numberTrios[0] = morris;
                return true;
            }else{
                if(morris< numberTrios[0]){
                    numberTrios[0] = morris;
                    return false;
                }
                if(morris==numberTrios[0]){
                    if(!aux.equals(whiteTriosPositions) && !undo){
                        isExcludeMode = true;
                        noPiecesRemovedCount = -1;
                        excludeProperty.set("Brancas podem remover uma peça preta já que fizeram um moinho");
                        return true;
                    }
                }

            }
        }else{
            Iterator<Integer> it = blackTriosPositions.iterator();
            while (it.hasNext()){
                int pos = it.next();
                aux.add(pos);
            }

            blackTriosPositions.clear();

            for(int i = 0,j=0; i< 16; i++){
                if(blackPieces.contains(TRIOS[i][0]) && blackPieces.contains(TRIOS[i][1]) && blackPieces.contains(TRIOS[i][2])){
                    morris++;
                    blackTriosPositions.add(TRIOS[i][0]);
                    blackTriosPositions.add(TRIOS[i][1]);
                    blackTriosPositions.add(TRIOS[i][2]);
                }
            }
            if(morris > numberTrios[1]){
                if(!undo){
                    isExcludeMode = true;
                    noPiecesRemovedCount = -1;
                    excludeProperty.set("Pretas podem remover uma peça preta já que fizeram um moinho");
                }
                numberTrios[1] = morris;
                return true;
            }else{
                if(morris< numberTrios[1]){
                    numberTrios[1] = morris;
                    return false;
                }
                if(morris==numberTrios[1]){
                    if(!aux.equals(blackTriosPositions) && !undo){
                        isExcludeMode = true;
                        noPiecesRemovedCount = -1;
                        excludeProperty.set("Pretas podem remover uma peça preta já que fizeram um moinho");
                        return true;
                    }
                }

            }
        }
        return false;
    }


    public void changeTurn(){
        turn = !turn;
        if(turn)
            turnProperty.set("Turno: Brancas jogam");
        if(!turn)
            turnProperty.set("Turno: Pretas jogam. Aperte o botão Joga IA, por favor");
        incrementNumberOfTurns();
        incrementNoPieceRemovedCount();
        turnsProperty.set("Rodadas sem remoção de peças: " + noPiecesRemovedCount);
    }

    public boolean isOver(boolean isStuck){
        System.out.println("Alguém foi travado: "+isStuck);
        if(!isBlackPhase3 && turn && isStuck){
            winProperty.set("Parabéns!! Você faz parte de um hall seleto de pessoas. Você travou a nossa IA, ela não tem mais jogadas a fazer.");
            return true;
        }

        if(!isWhitePhase3 && !turn && isStuck){
            winProperty.set("Nossa IA ganhou!!! Wohooooooo. Você foi travado. Não possui mais jogadas");
            return true;
        }
        if(whitePieces.size() <= 2){
            winProperty.set("Nossa IA ganhou!!! Wohooooooo. Você só possui 2 peças.");
            return true;
        }
        if(blackPieces.size()<=2){
            winProperty.set("Parabéns!! Você faz parte de um hall seleto de pessoas. Você fez com que nossa IA ficasse com 2 peças.");
            return true;
        }
        if(noPiecesRemovedCount >= 35){
            winProperty.set("Ocorreu um empate entre o player e a IA. Ocorreram 35 jogadas sem a remoção de uma peça.");
            return true;
        }
        if(whitePieces.size()==3 && numberOfTurns > 18){
            isWhitePhase3 = true;
            return false;
        }
        if(blackPieces.size()==3 && numberOfTurns > 18){
            isBlackPhase3 = true;
            return false;
        }
        return false;
    }


    public void restart(){
        gameMatrix = new int[24];
        for (int i = 0; i < 24; i++) gameMatrix[i] = Board.EMPTY;
        noPiecesRemovedCount = 0;
        whitePieces.clear();
        blackPieces.clear();
        turn = true;
        turnProperty = new SimpleStringProperty("Turno: Brancas jogam");
        isExcludeMode = false;
        turnsProperty.set("Rodadas sem remoção de peças: " + noPiecesRemovedCount);
        numberOfTurns = 0;
        isBlackPhase3 = isWhitePhase3 = false;
        numberTrios[0] = numberTrios[1] = 0;
        whiteTriosPositions.clear();
        blackTriosPositions.clear();
        excludeProperty.set("A IA é muito forte! Cuidado...");
        winProperty.set("Quem será que irá ganhar? ");
    }



    @Override
    public String toString(){
        char[] s = new char[24];
        for (int i = 0; i < 24; i++){
            s[i] = (char)('0' + gameMatrix[i]);
        }
        return "" +
                "  "+s[ 0]+" --------- "+s[ 1]+" --------- "+s[ 2]                         + "                      0 --------- 1 --------- 2"+ "\n" +
                "  |           |           |"                                              + "                      |           |           |"+ "\n" +
                "  |   "+s[ 3]+" ----- "    +s[ 4]+" ----- "    +s[ 5]+"   |"              + "                      |   3 ----- 4 ----- 5   |"+ "\n" +
                "  |   |       |       |   |"                                              + "                      |   |       |       |   |"+ "\n" +
                "  |   |   "+s[ 6]+" - "+s[ 7]+" - "+s[ 8]+"   |   |"                      + "                      |   |   6 - 7 - 8   |   |"+ "\n" +
                "  |   |   |       |   |   |"                                              + "                      |   |   |       |   |   |"+ "\n" +
                "  "+s[ 9]+" - "+s[10]+" - "+s[11]+"       "+s[12]+" - "+s[13]+" - "+s[14] + "                      9 - 10- 11      12- 13- 14"+ "\n" +
                "  |   |   |       |   |   |"                                              + "                      |   |   |       |   |   |"+ "\n" +
                "  |   |   "+s[15]+" - "+s[16]+" - "+s[17]+"   |   |"                      + "                      |   |   15- 16- 17  |   |"+ "\n" +
                "  |   |       |       |   |"                                              + "                      |   |       |       |   |"+ "\n" +
                "  |   "+s[18]+" ----- "+s[19]+" ----- "+s[20]+"   |"                      + "                      |   18----- 19----- 20  |"+ "\n" +
                "  |           |           |"                                              + "                      |           |           |"+ "\n" +
                "  "+s[21]+" --------- "+s[22]+" --------- "+s[23]                         + "                      21--------- 22--------- 23"       ;
    }
}