package main.java.com.overtheinfinite.newregion.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Logger {
	private ArrayList<LogData> logs = new ArrayList<>();
	private ArrayList<String> include = new ArrayList<>();
	
	public void add(String tag, String log) {
		LogData logdata = new LogData(tag, log);
		logs.add(logdata);
		if(include.contains(tag)) {
			System.out.println(logdata);
		}
	}
	
	public void addTags(String...args) {
		for(int i = 0; i < args.length; i++) {
			include.add(args[i]);
		}
	}
	
	public void write(String filename) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
		for(int i = 0; i < logs.size(); i++) {
			bw.write(logs.get(i).toString());
			bw.write("\n");
		}
		bw.close();
	}
	
	private static Logger instance;
	public static Logger getInstance() {
		if(instance == null) {
			instance = new Logger();
		}
		return instance;
	}
	private class LogData {
		public String tag, msg;
		public LogData(String tag, String msg) {
			this.tag = tag;
			this.msg = msg;
		}
		public String toString() {
			return "[" + tag + "] : " + msg;
		}
	}
}
