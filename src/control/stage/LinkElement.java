package control.stage;

public class LinkElement {
	public float startX,startY;
	public float endX,endY;
	public String nameOfEnd;
	public String nameOfStart;
	
	public void setStart(float x, float y){
		startX = x;
		startY = y;
	}
	
	public void setEnd(float x, float y){
		endX = x;
		endY = y;
	}
	
	public void setNameE(String name) {
		nameOfEnd = name;
	}
	
	public void setNameS(String name) {
		nameOfStart = name;
	}
}
