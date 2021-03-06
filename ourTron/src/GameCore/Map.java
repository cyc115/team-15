package GameCore;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The Map object is used to store information about the game map. 
 * This includes the height and width, as well as the mapArray which at each point in the array, 
 * contains a MapSign which indicated what is in that position (such as WALL or EMPTY)
 * New game maps can also be generated, stored to a file, a loaded from a file.
 *
 */
public class Map implements Serializable {
	private static final long serialVersionUID = 1L;
	private int difficulty;
	private int height = 50;
	private int width = 75;
	
	//the mapArray stores a MapSign (such as WALL, player1trail, or EMPTY) at each location on the Map
	private MapSign[][] mapArray = new MapSign[width][height];
	
	// exact copy of mapArray but in 1D
	private MapSign[] convertedMapArray = new MapSign[width * height];
	private String mapName;

	/**
	 * These are the options of what the {@link mapArray} is filled with. It can be extended to add more types of powerups in the future.
	 *
	 */
	public enum MapSign {
		WALL, player1Trail, player2Trail, power1, power2, EMPTY, player1Head, player2Head
	}

	/**
	 * Constructs a map using an existing map
	 * @param map
	 */
	public Map(Map map) {
	 	this.difficulty = map.difficulty;
	 	this.height = map.height;
	 	this.width = map.width;
	 	this.mapArray = new MapSign[map.width][map.height];
	 	for (int i = 0; i < width; i++) {
	 		for (int j = 0; j < height; j++) {
	 			this.mapArray[i][j] = map.mapArray[i][j];
	 		}
	 	}
	 	this.convertedMapArray = new MapSign[width * height];
	 	for(int i = 0; i < map.convertedMapArray.length; i++)
	 	this.convertedMapArray[i] = map.convertedMapArray[i];
	 	this.mapName = map.mapName;
	 	convert2Dto1D();
	 }

	public Map(String name, boolean generate) {
		this.difficulty = 1;
		this.mapName = name;
		for (MapSign[] a : mapArray)
			for (int i = 0; i < a.length; i++)
				a[i] = MapSign.EMPTY;
		convert2Dto1D();
	}

	/**
	 * Constructs a blank map 
	 */
	public Map() {
		this.difficulty = 1;
		this.mapName = "blankMap.map";
		generateEmptyMap();
		convert2Dto1D();
	}

	public static void main(String[] args) {
		generateDefaultMaps();
	}

