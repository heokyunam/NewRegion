package main.java.com.overtheinfinite.newregion.campaign;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import main.java.com.overtheinfinite.newregion.tools.DB;
import main.java.com.overtheinfinite.newregion.tools.Logger;
import main.java.com.overtheinfinite.newregion.tools.SQLiteManager;

public class CampaignApp {
	private static final int MISSION_RESOURCE = 1, MISSION_BUILDING = 2, MISSION_WORLDMAP = 3, MISSION_WAR = 4;
	private ArrayList<MessageData> datas = new ArrayList<>();
	private int campaign = -1, mission = -1;
	private SQLiteManager sdb, ddb;
	public void init() throws SQLException {
		sdb = DB.getInstance("sdb.db");
		ddb = DB.getInstance("ddb.db");
	}
	
	public void clear() throws SQLException {
		ResultSet set = ddb.query("select * from Campaign;");
		if(set.isClosed()) {
			//데이터가 존재하지 않음. 첰부터 시작
			ddb.execute("insert into Campaign(user_id, campaign_id, mission_id) values (?,?,?)"
					,1, 1, 1);
		}
		else {
			ddb.execute("update Campaign set user_id = ?, campaign_id = ?, mission_id = ?"
					, 1, 1, 1);
		}
	}
	//처음에 어디까지 진행했는지는 알아야 하니까
	//없으면 새로 만들고
	public boolean load() throws SQLException {
		ResultSet set = ddb.query("select * from Campaign;");
		if(set.isClosed()) {
			//데이터가 존재하지 않음. 첰부터 시작
			ddb.execute("insert into Campaign(user_id, campaign_id, mission_id) values (?,?,?)"
					,1, 1, 1);
		}
		else {
			//해당 부분 미션부터 데이터를 가져오자
			this.campaign = set.getInt("campaign_id");
			this.mission = set.getInt("mission_id");
			
			//message data
			loadMessageData();
		}
		return false;
	}
	
	public void loadMessageData() throws SQLException {
		if(mission == 1) {
			//chapter intro
			ResultSet titleSet = sdb.query("select * from Campaign where campaign_id = ?", campaign);
			String title = titleSet.getString("name");
			datas.add(new MessageData("chapter" + campaign, null,  title));
		}
		ResultSet msgSet = sdb.query("select m.actor, m.message, i.image_name "
				+ "from CampaignMessage m, ImageName i "
				+ "where campaign_id = ? and chapter_id = ? "
				+ "and i.image_id = m.image "
				+ "order by message_id", this.campaign, this.mission);
		while(msgSet.next()) {
			String actor = msgSet.getString("actor");
			String msg = msgSet.getString("message");
			String image = msgSet.getString("image_name");
			MessageData data = new MessageData(actor, image, msg);
			Logger.getInstance().add("campaign.load", data.toString());
			datas.add(data);
		}	
	}
	//퀘스트 조건에 부합하는지 확인한다
	public boolean checkCondition() throws SQLException {
		ResultSet missionSet = sdb.query("select * from CampaignMission where campaign_id = ? and mission_id = ?"
				, this.campaign, this.mission);
		return false;
	}
	
	public boolean checkConditionEach(ResultSet missionSet) throws SQLException {
		int event = missionSet.getInt("event");
		int id = missionSet.getInt("data_id");
		int num = missionSet.getInt("number");
		String sql = null;
		switch(event) {
		//ddb에다 사용할 SQL문 작성
		case MISSION_RESOURCE:
			sql = "select * from Resource where resource_id = ?;";
			break;
		case MISSION_BUILDING:
			sql = "select count(building_id) "
					+ "from Building where building_kind_id = ? "
					+ "group by building_kind_id;";
			break;
		case MISSION_WORLDMAP:
			break;
		case MISSION_WAR:
			break;
		}
		return false;
	}
	//다음 미션으로 넘어갈때 필요한 데이터들을 넣어준다
	public boolean nextLevel() {
		return false; 
	}
	
	public Iterator<MessageData> iterator() {
		return datas.iterator();
	}
}
