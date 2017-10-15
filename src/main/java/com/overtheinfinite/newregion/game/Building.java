package main.java.com.overtheinfinite.newregion.game;

import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.com.overtheinfinite.newregion.game.element.ButtonData;
import main.java.com.overtheinfinite.newregion.game.element.ButtonListener;
import main.java.com.overtheinfinite.newregion.tools.DB;
import main.java.com.overtheinfinite.newregion.tools.SQLiteManager;

public class Building implements ButtonListener {
	
	//�� Ÿ���� ��ġ�� ������ �Ǽ��ؾ��� �� �̿� �ɸ´� ������
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
	
	//���� ������ �ѱ� �� ������ �� ó���ؾ��ϴ� �нú���� ó��
	public void nextTurn() {
		
	}
	
	//�������� Ư�� ��ư�� ������ �� �̿� ���� ó�����ִ� ��
	public boolean execute(ButtonData data, int building_id) {
		
	}

	//���� ��� �����͸� �����ߴ°�?
	@Override
	public void onTouch(String label) {
		// TODO Auto-generated method stub
		
	}
}
