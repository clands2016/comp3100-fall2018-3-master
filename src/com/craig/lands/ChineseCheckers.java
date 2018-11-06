package com.craig.lands;


import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import static javafx.geometry.Pos.CENTER;


public class ChineseCheckers extends Application {

    private int numberOfPlayers = 0;
    private int numberOfNamesEntered = 0;
    private boolean[] validName;
    String firstName = "";
    TextField[] nameList;


    //Size of tiles
    public static final int TILE_HEIGHT = 40;
    public static final int TILE_WIDTH = 40;


    public static final int WIDTH = 27;
    public static final int HEIGHT = 19;


    private Tile[][] tileArray = new Tile[WIDTH][HEIGHT];
    private Player[] playerArray;
    private Color[] colorArray = new Color[6];
    int[] xArray = new int[6];
    int[] yArray = new int[6];
    int x;
    int y;


    Player currentPlayer = null;


    //Creates the board and pieces

    private Parent createContent() {

        Pane root = new Pane();
        root.setPrefSize(WIDTH * TILE_WIDTH, HEIGHT * TILE_HEIGHT);


        //Adds six players to an array
        playerArray = new Player[6];
        for (int i = 0; i < playerArray.length; i++) {
            Player player = new Player();
            player.setId(i);
            playerArray[i] = player;
        }

        //Adds tiles to an array and translates them to the correct spot on the gui
        for (int h = 0; h < HEIGHT; h++) {
            for (int w = 0; w < WIDTH; w++) {
                Tile tile = new Tile();
                tileArray[w][h] = tile;
                root.getChildren().add(tile);
                tile.setTranslateX(w * TILE_WIDTH);
                if(h % 2 == 0) {
                    tile.setTranslateX(w * TILE_WIDTH+20);
                    Tile tile2 = new Tile();
                    tile2.setWidth(20);
                    tile2.changeColor(Color.RED, Color.WHITE);
                    root.getChildren().add(tile2);
                    tile2.setTranslateY(h * TILE_HEIGHT);
                }
                tile.setTranslateY(h * TILE_HEIGHT);
            }
        }





        int x = (WIDTH - 1) / 2;
        int lowestX = (WIDTH - 1) / 2;
        int highestX = (WIDTH - 1) / 2;
        boolean first = true;


        //Adding pieces to correct tiles and players
        for (int y = 1; y <= 13; y++) {
            for (; ; x += 2) {
                if (lowestX < x && first) {
                    x = lowestX;
                    first = false;
                }

                Piece piece = null;
                if (x >= 10 && x <= 16) {
                    piece = new Piece(0,x,y);
                } else if (x >= 1 && x <= 7) {
                    piece = new Piece(3,x,y);
                } else if (x >= 19 && x <= 25) {
                    piece = new Piece(4,x,y);
                }

                if (y < 5) {
                    tileArray[x][y].setPiece(piece);
                    playerArray[0].addPiece(piece);

                } else if (y == 10 && (x == 4 || x == 22)) {
                    tileArray[x][y].setPiece(piece);
                    playerArray[piece.returnID()].addPiece(piece);
                } else if ((y == 11) && (x == 3 || x == 5 || x == 21 || x == 23)) {
                    tileArray[x][y].setPiece(piece);
                    playerArray[piece.returnID()].addPiece(piece);
                } else if ((y == 12) && (x == 2 || x == 4 || x == 6 || x == 20 || x == 22 || x == 24)) {
                    tileArray[x][y].setPiece(piece);
                    playerArray[piece.returnID()].addPiece(piece);
                } else if ((y == 13) && (x == 1 || x == 3 || x == 5 || x == 7 || x == 19 || x == 21 || x == 23 || x == 25)) {
                    tileArray[x][y].setPiece(piece);
                    playerArray[piece.returnID()].addPiece(piece);
                }
                if (x >= highestX) {
                    break;
                }

            }
            lowestX = lowestX - 1;
            highestX++;
            first = true;
        }


        currentPlayer = playerArray[0];


        x = (WIDTH - 1) / 2;
        lowestX = (WIDTH - 1) / 2;
        highestX = (WIDTH - 1) / 2;
        first = true;
        //Adding pieces to correct tiles and players

        for (int y = 17; y > 4; y--) {
            for (; ; x += 2) {
                if (lowestX < x && first) {
                    x = lowestX;
                    first = false;
                }
                Piece piece = null;
                if (x >= 10 && x <= 16) {
                    piece = new Piece(1,x,y);
                } else if (x >= 1 && x <= 7) {
                    piece = new Piece(5,x,y);
                } else if (x >= 19 && x <= 25) {
                    piece = new Piece(2,x,y);
                }
                if (y > 13) {
                    tileArray[x][y].setPiece(piece);
                    playerArray[1].addPiece(piece);
                } else if ((y == 5) && (x == 1 || x == 3 || x == 5 || x == 7 || x == 19 || x == 21 || x == 23 || x == 25)) {
                    tileArray[x][y].setPiece(piece);
                    playerArray[piece.returnID()].addPiece(piece);
                } else if ((y == 6) && (x == 2 || x == 4 || x == 6 || x == 20 || x == 22 || x == 24)) {
                    tileArray[x][y].setPiece(piece);
                    playerArray[piece.returnID()].addPiece(piece);
                } else if ((y == 7) && (x == 3 || x == 5 || x == 21 || x == 23)) {
                    tileArray[x][y].setPiece(piece);
                    playerArray[piece.returnID()].addPiece(piece);
                } else if ((y == 8) && (x == 4 || x == 22)) {
                    tileArray[x][y].setPiece(piece);
                    playerArray[piece.returnID()].addPiece(piece);
                }

                if (x >= highestX) {
                    break;
                }
            }
            lowestX = lowestX - 1;
            highestX++;
            first = true;
        }




        //Relocating pieces on gui to remove large gaps between pieces
        for (int h = 0; h < HEIGHT; h++) {
            for (int w = 0; w < WIDTH; w++) {
                Piece piece = tileArray[w][h].getPiece();
                if (piece != null) {
                    root.getChildren().add(piece);
                    piece.setTranslateX(w*TILE_WIDTH);
                    if(w == 12) {
                        piece.setTranslateX((w * TILE_WIDTH) + 20);
                    } else if(w == 14){
                        piece.setTranslateX((w * TILE_WIDTH) - 20);
                    } else if(w == 11){
                        piece.setTranslateX((w * TILE_WIDTH) + 40);
                    } else if(w == 15) {
                        piece.setTranslateX((w * TILE_WIDTH) - 40);
                    } else if(w == 10){
                        piece.setTranslateX((w * TILE_WIDTH) + 60);
                    } else if(w == 16){
                        piece.setTranslateX((w * TILE_WIDTH)  - 60);
                    } else if(w == 7) {
                        piece.setTranslateX((w * TILE_WIDTH) + 120);
                    } else if(w == 19) {
                        piece.setTranslateX((w * TILE_WIDTH) - 120);
                    } else if(w == 6) {
                        piece.setTranslateX((w * TILE_WIDTH) + 140);
                    } else if(w == 20) {
                        piece.setTranslateX((w * TILE_WIDTH) - 140);
                    } else if(w == 5){
                        piece.setTranslateX((w * TILE_WIDTH) + 160);
                    } else if(w == 21) {
                        piece.setTranslateX((w * TILE_WIDTH) - 160);
                    } else if(w == 4){
                        piece.setTranslateX((w * TILE_WIDTH) + 180);
                    } else if(w == 22) {
                        piece.setTranslateX((w * TILE_WIDTH) - 180);
                    } else if(w == 3){
                        piece.setTranslateX((w * TILE_WIDTH) + 200);
                    } else if(w == 23) {
                        piece.setTranslateX((w * TILE_WIDTH) - 200);
                    } else if(w == 2){
                        piece.setTranslateX((w * TILE_WIDTH) + 220);
                    } else if(w == 24) {
                        piece.setTranslateX((w * TILE_WIDTH) - 220);
                    } else if(w == 1){
                        piece.setTranslateX((w * TILE_WIDTH) + 240);
                    } else if(w == 25) {
                        piece.setTranslateX((w * TILE_WIDTH) - 240);
                    }
                    piece.setTranslateY(h * TILE_HEIGHT);
                    movePiece(piece);
                    int id = piece.returnID();
                    if (id == 0) {
                        piece.changeColor(Color.RED);
                        colorArray[0] = Color.RED;
                    } else if (id == 1) {
                        piece.changeColor(Color.GREEN);
                        colorArray[1] = Color.GREEN;
                    } else if (id == 2) {
                        piece.changeColor(Color.BLACK);
                        colorArray[2] = Color.BLACK;
                    } else if (id == 3) {
                        piece.changeColor(Color.DARKGREY);
                        colorArray[3] = Color.DARKGREY;
                    } else if (id == 4) {
                        piece.changeColor(Color.BLUE);
                        colorArray[4] = Color.BLUE;
                    } else if (id == 5) {
                        piece.changeColor(Color.YELLOW);
                        colorArray[5] = Color.YELLOW;
                    }
                }
            }
        }





//Setting id to 0 for any tiles that are in a position which can be moved into by a piece
        for (int h = 0; h < HEIGHT; h++) {
            for (int w = 0; w < WIDTH; w++) {
                Piece piece = tileArray[w][h].getPiece();
                if (piece != null) {

                    tileArray[w][h].setId(1);
                }

                if ((h == 5 || h == 13) && (w >= 11 && w <= 15)) {
                    tileArray[w][h].setId(0);
                } else if ((h == 6 || h == 12) && (w >= 10 && w <= 15)) {
                    tileArray[w][h].setId(0);
                } else if ((h == 7 || h == 11) && (w >= 10 && w <= 16)) {
                    tileArray[w][h].setId(0);
                } else if ((h == 8 || h == 10) && (w >= 9 && w <= 16)) {
                    tileArray[w][h].setId(0);
                } else if ((h == 9) && (w >= 9 && w <= 17)) {
                    tileArray[w][h].setId(0);
                }

            }
        }





//Adding Player labels
        root.getChildren().add(createLabels(8*40,40, 0));
        root.getChildren().add(nameLabel(9*40,2*40, 0));

        root.getChildren().add(createLabels(16*40, 15*40, 1));
        root.getChildren().add(nameLabel(17*40,16*40, 1));

        root.getChildren().add(createLabels(20*40, 11*40, 4));
        root.getChildren().add(nameLabel(21*40,12*40, 4));

        root.getChildren().add(createLabels(20*40+20, 6*40, 2));
        root.getChildren().add(nameLabel(21*40+20,7*40, 2));

        root.getChildren().add(createLabels(3*40, 11*40, 3));
        root.getChildren().add(nameLabel(4*40,12*40, 3));

        root.getChildren().add(createLabels(2*40+20, 6*40, 5));
        root.getChildren().add(nameLabel(3*40+20,7*40, 5));

        Label label = new Label();

        label.setPrefWidth(360);
        label.setPrefHeight(160);
        label.setTranslateX(720);
        label.setText("Each players moves a piece at a time until\none player has all of their pieces in the\nopposite home base. Player 0 moves\nfirst, followed by Player 1, in numerical\norder.");


        root.getChildren().add(label);

        return root;
    }






