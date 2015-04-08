package mobile.android.link.link.game;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;

public class Painter extends View{

	private Paint paint;
	private Canvas canvas;
	private Bitmap[] bitmaps;
	private int width = 0;
	private int height = 0;
	
	public Painter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public Painter(Context context, int width, int height, int pics) {
		// TODO Auto-generated constructor stub
		super(context);
		paint = new Paint();
		
		paint.setColor(Color.RED);
		// ���ÿ����
		paint.setAntiAlias(true);
		// �����߿�
		paint.setStrokeWidth(3);
		// ���÷����
		paint.setStyle(Style.STROKE);
		
		this.width = width;
		this.height = height;
		
		//bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		initialBitmaps(pics);
		//canvas = new Canvas();
	}
	/**
	 * ��ʼ��painter��bitmaps
	 */
	private void initialBitmaps(int pics) {
		//SRC�����кܶ���Դ��ѡȡԽ�࣬���Ѷ�Խ��
    	
		bitmaps = new Bitmap[pics];
		for (int i = 0; i < pics; i++) {
			bitmaps[i] = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		}
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
//		canvas.drawBitmap(bitmap, 0, 0, null);
		super.onDraw(canvas);
	}

	/**
	 * ����
	 * 
	 * @return
	 */
	public void drawLine(int startX,int startY, int endX, int endY) {
		canvas.drawLine(startX, startX, startX, startX, paint);
//		return bitmap;
	}
	

}
