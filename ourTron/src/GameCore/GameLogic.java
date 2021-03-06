package GameCore;

import java.util.LinkedList;

import UI.GamePanel;
import Backend.User;
import Backend.UserDataBase;
/**
 * This class contains all the logic for the LightRacer game. 
 * Its main functionality are to update the player's position within the map,check for collision and update the pixel array used by GamePanel.
 *
 */
public class GameLogic {

	//size of the game window
	public int width = 600;
	public int height = 400;
	private int[] pixels = new int[width*height];
	//this determines the size of each square in pixel, 2 = 4x4 , 3 = 8x8 , 4= 16x16
	private final int bitshift = 3;
	
	//size of the map
	private final int tilesWidth = 75;
	private final int  tilesHeight = 50;
	public int[] tiles = new int [tilesWidth * tilesHeight];
	public Map gameMap;
	public Map[] gameMapCopy = new Map[3];
	//creates 3 maps
	public static Map[] allMaps = new Map[3];
	
	
	private static Player player1;
	private static Player player2;
	
	private static Player none;
	private static  User user1 = new User();
	private static  User user2 = new User();
	// userNone is used in Junit test to test when a match is draw
	private static  User userNone = new User();
	private static Coordinate startingPosP1;
	private static Coordinate startingPosP2;
	private GameScore gamescore;
	private int roundNumber = 0;
	
	//creates 2 LinkedList for both players direction
	private LinkedList <Control> p1Direction = new LinkedList <Control> ();
	private LinkedList <Control> p2Direction = new LinkedList < Control >();
	private final int  MAX_KEYINPUT = 5;
	
	private boolean gameStop = false;
	private boolean gamePause = false;
	
	//only 1 instance of GameLogic should be created
	private static GameLogic gameLogicInstance = new GameLogic();
	
	/**
	 * return a static instance of GameLogic and creates 2 new Players
	 * 	@return {@link GameLogic} a static instance of the GameLogic class
	 */
	public static GameLogic getInstance(){
		
		player1 = new Player(startingPosP1, user1, Control.NORTH);
		player2 = new Player(startingPosP2, user2, Control.SOUTH);
		//note that "none" never moves and never updates
		none = new Player(startingPosP1,userNone,Control.EAST);
		return gameLogicInstance;
		
	}
	/**
	 * Initialize players before each round. Sets player's initial position,sets the map and sets collision for both players to false.
	 */
	public void initializePlayers(){
		// reset the previous game.
		
			
		 	startingPosP1 = new Coordinate(0,49);
		 	startingPosP2 = new Coordinate(74,0);
		 	player1.setPlayerLocation(startingPosP1);
 			player2.setPlayerLocation(startingPosP2);
		 	player1.setCollision(false);
	 		player2.setCollision(false);
	 		gameStop= false;
			gamePause = false;
		 	for ( int i = 0 ; i < 3 ; ++i){
		 	 gameMapCopy[i] = new Map(allMaps[i]);
		 	}
		 	
		
		 	if(roundNumber == 0){
		 		
		 		
				p1Direction.add(player1.getDirection());
				p2Direction.add(player2.getDirection());
				gamescore = new GameScore();
				gameMap = gameMapCopy[0];
		 	}
		 	
		 	else{
		 		
				p1Direction.clear();
				p2Direction.clear();
				p1Direction.add(player1.getDirection());
				p2Direction.add(player2.getDirection());
				if(roundNumber != 3){
					gameMap = gameMapCopy[roundNumber];
				}
				
				else {
					gameMap = gameMapCopy[0];
				}
		 	}
	}

	/**
	 * Updates the game by determining players new direction and position, the method movePlayers breaks if obstacle is detected. 
	 * This method then proceeds to handle the collision by calling handleCollision method
	 * Finally, it will update the direction LinkedList for both players
	 */
	public void update() {
		if(!gameStop){
			movePlayers(p1Direction, player1, gameMap,  "player1Trail" , "player1Head");
			movePlayers(p2Direction, player2, gameMap,  "player2Trail" , "player2Head");
			handleCollisions(player1, player2);			 
			if(p1Direction.size() > 1)
				p1Direction.poll();
			if(p2Direction.size()> 1)
				p2Direction.poll();
		}
	}
	
