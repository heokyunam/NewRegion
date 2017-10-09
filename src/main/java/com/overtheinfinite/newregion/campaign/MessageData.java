package main.java.com.overtheinfinite.newregion.campaign;

public class MessageData {
	private String actor, imageFile, msg;

	public MessageData(String actor, String imageFile, String msg) {
		this.msg = msg;
		this.imageFile = imageFile;
		this.actor = actor;
	}
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getImageFile() {
		return imageFile;
	}

	public void setImageFile(String imageFile) {
		this.imageFile = imageFile;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}
	public String toString() {
		return "[" + actor + "," + imageFile + "] : " + msg;
	}
}
