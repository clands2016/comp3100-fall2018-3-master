package com.craig.lands;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by craig on 10/17/2018.
 */
public class Player {

    private List<Piece> pieceList = new ArrayList<>();

    private String name = "";
    private int id = 0;







    public void addPiece(Piece piece){
        pieceList.add(piece);
    }



    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }


}
