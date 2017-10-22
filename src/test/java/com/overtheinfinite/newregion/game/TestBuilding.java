package test.java.com.overtheinfinite.newregion.game;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

import main.java.com.overtheinfinite.newregion.game.Building;
import main.java.com.overtheinfinite.newregion.game.element.ButtonData;
import main.java.com.overtheinfinite.newregion.game.element.ViewData;
import main.java.com.overtheinfinite.newregion.tools.DB;
import main.java.com.overtheinfinite.newregion.tools.Logger;
import main.java.com.overtheinfinite.newregion.tools.SQLiteManager;

public class TestBuilding {
	@Test
	public void init() throws SQLException, IOException {
		DB.delete(2);
		DB.init(2);
	}

	@Test
	public void testGetAllBuildingData() throws SQLException {
		Building building = new Building();
		ButtonData[] btns = building.getAllBuildingData();
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
			int id = btn.getId();
			assertEquals(img[i], btn.getFilename());
		}
	}

	@Test
	public void testGetBuildingData() throws SQLException, IOException {
		Building building = new Building();
		ViewData[] views = new ViewData[2];
		ButtonData[] btns = new ButtonData[2];
		
		SQLiteManager ddb = DB.getInstance(DB.DB_DYNAMIC);
		ddb.execute("insert into Building"
				+ "(building_id, building_kind_id, x, y,"
				+ "isBenefitted, map_id)"
				+ "values (2, 1, 3, 5, 1, 1)");
		Logger.getInstance().addTags("getBuildingData");
		building.getBuildingData(views, btns, 2);
		
		assertEquals("resource/sword.png", views[0].getFilename());
		assertEquals("button/sword.png", btns[0].getFilename());		
	}

	@Test
	public void testNextTurn() throws SQLException {
		//SQLiteManager sdb = DB.getInstance(DB.DB_STATIC);
		SQLiteManager ddb = DB.getInstance(DB.DB_DYNAMIC);
		ddb.execute("insert into Building"
				+ "(building_id, building_kind_id, x, y,"
				+ "isBenefitted, map_id)"
				+ " values (3, 6, 1, 1, 0, 1)");
		Logger.getInstance().addTags("nextTurn");
		Building building = new Building();
		building.nextTurn();
		
		ResultSet set = ddb.query("select number from Resource"
				+ " where resource_id = ?", 3); //id = 3이 고기를 의미한다
		set.next();
		assertEquals(1, set.getInt("number"));
	}

	@Test
	public void testExecute() {
		
	}

	@Test
	public void testAddValue() throws SQLException {
		Building building = new Building();
		//functionSet을 추출한다.
		SQLiteManager sdb = DB.getInstance(DB.DB_STATIC);
		ResultSet funcSet = sdb.query("select"
				+ " resource_id, value, add_value"
				+ " from BuildingFunction"
				+ " where building_id = 8");
		//addValue 실행
		building.addValue(funcSet, true);//3증가
		//리소스 데이터가 알맞게 바뀌었는지 확인한다
		SQLiteManager ddb = DB.getInstance(DB.DB_DYNAMIC);
		ResultSet rsrcSet = ddb.query("select number from Resource"
				+ " where resource_id = ?", 4);
		rsrcSet.next();
		assertEquals(3, rsrcSet.getInt("number"));
	}

}
