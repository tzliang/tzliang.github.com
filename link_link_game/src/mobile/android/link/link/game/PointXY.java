package mobile.android.link.link.game;
/**
 * ��¼���꣬��Ҫ��Main����ʹ�ã����ڼ�¼������������ͼƬ�����꣬�Լ�ʵ�ֻ��������ĵ������
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