    //Create Descriptive labels
    private Label createLabels(int x, int y, int player){
        Label player1Label = new Label();
        String text = "";
        text = "Players " + player + " Name:";
        player1Label.setText(text);
        player1Label.setAlignment(CENTER);
        player1Label.setTranslateX(x);
        player1Label.setTranslateY(y);
        player1Label.setPrefHeight(40);
        player1Label.setPrefWidth(160);

        return player1Label;
    }





    //Create Player name Labels
    private Label nameLabel(int x, int y, int player){
        Label player1Label = new Label();

        if(nameList[player].getText() != "" && nameList[player].getText() != "Select Number of Players") {
            player1Label.setText(nameList[player].getText());
        } else {
            player1Label.setText("");
        }
        player1Label.setAlignment(CENTER);
        player1Label.setTranslateX(x);
        player1Label.setTranslateY(y);
        player1Label.setPrefHeight(40);
        player1Label.setPrefWidth(80);

        return player1Label;
    }






    //Sets who the current player is.
    private void setPlayer(){
        if(numberOfPlayers == 2){
            if(currentPlayer == playerArray[0]){
                currentPlayer = playerArray[1];
            } else if(currentPlayer == playerArray[1]){
                currentPlayer = playerArray[0];
            }
        }
        if(numberOfPlayers == 4){
            if(currentPlayer == playerArray[0]){
                currentPlayer = playerArray[1];
            } else if(currentPlayer == playerArray[1]){
                currentPlayer = playerArray[2];
            } else if(currentPlayer == playerArray[2]){
                currentPlayer = playerArray[3];
            } else if(currentPlayer == playerArray[3]) {
                currentPlayer = playerArray[0];
            }
        }
        if(numberOfPlayers == 6){
            if(currentPlayer == playerArray[0]){
                currentPlayer = playerArray[1];
            } else if(currentPlayer == playerArray[1]){
                currentPlayer = playerArray[2];
            } else if(currentPlayer == playerArray[2]){
                currentPlayer = playerArray[3];
            } else if(currentPlayer == playerArray[3]){
                currentPlayer = playerArray[4];
            } else if(currentPlayer == playerArray[4]){
                currentPlayer = playerArray[5];
            } else if(currentPlayer == playerArray[5]){
                currentPlayer = playerArray[0];
            }
        }
    }