	/**
	 * This class takes in the {@link Map} array and converts them into pixels to be displayed on the screen.
	 * We first get each tiles in {@link Map} array and assign them to a ARGB int value. This will determine the type of the tile.
	 * Then we assign each pixels to be part of a tile
	 * 	@param {@link int[]} pixels
	 */
	public void renderScreen( int[] pixels){

		//Render the map onto the screen
		for (int i = 0 ; i < gameMap.getMapSize(); i++){

			switch(gameMap.getOccupation1D(i)){
			case EMPTY:
				//transparent
				tiles[i]= 0x000000;
				break;
			case WALL:
				//orange with slight transparency 
				tiles[i]= 0x00D121 | 0xB0000000 ;
				break;
			case player1Trail:
				//blue 0x00F0FC
				tiles[i]= 0x00F0FC | 0xD0000000;
				break;
			case player2Trail:
				//red
				tiles[i]= 0xFC0000 | 0xD0000000;
				break;
			case player1Head:
				//light blue
				tiles[i] = 0x7DB3E3 | 0xFF000000;
				break;
			case player2Head:
				//light red 
				tiles[i] = 0xDE8181 | 0xFF000000;
				break;
			default:
				break;
			}
		}
		//This converts the tiles to pixels to be displayed on the screen
		for (int y = 0; y < height ; y++){
			for (int x = 0 ; x < width ; x++){
				//updates pixels line by line from left to right and up to bottom
				//each tiles has 16x16 pixels
				int tileIndex = (x >> bitshift) + (y >> bitshift) * tilesWidth;
				pixels[ x + y * width ] = tiles[tileIndex];
			}
		}
	}
	
	/**
	 * This functions determines the players new position based on the playerDirection LinkedList. <br>
	 * The LinkedList contains the player's movement direction (NORTH,EAST,WEST,SOUTH).<br>
	 * The method checks if there is an obstacle in the adjacent tile specified by the player's direction.<br>
	 * If not it will modify the mapArray with updated values about the player's head position and tail. If obstacle is detected, the method breaks<br>
	 * 	@param {@link LinkedList} playerDirection
	 * 	@param {@link Player} player
	 * 	@param {@link Map} mapArray
	 * 	@param {@link String} trail
	 * 	@param {@link String} head
	 */
	public void movePlayers(
			LinkedList<Control> playerDirection, 
			Player player,
			Map mapArray ,
			String trail ,
			String head){ 
		
		
		Control playerDir = playerDirection.peekFirst();
		Coordinate playerCoords = player.getPlayerLocation();
		mapArray.setOccupation(playerCoords, trail);
		
		switch(playerDir){
		
		case NORTH:
			playerCoords.setY(playerCoords.getY() -1 );
			if(hasCollided(player, mapArray, playerCoords))
				break;
			mapArray.setOccupation(playerCoords, head);
			break;
			
		case SOUTH:
			playerCoords.setY(playerCoords.getY() + 1 );
			if(hasCollided(player, mapArray, playerCoords))
				break;
			mapArray.setOccupation(playerCoords, head);
			break;
		case WEST:
			playerCoords.setX(playerCoords.getX() -1 );
			if(hasCollided(player, mapArray, playerCoords))
				break;
			mapArray.setOccupation(playerCoords, head);
			break;
		case EAST:
			playerCoords.setX(playerCoords.getX() + 1 );
			if(hasCollided(player, mapArray, playerCoords))
				break;
			mapArray.setOccupation(playerCoords, head);
			break;
		 default: 
			break;
		}
	}
	public int getGameRoundNumber(){ 
		return roundNumber;
	}
	public void incrRoundNumber(){
		roundNumber++;
	}
		
