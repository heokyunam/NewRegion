package test.java.com.overtheinfinite.newregion.campaign;

import java.sql.SQLException;
import java.util.Iterator;

import org.junit.Test;

import junit.framework.TestCase;
import main.java.com.overtheinfinite.newregion.campaign.CampaignApp;
import main.java.com.overtheinfinite.newregion.campaign.MessageData;
import main.java.com.overtheinfinite.newregion.tools.Logger;

public class TestCampaignApp extends TestCase{
	@Test
	public void test1() throws SQLException {
		//Logger.getInstance().addTags("sql", "sql.args", "sql.args.loop", "sql.prep");
		Logger.getInstance().addTags("campaign.load");
		CampaignApp ca = new CampaignApp();
		ca.init();
		ca.clear();
		ca.load();
		Iterator<MessageData> it = ca.iterator();
		MessageData data = it.next();
		String actor = data.getActor();
		assertEquals("chapter1", actor);
	}

}