    //Sets the logic for the movement of pieces jumping over other pieces
    private void movementLogic(Piece piece, int num, int firstPassedX, int firstPassedY, int passedX, int passedY) {

            Piece nearbyPiece = tileArray[xArray[num]][yArray[num]].getPiece();
            if (nearbyPiece != null) {
                int nearbyY = (int) nearbyPiece.getTranslateY() / TILE_HEIGHT;
                int nearbyX = (int) nearbyPiece.getTranslateX() / TILE_WIDTH;
                if ((nearbyX == firstPassedX) && (nearbyY == firstPassedY)) {
                    tileArray[passedX][passedY].changeColor(Color.RED, Color.AQUA);
                    tileArray[passedX][passedY].setOnMousePressed(d -> {
                        piece.setTranslateX(tileArray[passedX][passedY].getTranslateX());
                        piece.setTranslateY(tileArray[passedX][passedY].getTranslateY());
                        tileArray[passedX][passedY].setId(1);
                        tileArray[x][y].setId(0);
                        tileArray[x][y].removePiece();
                        tileArray[passedX][passedY].setPiece(piece);
                        piece.changeColor(colorArray[piece.returnID()]);


                        revertBoard();
                        setPlayer();

                    });
                }
            }


    }




    //Allows for the movement of the pieces

