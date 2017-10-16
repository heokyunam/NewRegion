package main.java.com.overtheinfinite.newregion.game;

import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.com.overtheinfinite.newregion.game.element.ButtonData;
import main.java.com.overtheinfinite.newregion.game.element.ButtonListener;
import main.java.com.overtheinfinite.newregion.game.element.ViewData;
import main.java.com.overtheinfinite.newregion.tools.DB;
import main.java.com.overtheinfinite.newregion.tools.SQLiteManager;

public class Building implements ButtonListener {
	private static final int FUNC_PASSIVE = 1, FUNC_INIT = 2, FUNC_NEXTTURN = 3,
			FUNC_NORMAL = 4, FUNC_WORLDMAP = 5, FUNC_TRAIN = 6;
	//빈 타일을 터치해 빌딩을 건설해야할 때 이에 걸맞는 값으로
	public ButtonData[] getAllBuildingData() throws SQLException {
		String getAllBuildingSQL = "select b.building_id, b.name, i.image_name"
				+ " from Building b, ImageName i"
				+ " where b.button_image_id = i.image_id;";
		SQLiteManager sdb = DB.getInstance(DB.DB_STATIC);
		ResultSet allBuildingSet = sdb.query(getAllBuildingSQL);
		allBuildingSet.last();
		ButtonData[] btns = new ButtonData[allBuildingSet.getRow()];
		allBuildingSet.beforeFirst();
		for(int i = 0; allBuildingSet.next(); i++) {
			btns[i] = new ButtonData(this, 
					allBuildingSet.getString("i.image_name"),
					"build",
					allBuildingSet.getInt("b.building_id"));
		}
		return btns;
	}
	
	public void getBuildingData(ViewData[] views, ButtonData[] btns, int building_id) throws SQLException {
		//load about button data
		String btnSQL = "select f.function_id id, i.image_name image"
				+ " from BuildingFunction f, Imagename i"
				+ " where f.button_image_id = i.image_id"
				+ " and f.building_id = ?";
		SQLiteManager sdb = DB.getInstance(DB.DB_STATIC);
		ResultSet btnSet = sdb.query(btnSQL, building_id);
		for(int i = 0; btnSet.next(); i++) {
			btns[i] = new ButtonData(this,
					btnSet.getString("image"),
					"function",
					btnSet.getInt("id"));
		}
		
		//load about view data
		String viewSQL = "select v.view_id view, r.resource_id rsrc, r.image image"
				+ " from BuildingView v, Resource r"
				+ " where v.resource_id = r.resource_id"
				+ " and v.building_id = ?";
		ResultSet viewSet = sdb.query(viewSQL, building_id);
		for(int i = 0; viewSet.next(); i++) {
			views[i] = new ViewData(
					viewSet.getString("image"),
					viewSet.getInt("rsrc"));
			//resource 개수는 ddb라 처리하기 복잡함.
			//viewData 내에서 해주는게 좋음
		}
	}
	
	//다음 턴으로 넘길 때 빌딩들 중 처리해야하는 패시브들을 처리
	public void nextTurn() {
		
	}
	
	//빌딩에서 특정 버튼을 눌렀을 때 이에 대해 처리해주는 것
	//building_id : ddb상 실질적인 빌딩 id
	public boolean execute(ButtonData data, int building_id) throws SQLException {
		SQLiteManager sdb = DB.getInstance(DB.DB_STATIC);
		SQLiteManager ddb = DB.getInstance(DB.DB_DYNAMIC);
		int function_id = data.getId();
		String funcSQL = "select resource_id, value, add_value, type, lack_type"
				+ " from BuildingFunction"
				+ " where function_id = ?";
		ResultSet funcSet = sdb.query(funcSQL, function_id);

		//1. add_value가 포함되어야 하는가?
		String buildingSQL = "select isBenefitted from Building"
				+ " where building_id = ?";
		ResultSet buildingSet = ddb.query(buildingSQL, building_id);
		boolean isBenefitted = (buildingSet.getInt("isBenefitted") == 1)? true : false;
		
		//2. lack_type이 있다면 조건을 만족하는가?
		while(funcSet.next()) {
			int lack_type = funcSet.getInt("lack_type");
			if(lack_type == 1) {
				int rsrc = funcSet.getInt("resource_id");
				//건물이 해당 지형에 친화적인지 확인 후 값 반환
				int value = getValue(funcSet, isBenefitted);
				
				String lackSQL = "select number from Resource"
						+ " where resource_id = ?";
				ResultSet lackSet = ddb.query(lackSQL, rsrc);
				lackSet.next();
				
				//실제 가지고 있는 값 < 필요한 값
				if(lackSet.getInt("number") + value > 0) {
					return false;
				}
			}
		}
		funcSet.beforeFirst();
		
		//3. resource 증가
		while(funcSet.next()) {
			int rsrc = funcSet.getInt("resource_id");
			//건물이 해당 지형에 친화적인지 확인 후 값 반환
			int value = getValue(funcSet, isBenefitted);
			
			String updateSQL = "update Resource"
					+ " set number = number + ?"
					+ " where resource_id = ?";
			ddb.execute(updateSQL, value, rsrc);
		}
		
		return true;
	}
	
	public int getValue(ResultSet funcSet, boolean isBenefitted) throws SQLException {
		int value = funcSet.getInt("value");
		//건물이 지형친화적인가?
		if(isBenefitted) {
			int add_value = funcSet.getInt("add_value");
			value += add_value;
		}
		return value;
	}

	//내가 어느 버튼을 선택했는가?
	@Override
	public void onTouch(ButtonData btn) {
		// TODO Auto-generated method stub
		
	}
}
