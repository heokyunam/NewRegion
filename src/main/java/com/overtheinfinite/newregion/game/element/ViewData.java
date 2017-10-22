package main.java.com.overtheinfinite.newregion.game.element;

import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.com.overtheinfinite.newregion.tools.DB;
import main.java.com.overtheinfinite.newregion.tools.SQLiteManager;

public class ViewData {
	private String filename;
	private int data_id;
	
	public ViewData(String filename, int data) {
		this.filename = filename;
		this.data_id = data;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public int getDataId() {
		return data_id;
	}
	
	public int getResourceValueByDataId() throws SQLException {
		SQLiteManager ddb = DB.getInstance(DB.DB_DYNAMIC);
		ResultSet set = ddb.query("select number from Resource where resource_id = ?;",data_id);
		set.next();
		return set.getInt("number");
	}
	
	public String toString() {
		return this.data_id + "/" + this.filename;
	}
}
