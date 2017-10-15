package main.java.com.overtheinfinite.newregion.tools;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;

public class DB {
	public static final String DB_DYNAMIC = "ddb.db", DB_STATIC = "sdb.db";
	private static Hashtable<String, SQLiteManager> managers = new Hashtable<>();
	public static SQLiteManager getInstance(String name) throws SQLException {
		if(managers.get(name) == null) {
			SQLiteManager manager = new SQLiteManager();
			manager.init(name);
			managers.put(name, manager);
		}
		return managers.get(name);
	}
	public static void init(String ddbname, int type) throws SQLException, IOException {
		SQLiteManager sdb = getInstance(DB.DB_STATIC);
		SQLiteManager ddb = getInstance(ddbname);
		switch(type) {
		case 1:
			Logger.getInstance().add("DB.init", "load sdb data");
			sdb.readSQL("sqls.txt");
		case 2:
			ddb.readSQL("dsqls.txt");
			break;			
		}
	}
	public static void init(int type) throws SQLException, IOException {
		init("ddb.db", type);
	}
	public static boolean delete(int type) throws SQLException, IOException {
		boolean isSuccess = true;
		switch(type) {
		case 1:
			SQLiteManager sdb = getInstance(DB.DB_STATIC);
			isSuccess = isSuccess && sdb.delete();
		case 2:
			SQLiteManager ddb = getInstance(DB.DB_DYNAMIC);
			isSuccess = isSuccess && ddb.delete();
			break;
		}
		return isSuccess;
	}
}
