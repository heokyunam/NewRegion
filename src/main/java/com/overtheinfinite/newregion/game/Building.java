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
 * 1. ����� �̵� ���
 * 2. nextTurn �Լ�
 * 3. onTouch �Լ�
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
	 * ddb�� ���� id�� �Է����ָ� �̿� �ʿ��� ��� ��ư �����͸� ��ȯ���ش�
	 * @param views
	 * @param btns
	 * @param building_id ddb�� ���� id
	 * @throws SQLException
	 */
	public void getBuildingData(ViewData[] views, ButtonData[] btns, int building_id) throws SQLException {
		//get static building id	
		String buildingSQL = "select building_kind_id from Building where building_id = ?";
		ResultSet buildingSet = this.ddb.query(buildingSQL, building_id);
		buildingSet.next();
		building_id = buildingSet.getInt("building_kind_id");
		//building_id�� ó�� ����
		
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
			//resource ������ ddb�� ó���ϱ� ������.
			//viewData ������ ���ִ°� ����
		}
	}
	
	//���� ������ �ѱ� �� ������ �� ó���ؾ��ϴ� �нú���� ó��
	public void nextTurn() throws SQLException {
		//1. sdb���� ���� ��� �� nextTurn ����� ����� �̾ƿ�
		String funcSQL = "select function_id, building_id, resource_id, "
				+ " value, add_value"
				+ " from BuildingFunction"
				+ " where type = ?";
		ResultSet funcSet = this.sdb.query(funcSQL, FUNC_PASSIVE);
		
		while(funcSet.next()) {
			Logger.getInstance().add("nextTurn", 
					"resource_id : " + funcSet.getString("resource_id"));
			//2. ddb���� �ش� ����� ���� ������ ã�ƿ�
			String buildingSQL = "select building_id, isBenefitted"
					+ " from Building"
					+ " where building_kind_id = ?";
			//3. �ش� ��� ����
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

	//building_id : ddb�� �������� ���� id
	/**
	 * �������� Ư�� ��ư�� ������ �� �̿� ���� ó�����ִ� ��
	 * @param function_id sdb�� ���� ��� id
	 * @param building_id ddb�� �������� ���� id
	 * @return
	 * @throws SQLException
	 */
	public boolean execute(int function_id, int building_id) throws SQLException {
		//int function_id = data.getId();
		String funcSQL = "select resource_id, value, add_value, type, lack_type"
				+ " from BuildingFunction"
				+ " where function_id = ?";
		ResultSet funcSet = this.sdb.query(funcSQL, function_id);

		//1. add_value�� ���ԵǾ�� �ϴ°�?
		String buildingSQL = "select isBenefitted from Building"
				+ " where building_id = ?";
		ResultSet buildingSet = this.ddb.query(buildingSQL, building_id);
		boolean isBenefitted = (buildingSet.getInt("isBenefitted") == 1)? true : false;
		
		//2. lack_type�� �ִٸ� ������ �����ϴ°�?
		//���ҽ� �ش� ������ 0�̻����� Ȯ�θ� ��. ���Ҵ� 3������ ��.
		while(funcSet.next()) {
			int lack_type = funcSet.getInt("lack_type");
			if(lack_type == 1) {
				int rsrc = funcSet.getInt("resource_id");
				//�ǹ��� �ش� ������ ģȭ������ Ȯ�� �� �� ��ȯ
				int value = getValue(funcSet, isBenefitted);
				
				String lackSQL = "select number from Resource"
						+ " where resource_id = ?";
				ResultSet lackSet = this.ddb.query(lackSQL, rsrc);
				lackSet.next();
				Logger.getInstance().add("execute", 
						"lack resource : " + rsrc
						+ "/" + lackSet.getInt("number")
						+ "+" + value);
				
				//���� ������ �ִ� �� < �ʿ��� ��
				if(lackSet.getInt("number") + value < 0) {
					return false;
				}
			}
		}
		funcSet = this.sdb.query(funcSQL, function_id);
		
		//3. resource ����
		while(funcSet.next()) {
			Logger.getInstance().add("execute", 
					"add resource : " + funcSet.getInt("resource_id"));
			addValue(funcSet, isBenefitted);
		}		
		//4. ����� ��ɽ� ��� ó���� ���ΰ�?
		
		return true;
	}
	
	/**
	 * @param funcSet �߰��ؾ��ϴ� resource_id, value, add_value�� ������ ResultSet
	 * @param isBenefitted �ش� �ǹ��� ���� �̵��� ���� �ִ°�
	 * @throws SQLException
	 */
	public void addValue(ResultSet funcSet, boolean isBenefitted) throws SQLException {
		int rsrc = funcSet.getInt("resource_id");
		//�ǹ��� �ش� ������ ģȭ������ Ȯ�� �� �� ��ȯ
		int value = getValue(funcSet, isBenefitted);
		
		String updateSQL = "update Resource"
				+ " set number = number + ?"
				+ " where resource_id = ?";
		this.ddb.execute(updateSQL, value, rsrc);
	}
	
	/**
	 * �ǹ��� ������ ������ ���� �ִ����� �ľ��� ��Ȯ�� value���� ��ȯ
	 * @param funcSet
	 * @param isBenefitted
	 * @return
	 * @throws SQLException
	 */
	public int getValue(ResultSet funcSet, boolean isBenefitted) throws SQLException {
		int value = funcSet.getInt("value");
		//�ǹ��� ����ģȭ���ΰ�?
		if(isBenefitted) {
			int add_value = funcSet.getInt("add_value");
			value += add_value;
		}
		return value;
	}

	//���� ��� ��ư�� �����ߴ°�?
	//���� ��� ���ุ �̰��� �����.
	@Override
	public boolean onTouch(ButtonData btn) throws SQLException {
		// TODO Auto-generated method stub
		//1. normal, train�� ��
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
		//2. execute�ϸ� ��
		return execute(btn.getFunctionId(), btn.getBuildingId());
	}
}
