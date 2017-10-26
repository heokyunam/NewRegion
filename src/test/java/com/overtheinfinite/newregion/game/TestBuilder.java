package test.java.com.overtheinfinite.newregion.game;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import junit.framework.TestCase;
import main.java.com.overtheinfinite.newregion.game.Builder;
import main.java.com.overtheinfinite.newregion.game.element.ButtonData;
import main.java.com.overtheinfinite.newregion.game.element.TileData;
import main.java.com.overtheinfinite.newregion.tools.DB;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestBuilder {
	@Test
	public void ainit() throws SQLException, IOException {
		DB.delete(2);
		DB.init(2);
	}

	@Test
	public void testTileData() throws SQLException {
		TileData data = new TileData(1, 1, 1, 1);
		String filename = data.getFilename();
		assertEquals("tile/grass.png", filename);
	}
	@Test
	public void testGetAllBuildingData() throws SQLException {
		Builder builder = new Builder();
		ButtonData[] btns = builder.getAllBuildingData();
		String[] img = {
				"buildingbutton/armory.png",
				"buildingbutton/castle.png",
				"buildingbutton/church.png",
				"buildingbutton/horse_ranch.png",
				"buildingbutton/house.png",
				"buildingbutton/ranch.png",
				"buildingbutton/store.png",
				"buildingbutton/windmill.png"

		};
		for(int i = 0; i < btns.length; i++) {
			ButtonData btn = btns[i];
			assertEquals(img[i], btn.getFilename());
		}
	}
	
	@Test
	public void testBuild() throws SQLException {
		Builder builder = new Builder();
		ButtonData temp = null;
		ButtonData[] btns = builder.getAllBuildingData();
		for(int i = 0; i < btns.length; i++) {
			if(btns[i].getBuildingId() == 6) {
				temp = btns[i];
			}
		}
		assertTrue(temp != null);
		builder.onTouch(new TileData(1,1,1,1));
		builder.onTouch(temp);
		
		String findSQL = "select building_kind_id, isBenefitted "
				+ "from Building where building_id = 2";
		DB ddb = DB.getInstance(DB.DB_DYNAMIC);
		ResultSet findSet = ddb.query(findSQL);
		findSet.next();
		
		assertEquals(6, findSet.getInt("building_kind_id"));
		assertEquals(1, findSet.getInt("isBenefitted"));
		
	}
}
