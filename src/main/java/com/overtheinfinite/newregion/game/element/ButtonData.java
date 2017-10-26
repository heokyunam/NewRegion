package main.java.com.overtheinfinite.newregion.game.element;

import java.sql.SQLException;

public class ButtonData {
	private ButtonListener listener;
	private String filename, label;
	private int function_id, building_id;
	
	/**
	 * 
	 * @param listener 해당 버튼을 터치시 실행할 이벤트를 처리하는 리스너
	 * @param filename 이미지버튼에 나타낼 이미지 파일 이름이다.
	 * @param label 주로 버튼의 타입을 나타낸다.
	 * @param function_id sdb의 function_id
	 * @param building_id ddb의 building_id
	 */
	public ButtonData(ButtonListener listener, String filename, String label, 
			int function_id, int building_id) {
		this.listener = listener;
		this.filename = filename;
		this.label = label;
		this.function_id = function_id;
		this.building_id = building_id;
	}

	/**
	 * 해당 버튼을 터치시 실행할 이벤트를 처리하는 리스너를 실행해준다
	 * @throws SQLException
	 */
	public void listen() throws SQLException {
		this.listener.onTouch(this);
	}
	
	/**
	 * 
	 * @return 해당 버튼을 터치시 실행할 이벤트를 처리하는 리스너
	 */
	public ButtonListener getListener() {
		return listener;
	}

	/**
	 * 
	 * @return 이미지버튼에 나타낼 이미지 파일 이름이다.
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * 
	 * @return 주로 버튼의 타입을 나타낸다
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * 
	 * @return function_id sdb의 function_id
	 */
	public int getFunctionId() {
		return function_id;
	}
	/**
	 * @return ddb의 building_id
	 */
	public int getBuildingId() {
		return building_id;
	}

	public String toString() {
		return this.building_id + "/" + this.function_id
				+ "/" + this.label + "/" + this.filename;
	}
}
