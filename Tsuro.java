import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Button;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 * A class to represent the Tsuro game board and player actions
 * @author Ali Puccio
 */

public class Tsuro extends Application {
  /** The game board */
  private TsuroButton[][] buttonArray = new TsuroButton[boardWidth][boardHeight];
  /** Player 1's hand buttons */
  private TsuroButton[] player1Hand = new TsuroButton[playerHandSize];
  /** Player 2's hand buttons*/
  private TsuroButton[] player2Hand = new TsuroButton[playerHandSize];
  /** Stores whether or not it's player 1's turn */
  private static boolean player1Turn = true;
  /** Stores whether or not it's player 2's turn */
  private static boolean player2Turn = false;
  /** Stores whether or not it's player 1's first turn */
  private boolean p1FirstTurn = true;
  /** Stores whether or not it's player 2's first turn */
  private boolean p2FirstTurn = true;
  /** The coordinates of the last tile placed by player 1 */
  private int[] p1lastTilePlayed = {0, 0};
  /** The coordinates of the last tile placed by player 2 */
  private int[] p2lastTilePlayed = {0, 0};
  /** Stores the endpoints that the endpoints map to during rotation */
  private int[] mappingNums = {2, 3, 5, 4, 6, 7, 1, 0};
  /** The connections are rotated in two steps, requiring two arrays. This is the first of the arrays */
  private int[] updatedEndpoints1 = new int[8];
  /** The connections are rotated in two steps, requiring two arrays. This is the second of the arrays */
  private int[] updatedEndpoints2 = new int[8];
  /** The button selected by the player */
  private TsuroButton buttonPlayed = new TsuroButton(100, 100);
  /** The current endpoint of player 1's stone */
  private int p1stonePlace = 0;
  /** The current endpoint of player 2's stone */
  private int p2stonePlace = 0;
  /** Stores the endpoints that the stone's transfer to when travelling across tiles */
  private int[] stoneTransfer = {4, 5, 6, 7, 0, 1, 2, 3};
  /** Stores whether or not there is a tile for the stone to move to after it has moved through its first path (aka when it has to travel over multiple tiles) */
  private boolean p1stoneMoveable = true;
  /** Stores whether or not there is a tile for the stone to move to after it has moved through its first path (aka when it has to travel over multiple tiles) */
  private boolean p2moving = true;
  /** Player 1s hand stage */
  private Stage player1Stage = new Stage();
  /** Player 2s hand stage */
  private Stage player2Stage = new Stage();
  /** Board length */
  private static int boardWidth = 0;
  /** Board height */
  private static int boardHeight = 0;
  /** Player hand size */
  private static int playerHandSize = 0;
  
