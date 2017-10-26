package main.java.com.overtheinfinite.newregion.game.element;

import java.sql.SQLException;

public class ButtonData {
	private ButtonListener listener;
	private String filename, label;
	private int function_id, building_id;
	
	/**
	 * 
	 * @param listener �ش� ��ư�� ��ġ�� ������ �̺�Ʈ�� ó���ϴ� ������
	 * @param filename �̹�����ư�� ��Ÿ�� �̹��� ���� �̸��̴�.
	 * @param label �ַ� ��ư�� Ÿ���� ��Ÿ����.
	 * @param function_id sdb�� function_id
	 * @param building_id ddb�� building_id
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
	 * �ش� ��ư�� ��ġ�� ������ �̺�Ʈ�� ó���ϴ� �����ʸ� �������ش�
	 * @throws SQLException
	 */
	public void listen() throws SQLException {
		this.listener.onTouch(this);
	}
	
	/**
	 * 
	 * @return �ش� ��ư�� ��ġ�� ������ �̺�Ʈ�� ó���ϴ� ������
	 */
	public ButtonListener getListener() {
		return listener;
	}

	/**
	 * 
	 * @return �̹�����ư�� ��Ÿ�� �̹��� ���� �̸��̴�.
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * 
	 * @return �ַ� ��ư�� Ÿ���� ��Ÿ����
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * 
	 * @return function_id sdb�� function_id
	 */
	public int getFunctionId() {
		return function_id;
	}
	/**
	 * @return ddb�� building_id
	 */
	public int getBuildingId() {
		return building_id;
	}

	public String toString() {
		return this.building_id + "/" + this.function_id
				+ "/" + this.label + "/" + this.filename;
	}
}
