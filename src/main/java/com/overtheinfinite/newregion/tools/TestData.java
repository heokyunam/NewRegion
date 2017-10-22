package main.java.com.overtheinfinite.newregion.tools;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

/*
 * insert format
 * insert table id ? value ?
 * 
 * update format
 * update table value ? if>id ?
 * if는 무조건 마지막이어야 하며 하나만 사용가능
 * 
 * delete format
 * delete table id ?
 * 
 * select
 * select table value ? if>id ?
 */
public class TestData {
	private static final String FORMAT_TYPE = "format_type",
			TABLE_NAME = "table_name";
	public boolean execute(String str, SQLiteManager db) throws SQLException {
		String sql = makeSQL(str);
		if(sql == null) return false;
		db.execute(sql);
		return true;
	}
	
	public String makeSQL(String str) {
		Hashtable<String, String> table = makeTable(str);
		String format_type = table.get(FORMAT_TYPE);
		
		String sql;
		switch(format_type.charAt(0)) {
		case 'i':
			sql = insert(table);
			break;
		case 'u':
			sql = update(table);
			break;/*
		case 'd':
			break;
		case 's':
			break;*/
		default:
			return null;
		}
		return sql;
	}
	/*
	 * 이걸 추가하는게 도움이 될까?
	 * TestData.execute("insert id : 3, value : 5");
	 * TestData.execute("update id : 5, if>value : 3");
	*/
	public String update(Hashtable<String, String> table) {
		StringBuilder sb = new StringBuilder();
		sb.append("update ");
		sb.append(table.get(TABLE_NAME));
		sb.append(" set ");
		Object[] keys = table.keySet().toArray();
		int i;
		boolean needToInputWhere = true;
		
		//키 값에 대해서 정렬을 해야할 필요를 느낌
		for(i = 0; i < keys.length; i++) {
			if(keys[i].equals(TABLE_NAME) || keys[i].equals(FORMAT_TYPE))
				continue;
			String key = keys[i].toString();
			if(!key.startsWith("if>")) {
				sb.append(key);
				sb.append(" = ");
				sb.append(table.get(keys[i]));
			}
			else {
				key = key.replaceFirst("if>", "");
				if(needToInputWhere) {
					sb.append(" where ");
					needToInputWhere = false;
				}
				sb.append(key);
				sb.append(" = ");
				sb.append(table.get(keys[i]));
			}
			if(i != keys.length - 1 && !keys[i+1].toString().startsWith("if>")) {
				sb.append(", ");
			}
		}
		
		return sb.toString();
	}
	public String insert(Hashtable<String, String> table) {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into ");
		sb.append(table.get(TABLE_NAME));
		sb.append("(");
		Object[] keys = table.keySet().toArray();
		int i;
		for(i = 0; i < keys.length; i++) {
			if(keys[i].equals(TABLE_NAME) || keys[i].equals(FORMAT_TYPE))
					continue;
			sb.append(keys[i]);
			if(i != keys.length-1)
				sb.append(",");
		}
		sb.append(") values (");
		
		for(i = 0; i < keys.length; i++) {
			if(keys[i].equals(TABLE_NAME) || keys[i].equals(FORMAT_TYPE))
					continue;
			sb.append(table.get(keys[i]));
			if(i != keys.length-1)
				sb.append(",");
		}
		sb.append(")");
		return sb.toString();
	}
	
	public Hashtable<String, String> makeTable(String str) {
		Hashtable<String, String> table = new Hashtable<>();
		String[] splittedStr = str.split(" ");
		table.put(FORMAT_TYPE, splittedStr[0]);
		table.put(TABLE_NAME, splittedStr[1]);
		for(int i = 2; i < splittedStr.length; i+=2) {
			// format => id : ?
			String line = splittedStr[i];
			Logger.getInstance().add("TestData.makeTable", line);
			table.put(splittedStr[i], "'" + splittedStr[i+1] + "'");			
		}
		return table;
	}
}