  public void start(Stage primaryStage){
    
    ///////////////////////////Player 1 code////////////////////////////////////
    //Setting up player 1's hand GUIs
    GridPane gridPane2 = new GridPane();
    Scene scene2 = new Scene(gridPane2);
    player1Stage.setTitle("Player 1");
    player1Stage.setScene(scene2);
    player1Stage.show();
    
    //Sets up player 1's button's connections and actions 
    for(int i=0; i < playerHandSize; i++){
      player1Hand[i] = new TsuroButton(100,100);
      gridPane2.add(player1Hand[i], i, 0);
      player1Hand[i].setConnections(player1Hand[i].makeRandomConnectionArray());
      player1Hand[i].addStone(Color.BLUE, 6);
      
      player1Hand[i].setOnAction(new EventHandler<ActionEvent>(){   
        public void handle(ActionEvent e) {          
          TsuroButton b = (TsuroButton)e.getSource();
          updatedEndpoints1 = new int[8];
          updatedEndpoints2 = new int[8];
          //Rotates the tile selected by the user
          if(player1Turn && b.getBackgroundColor() == Color.YELLOW)
            rotate(b);
          //Highlights the tile selected by the user
          if(player1Turn && b.getBackgroundColor() != Color.YELLOW){
            for(int i = 0; i < playerHandSize; i ++)
              player1Hand[i].setBackgroundColor(Color.WHITE);
            b.setBackgroundColor(Color.YELLOW);
          }
        }
      });
    }
    
    
    /////////////////////////////////////Player 2 code////////////////////////////////////
    //Setting up player 2's hand GUIs
    GridPane gridPane3 = new GridPane();
    Scene scene3 = new Scene(gridPane3);
    player2Stage.setTitle("Player 2");
    player2Stage.setScene(scene3);
    player2Stage.show();
    
    //Sets up the connections and actions for player 2's buttons
    for(int i=0; i < playerHandSize; i++){
      player2Hand[i] = new TsuroButton(100, 100);
      gridPane3.add(player2Hand[i], i, 0);
      player2Hand[i].setConnections(player2Hand[i].makeRandomConnectionArray());
      player2Hand[i].addStone(Color.GREEN, 2);
      player2Hand[i].setOnAction(new EventHandler<ActionEvent>(){   
        
        public void handle(ActionEvent e) {          
          TsuroButton b = (TsuroButton)e.getSource();
          updatedEndpoints1 = new int[8];
          updatedEndpoints2 = new int[8];
          //Rotates the tile selected by the user
          if(player2Turn && b.getBackgroundColor() == Color.YELLOW)
            rotate(b);
          //Highlights the tile selected by the user
          if(player2Turn && b.getBackgroundColor() != Color.YELLOW){
            for(int i = 0; i < playerHandSize; i ++)
              player2Hand[i].setBackgroundColor(Color.WHITE);
            b.setBackgroundColor(Color.YELLOW);
          }
        }
      });
    }
    
    //////////////////////////////////////////////GameBoard Code////////////////////////////////////////////
    GridPane gridPane = new GridPane();
    Scene scene = new Scene(gridPane);
    
    //Creates the tile grid for the gameboard
    for(int i=0; i < boardWidth; i++){
      for(int j = 0; j < boardHeight; j++){
        buttonArray[i][j] = new TsuroButton(100, 100);
        gridPane.add(buttonArray[i][j], i, j);
        
        //Sets up the actions for the game board tiles
        buttonArray[i][j].setOnAction(new EventHandler<ActionEvent>(){
         
          public void handle(ActionEvent e) {
            TsuroButton b = (TsuroButton)e.getSource();
            
            //Player 1's first turn
            if(player1Turn && p1FirstTurn){
              //Checks if tile is placed in the left column and playes the tile if it is
              for(int i = 0; i < boardHeight; i++){
                if(b == buttonArray[0][i]){
                  playTile(p1FindButtonPlayed(), b);
                  b.addStone(Color.BLUE, b.getConnections()[6]);
                  p1stonePlace = b.getConnections()[6];
                  p1rememberLastTile(b);
                  p1FirstTurn = false;
                  checkIfHitWall(b);
                  //Removes stones from player 1's hand so they can be added to the correct endpoint after the first tile is played
                  for(int k = 0; k < playerHandSize; k ++){ 
                    for(int j = 0; j < 8; j++){
                      player1Hand[k].removeStone(j);
                    }
                  }
                  //Adds stones to the correct endpoint on the tiles in player 1s hands
                  for(int s = 0; s < playerHandSize; s ++){ 
                    player1Hand[s].addStone(Color.BLUE, stoneTransfer[p1stonePlace]);
                  }
                }
              }
            }
            
            
            //Player 2 first turn (button can go in right column)
            if(player2Turn && p2FirstTurn){
              for(int i = 0; i < boardHeight; i++){
                if(b == buttonArray[boardWidth-1][i]){
                  playTile(p2FindButtonPlayed(), b);
                  b.addStone(Color.GREEN, b.getConnections()[2]);
                  p2stonePlace = b.getConnections()[2];
                  p2rememberLastTile(b);
                  p2FirstTurn = false;
                  p2checkIfHitWall(b);
                  //Removes stones from player 1's hand so they can be added to the correct endpoint after the first tile is played
                  for(int k = 0; k < playerHandSize; k ++){ 
                    for(int j = 0; j < 8; j++){
                      player2Hand[k].removeStone(j);
                    }
                  }
                  //Adds stones to the correct endpoint on the tiles in player 1s hands
                  for(int s = 0; s < playerHandSize; s ++){ 
                    player2Hand[s].addStone(Color.GREEN, stoneTransfer[p2stonePlace]);
                  }
                }
              }
            }
            
            //Code for player 1's turns after their first turn
            else if(player1Turn && (p1stonePlace == 0 || p1stonePlace == 1) && correctPlacementButton10(b)){
              playTile(p1FindButtonPlayed(), b); 
              p1moveStone(b);
              p1rememberLastTile(b);
              while (p1stoneMoveable)
                p1moveStoneFurther();  
            }
            else if(player1Turn && (p1stonePlace == 2 || p1stonePlace == 3) && correctPlacementButton23(b)){
              playTile(p1FindButtonPlayed(), b); 
              p1moveStone(b);
              p1rememberLastTile(b);
              while (p1stoneMoveable)
                p1moveStoneFurther();
            }
            else if(player1Turn && (p1stonePlace == 4 || p1stonePlace == 5) && correctPlacementButton45(b)){
              playTile(p1FindButtonPlayed(), b); 
              p1moveStone(b);
              p1rememberLastTile(b);
              while (p1stoneMoveable)
                p1moveStoneFurther();
            }
            else if(player1Turn && (p1stonePlace == 6 || p1stonePlace == 7) && correctPlacementButton67(b)){
              playTile(p1FindButtonPlayed(), b); 
              p1moveStone(b);
              p1rememberLastTile(b);
              while (p1stoneMoveable)
                p1moveStoneFurther();
            }
            
            
            //Code for all of player 2s turns after their first turn
            else if(player2Turn && (p2stonePlace == 0 || p2stonePlace == 1) && p2correctPlacementButton10(b)){
              playTile(p2FindButtonPlayed(), b); 
              p2moveStone(b);
              p2rememberLastTile(b);
              while (p2moving)
                p2moveStoneFurther();
            }
            else if(player2Turn && (p2stonePlace == 2 || p2stonePlace == 3) && p2correctPlacementButton23(b)){
              playTile(p2FindButtonPlayed(), b); 
              p2moveStone(b);
              p2rememberLastTile(b);
              while (p2moving)
                p2moveStoneFurther();
            }
            else if(player2Turn && (p2stonePlace == 4 || p2stonePlace == 5) && p2correctPlacementButton45(b)){
              playTile(p2FindButtonPlayed(), b); 
              p2moveStone(b);
              p2rememberLastTile(b);
              while (p2moving)
                p2moveStoneFurther();
            }
            else if(player2Turn && (p2stonePlace == 6 || p2stonePlace == 7) && p2correctPlacementButton67(b)){
              playTile(p2FindButtonPlayed(), b); 
              p2moveStone(b);
              p2rememberLastTile(b);
              while (p2moving)
                p2moveStoneFurther();
            }
          } 
        });
      }
    }
    
    primaryStage.setTitle("Tsuro Board");
    primaryStage.setScene(scene);
    primaryStage.show();
    
  }
  
