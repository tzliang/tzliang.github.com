package mobile.android.link.link.game;
/**
 * 记录坐标，主要在Main里面使用，用于记录两个能消除的图片的坐标，以及实现画线消除的点的坐标
 * @author tzliang
 *
 */
public class PointXY {
	private int x = 0;
	private int y = 0;
	public PointXY(int x, int y) {
		this.setX(x);
		this.setY(y);
	}
	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}
	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}
	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}
	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}
}