	/**
	 * Checks is player has collided with anything on the gameMap
	 * @return {@link boolean}
	 */
    public static boolean hasCollided(Player player, Map gameMap, Coordinate nextLocation){ 
    	
    		if(gameMap.isOccupied(nextLocation)){
    			player.setCollision(true);
    			return true;
    		}
    		else{
    			return false;
    		}
    }
	/**
	 * handleCollision will first check if there are any collision, and then handle them.
	 * If there are no collisions, then it will return false
	 * If there are collisions, then it will return true, update the {@link GameScore} and end the game
	 * 	@return {@link boolean}
	 */
	public boolean handleCollisions(Player player1, Player player2){ 
		boolean p1HasCollided = player1.getCollision();
		boolean p2HasCollided = player2.getCollision();

		if (!p2HasCollided && !p1HasCollided){
			return false;
		}
		else if(p2HasCollided && p1HasCollided){
			gameStop = true;
			GamePanel.resetGame=true;
			GamePanel.isDraw = true;
			GamePanel.winner = none;
			return false;
			
		}
		else if( (!p1HasCollided) && (p2HasCollided)){ //p1 wins
			gameStop = true;
			gamescore.incrP1Win();
			incrRoundNumber();
			GamePanel.winner = player1;
			
			
		}
		else if ( (p1HasCollided) && (!p2HasCollided) ){ //p2 wins
			gameStop = true;
			gamescore.incrP2Win();
			incrRoundNumber();
			GamePanel.winner = player2;
		}
		if(roundNumber <3){
			GamePanel.resetGame=true;
			return true;
		}
		else{
			GamePanel.endGame = true;
			clearScreen(this.pixels);
			//handle the game results and updates the gamescore. 
			if(this.gamescore.getPlayerOneScore() > this.gamescore.getPlayerTwoScore()) {
				user1.addGameResult(user2, true);
				user2.addGameResult(user1, false);
				GamePanel.winner = player1;
			} else {
				user1.addGameResult(user2, false);
				user2.addGameResult(user1, true);
				GamePanel.winner = player2;
			}
			user1 = UserDataBase.retrieveUser(user1.getUsername());
			user2 = UserDataBase.retrieveUser(user2.getUsername());
			this.gamescore = new GameScore();
			
			return true;
		}
	}
	
	/**
	 * Add a direction to player1's direction LinkedList based on the keyboard input
	 * 	@param {@link Control}
	 */
	public void addP1Direction(Control direction){
		switch(direction){
		
		case NORTH:
			if(!gamePause && !gameStop) {
				//makes sure we don't poll more than 3 times
				if(p1Direction.size() < MAX_KEYINPUT) {
					//checks that the most recent direction is either EAST or WEST
					Control last = p1Direction.peekLast();
					if(last != Control.SOUTH && last != Control.NORTH) {
						p1Direction.addLast(Control.NORTH);
					}
				}
			}
			break;
		
		 case SOUTH:
             if(!gameStop && !gamePause) {
                     //makes sure we don't poll more than 3 times
                     if(p1Direction.size() < MAX_KEYINPUT) {
                             Control last = p1Direction.peekLast();
                             //checks that the most recent direction is either EAST or WEST
                             if(last != Control.NORTH && last != Control.SOUTH) {
                                     p1Direction.addLast(Control.SOUTH);
                             }
                     }
             }
             break;	
			
		 case WEST:
             if(!gameStop && !gamePause) {
                     //makes sure we don't poll more than 3 times
                     if(p1Direction.size() < MAX_KEYINPUT) {
                             Control last = p1Direction.peekLast();
                             //checks that the most recent direction is either North or South
                             if(last != Control.WEST && last != Control.EAST) {
                                     p1Direction.addLast(Control.WEST);
                             }
                     }
             }
             break;	
		//for player 1
    	case EAST:
            if(!gameStop && !gamePause) {
                    //makes sure we don't poll more than 3 times
                    if(p1Direction.size() < MAX_KEYINPUT) {
                            Control last = p1Direction.peekLast();
                            //checks that the most recent direction is either North or South
                            if(last != Control.WEST && last != Control.EAST) {
                                    p1Direction.addLast(Control.EAST);
                            }
                    }
            }
            break;

		} 
	}
	
