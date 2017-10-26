package main.java.com.overtheinfinite.newregion.game.element;

import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.com.overtheinfinite.newregion.tools.DB;
import main.java.com.overtheinfinite.newregion.tools.SQLiteManager;

public class TileData {
	public static final int GRASS = 1, DIRT = 2, STONE = 3, SAND = 4;
	public static final int TILESIZE = 30;
	private int x, y, type;
	private int map_id;

	public TileData(int x, int y, int type, int map_id) {
		super();
		this.x = x;
		this.y = y;
		this.type = type;
		this.map_id = map_id;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public int getType() {
		return type;
	}

	public int getMapId() {
		return map_id;
	}
	
	public String getFilename() throws SQLException {
		SQLiteManager sdb = DB.getInstance(DB.DB_STATIC);
		String imageSQL = "select i.image_name image"
				+ " from ImageName i, Terrain t "
				+ " where i.image_id = t.image"
				+ " and terrain_id = ?";
		ResultSet imageSet = sdb.query(imageSQL, type);
		imageSet.next();
		return imageSet.getString("image");
	}
}