  //////////////////////////////HELPER METHODS//////////////////////////////////
  
  /** 
   * Switches the players' turns 
   */
  public static void switchTurns(){
    player1Turn = !player1Turn;
    player2Turn = !player2Turn;
  }
  
  /**
   * Finds the tile that player 1 has chosen to place on the board
   * @return the tile being played
   */
  public TsuroButton p1FindButtonPlayed(){
    //finds the current highlighted button
    for(int i = 0; i < playerHandSize; i ++){ 
      if (player1Hand[i].getBackgroundColor() == Color.YELLOW){
        return player1Hand[i];
      }
    }
    return null;
  }
  
  /**
   * Finds the tile that player 2 has chosen to place on the board
   * @return the tile being played
   */
  public TsuroButton p2FindButtonPlayed(){
    //finds the current highlighted button
    for(int i = 0; i < playerHandSize; i ++){ 
      if (player2Hand[i].getBackgroundColor() == Color.YELLOW){
        return player2Hand[i];
      }
    }
    return null;
  }
  
  /**
   * Places the tile selected by the player onto the game board tile selected
   * @param tilePlayed the tile from the player's hand that they are placing on the board
   * @param gameSpace the tile in the button array that the player wants to place their game piece on
   */
  public void playTile(TsuroButton tilePlayed, TsuroButton gameSpace){
    gameSpace.setConnections(tilePlayed.getConnections());
    tilePlayed.setConnections(tilePlayed.makeRandomConnectionArray());
    tilePlayed.setBackgroundColor(Color.WHITE);
    switchTurns();
    p1stoneMoveable = true;
    p2moving = true;
  }
  
