package main.java.com.overtheinfinite.newregion.game;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import main.java.com.overtheinfinite.newregion.game.element.ButtonData;
import main.java.com.overtheinfinite.newregion.game.element.ButtonListener;
import main.java.com.overtheinfinite.newregion.game.element.ViewData;
import main.java.com.overtheinfinite.newregion.tools.DB;
import main.java.com.overtheinfinite.newregion.tools.Logger;
import main.java.com.overtheinfinite.newregion.tools.SQLiteManager;

/**
 * [TODO] 
 * 1. 월드맵 이동 기능
 * 2. nextTurn 함수
 * 3. onTouch 함수
 */
public class Building implements ButtonListener {
	private SQLiteManager sdb, ddb;
	private static final int FUNC_PASSIVE = 1, FUNC_INIT = 2, FUNC_NEXTTURN = 3,
			FUNC_NORMAL = 4, FUNC_WORLDMAP = 5, FUNC_TRAIN = 6;
	public Building() throws SQLException {
		this.sdb = DB.getInstance(DB.DB_STATIC);
		this.ddb = DB.getInstance(DB.DB_DYNAMIC);
	}
	
	/**
	 * ddb의 빌딩 id를 입력해주면 이에 필요한 뷰와 버튼 데이터를 반환해준다
	 * @param views
	 * @param btns
	 * @param building_id ddb의 빌딩 id
	 * @throws SQLException
	 */
	public void getBuildingData(ViewData[] views, ButtonData[] btns, int building_id) throws SQLException {
		//get static building id	
		String buildingSQL = "select building_kind_id from Building where building_id = ?";
		ResultSet buildingSet = this.ddb.query(buildingSQL, building_id);
		buildingSet.next();
		building_id = buildingSet.getInt("building_kind_id");
		//building_id를 처음 쓸때
		
		//load about button data
		String btnSQL = "select f.function_id func,"
				+ " f.name name, i.image_name image"
				+ " from BuildingFunction f, Imagename i"
				+ " where f.button_image_id = i.image_id"
				+ " and f.building_id = ?";
		ResultSet btnSet = this.sdb.query(btnSQL, building_id);
		btnSet.next();
		for(int i = 0; btnSet.next(); i++) {
			//Logger.getInstance().add("getBuildingData", btnSet.getString("name"));
			btns[i] = new ButtonData(this,
					btnSet.getString("image"),
					"function",
					btnSet.getInt("func"),
					building_id);
		}
		
		//load about view data
		String viewSQL = "select v.view_id view, r.resource_id rsrc, r.image image"
				+ " from BuildingView v, Resource r"
				+ " where v.resource_id = r.resource_id"
				+ " and v.building_id = ?";
		ResultSet viewSet = this.sdb.query(viewSQL, building_id);
		for(int i = 0; viewSet.next(); i++) {
			views[i] = new ViewData(
					viewSet.getString("image"),
					viewSet.getInt("rsrc"));
			//resource 개수는 ddb라 처리하기 복잡함.
			//viewData 내에서 해주는게 좋음
		}
	}
	
	//다음 턴으로 넘길 때 빌딩들 중 처리해야하는 패시브들을 처리
	public void nextTurn() throws SQLException {
		//1. sdb에서 빌딩 기능 중 nextTurn 기능인 기능을 뽑아옴
		String funcSQL = "select function_id, building_id, resource_id, "
				+ " value, add_value"
				+ " from BuildingFunction"
				+ " where type = ?";
		ResultSet funcSet = this.sdb.query(funcSQL, FUNC_PASSIVE);
		
		while(funcSet.next()) {
			Logger.getInstance().add("nextTurn", 
					"resource_id : " + funcSet.getString("resource_id"));
			//2. ddb에서 해당 기능을 가진 빌딩을 찾아옴
			String buildingSQL = "select building_id, isBenefitted"
					+ " from Building"
					+ " where building_kind_id = ?";
			//3. 해당 기능 실행
			ResultSet buildingSet = this.ddb.query(
					buildingSQL, funcSet.getInt("building_id"));
			while(buildingSet.next()) {
				Logger.getInstance().add("nextTurn", 
						"building_id : " + 
						buildingSet.getString("building_id"));
				boolean isBenefitted = buildingSet.getInt("isBenefitted") == 1;
				addValue(funcSet, isBenefitted);
			}
		}
	}