	/**
	 * This method simply generates the three maps the teacher requested. It adds the walls as requested. 
	 */
	private static void generateDefaultMaps() {
		Map map1 = new Map("basicMap1", true);
		map1.height = 50;
		map1.width = 75;
		Map.createMap(map1, "res/data/basicMap1.map");

		Map map2 = new Map("basicMap2", true);
		map2.height = 50;
		map2.width = 75;
		map2.generateWalls(new Point(15, 20), new Point(25, 30));
		map2.generateWalls(new Point(50, 20), new Point(60, 30));
		Map.createMap(map2, "res/data/basicMap2.map");

		Map map3 = new Map("basicMap3", true);
		map3.height = 50;
		map3.width = 75;
		map3.generateWalls(new Point(05, 25), new Point(25, 45));
		map3.generateWalls(new Point(30, 20), new Point(45, 30));
		map3.generateWalls(new Point(50, 05), new Point(70, 25));
		Map.createMap(map3, "res/data/basicMap3.map");

	}
	/**
	 * This method sets all the tiles in the map to Empty
	 */
	public void generateEmptyMap(){
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				this.mapArray[i][j] = MapSign.EMPTY;
			}
		}
		
	}
	
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public MapSign[][] getMapArray() {
		return mapArray;
	}

	public void setMapArray(MapSign[][] mapArray) {
		this.mapArray = mapArray;
	}

	public MapSign[] getConvertedMapArray() {
		return convertedMapArray;
	}

	public void setConvertedMapArray(MapSign[] convertedMapArray) {
		this.convertedMapArray = convertedMapArray;
	}

	/**
	 * Generates blocks of WALL on the mapArray
	 * @param bottomLeft
	 * @param topRight
	 */
	private void generateWalls(Point bottomLeft, Point topRight) {
		for (int i = bottomLeft.x; i <= topRight.x; ++i)
			for (int j = bottomLeft.y; j < topRight.y; ++j) {
				this.mapArray[i][j] = MapSign.WALL;
			}
	}

	/**
	 * This with generate random blocks of walls into the map. It makes between
	 * 3-11 blocks
	 */
	public void generateRandomWalls() {
		Random randomGenerator = new Random();
		int numberOfBlocks = randomGenerator.nextInt(3) + 3;

		for (int i = 0; i < numberOfBlocks; i++) {
			int blockWidth = randomGenerator.nextInt(30) + 1;
			int blockHeight = randomGenerator.nextInt(30) + 1;
			int randomX = randomGenerator.nextInt(75 - blockWidth) + 1;
			int randomY = randomGenerator.nextInt(50 - blockHeight) + 1;
			for (int j = randomX; j < randomX + blockWidth; j++) {
				for (int k = randomY; k < randomY + blockHeight; k++) {
					this.mapArray[j][k] = MapSign.WALL;
					// this.mapArray[k][j] = MapSign.WALL;
				}
			}
		}
	}
	
	

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
	/**
	 * checks if the tile in 2D mapArray is occupied (not EMPTY)
	 * 	@param {@link Coordinate} coordinate
	 *	@return {@link boolean}
	 */
	public boolean isOccupied(Coordinate coordinate) {
		int x = coordinate.getX();
		int y = coordinate.getY();
		if (x >= 0 && x < width && y >= 0 && y < height) {
			if (mapArray[x][y] != MapSign.EMPTY) {
				return true;
			} else {
				return false;
			}
		} else
			return true;
	}
	/**
	 * checks if the tile in 2D mapArray is occupied ( not EMPTY)
	 * 	@param {@link int} x : x coordinate 
	 * 	@param {@link int} y : y coordinate 
	 * 	@return {@link boolean}
	 */
	public boolean isOccupied(int x, int y) {
		if (mapArray[x][y] != MapSign.EMPTY) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * returns the MapSign Enum that is contained in each tile from the 2D mapArray
	 * 	@param {@link Coordinate} coordinate
	 * 	@return {@link MapSign}
	 */
	public MapSign getOccupation(Coordinate coordinate) {
		int x = coordinate.getX();
		int y = coordinate.getY();
		return mapArray[x][y];
	}
	/**
	 * returns the MapSign Enum that is contained in each tile from the 1D convertedMapArray
	 * 	@param  {@link int} i : position of the tile in the array
	 * 	@return {@link MapSign}
	 */
	public MapSign getOccupation1D(int i) {
		return convertedMapArray[i];
	}
	/**
	 * sets what each tile of the mapArray contains.
	 * 	@param {@link Coordinate} coordinate : location of the tile (x,y position)
	 * 	@param {@link MapSign} attribute  : what is contained in the tile
	 */
	public void setOccupation(Coordinate coordinate, String attribute) {
		int x = coordinate.getX();
		int y = coordinate.getY();
		MapSign enumAttribute = MapSign.valueOf(attribute);
		switch (enumAttribute) {
		case WALL:
			mapArray[x][y] = MapSign.WALL;
			break;
		case player1Trail:
			mapArray[x][y] = MapSign.player1Trail;
			break;
		case player2Trail:
			mapArray[x][y] = MapSign.player2Trail;
			break;
		case EMPTY:
			mapArray[x][y] = MapSign.EMPTY;
			break;
		case player1Head:
			mapArray[x][y] = MapSign.player1Head;
			break;
		case player2Head:
			mapArray[x][y] = MapSign.player2Head;
			break;
		default: {
			throw new IllegalArgumentException("Can't handle " + attribute);
		}
		}
		convert2Dto1D();

	}

	/**
	 * This method writes the input map to a location specified. The map is
	 * stored as a serialized object at the location specified.
	 * <p>
	 * It is assumed this method will receive a valid file location, there is no
	 * guarantee of its performance outside this case.
	 * 
	 * @param map
	 *            A {@link Map} file to write to the hard disk.
	 * @param filename
	 *            A String object which contains the directory to write to.
	 */
	public static void createMap(Map map, String filename) {
		try {
			FileOutputStream fileOut = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(map);
			out.close();
			fileOut.close();
		} catch (IOException i) {
			i.printStackTrace();
		}
	}


	/**
	 * This method will load a map from a file. Specifically, it takes a String
	 * location path and loads that into this Map object.
	 * <p> This method is only guaranteed to work if the map either exists or does not exist. It is not guaranteed if the .map file is not the expected serialized file. 
	 * 
	 * @param filename
	 *            A String object with the location of the file to load up. 
	 */
	public void loadMapFromFile(String filename) {
		try {
			FileInputStream fileIn = new FileInputStream(filename);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			setMap((Map) in.readObject());
			in.close();
			fileIn.close();
		} catch (ClassNotFoundException c) {
			c.printStackTrace();
			return;
		} catch (IOException i) {
			i.printStackTrace();
			return;
		}
	}

	/**
	 * This method writes this Map object to a file in a serialized fashion. The location of
	 * output is defined.
	 * <p>This map assumes this map file is a valid map file, which should be true by type-safety in java. 
	 * @param filename	A String object containing the location to write to. 
	 */
	public void saveMapToFile(String filename) {
		try {
			FileOutputStream fileOut = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(this);
			out.close();
			fileOut.close();
		} catch (IOException i) {
			i.printStackTrace();
		}
	}
	/**
	 * return the map's name
	 *	@return String
	 */
	public String getMapName() {
		return mapName;
	}
	/**
	 * sets the map's name
	 *	@param mapName
	 */
	public void setMapName(String mapName) {
		this.mapName = mapName;
	}
	/*
	 * sets the Map
	 */
	private void setMap(Map map) {
		this.difficulty = map.difficulty;
		this.mapArray = map.mapArray;
		this.mapName = map.mapName;
	}
	/**
	 * return the 2D mapArray
	 *	@return MapSign[][] 
	 */
	public MapSign[][] getMap() {
		return this.mapArray;
	}
	/**
	 * gets the size of the map
	 *	@return int
	 */
	public int getMapSize() {
		return convertedMapArray.length;
	}

	/**
	 * Converts the 2D mapArray into a 1D mapArray
	 * the result is stored in convertedMapArray
	 */
	public void convert2Dto1D() {
		
		List <MapSign> tempList =new ArrayList<MapSign>();
		for(int i = 0 ; i < 50 ; i++){
			for(int j = 0 ; j < 75 ; j++){
				tempList.add( mapArray[j][i]);
			}
		}
		
		for(int i = 0 ; i < convertedMapArray.length ; i++){
			convertedMapArray[i] = tempList.get(i);
		}
	}

}
