package mobile.android.link.link.game;

import android.content.Context;
import android.widget.Button;

public class GameButton extends Button{
	public int id = 0;
	public String imageSrc = "";

	public GameButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public GameButton(Context context, int id) {
		this(context);
		this.id = id;		
		// TODO Auto-generated constructor stub
	}

}
