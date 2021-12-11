package cornerstone.biz.domain;

/**
 * 
 * @author cs
 *
 */
public class Rectangle {
	//
	public int x;
	public int y;
	public int width;
	public int height;
	public int maxX;
	public int maxY;
	//
	public Rectangle(int x,int y,int width,int height) {
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.maxX=x+width-1;
		this.maxY=y+height-1;
	}
	//
	public boolean contains(Rectangle r) {
		if(r.maxX<x) {
			return false;
		}
		if(r.maxY<y) {
			return false;
		}
		if(r.x>maxX) {
			return false;
		}
		if(r.y>maxY) {
			return false;
		}
		return true;
	}
}