    public void movePiece(Piece piece) {

        System.out.println("CurrentPlayer = " + currentPlayer);
        System.out.println("Number of Players = " + numberOfPlayers);
            piece.setOnMousePressed(e -> {
                if(currentPlayer.getId() == piece.returnID()) {

                    x = (int) piece.getTranslateX() / TILE_WIDTH;
                    y = (int) piece.getTranslateY() / TILE_HEIGHT;

                    piece.changeColor(Color.GOLD);

                    int offset;

                    if (y % 2 != 0) {
                        offset = 1;
                    } else {
                        offset = 0;
                    }

                    xArray[0] = x - 1;
                    xArray[1] = x + 1;
                    xArray[2] = x - offset;
                    xArray[3] = x - offset;
                    xArray[4] = x + 1 - offset;
                    xArray[5] = x + 1 - offset;


                    yArray[0] = y;
                    yArray[1] = y;
                    yArray[2] = y + 1;
                    yArray[3] = y - 1;
                    yArray[4] = y - 1;
                    yArray[5] = y + 1;

                    for (int i = 0; i < 6; i++) {
                        final int num = i;
                        boolean xPositive = false;
                        boolean yPositive = false;
                        boolean xSame = false;
                        boolean ySame = false;

                        if (xArray[num] > x) {
                            xPositive = true;
                        } else if (xArray[num] == x) {
                            xSame = true;
                        }

                        if (yArray[num] > y) {
                            yPositive = true;
                        } else if (yArray[num] == y) {
                            ySame = true;
                        }

                        //Movement logic if no jumping is involved
                        if (tileArray[xArray[num]][yArray[num]].returnID() == 0) {
                            tileArray[xArray[num]][yArray[num]].changeColor(Color.RED, Color.AQUA);
                            tileArray[xArray[num]][yArray[num]].setOnMousePressed(d -> {
                                piece.setTranslateX(tileArray[xArray[num]][yArray[num]].getTranslateX());
                                piece.setTranslateY(tileArray[xArray[num]][yArray[num]].getTranslateY());
                                tileArray[xArray[num]][yArray[num]].setId(1);
                                tileArray[x][y].setId(0);
                                tileArray[x][y].removePiece();
                                tileArray[xArray[num]][yArray[num]].setPiece(piece);
                                tileArray[x][y].setPiece(null);
                                piece.changeColor(colorArray[piece.returnID()]);


                                revertBoard();

                                setPlayer();

                            });

                        }

                        //Movement logic if jumping to the right
                        if (tileArray[x + 1][y].getPiece() != null && tileArray[x + 2][y].returnID() == 0) {
                            tileArray[x + 2][y].changeColor(Color.RED, Color.AQUA);
                            tileArray[x + 2][y].setOnMousePressed(d -> {
                                piece.setTranslateX(tileArray[x + 2][y].getTranslateX());
                                piece.setTranslateY(tileArray[x + 2][y].getTranslateY());
                                tileArray[x + 2][y].setId(1);
                                tileArray[x][y].setId(0);
                                tileArray[x][y].removePiece();
                                tileArray[x + 2][y].setPiece(piece);
                                piece.changeColor(colorArray[piece.returnID()]);


                                revertBoard();
                                setPlayer();

                            });
                        }



                        //Movement logic if jumping to the left
                        if (tileArray[x - 1][y].getPiece() != null && tileArray[x - 2][y].returnID() == 0) {
                            tileArray[x - 2][y].changeColor(Color.RED, Color.AQUA);
                            tileArray[x - 2][y].setOnMousePressed(d -> {
                                piece.setTranslateX(tileArray[x - 2][y].getTranslateX());
                                piece.setTranslateY(tileArray[x - 2][y].getTranslateY());
                                tileArray[x - 2][y].setId(1);
                                tileArray[x][y].setId(0);
                                tileArray[x][y].removePiece();
                                tileArray[x - 2][y].setPiece(piece);
                                piece.changeColor(colorArray[piece.returnID()]);
                                revertBoard();
                                setPlayer();

                            });
                        }


                        if (y % 2 == 0) {
                            movementLogic(piece, num, x, y + 1, x - 1, y + 2);
                            movementLogic(piece, num, x + 1, y + 1, x + 1, y + 2);
                            movementLogic(piece, num, x + 1, y - 1, x + 1, y - 2);
                            movementLogic(piece, num, x, y - 1, x - 1, y - 2);


                        } else {

                            movementLogic(piece, num, x, y - 1, x + 1, y - 2);
                            movementLogic(piece, num, x - 1, y - 1, x - 1, y - 2);
                            movementLogic(piece, num, x, y + 1, x + 1, y + 2);
                            movementLogic(piece, num, x, y - 1, x + 1, y - 2);
                            movementLogic(piece, num, x - 1, y + 1, x - 1, y + 2);


                        }
                    }

                }
            });


    }


//Resetting the boards colors and events
    private void revertBoard(){

        Player winner = winner();
        if(winner != null){
            System.exit(0);
        }

        tileArray[x][y].setOnMousePressed(null);

        try {
            tileArray[x+2][y].changeColor(Color.RED, Color.WHITE);
            tileArray[x+2][y].setOnMousePressed(null);
        } catch (Exception e){

        }


        try {
            tileArray[x-2][y].changeColor(Color.RED, Color.WHITE);
            tileArray[x-2][y].setOnMousePressed(null);
        } catch (Exception e){

        }

        try {
            tileArray[x - 1][y + 2].changeColor(Color.RED, Color.WHITE);
            tileArray[x - 1][y + 2].setOnMousePressed(null);
        } catch (Exception e){

        }

        try {
            tileArray[x+1][y+2].changeColor(Color.RED, Color.WHITE);
            tileArray[x+1][y+2].setOnMousePressed(null);
        } catch (Exception e) {

        }

        try {
            tileArray[x+1][y-2].changeColor(Color.RED, Color.WHITE);
            tileArray[x+1][y-2].setOnMousePressed(null);
        } catch (Exception e){

        }

        try {
            tileArray[x-1][y-2].changeColor(Color.RED, Color.WHITE);
            tileArray[x-1][y-2].setOnMousePressed(null);
        } catch (Exception e){

        }

        try {
            tileArray[x+1][y-2].changeColor(Color.RED, Color.WHITE);
            tileArray[x+1][y-2].setOnMousePressed(null);

        } catch (Exception E){

        }

        try {
            tileArray[x-1][y-2].changeColor(Color.RED, Color.WHITE);
            tileArray[x-1][y-2].setOnMousePressed(null);
        } catch (Exception e){

        }


        try {
            tileArray[x + 1][y + 2].changeColor(Color.RED, Color.WHITE);
            tileArray[x + 1][y + 2].setOnMousePressed(null);
        } catch (Exception e){

        }

        try {
            tileArray[x-1][y+2].changeColor(Color.RED, Color.WHITE);
            tileArray[x-1][y+2].setOnMousePressed(null);
        } catch (Exception e){

        }

        for (int m = 0; m < 6; m++) {
            tileArray[xArray[m]][yArray[m]].changeColor(Color.RED, Color.WHITE);
            tileArray[xArray[m]][yArray[m]].setOnMousePressed(null);
        }
    }




//Recording and tallying the winner
    private Player winner(){
        int zeroCounter = 0;
        int firstCounter = 0;
        int secondCounter = 0;
        int thirdCounter = 0;
        int fourthCounter = 0;
        int fifthCounter = 0;

        for (int h = 0; h < HEIGHT; h++) {
            for (int w = 0; w < WIDTH; w++) {
                if(tileArray[w][h].getPiece() != null) {
                    if ((h >= 14 && h <= 17)) {
                        if(tileArray[w][h].getPiece().returnID() == 0)
                        zeroCounter++;
                    }
                    if(h>=1 && h <= 4) {
                        if (tileArray[w][h].getPiece().returnID() == 1)
                            firstCounter++;
                    }
                    if ((h == 8 && w == 17) || (h == 7 && (w == 17 || w == 18)) || (h==6 && (w == 16 || w == 17 || w == 18)) || (h == 5 && (w == 15 || w == 16 || w == 17 || w == 18))) {
                        if(tileArray[w][h].getPiece().returnID() == 3){
                            thirdCounter++;
                        }
                    }
                    if ((h == 10 && w == 17) || (h == 11 && (w == 17 || w == 18)) || (h==12 && (w == 16 || w == 17 || w == 18)) || (h == 13 && (w == 15 || w == 16 || w == 17 || w == 18))) {
                        if(tileArray[w][h].getPiece().returnID() == 5){
                            fifthCounter++;
                        }
                    }
                    if ((h == 10 && w == 8) || (h == 11 && (w == 8 || w == 9)) || (h==12 && (w == 7 || w == 8 || w == 9)) || (h == 13 && (w == 7 || w == 8 || w == 9 || w == 10))) {
                        if(tileArray[w][h].getPiece().returnID() == 2){

                            secondCounter++;
                        }
                    }
                    if ((h == 8 && w == 8) || (h == 7 && (w == 8 || w == 9)) || (h==6 && (w == 7 || w == 8 || w == 9)) || (h == 5 && (w == 7 || w == 8 || w == 9 || w == 10))) {
                        if(tileArray[w][h].getPiece().returnID() == 4){
                            fourthCounter++;
                        }
                    }
                }
            }
        }



        //If any player has moved all 10 pieces into the opposite home base then they are returned as the winner
        if (zeroCounter == 10){
            System.out.println("Player 0 is the winner");
            return playerArray[0];

        } else if(firstCounter == 10){
            System.out.println("Player 1 is the winner");
            return playerArray[1];

        } else if(secondCounter == 10){
            System.out.println("Player 2 is the winner");
            return playerArray[2];

        } else if(thirdCounter == 10){
            System.out.println("Player 3 is the winner");
            return playerArray[3];

        } else if(fourthCounter == 10){
            System.out.println("Player 4 is the winner");
            return playerArray[4];

        } else if(fifthCounter == 10){
            System.out.println("Player 5 is the winner");
            return playerArray[5];
        } else {
            return null;
        }


    }


