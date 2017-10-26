package test.java.com.overtheinfinite.newregion.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import main.java.com.overtheinfinite.newregion.game.Builder;
import main.java.com.overtheinfinite.newregion.game.Building;
import main.java.com.overtheinfinite.newregion.game.element.ButtonData;
import main.java.com.overtheinfinite.newregion.game.element.Resource;
import main.java.com.overtheinfinite.newregion.game.element.ViewData;
import main.java.com.overtheinfinite.newregion.tools.DB;
import main.java.com.overtheinfinite.newregion.tools.Logger;
import main.java.com.overtheinfinite.newregion.tools.SQLiteManager;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestBuilding {
	@Test
	public void ainit() throws SQLException, IOException {
		DB.delete(2);
		DB.init(2);
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
	public void testExecute() throws SQLException {
		Building building = new Building();
		SQLiteManager ddb = DB.getInstance(DB.DB_DYNAMIC);
		ddb.execute("insert into Building"
				+ "(building_id, building_kind_id, x, y,"
				+ "isBenefitted, map_id)"
				+ " values (4, 1, 4, 1, 0, 1)");
		ddb.execute("update Resource set number = number + 100"
				+ " where resource_id = 2");
		Logger.getInstance().addTags("execute");
		boolean result = building.execute(5, 4);
		
		assertTrue(result);
		ResultSet set = ddb.query("select resource_id, number"
				+ " from Resource"
				+ " where resource_id = 6");
		
		set.next();
		assertEquals(1, set.getInt("number"));
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
	
	@Test
	public void testOnTouch() throws SQLException {
		Building building = new Building();
		ButtonData data = new ButtonData(building,
				"test/test.png",
				"train",
				1, 1); 
		SQLiteManager ddb = DB.getInstance(DB.DB_DYNAMIC);
		ddb.execute("update Resource set number = 10"
				+ " where resource_id = ?", Resource.MEAT);//고기
		
		building.onTouch(data);
		
		ResultSet set = ddb.query("select number from Resource "
				+ "where resource_id in (?,?) order by resource_id", 
				Resource.SOLDIER, Resource.MEAT);
		
		int[] expected = {1, 9};
		for(int i = 0; i < 2; i++) {
			set.next();
			assertEquals(expected[i], set.getInt("number"));
		}
	}

}
