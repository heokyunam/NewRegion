package main.java.com.overtheinfinite.newregion.campaign;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import main.java.com.overtheinfinite.newregion.game.AppController;
import main.java.com.overtheinfinite.newregion.tools.Logger;

public class CampaignApp extends AppController {
	private static final int MISSION_RESOURCE = 1, MISSION_BUILDING = 2, MISSION_WORLDMAP = 3, MISSION_WAR = 4;
	private ArrayList<MessageData> datas = new ArrayList<>();
	private int campaign = -1, mission = -1;
	
	public void clear() throws SQLException {
		ResultSet set = ddb.query("select * from Campaign;");
		if(set.isClosed()) {
			//데이터가 존재하지 않음. 첰부터 시작
			ddb.execute("insert into Campaign(user_id, campaign_id, mission_id) values (?,?,?);"
					,1, 1, 1);
		}
		else {
			ddb.execute("update Campaign set user_id = ?, campaign_id = ?, mission_id = ?;"
					, 1, 1, 1);
		}
	}
	//처음에 어디까지 진행했는지는 알아야 하니까
	//없으면 새로 만들고
	public boolean load() throws SQLException {
		ResultSet set = ddb.query("select * from Campaign;");
		if(set.isClosed()) {
			//데이터가 존재하지 않음. 첰부터 시작
			ddb.execute("insert into Campaign(user_id, campaign_id, mission_id) values (?,?,?);"
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
	
	public boolean loadMessageData() throws SQLException {
		datas.clear();
		if(mission == 1) {
			//chapter intro
			ResultSet titleSet = sdb.query("select * from Campaign where campaign_id = ?;", campaign);
			String title = titleSet.getString("name");
			datas.add(new MessageData("chapter" + campaign, null,  title));
		}
		ResultSet msgSet = sdb.query("select m.actor, m.message, i.image_name "
				+ "from CampaignMessage m, ImageName i "
				+ "where campaign_id = ? and chapter_id = ? "
				+ "and i.image_id = m.image "
				+ "order by message_id;", this.campaign, this.mission);
		while(msgSet.next()) {
			String actor = msgSet.getString("actor");
			String msg = msgSet.getString("message");
			String image = msgSet.getString("image_name");
			MessageData data = new MessageData(actor, image, msg);
			Logger.getInstance().add("campaign.load", data.toString());
			datas.add(data);
		}
		return datas.size() > 0;
	}
	//퀘스트 조건에 부합하는지 확인한다
	//마지막 미션 판정일경우 메시지만 뱉은 후 조건이 없으므로 true를 반환한다. 쉽게 넘어갈 수 있다
	public boolean checkCondition() throws SQLException {
		ResultSet missionSet = sdb.query("select * from CampaignMission where campaign_id = ? and mission_id = ?;"
				, this.campaign, this.mission);
		boolean value = true;
		while(missionSet.next()) {
			value = value && checkConditionEach(missionSet);
		}
		return value;
	}
	
	public boolean checkConditionEach(ResultSet missionSet) throws SQLException {
		int event = missionSet.getInt("event");
		int id = missionSet.getInt("data_id");
		int num = missionSet.getInt("number");
		int condition = missionSet.getInt("condition");
		String sql = null;
		ResultSet set = null;
		switch(event) {
		//ddb에다 사용할 SQL문 작성
		//각자 따로 돌아야 할 것 같은데 => 맞춰주려면 data_id가 있어야 함.
		case MISSION_RESOURCE:
			sql = "select number from Resource where resource_id = ?;";
			set = ddb.query(sql, id);
			break;
		case MISSION_BUILDING:
			sql = "select count(building_id) "
					+ "from Building where building_kind_id = ? "
					+ "group by building_kind_id;";
			set = ddb.query(sql, id);
			break;
		case MISSION_WORLDMAP:
			sql = "select history_id from History where event_type = " + AppController.EVENT_LOCALTOWORLD;
			set = ddb.query(sql, id);
			break;
		case MISSION_WAR:
			sql = "select history_id from History where event_type = " + AppController.EVENT_WAR;
			set = ddb.query(sql, id);
			break;
		}
		
		return false;
	}
	//다음 미션으로 넘어갈때 필요한 데이터들을 넣어준다
	public boolean nextLevel() throws SQLException {
		this.mission++;
		if(!loadMessageData()) {
			this.mission = 1;
			loadMessageData();
		}
		//mission이 끝났으므로 히스토리를 초기화 시켜준다
		ddb.execute("delete from History");
		return false; 
	}
	
	public Iterator<MessageData> iterator() {
		return datas.iterator();
	}
}
