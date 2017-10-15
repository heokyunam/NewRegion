package test.java.com.overtheinfinite.newregion.campaign;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

import org.junit.Test;

import junit.framework.TestCase;
import main.java.com.overtheinfinite.newregion.campaign.CampaignApp;
import main.java.com.overtheinfinite.newregion.campaign.MessageData;
import main.java.com.overtheinfinite.newregion.tools.DB;
import main.java.com.overtheinfinite.newregion.tools.Logger;
import main.java.com.overtheinfinite.newregion.tools.SQLiteManager;

public class TestCampaignApp extends TestCase{
	@Test
	public void test1() throws SQLException, IOException {
		//Logger.getInstance().addTags("sql", "sql.args", "sql.args.loop", "sql.prep");
		Logger.getInstance().addTags("campaign.load");
		DB.delete(2);
		DB.init(1);

		SQLiteManager ddb = DB.getInstance("ddb.db");
		ddb.execute("update Campaign set campaign_id = ?", 3);
		
		CampaignApp ca = new CampaignApp();
		ca.init();
		ca.clear();
		ca.load();
		Iterator<MessageData> it = ca.iterator();
		MessageData data = it.next();
		String actor = data.getActor();
		assertEquals("chapter1", actor);
		
		assertTrue(!ca.checkCondition());
		
		ddb.execute("update Resource set number = ? where resource_id = ?"
				, 3, 1);
		assertTrue(ca.checkCondition());
		
	}
}
