/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Matheus Sereno e Caio
 */

public class Triple {
    public int  currentPosition,
            destination,
            positionToRemove;

    Triple(){
        currentPosition=0;
        destination=0;
        positionToRemove=0;
    }

    Triple(int a, int b, int c){
        currentPosition=a;
        destination=b;
        positionToRemove=c;
    }

    public void setFirst(int value){
        currentPosition = value;
    }

    public void setSecond(int value){
        destination = value;
    }

    public void setThird(int value){
        positionToRemove = value;
    }

    public int getFirst(){
        return currentPosition;
    }

    public int getSecond(){
        return destination;
    }

    public int getThird(){
        return positionToRemove;
    }

    public String toString(){
        return "Movement: " + currentPosition + " " + destination + " " + positionToRemove;
    }
}