    //Creating the starting menu window
    public Parent createLobby() {
        StackPane root = new StackPane();
        root.setPrefSize(1000, 700);
        root.setId("pane2");


        //Creating and setting event for start button
        Button startButton = new Button();
        startButton.setPrefSize(1000, 50);
        startButton.setText("Start");
        StackPane.setAlignment(startButton, Pos.BOTTOM_CENTER);
        root.getChildren().add(startButton);

        //If number of players selcted goes down the numberOFNamesEntered variable deos not this needs to change
        startButton.setOnAction(e -> {
            if(numberOfNamesEntered == numberOfPlayers && (numberOfPlayers != 0)) {
                Scene scene = new Scene(createContent());
                scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();
            } else if (numberOfPlayers == 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("NO PLAYERS");
                alert.setContentText("There are currently no players");
                alert.showAndWait();
            } else if (numberOfNamesEntered < numberOfPlayers) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("TOO FEW PLAYERS");
                alert.setContentText("You must have a name entered for every player");
                alert.showAndWait();
            }
        });


        //Creating and setting options for ComboBox
        ComboBox<Integer> playerNumber = new ComboBox<>();
        playerNumber.setPromptText("Select Number of Players");
        playerNumber.getItems().addAll(
                2,
                4,
                6
        );
        StackPane.setAlignment(playerNumber, Pos.TOP_CENTER);
        playerNumber.setTranslateY(200);
        root.getChildren().add(playerNumber);


