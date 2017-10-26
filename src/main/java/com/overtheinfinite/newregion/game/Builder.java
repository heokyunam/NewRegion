package main.java.com.overtheinfinite.newregion.game;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import main.java.com.overtheinfinite.newregion.game.element.ButtonData;
import main.java.com.overtheinfinite.newregion.game.element.ButtonListener;
import main.java.com.overtheinfinite.newregion.game.element.Resource;
import main.java.com.overtheinfinite.newregion.game.element.TileData;
import main.java.com.overtheinfinite.newregion.tools.DB;
import main.java.com.overtheinfinite.newregion.tools.Logger;
import main.java.com.overtheinfinite.newregion.tools.SQLiteManager;

public class Builder implements ButtonListener {
	private SQLiteManager sdb, ddb;
	private TileData tile;

	public Builder() throws SQLException {
		this.sdb = DB.getInstance(DB.DB_STATIC);
		this.ddb = DB.getInstance(DB.DB_DYNAMIC);
	}
	
	public void onTouch(TileData tile) {
		this.tile = tile;
	}

	/**�� Ÿ���� ��ġ�� ������ �Ǽ��ؾ��� �� �̿� �ɸ´� ������
	 * 
	 * @return
	 * @throws SQLException
	 */
	public ButtonData[] getAllBuildingData() throws SQLException {
		String getAllBuildingSQL = "select b.building_id id"
				+ ", b.name name, i.image_name image"
				+ " from Building b, ImageName i"
				+ " where b.button_image_id = i.image_id;";
		ResultSet allBuildingSet = this.sdb.query(getAllBuildingSQL);
		LinkedList<ButtonData> datas = new LinkedList<>();
		while(allBuildingSet.next()) {
			ButtonData temp = new ButtonData(this, 
					allBuildingSet.getString("image"),
					"build",
					-1,
					//sdb��
					allBuildingSet.getInt("id"));
			datas.add(temp);
		}
		ButtonData[] btns = new ButtonData[datas.size()];
		datas.toArray(btns);
		return btns;
	}
	
	public boolean build(ButtonData data, int map_id, int x, int y) throws SQLException {
		//1. kind_id = data.building_id = staticId
		
		//2. kind_id�� ���������� �̸� ���� �ش� ���� �Ǽ� ������ �ľ�����
		String moneySQL = "select b.money money, e.terrain_id t_id"
				+ " from Building b, ExtendedBuilding e"
				+ " where b.building_id = ? "
				+ " and e.building_id = b.building_id";
		ResultSet moneySet = sdb.query(moneySQL, data.getBuildingId());
		int money = moneySet.getInt("money");
		
		//3. �� ���� ����� ���� ������ �ִ°�?
		String havingSQL = "select number from Resource"
				+ " where resource_id = ?";
		ResultSet havingSet = ddb.query(havingSQL, Resource.MONEY);
		int havingMoney = havingSet.getInt("number");
		
		//4. ���� ������ ��������
		if(havingMoney < money) return false;
		
		//5. ���� ���ҽ�Ű��
		String paySQL = "update Resource set number = number - ?"
				+ " where resource_id = ?";
		ddb.execute(paySQL, money, Resource.MONEY);
		
		//6. �ǹ��� �߰�����
		String buildSQL = "insert into Building"
				+ "(building_kind_id, x, y, map_id, isBenefitted) values (?,?,?,?,?);";
		
		boolean isBenefitted = moneySet.getInt("t_id") == tile.getType();
		ddb.execute(buildSQL, data.getBuildingId(), x, y
				, map_id, isBenefitted? 1:0);
		
		return true;
	}
	
	@Override
	public boolean onTouch(ButtonData btn) throws SQLException  {
		// TODO Auto-generated method stub
		return build(btn, tile.getMapId(), tile.getX(), tile.getY());
	}

}
