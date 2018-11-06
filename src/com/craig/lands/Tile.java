package com.craig.lands;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class Tile extends Rectangle {

        Piece piece;
        int id = -1;

        boolean available = false;

        //Creates a tile at the size of TILE_SIZE and relocates that tile based on the passed x, and y
        public Tile(Piece piece) {

            this.piece = piece;
            setWidth(40);
            setHeight(40);
            setStroke(Color.RED);
            setFill(Color.WHITE);

        }

        public Tile(){
            this.piece = piece;
            setWidth(40);
            setHeight(40);
            setStroke(Color.RED);
            setFill(Color.WHITE);
        }

        public void setPiece(Piece piece){
            this.piece = piece;
        }
        public void removePiece(){
            this.piece = null;
        }

        public String toString(){
            if(piece != null){
                return( "" + piece.returnID());
            }
           return " " ;
        }

        public Piece getPiece(){
            return piece;
        }

        public void setId(int id){
            this.id = id;
        }

        public int returnID(){
            return(id);
        }

        public Color changeColor(Color stroke, Color fill){
            this.setStroke(stroke);
            this.setFill(fill);
            available = true;
            return fill;
        }




    }