  /**
   * Rotates a selected tile in the player's hand
   */
  public void rotate(TsuroButton button){
    //Maps the values in the endpoint array to a new array. In other words, maps one set of endpoints
    for(int i = 0; i < 8; i++){
      for(int j = 0; j < 8; j++){
        if(button.getConnections()[i] == j)
          updatedEndpoints1[i] = mappingNums[j];
      }
    }
    //Maps the second set of endpoints to the final updated array
    for(int i = 0; i < 8; i++){
      updatedEndpoints2[mappingNums[i]] = updatedEndpoints1[i];
    }
    button.setConnections(updatedEndpoints2);
  }
  
  /**
   * Rotate method that doesn't require TsuroButton so it can be JUnit tested
   * @param originalConnections the connections to be rotated
   * @return the updated connections
   */
  public static int[] testableRotate(int[] originalConnections){ 
    int[] rotationMapNums = {2, 3, 5, 4, 6, 7, 1, 0};
    int[] updatedEndpoints = {0, 0, 0, 0, 0, 0, 0, 0};
    int[] updatedEndpoints2 = {0, 0, 0, 0, 0, 0, 0, 0};
    for(int i = 0; i < 8; i++){
      for(int j = 0; j < 8; j++){
        if(originalConnections[i] == j)
          updatedEndpoints[i] = rotationMapNums[j];
      }
    }
    //Maps the second set of endpoints to the final updated array
    for(int i = 0; i < 8; i++){
      updatedEndpoints2[rotationMapNums[i]] = updatedEndpoints[i];
    }
    return updatedEndpoints2;
  }
  
  /**
   * Stores the coordinates of the last place a player 1 tile was played on the game board
   */
  public void p1rememberLastTile(TsuroButton button){
    //Determines the coordinates of the gameboard tile that a player just placed their tile on
    for(int i = 0; i < boardWidth; i++){
      for(int j = 0; j < boardHeight; j++){
        if(buttonArray[i][j] == button){
          p1lastTilePlayed[0] = i;
          p1lastTilePlayed[1] = j;
        }
      }
    }
  }
  
  /**
   * Stores the coordinates of the last place a player 2 tile was played on the game board
   */
  public void p2rememberLastTile(TsuroButton button){
    //Determines the coordinates of the gameboard tile that a player just placed their tile on
    for(int i = 0; i < boardWidth; i++){
      for(int j = 0; j < boardHeight; j++){
        if(buttonArray[i][j] == button){
          p2lastTilePlayed[0] = i;
          p2lastTilePlayed[1] = j;
        }
      }
    }
  }
  
  /** 
   * Moves player 1's stone as far it can be moved through the paths on the game board tiles
   */
  public void p1moveStoneFurther(){
    TsuroButton buttonWithStone;                                                  //the gameboard tile with the player's stone
    if(p1stonePlace == 0 || p1stonePlace == 1){
      if ((p1lastTilePlayed[1] - 1) > -1 && buttonArray[p1lastTilePlayed[0]][p1lastTilePlayed[1] - 1].getConnections() != null){
        buttonWithStone = buttonArray[p1lastTilePlayed[0]][p1lastTilePlayed[1] - 1];
        p1moveStone(buttonWithStone);
        p1rememberLastTile(buttonWithStone);
      }else
        p1stoneMoveable = false;
    }
    if(p1stonePlace == 2 || p1stonePlace == 3){
      if ((p1lastTilePlayed[0] + 1) < 6 && buttonArray[p1lastTilePlayed[0] + 1][p1lastTilePlayed[1]].getConnections() != null){
        buttonWithStone = buttonArray[p1lastTilePlayed[0] + 1][p1lastTilePlayed[1]];
        p1moveStone(buttonWithStone);
        p1rememberLastTile(buttonWithStone);
      }else
        p1stoneMoveable = false;
    }
    if(p1stonePlace == 4 || p1stonePlace == 5){
      if ((p1lastTilePlayed[1] + 1) < 6 && buttonArray[p1lastTilePlayed[0]][p1lastTilePlayed[1] + 1].getConnections() != null){
        buttonWithStone = buttonArray[p1lastTilePlayed[0]][p1lastTilePlayed[1] + 1];
        p1moveStone(buttonWithStone);
        p1rememberLastTile(buttonWithStone);
      }else
        p1stoneMoveable = false;
    }
    if(p1stonePlace == 6 || p1stonePlace == 7){
      if ((p1lastTilePlayed[0] - 1) > -1 && buttonArray[p1lastTilePlayed[0] - 1][p1lastTilePlayed[1]].getConnections() != null){
        buttonWithStone = buttonArray[p1lastTilePlayed[0] - 1][p1lastTilePlayed[1]];
        p1moveStone(buttonWithStone);
        p1rememberLastTile(buttonWithStone);
      }else
        p1stoneMoveable = false;
    }
    
  }
  