	//building_id : ddb상 실질적인 빌딩 id
	/**
	 * 빌딩에서 특정 버튼을 눌렀을 때 이에 대해 처리해주는 것
	 * @param function_id sdb상 빌딩 기능 id
	 * @param building_id ddb상 실질적인 빌딩 id
	 * @return
	 * @throws SQLException
	 */
	public boolean execute(int function_id, int building_id) throws SQLException {
		//int function_id = data.getId();
		String funcSQL = "select resource_id, value, add_value, type, lack_type"
				+ " from BuildingFunction"
				+ " where function_id = ?";
		ResultSet funcSet = this.sdb.query(funcSQL, function_id);

		//1. add_value가 포함되어야 하는가?
		String buildingSQL = "select isBenefitted from Building"
				+ " where building_id = ?";
		ResultSet buildingSet = this.ddb.query(buildingSQL, building_id);
		boolean isBenefitted = (buildingSet.getInt("isBenefitted") == 1)? true : false;
		
		//2. lack_type이 있다면 조건을 만족하는가?
		//감소시 해당 개수가 0이상인지 확인만 함. 감소는 3번에서 함.
		while(funcSet.next()) {
			int lack_type = funcSet.getInt("lack_type");
			if(lack_type == 1) {
				int rsrc = funcSet.getInt("resource_id");
				//건물이 해당 지형에 친화적인지 확인 후 값 반환
				int value = getValue(funcSet, isBenefitted);
				
				String lackSQL = "select number from Resource"
						+ " where resource_id = ?";
				ResultSet lackSet = this.ddb.query(lackSQL, rsrc);
				lackSet.next();
				Logger.getInstance().add("execute", 
						"lack resource : " + rsrc
						+ "/" + lackSet.getInt("number")
						+ "+" + value);
				
				//실제 가지고 있는 값 < 필요한 값
				if(lackSet.getInt("number") + value < 0) {
					return false;
				}
			}
		}
		funcSet = this.sdb.query(funcSQL, function_id);
		
		//3. resource 증가
		while(funcSet.next()) {
			Logger.getInstance().add("execute", 
					"add resource : " + funcSet.getInt("resource_id"));
			addValue(funcSet, isBenefitted);
		}		
		//4. 월드맵 기능시 어떻게 처리할 것인가?
		
		return true;
	}
	
	/**
	 * @param funcSet 추가해야하는 resource_id, value, add_value를 소유한 ResultSet
	 * @param isBenefitted 해당 건물이 지역 이득을 보고 있는가
	 * @throws SQLException
	 */
	public void addValue(ResultSet funcSet, boolean isBenefitted) throws SQLException {
		int rsrc = funcSet.getInt("resource_id");
		//건물이 해당 지형에 친화적인지 확인 후 값 반환
		int value = getValue(funcSet, isBenefitted);
		
		String updateSQL = "update Resource"
				+ " set number = number + ?"
				+ " where resource_id = ?";
		this.ddb.execute(updateSQL, value, rsrc);
	}
	
	/**
	 * 건물이 지형의 이점을 보고 있는지를 파악해 정확한 value값을 반환
	 * @param funcSet
	 * @param isBenefitted
	 * @return
	 * @throws SQLException
	 */
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
	//빌딩 기능 실행만 이곳에 따라옴.
	@Override
	public boolean onTouch(ButtonData btn) throws SQLException {
		// TODO Auto-generated method stub
		//1. normal, train만 씀
		String functionSQL = "select type"
				+ " from BuildingFunction"
				+ " where function_id = ?";
		ResultSet functionSet = this.sdb.query(functionSQL, 
				btn.getFunctionId());
		
		while(functionSet.next()) {
			int type = functionSet.getInt("type");
			if(type != FUNC_NORMAL && type != FUNC_TRAIN) {
				return false;
			}
		}
		//2. execute하면 끝
		return execute(btn.getFunctionId(), btn.getBuildingId());
	}
}
