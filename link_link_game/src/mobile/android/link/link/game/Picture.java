package mobile.android.link.link.game;
/**
 * ��������ÿ��ͼƬ������
 * ͼƬ��Main.SRC�е�id��picID
 * ͼƬ��λ�ã�location
 * ͼƬ�Ƿ������cleaned��Ĭ��false
 * ͼƬ�Ŀ�ȣ�width
 * ͼƬ�ĸ߶ȣ�height
 * @author tzliang
 *
 */
public class Picture {
	/**
	 * ���ͼƬid,��ʾRSC������λ��
	 */
	private int picID;
	/**
	 * ���Ƿ����
	 */
	private boolean cleaned = false;
	/**
	 * �������λ��
	 */
	private int location = 0;
	/**
	 * ������λ��ͼƬ�Ŀ��
	 */
	private int width = 0;
	/**
	 * ������λ��ͼƬ�ĸ߶�
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