  /** 
   * Moves player 1's stone as far it can be moved through the paths on the game board tiles
   */
  public void p2moveStoneFurther(){
    TsuroButton buttonWithStone;                                               //the gameboard tile with the player's stone
    if(p2stonePlace == 0 || p2stonePlace == 1){
      if ((p2lastTilePlayed[1] - 1) > -1 && buttonArray[p2lastTilePlayed[0]][p2lastTilePlayed[1] - 1].getConnections() != null){
        buttonWithStone = buttonArray[p2lastTilePlayed[0]][p2lastTilePlayed[1] - 1];
        p2moveStone(buttonWithStone);
        p2rememberLastTile(buttonWithStone);
      }else
        p2moving = false;
    }
    else if(p2stonePlace == 2 || p2stonePlace == 3){
      if ((p2lastTilePlayed[0] + 1) < 6 && buttonArray[p2lastTilePlayed[0] + 1][p2lastTilePlayed[1]].getConnections() != null){
        buttonWithStone = buttonArray[p2lastTilePlayed[0] + 1][p2lastTilePlayed[1]];
        p2moveStone(buttonWithStone);
        p2rememberLastTile(buttonWithStone);
      }else
        p2moving = false;
    }
    else if(p2stonePlace == 4 || p2stonePlace == 5){
      if ((p2lastTilePlayed[1] + 1) < 6 && buttonArray[p2lastTilePlayed[0]][p2lastTilePlayed[1] + 1].getConnections() != null){
        buttonWithStone = buttonArray[p2lastTilePlayed[0]][p2lastTilePlayed[1] + 1];
        p2moveStone(buttonWithStone);
        p2rememberLastTile(buttonWithStone);
      }else
        p2moving = false;
    }
    else if(p2stonePlace == 6 || p2stonePlace == 7){
      if ((p2lastTilePlayed[0] - 1) > -1 && buttonArray[p2lastTilePlayed[0] - 1][p2lastTilePlayed[1]].getConnections() != null){
        buttonWithStone = buttonArray[p2lastTilePlayed[0] - 1][p2lastTilePlayed[1]];
        p2moveStone(buttonWithStone);
        p2rememberLastTile(buttonWithStone);
      }else
        p2moving = false;
    }
    
  }
  
  /** 
   * Moves player 1's stone down the path of the tile played
   */
  public void p1moveStone(TsuroButton button){
    int a = -1;                                                    //the x coordinate of the selected gameboard tile 
    int b = -1;                                                    //the y coordinate of the selected gameboard tile 
    //remove stone from previous tile
    buttonArray[p1lastTilePlayed[0]][p1lastTilePlayed[1]].removeStone(p1stonePlace);
    //move stone down path of next tile
    button.addStone(Color.BLUE, button.getConnections()[stoneTransfer[p1stonePlace]]);
    p1stonePlace = button.getConnections()[stoneTransfer[p1stonePlace]];
    //checks if stone has hit the wall
    checkIfHitWall(button);
    //remove stones from player1s hand so the stones can be added to the endpoint that corresponds to the endpoint of the recently played tile
    for(int i = 0; i < playerHandSize; i ++){ 
      for(int j = 0; j < 8; j++){
        player1Hand[i].removeStone(j);
      }
    }
    //adds stones to the correct endpoint of the tiles in player 1s hand
    for(int i = 0; i < playerHandSize; i ++){ 
      player1Hand[i].addStone(Color.BLUE, stoneTransfer[p1stonePlace]);
    }
  }
  
