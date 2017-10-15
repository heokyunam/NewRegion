package main.java.com.overtheinfinite.newregion.game;

import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.com.overtheinfinite.newregion.game.element.ButtonData;
import main.java.com.overtheinfinite.newregion.game.element.ButtonListener;
import main.java.com.overtheinfinite.newregion.tools.DB;
import main.java.com.overtheinfinite.newregion.tools.SQLiteManager;

public class Building implements ButtonListener {
	
	//빈 타일을 터치해 빌딩을 건설해야할 때 이에 걸맞는 값으로
	public ButtonData[] getBuildingData() throws SQLException {
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
					allBuildingSet.getString("b.name"),
					allBuildingSet.getString("i.image_name"),
					allBuildingSet.getInt("b.building_id"));
		}
		return btns;
	}
	
	//다음 턴으로 넘길 때 빌딩들 중 처리해야하는 패시브들을 처리
	public void nextTurn() {
		
	}
	
	//빌딩에서 특정 버튼을 눌렀을 때 이에 대해 처리해주는 것
	public boolean execute(ButtonData data, int building_id) {
		
	}

	//내가 어느 데이터를 선택했는가?
	@Override
	public void onTouch(String label) {
		// TODO Auto-generated method stub
		
	}
}
