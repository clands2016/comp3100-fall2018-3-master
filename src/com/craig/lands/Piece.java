package com.craig.lands;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;


public class Piece extends StackPane {


    //x and y of the piece which has been clicked


    //x and y of original piece position

    private int id = 0;
    private boolean movable = true;
    private int x;
    private int y;
    private boolean inBase = false;



    //Creates the ellipse which is the piece and translates it in relation to the TILE_SIZE
    public Piece(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public Piece(int id) {
        this.id = id;
    }

    public boolean belongToSamePlayer(Piece piece){
        if(piece.returnID() == this.id){
            return true;
        } else {
            return false;
        }
    }


    public int returnID(){
        return id;
    }



    public Paint changeColor(Color color){
        Ellipse ellipse = new Ellipse(20,20);
        ellipse.setFill(color);
        ellipse.setStroke(color);
        getChildren().add(ellipse);
        return ellipse.getFill();
    }




}
