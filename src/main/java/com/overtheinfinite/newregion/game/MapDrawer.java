package main.java.com.overtheinfinite.newregion.game;

import java.sql.SQLException;

import main.java.com.overtheinfinite.newregion.tools.DB;

public class MapDrawer {
	private Building building;
	private Builder builder;
	private DB sdb, ddb;
	
	//connected to uidrawers
	public MapDrawer(Building building, Builder builder) throws SQLException {
		this.builder = builder;
		this.building = building;
		sdb = DB.getInstance(DB.DB_STATIC);
		ddb = DB.getInstance(DB.DB_DYNAMIC);
	}
	
	public void draw() {
		
	}
	public void touch(float x, float y) {
		
	}
	public void update() {
		
	}
}
