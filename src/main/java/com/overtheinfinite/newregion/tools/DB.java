package main.java.com.overtheinfinite.newregion.tools;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;

public class DB {
	private static Hashtable<String, SQLiteManager> managers = new Hashtable<>();
	public static SQLiteManager getInstance(String name) throws SQLException {
		if(managers.get(name) == null) {
			SQLiteManager manager = new SQLiteManager();
			manager.init(name);
			managers.put(name, manager);
		}
		return managers.get(name);
	}
	public static void init() throws SQLException, IOException {
		SQLiteManager sdb = getInstance("sdb.db");
		sdb.readSQL("sqls.txt");
		SQLiteManager ddb = getInstance("ddb.db");
		ddb.readSQL("dsqls.txt");
	}
	public static boolean delete() throws SQLException, IOException {
		boolean isSuccess = true;
		SQLiteManager sdb = getInstance("sdb.db");
		isSuccess = isSuccess && sdb.delete();
		SQLiteManager ddb = getInstance("ddb.db");
		isSuccess = isSuccess && ddb.delete();
		return isSuccess;
	}
}
