package main.java.com.overtheinfinite.newregion.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.java.com.overtheinfinite.newregion.game.element.TileData;

public class TileLoader {
	public TileData[] loadMap(String filename, int map_id) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		List<TileData> list = new ArrayList<>();
		for(int i = 0; br.ready(); i++) {
			String line = br.readLine();
			for(int j = 0; j < line.length(); j++) {
				char ch = line.charAt(j);
				TileData tile = new TileData(j, i, Integer.parseInt(ch + ""), map_id);
				list.add(tile);
			}
		}
		
		TileData[] returnVal = new TileData[list.size()];
		list.toArray(returnVal);
		br.close();
		return returnVal;
	}
}
