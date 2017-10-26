package main.java.com.overtheinfinite.newregion.game;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.com.overtheinfinite.newregion.tools.DB;
import main.java.com.overtheinfinite.newregion.tools.SQLiteManager;

public class AppController {
	public static final int EVENT_LOCALTOWORLD = 1, EVENT_WORLDTOLOCAL = 2, EVENT_WAR = 3;
	protected SQLiteManager sdb, ddb;
	/**
	 * sdb,ddb들을 객체변수에 넣어주고 사용하기 편하도록 한다
	 * @throws SQLException
	 * @throws IOException
	 */
	public void init() throws SQLException, IOException {
		sdb = DB.getInstance(DB.DB_STATIC);
		ddb = DB.getInstance(DB.DB_DYNAMIC);
	}
	
	/**
	 * 게임에서 특정 이벤트가 일어날 경우 이를 호출해준다.
	 * @param event
	 * @ddb History
	 * @throws SQLException
	 */
	public void onEvent(int event, int arg) throws SQLException {
		ddb.execute("insert into History(event_type,arg) values(?,?)", event,arg); 
	}
}