  /** 
   * Moves player 2's stone down the path of the tile played
   */
  public void p2moveStone(TsuroButton button){
    int a = -1;                                                    //the x coordinate of the selected gameboard tile 
    int b = -1;                                                    //the y coordinate of the selected gameboard tile 
    //remove stone from previous tile
    buttonArray[p2lastTilePlayed[0]][p2lastTilePlayed[1]].removeStone(p2stonePlace);
    //move stone down path of next tile
    button.addStone(Color.GREEN, button.getConnections()[stoneTransfer[p2stonePlace]]);
    p2stonePlace = button.getConnections()[stoneTransfer[p2stonePlace]];
    //These if statements check if the stone is at the edge of the board and end the game if it is
    p2checkIfHitWall(button);
    //remove stones from player2s hand so the stones can be added to the endpoint that corresponds to the endpoint of the recently played tile
    for(int i = 0; i < playerHandSize; i ++){ 
      for(int j = 0; j < 8; j++){
        player2Hand[i].removeStone(j);
      }
    }
    //adds stones to the correct endpoint of the tiles in player 2s hand
    for(int i = 0; i < playerHandSize; i ++){ 
      player2Hand[i].addStone(Color.GREEN, stoneTransfer[p2stonePlace]);
    }
  }
  
  /** 
   * Ends the game if a stone has hit the edge of the gameboard
   * @param button the button played by the user
   */
  public void checkIfHitWall(TsuroButton button){
    //finding location of button on Tsuro gameboard
    int a = -1;
    int b = -1;
    for(int i = 0; i < boardWidth; i++){
      for(int j = 0; j < boardHeight; j++){
        if(button == buttonArray[i][j]){
          a = i;
          b = j;
        }
      }
    }
    //ends game if the stone hits a wall
    if(a == 0 && (p1stonePlace == 6 || p1stonePlace == 7)){
      player1Turn = false;
      player2Turn = false;
      player1Stage.setTitle("GAMEOVER");
      player2Stage.setTitle("GAME OVER");
    }
    else if (a == (boardWidth - 1) && (p1stonePlace == 2 || p1stonePlace == 3)){
      player1Turn = false;
      player2Turn = false;
      player1Stage.setTitle("GAME OVER");
      player2Stage.setTitle("GAME OVER");
    }
    else if (b == 0 && (p1stonePlace == 0 || p1stonePlace == 1)){
      player1Turn = false;
      player2Turn = false;
      player1Stage.setTitle("GAME OVER");
      player2Stage.setTitle("GAME OVER");
    }
    else if (b == (boardHeight-1) && (p1stonePlace == 4 || p1stonePlace == 5)){
      player1Turn = false;
      player2Turn = false;
      player1Stage.setTitle("GAME OVER");
      player2Stage.setTitle("GAME OVER");
    }
  }
  
  /** 
   * Ends the game if a stone has hit the edge of the gameboard
   * @param button the button played by the user
   */
  public void p2checkIfHitWall(TsuroButton button){
    //finding location of button on Tsuro gameboard
    int a = -1;
    int b = -1;
    for(int i = 0; i < boardWidth; i++){
      for(int j = 0; j < boardHeight; j++){
        if(button == buttonArray[i][j]){
          a = i;
          b = j;
        }
      }
    }
    //ends game if the stone hits a wall
    if(a == 0 && (p2stonePlace == 6 || p2stonePlace == 7)){
      player1Turn = false;
      player2Turn = false;
      player1Stage.setTitle("GAMEOVER");
      player2Stage.setTitle("GAME OVER");
    }
    else if (a == (boardWidth - 1) && (p2stonePlace == 2 || p2stonePlace == 3)){
      player1Turn = false;
      player2Turn = false;
      player1Stage.setTitle("GAME OVER");
      player2Stage.setTitle("GAME OVER");
    }
    else if (b == 0 && (p2stonePlace == 0 || p2stonePlace == 1)){
      player1Turn = false;
      player2Turn = false;
      player1Stage.setTitle("GAME OVER");
      player2Stage.setTitle("GAME OVER");
    }
    else if (b == (boardHeight-1) && (p2stonePlace == 4 || p2stonePlace == 5)){
      player1Turn = false;
      player2Turn = false;
      player1Stage.setTitle("GAME OVER");
      player2Stage.setTitle("GAME OVER");
    }
  }
  
  
  
