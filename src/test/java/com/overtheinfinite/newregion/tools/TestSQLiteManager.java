package test.java.com.overtheinfinite.newregion.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

import org.junit.Test;

import junit.framework.TestCase;
import main.java.com.overtheinfinite.newregion.tools.DB;
import main.java.com.overtheinfinite.newregion.tools.Logger;

public class TestSQLiteManager extends TestCase {
	@Test
	public void test1() throws SQLException, IOException {
		Logger.getInstance().addTags("db.delete", "db.readSQL");
		assertTrue(DB.delete());
		DB.init();
	}

}
