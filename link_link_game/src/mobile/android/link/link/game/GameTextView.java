package mobile.android.link.link.game;

import android.content.Context;
import android.widget.TextView;

public class GameTextView extends TextView{

	private int id;
	private boolean cleaned = false;
	public GameTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public GameTextView(Context context, int id) {
		super(context);
		// TODO Auto-generated constructor stub
		this.id = id;
		cleaned = false;
	}
	public int getID() {
		return id;
	}
	public void setID(int value) {
		id = value;
	}
	public void setCleaned(boolean value) {
		cleaned = value;
	}
	public boolean getCleaned() {
		return cleaned;
	}

}