  /**
   * Checks whether or not player1 has selected a valid game board tile if their stone is at endpoint 0 or 1
   * @param button the game board tile selected  
   * @return whether or not the tile selected is valid
   */
  public boolean correctPlacementButton10(TsuroButton button){
    int i = -1;
    int j = -1;
    //Determines which game board tile was selected by the player
    for(int a = 0; a < boardWidth; a++){
      for(int b = 0; b < boardHeight; b++){
        if(buttonArray[a][b] == button){
          i = a;
          j = b;
        }
      }
    }
    if(i == p1lastTilePlayed[0] && j == (p1lastTilePlayed[1] - 1))
      return true;
    else
      return false;
  }
  
  /**
   * Checks whether or not player1 has selected a valid game board tile if their stone is at endpoint 2 or 3
   * @param button the game board tile selected  
   * @return whether or not the tile selected is valid
   */
  public boolean correctPlacementButton23(TsuroButton button){
    int i = -1;
    int j = -1;
    //Determines which game board tile was selected by the player
    for(int a = 0; a < boardWidth; a++){
      for(int b = 0; b < boardHeight; b++){
        if(buttonArray[a][b] == button){
          i = a;
          j = b;
        }
      }
    }
    if(i == (p1lastTilePlayed[0] + 1) && j == p1lastTilePlayed[1])
      return true;
    else
      return false;
  }
  
  /**
   * Checks whether or not player1 has selected a valid game board tile if their stone is at endpoint 4 or 5
   * @param button the game board tile selected  
   * @return whether or not the tile selected is valid
   */
  public boolean correctPlacementButton45(TsuroButton button){
    int i = -1;                                                    //the x coordinate of the selected gameboard tile 
    int j = -1;                                                    //the y coordinate of the selected gameboard tile 
    //Determines which game board tile was selected by the player
    for(int a = 0; a < boardWidth; a++){
      for(int b = 0; b < boardHeight; b++){
        if(buttonArray[a][b] == button){
          i = a;
          j = b;
        }
      }
    }
    if(i == p1lastTilePlayed[0] && j == (p1lastTilePlayed[1] + 1))
      return true;
    else
      return false;
  }
  
  /**
   * Checks whether or not player1 has selected a valid game board tile if their stone is at endpoint 6 or 7
   * @param button the game board tile selected  
   * @return whether or not the tile selected is valid
   */
  public boolean correctPlacementButton67(TsuroButton button){
    int i = -1;                                                    //the x coordinate of the selected gameboard tile 
    int j = -1;                                                    //the y coordinate of the selected gameboard tile 
    //Determines which game board tile was selected by the player
    for(int a = 0; a < boardWidth; a++){
      for(int b = 0; b < boardHeight; b++){
        if(buttonArray[a][b] == button){
          i = a;
          j = b;
        }
      }
    }
    if(i == (p1lastTilePlayed[0] - 1) && j == p1lastTilePlayed[1])
      return true;
    else
      return false;
  }
  
  /**
   * Checks whether or not player2 has selected a valid game board tile if their stone is at endpoint 0 or 1
   * @param button the game board tile selected  
   * @return whether or not the tile selected is valid
   */
  public boolean p2correctPlacementButton10(TsuroButton button){
    int i = -1;                                                    //the x coordinate of the selected gameboard tile 
    int j = -1;                                                    //the y coordinate of the selected gameboard tile 
    //Determines which game board tile was selected by the player
    for(int a = 0; a < boardWidth; a++){
      for(int b = 0; b < boardHeight; b++){
        if(buttonArray[a][b] == button){
          i = a;
          j = b;
        }
      }
    }
    if(i == p2lastTilePlayed[0] && j == (p2lastTilePlayed[1] - 1))
      return true;
    else
      return false;
  }
  
