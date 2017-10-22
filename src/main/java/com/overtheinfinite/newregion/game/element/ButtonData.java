package main.java.com.overtheinfinite.newregion.game.element;

public class ButtonData {
	private ButtonListener listener;
	private String filename, label;
	private int id;
	
	public ButtonData(ButtonListener listener, String filename, String label, int id) {
		this.listener = listener;
		this.filename = filename;
		this.label = label;
		this.id = id;
	}

	public ButtonListener getListener() {
		return listener;
	}

	public String getFilename() {
		return filename;
	}

	public String getLabel() {
		return label;
	}

	public int getId() {
		return id;
	}
		
	public String toString() {
		return this.id + "/" + this.label + "/" + this.filename;
	}
}
