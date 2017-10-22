package test.java.com.overtheinfinite.newregion.tools;

import org.junit.Test;

import junit.framework.TestCase;
import main.java.com.overtheinfinite.newregion.tools.Logger;
import main.java.com.overtheinfinite.newregion.tools.TestData;

public class TestTestData extends TestCase {
	@Test
	public void testInsert() {
		Logger.getInstance().addTags("TestData", "TestData.makeTable");
		TestData data = new TestData();
		String insertSQL = data.makeSQL("insert test value 3 id 5");
		System.out.println(insertSQL);
		//hashtable to array�� �Ҷ�, ���ڿ��� ���� ������ ���ĵǾ� ������
		//���� �� ��Ұ� ���� ���� �´�
		String expected = "insert into test(value,id) "
				+ "values ('3','5')";
		System.out.println(expected);
		assertEquals(expected, insertSQL);
	}
	@Test
	public void testUpdate() {
		Logger.getInstance().addTags("TestData", "TestData.makeTable");
		TestData data = new TestData();
		String updateSQL = data.makeSQL("update test value 3 val 10 if>id 5");
		System.out.println(updateSQL);
		//hashtable to array�� �Ҷ�, ���ڿ��� ���� ������ ���ĵǾ� ������
		//���� �� ��Ұ� ���� ���� �´�
		String expected = "update test set value = '3', val = '10' where id = '5'";
		System.out.println(expected);
		assertEquals(expected, updateSQL);
	}
}