  /**
   * Checks whether or not player2 has selected a valid game board tile if their stone is at endpoint 2 or 3
   * @param button the game board tile selected  
   * @return whether or not the tile selected is valid
   */
  public boolean p2correctPlacementButton23(TsuroButton button){
    int i = -1;                                                    //the x coordinate of the selected gameboard tile 
    int j = -1;                                                    //the y coordinate of the selected gameboard tile 
    //Determines which game board tile was selected by the player
    for(int a = 0; a < boardWidth; a++){
      for(int b = 0; b < boardHeight; b++){
        if(buttonArray[a][b] == button){
          i = a;
          j = b;
        }
      }
    }
    if(i == (p2lastTilePlayed[0] + 1) && j == p2lastTilePlayed[1])
      return true;
    else
      return false;
  }
  
  /**
   * Checks whether or not player2 has selected a valid game board tile if their stone is at endpoint 4 or 5
   * @param button the game board tile selected  
   * @return whether or not the tile selected is valid
   */
  public boolean p2correctPlacementButton45(TsuroButton button){
    int i = -1;                                                    //the x coordinate of the selected gameboard tile 
    int j = -1;                                                    //the y coordinate of the selected gameboard tile
    //Determines which game board tile was selected by the player 
    for(int a = 0; a < boardWidth; a++){
      for(int b = 0; b < boardHeight; b++){
        if(buttonArray[a][b] == button){
          i = a;
          j = b;
        }
      }
    }
    if(i == p2lastTilePlayed[0] && j == (p2lastTilePlayed[1] + 1))
      return true;
    else
      return false;
  }
  
  /**
   * Checks whether or not player2 has selected a valid game board tile if their stone is at endpoint 6 or 7
   * @param button the game board tile selected  
   * @return whether or not the tile selected is valid
   */
  public boolean p2correctPlacementButton67(TsuroButton button){
    int i = -1;                                                    //the x coordinate of the selected gameboard tile 
    int j = -1;                                                    //the y coordinate of the selected gameboard tile 
    //Determines which game board tile was selected by the player
    for(int a = 0; a < boardWidth; a++){
      for(int b = 0; b < boardHeight; b++){
        if(buttonArray[a][b] == button){
          i = a;
          j = b;
        }
      }
    }
    if(i == (p2lastTilePlayed[0] - 1) && j == p2lastTilePlayed[1])
      return true;
    else
      return false;
  }
  
  /**
   * Sets the board height
   * @param height the board height
   */
  public static void setBoardHeight(int height){
    boardHeight = height;
  }
  
  /**
   * Gets the board height
   * @return the board height
   */
  public static int getBoardHeight(){
    return boardHeight;
  }
  
  /**
   * Sets the board width
   * @param width the board width
   */
  public static void setBoardWidth(int width){
    boardWidth = width;
  }
  
  /**
   * Gets the board width
   * @return the board width
   */
  public static int getBoardWidth(){
    return boardWidth;
  }
  
  /**
   * Sets the player hand size 
   * @param size the player hand size
   */
  public static void setPlayerHandSize(int size){
    playerHandSize = size;
  }
  
  /**
   * Gets the player hand size 
   * @return the player hand size
   */
  public static int getPlayerHandSize(){
    return playerHandSize;
  }
  
  /**
   * Returns whether or not it's player 1's turn
   * @return true if player 1's turn, false if not
   */
  public static boolean getPlayer1Turn(){
    return player1Turn;
  }
  
  /**
   * Returns whether or not it's player 2's turn
   * @return true if player 2's turn, false if not
   */
  public static boolean getPlayer2Turn(){
    return player2Turn;
  }
  
  /** 
   * Returns an array containing the coordinates of the last tile placed on the board
   * @return the array
   */
  public int[] getP1LastTilePlayed(){
    return p1lastTilePlayed;
  }
  
  
  /** 
   * Runs the Tsuro game
   * @param args the program to be run
   */
  public static void main(String[] args){
    if(args.length == 0){
      setBoardWidth(6);
      setBoardHeight(6);
      setPlayerHandSize(3);
    }
    if(args.length == 2){
      setBoardWidth(Integer.parseInt(args[0]));
      setBoardHeight(Integer.parseInt(args[1]));
    }
    if(args.length == 3){
      setBoardWidth(Integer.parseInt(args[0]));
      setBoardHeight(Integer.parseInt(args[1]));
      setPlayerHandSize(Integer.parseInt(args[2]));
    }
    System.out.println(boardWidth);
    System.out.println(boardHeight);
    System.out.println(playerHandSize);
    Application.launch(args);
  }
  
  
}