        //Creating and setting properties for Title
        TextField title = new TextField("Chinese Checkers");
        title.setAlignment(CENTER);
        title.setTranslateY(50);
        title.setId("title");
        StackPane.setAlignment(title, Pos.TOP_CENTER);
        title.setMaxWidth(600);
        root.getChildren().add(title);


        //Creating TextFields and storing them in List to allow for easily setting properties
        int move = -375;
        nameList = new TextField[6];
        for (int i = 0; i < nameList.length; ++i) {
            nameList[i] = new TextField("Enter Player " + (i + 1) + " Name");
            onTextFieldAction(nameList[i], i);
            root.getChildren().add(nameList[i]);
            StackPane.setAlignment(nameList[i], CENTER);
            nameList[i].setMaxWidth(150);
            nameList[i].setAlignment(CENTER);
            nameList[i].setTranslateX(move);
            nameList[i].setDisable(true);
            move += 150;
        }


        //Disabling and enabling textFields based on number of players selected
        //Number of players already set to be used in start button event if current number of names already entered
        playerNumber.setOnAction(e -> {
            numberOfPlayers = playerNumber.getValue();
            for (int i = 0; i < numberOfPlayers; ++i) {
                nameList[i].setDisable(false);
            }
            for (int i = nameList.length; i > numberOfPlayers; i--) {
                nameList[i - 1].setText("Enter Player " + i + " Name");
                nameList[i - 1].setDisable(true);
            }
            validName = new boolean[numberOfPlayers];
        });


        return root;
    }


    //Event when TextField is selected
    public void onTextFieldAction(TextField playerName, int index) {
        String text = playerName.getText();

        playerName.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if ((playerName.getText().equals(text))) {
                playerName.clear();
            } else if (playerName.getText().equals("")) {

                playerName.setText(text);
                firstName = text;
                if (validName[index]) {
                    validName[index] = false;
                    numberOfNamesEntered--;
                }
            } else {
                if (!validName[index]) {
                    validName[index] = true;
                    numberOfNamesEntered++;
                }
            }


        });
    }


    //Sets up the window
    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createLobby());
        scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    //Launches program

    public static void main(String[] args) {

        launch(args);

    }

}