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
	 * sdb,ddb���� ��ü������ �־��ְ� ����ϱ� ���ϵ��� �Ѵ�
	 * @throws SQLException
	 * @throws IOException
	 */
	public void init() throws SQLException, IOException {
		sdb = DB.getInstance(DB.DB_STATIC);
		ddb = DB.getInstance(DB.DB_DYNAMIC);
	}
	
	/**
	 * ���ӿ��� Ư�� �̺�Ʈ�� �Ͼ ��� �̸� ȣ�����ش�.
	 * @param event
	 * @ddb History
	 * @throws SQLException
	 */
	public void onEvent(int event, int arg) throws SQLException {
		ddb.execute("insert into History(event_type,arg) values(?,?)", event,arg); 
	}
}
