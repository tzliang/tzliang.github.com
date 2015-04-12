package mobile.android.link.link.game;
/**
 * 连连看中每个图片，包含
 * 图片在Main.SRC中的id：picID
 * 图片的位置：location
 * 图片是否被清除：cleaned，默认false
 * 图片的宽度：width
 * 图片的高度：height
 * @author tzliang
 *
 */
public class Picture {
	/**
	 * 点的图片id,表示RSC的数组位置
	 */
	private int picID;
	/**
	 * 点是否被清除
	 */
	private boolean cleaned = false;
	/**
	 * 点的行列位置
	 */
	private int location = 0;
	/**
	 * 点所在位置图片的宽度
	 */
	private int width = 0;
	/**
	 * 点所在位置图片的高度
	 */
	private int height = 0;
	public Picture( int picID) {
		// TODO Auto-generated constructor stub
		this.picID = picID;
		cleaned = false;
	}
	public int getPicID() {
		return picID;
	}
	public void setPicID(int value) {
		picID = value;
	}
	public void setCleaned(boolean value) {
		cleaned = value;
	}
	public boolean getCleaned() {
		return cleaned;
	}
	public int getLocation() {
		return location;
	}
	public void setLocation(int location) {
		this.location = location;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
}
