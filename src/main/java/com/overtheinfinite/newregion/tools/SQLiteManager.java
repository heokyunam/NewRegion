package main.java.com.overtheinfinite.newregion.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLiteManager {
	private Connection conn;
	private String dbFilename;
	private boolean isOpened = false;
	public void init(String db) throws SQLException {
		this.dbFilename = db;
		this.conn = DriverManager.getConnection("jdbc:sqlite:" + dbFilename);
		isOpened = true;
	}
	public boolean delete() {
		try {
			this.conn.close();
			Files.delete(Paths.get(dbFilename));
			Logger.getInstance().add("db.delete", dbFilename + " deletion ");
			init(dbFilename);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	public void close() throws SQLException {
		this.conn.close();
	}
	
	public boolean execute(String sql, Object...args) throws SQLException {
		PreparedStatement ps = makeStatement(sql, args);
		if(ps == null) return false;
		return ps.execute();
	}

	public ResultSet query(String sql, Object...args) throws SQLException {
		PreparedStatement ps = makeStatement(sql, args);
		if(ps == null) return null ;
		return ps.executeQuery();
	}
	
	public void readSQL(String filename) throws IOException, SQLException {
		Logger.getInstance().add("db.readSQL", filename + "->" + dbFilename);
		BufferedReader br = new BufferedReader(new FileReader(filename));
		while(br.ready()) {
			String line = br.readLine();
			execute(line);
		}
		br.close();
	}
	
	public PreparedStatement makeStatement(String sql, Object...args) throws SQLException {
		if(isOpened == false) return null;
		
		PreparedStatement prep = this.conn.prepareStatement(sql);
		Logger.getInstance().add("sql", sql);
		Logger.getInstance().add("sql.args", "args size : " + args.length);
		for(int i = 0; i < args.length; i++) {
			Object arg = args[i];
			if(arg instanceof Integer) {
				Logger.getInstance().add("sql.args.loop", i+1 + " -> " + arg);
				prep.setInt(i+1, (Integer)arg);
			}
			else if(arg instanceof String) {
				prep.setString(i, (String)arg);
			}
		}

		return prep;
	}
	
	static {
		try {
			Class.forName("org.sqlite.JDBC");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static SQLiteManager instance;
	public static SQLiteManager getInstance() {
		if(instance == null) {
			instance = new SQLiteManager();
		}
		return instance;
	} 
}