	public static User getUser1() {
		return user1;
	}
	public static void setUser1(User user1) {
		GameLogic.user1 = user1;
	}
	public static User getUser2() {
		return user2;
	}
	public static void setUser2(User user2) {
		GameLogic.user2 = user2;
	}
	
	/**
	 * Add a direction to player2's direction LinkedList 
	 * @param {@link Control}
	 */
	public void addP2Direction(Control direction){
		switch(direction){
		
		case NORTH:
			if(!gamePause && !gameStop) {
				//makes sure we don't poll more than 3 times
				if(p2Direction.size() < MAX_KEYINPUT) {
					//checks that the most recent direction is either EAST or WEST
					Control last = p2Direction.peekLast();
					if(last != Control.SOUTH && last != Control.NORTH) {
						p2Direction.addLast(Control.NORTH);
					}
				}
			}
			break;
		 case SOUTH:
             if(!gameStop && !gamePause) {
                     //makes sure we don't poll more than 3 times
                     if(p2Direction.size() < MAX_KEYINPUT) {
                             Control last = p2Direction.peekLast();
                             //checks that the most recent direction is either EAST or WEST
                             if(last != Control.NORTH && last != Control.SOUTH) {
                                     p2Direction.addLast(Control.SOUTH);
                             }
                     }
             }
             break;	
		 case WEST:
             if(!gameStop && !gamePause) {
                     //makes sure we don't poll more than 3 times
                     if(p2Direction.size() < MAX_KEYINPUT) {
                             Control last = p2Direction.peekLast();
                             //checks that the most recent direction is either North or South
                             if(last != Control.WEST && last != Control.EAST) {
                                     p2Direction.addLast(Control.WEST);
                             }
                     }
             }
             break;	
    	case EAST:
            if(!gameStop && !gamePause) {
                    //makes sure we don't poll more than 3 times
                    if(p2Direction.size() < MAX_KEYINPUT) {
                            Control last = p2Direction.peekLast();
                            //checks that the most recent direction is either North or South
                            if(last != Control.WEST && last != Control.EAST) {
                                    p2Direction.addLast(Control.EAST);
                            }
                    }
            }
            break;

		} 
	}
		
	/**
	 * return pixel array
	 * @return {@link int[]}
	 */
	public int[] getPixels(){
		return pixels;
	}
	
	/**
	 * return round number
	 * @return {@link int}
	 */
	public int getRoundNumber(){
		return roundNumber;
	}
	/**
	 * clear the screen
	 * @param pixels
	 */
	public void clearScreen(int[] pixels){
		for ( int i = 0 ; i < pixels.length ; i++){
			pixels[i] = 0;
		}
	}
	/**
	 * resets the round number to 0
	 */
	public void resetRoundNumber(){
		roundNumber = 0;
	}
	/**
	 * return user
	 *	@param i if i == 1 then get the first user, else return the 2nd user
	 * 	@return {@link User}
	 */
	public static User getUser(int i){
		if (i == 1 ) return user1 ;
		else return user2;
	}
	/**
	 * sets user
	 *	@param i if i == 1 then get the first user, else return the 2nd user
	 */
	public static void setUser(int i, User u){
	if (i == 1 ) 
		user1 = u ;
	else if ( i == 2 )
		user2 = u;
	else userNone = u ;
	}
	/**
	 * Method used in Junit testing to override the maps, usually the maps are loaded by a different class
	 * @param {@link Map}
	 */
	public void overrideMap(Map map){
		for ( int i = 0 ; i < 3 ; i++){
			allMaps[i] = map;
		}
	}
}
