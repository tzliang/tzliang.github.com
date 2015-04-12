package mobile.android.link.link.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;


//检查控件的初始化顺序
public class Main extends Activity{
	
	private static final int ROW = 11;
	private static final int COLUMN = 8;
	private static final int MAX_TIME = 180;//设置最大时间
	
	//屏幕会分层,高分为ROW + ROWEXTENSION，宽分为COLUMN + COLUMNEXTENSION
	private static final int ROWEXTENSION = 3;
	private static final int COLUMNEXTENSION = 1;
	
	private Button pauseButton;
	private Button refreshButton;
	private Button restartButton;
	private Button hintButton;
	private Handler progressHandler;
	private boolean paused = false;
	private int progress = 0;
	private ProgressBar progressBar;
	private Picture[][] pictures = new Picture[ROW][COLUMN];
	private int[][] ids = new int[ROW + 2][COLUMN + 2];
	private int displayWidth;
	private int displayHeight;
	
	private int picWidth;
	private int picHeight;
	
	private int pairs = ROW * COLUMN / 2;
	
	private int clickCount = 0;
	/**
	 * 记录点击的行坐标
	 */
	private static int row[] = new int[2];
	/**
	 * 记录点击的列坐标
	 */
	private static int col[] = new int[2];
	//需要修改，不需要静态变量
	static{
		row[0] = row[1] = -1;
		col[0] = col[1] = -1;
	}
	
	private GameView gameView;
	private Bitmap[] bitmaps;
	
	//
	private Map<Integer, Integer> map;
	private List<Picture> []pictureLists;
	
	private List<PointXY> lines = new LinkedList<PointXY>();
	
	public static final int []SRC = new int[]{
		R.drawable.pic_1,R.drawable.pic_2,R.drawable.pic_3,R.drawable.pic_4,R.drawable.pic_5,
		R.drawable.pic_6,R.drawable.pic_7,R.drawable.pic_8,R.drawable.pic_9,R.drawable.pic_10,
		R.drawable.pic_11,R.drawable.pic_12,R.drawable.pic_13,R.drawable.pic_14,R.drawable.pic_15,
		R.drawable.pic_16,R.drawable.pic_17,R.drawable.pic_18,R.drawable.pic_19,R.drawable.pic_20,
		R.drawable.pic_21,R.drawable.pic_22,R.drawable.pic_23,R.drawable.pic_24,R.drawable.pic_25,
		R.drawable.pic_26,R.drawable.pic_27,R.drawable.pic_28,R.drawable.pic_29,R.drawable.pic_30,
		R.drawable.pic_31,R.drawable.pic_32,R.drawable.pic_33,R.drawable.pic_34,R.drawable.pic_35,
		R.drawable.pic_36,R.drawable.pic_37
	};
	
    @SuppressWarnings("deprecation")
	@SuppressLint({ "HandlerLeak", "InflateParams" }) 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getScreenSize();       
        
        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_main, null);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setBackgroundResource(R.drawable.background1);
        
        initialProgressBar();        
        progress = progressBar.getMax();        
        initialProgressHandler();
        
        layout.addView(progressBar); 
        
//        initialTableLayout();        
//        layout.addView(tableLayout);
        initialGameViewAndBitmaps();
        layout.addView(gameView,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, displayHeight * (ROW + ROWEXTENSION - 2)/ (ROW + ROWEXTENSION)));
        
        LinearLayout horizontalLayout = new LinearLayout(this);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        
        initialPauseButton();
        horizontalLayout.addView(pauseButton);  
        
        initialRefreshButton();        
        horizontalLayout.addView(refreshButton); 
        
        initialHintButton();
        horizontalLayout.addView(hintButton);
        
        initialRestartButton();
        horizontalLayout.addView(restartButton); 
        
        
        layout.addView(horizontalLayout);
        setContentView(layout);
        //layout.setOnFocusChangeListener(this);
        //界面初始化完毕，下面初始化数据
        initialID();
//        gameView.invalidate();
//        painter = new Painter(this, displayWidth, displayHeight);
    }
    /**
     * 得到屏幕宽度displayWidth，高度displayHeight
     */
    private void getScreenSize() {
    	DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        displayWidth = dm.widthPixels;
        displayHeight = dm.heightPixels;
        picWidth = displayWidth / (COLUMN + COLUMNEXTENSION);
    	picHeight = displayHeight / (ROW + ROWEXTENSION);
	}
    /**
     * 初始化progressBar
     */
    private void initialProgressBar() {
    	progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(MAX_TIME);
        progressBar.setProgress(MAX_TIME);   
        Drawable progressDrawable=getResources().getDrawable(R.drawable.progressbar_color);
        //progressBar.setBackgroundResource(R.drawable.progressbar_color);
        progressBar.setProgressDrawable(progressDrawable);
	}
    /**
     * 初始化progressHandler
     */
    @SuppressLint("HandlerLeak") 
    private void initialProgressHandler() {
    	progressHandler = new Handler(){
        	public void handleMessage(Message msg) {
				super.handleMessage(msg);
				//pauseButton.setText(String.valueOf(progress));
				if (progress == 0) {
					Builder builder = new AlertDialog.Builder(Main.this).setIcon(R.drawable.lose).setTitle("提示").
					setMessage("你输了！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							restart();
						}
					});
					builder.setCancelable(false);
					builder.show();
				}
				else{
					if (!paused) {
						progress --;
						progressBar.incrementProgressBy(-1);
						progressHandler.sendEmptyMessageDelayed(1, 1000);
					}
				}
			}
    	};
    	progressHandler.sendEmptyMessage(1);
	}
    /**
     * 初始化painter和bitmaps
     */
	private void initialGameViewAndBitmaps() {
		generatePoints();
    	bitmaps = new Bitmap[SRC.length];
    	Resources res = getResources();    	
    	//需要修改，图片大小正方形还是矩形？？？
    	for (int i = 0; i < SRC.length; i++) {
    		Bitmap bitmap = Bitmap.createBitmap(picWidth,picHeight,Bitmap.Config.ARGB_8888);
    		Canvas canvas = new Canvas(bitmap);
    		Drawable drawable = res.getDrawable(SRC[i]);
    		drawable.setBounds(0, 0, picWidth, picHeight);
    		drawable.draw(canvas);
            bitmaps[i] = bitmap;
		}
    	generatePoints();
    	gameView = new GameView(this,displayWidth,displayHeight * (ROW + ROWEXTENSION - 2)/ (ROW + ROWEXTENSION));
	}
    /**
     * 生成图片背景
     */
    @SuppressWarnings("unchecked")
	private void generatePoints() {
    	//SRC里面有很多资源，选取越多，则难度越大
    	//修改len，要修改后面的textViewLists = new List[len];
    	int len = SRC.length;
    	generateResources(len);
    	List<Integer> list = new ArrayList<Integer>();
		for (Integer integer : map.keySet()) {
			list.add(integer);
		}
		pictureLists = new List[len];
		for (int i = 0; i < SRC.length; i++) {
			pictureLists[i] = new ArrayList<Picture>();
		}
    	for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COLUMN; j++) {
				int index = new Random().nextInt(list.size());
				int id = list.get(index);
				while (map.get(id) <= 0) {
					list.remove(index);
					index = new Random().nextInt(list.size());
					id = list.get(index);
				}
				pictures[i][j] = new Picture(id);
				pictures[i][j].setWidth(picWidth);
				pictures[i][j].setHeight(picHeight);
				pictures[i][j].setLocation(i * COLUMN + j);
//				tableRow[i].addView(points[i][j]);
				pictureLists[id].add(pictures[i][j]);
				map.put(id, map.get(id) - 1);
			}
		}
	}
    /**
     * 生成所有的资源
     * @param len 表示SRC[0]~SRC[len]的资源
     */
    @SuppressLint("UseSparseArrays") 
    private void generateResources(int len){
    	map = new HashMap<Integer, Integer>();
//    	leftMap = new HashMap<Integer, ArrayList<TextView>>();
    	if (len > SRC.length) {
			return ;
		}
    	for (int i = 0; i < ROW * COLUMN; i++) {
    		int id = new Random().nextInt(len);
			int temp = 0;
			temp += map.get(id) == null ? 0 : map.get(id);
			if (temp >= 4) {
				i --;
				continue;
			}
			else{
				map.put(id, temp + 2);
				i ++;
			}
		}    	
    }
    /**
     * 初始化pauseButton
     */
    @SuppressLint("RtlHardcoded") 
    private void initialPauseButton() {
    	 pauseButton = new Button(this);
         //pauseButton.setText(String.valueOf(pauseButton.id));
		pauseButton.setGravity(Gravity.LEFT);
		pauseButton.setLayoutParams(new LinearLayout.LayoutParams(displayWidth
				/ (COLUMN + COLUMNEXTENSION), displayHeight
				/ (ROW + ROWEXTENSION)));
		pauseButton.setBackgroundResource(R.drawable.pause);
		pauseButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				paused = !paused;
				if (paused) {
					progressHandler.removeMessages(1);
					pauseButton.setBackgroundResource(0);
					pauseButton.setBackgroundResource(R.drawable.start);
					final AlertDialog dialog = new AlertDialog.Builder(
							Main.this).create();
					dialog.setCancelable(false);
					dialog.show();
					Window window = dialog.getWindow();					
					// 设置窗口的内容页面,pause_dialog.xml文件中定义view内容
					window.setContentView(R.layout.pause_dialog);
					// 为确认按钮添加事件,执行退出应用操作
					Button button = (Button) window.findViewById(R.id.startButton);
					button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialog.cancel();
							paused = !paused;
							pauseButton.setBackgroundResource(0);
							pauseButton.setBackgroundResource(R.drawable.pause);
							progressBar.setProgress(progress);
							progressHandler.sendEmptyMessage(1);
						}
					});
					TextView pauseTextView = (TextView)window.findViewById(R.id.pauseTextView);
					pauseTextView.setMovementMethod(LinkMovementMethod.getInstance());
					TextView pauseStatisticsTextView = (TextView)window.findViewById(R.id.pauseStatisticsTextView);
					StringBuffer buffer = new StringBuffer("时间还剩：" + progress +"S\n");
					buffer.append("还剩：" + pairs + "对\n");
					buffer.append("加油！！！");
					pauseStatisticsTextView.setText(buffer.toString());
				}
			}
		});
	}
    /**
     * 初始化refreshButton
     */
    @SuppressLint("RtlHardcoded") 
    private void initialRefreshButton() {
    	 refreshButton = new Button(this);
    	 refreshButton.setGravity(Gravity.RIGHT);
    	 refreshButton.setLayoutParams(new LinearLayout.LayoutParams(displayWidth / (COLUMN + COLUMNEXTENSION),displayHeight / (ROW + ROWEXTENSION)));
    	 refreshButton.setBackgroundResource(R.drawable.refresh);
    	 refreshButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//refresh
				progressHandler.removeMessages(1);
				refresh();
				progressHandler.sendEmptyMessage(1);
			}
		});
	}
    /**
     * 初始化hintButton
     */
	@SuppressLint("RtlHardcoded") 
	private void initialHintButton() {
		hintButton = new Button(this);
		hintButton.setGravity(Gravity.RIGHT);
		hintButton.setLayoutParams(new LinearLayout.LayoutParams(displayWidth
				/ (COLUMN + COLUMNEXTENSION), displayHeight / (ROW + ROWEXTENSION)));
		hintButton.setBackgroundResource(R.drawable.hint);
		hintButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// hint
				 progressHandler.removeMessages(1);
				 hint();
				 progressHandler.sendEmptyMessage(1);
			}
		});
	}
    /**
     * 初始化restartButton
     */
	@SuppressLint("RtlHardcoded") 
	private void initialRestartButton() {
		restartButton = new Button(this);
		restartButton.setText("重新开始");
		restartButton.setGravity(Gravity.RIGHT);
//		restartButton.setLayoutParams(new LinearLayout.LayoutParams(displayWidth / (COLUMN + 1), displayWidth / (COLUMN + 1)));
		// restartButton.setBackgroundResource(R.drawable.refresh);
		restartButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// refresh
				progressHandler.removeMessages(1);
				Builder builder = new AlertDialog.Builder(Main.this)
						.setIcon(R.drawable.think).setTitle("提示")
						.setMessage("确定要重新开始游戏吗？")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										restart();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										progressHandler.sendEmptyMessage(1);
									}
								});
				builder.setCancelable(false);
				builder.show();
			}
		});
	}
    /**
     * 数组ids用于记录points显示情况，0表示此处为空，大于0表示此处还有图片
     */
    private void initialID() {
		for (int i = 0; i < ROW + 2; i++) {
			for (int j = 0; j < COLUMN + 2; j++) {
				if (i == 0 || i == ROW + 1) {
					ids[i][j] = -1;
				}
				else if (j == 0 || j == COLUMN + 1) {
					ids[i][j] = -1;
				}
				else{
					ids[i][j] = pictures[i - 1][j - 1].getPicID();
				}
			}
		}
	}
	/**
	 * 消除一对
	 * @param row0 textViews[][]里面的行
	 * @param col0 textViews[][]里面的列
	 * @param row1
	 * @param col1
	 */
	private void deletePair(int row0, int col0, int row1, int col1){
		clickCount = 0;
		//消去
		pictureLists[pictures[row0][col0].getPicID()].remove(pictures[row0][col0]);
		pictureLists[pictures[row1][col1].getPicID()].remove(pictures[row1][col1]);
//		points[row0][col0].setBackgroundResource(0);
//		points[row1][col1].setBackgroundResource(0);
		pictures[row0][col0].setCleaned(true);
		pictures[row1][col1].setCleaned(true);
		ids[row0 + 1][col0 + 1] = -1;
		ids[row1 + 1][col1 + 1] = -1;
		gameView.invalidate();
		
		pairs --;
		if (pairs == 0) {
			progressHandler.removeMessages(1);
			Builder builder = new AlertDialog.Builder(Main.this).setIcon(R.drawable.win).setTitle("提示").
			setMessage("恭喜你赢了！！剩余时间：" + progress + "S").setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					restart();
				}
			});
			builder.setCancelable(false);
			builder.show();
		}
		if (!judgeAndDelete(false)) {
			refresh();
		}
	}
	/**
	 * 判断是否可以消除
	 * @param row0 pictures[][]里面的行
	 * @param col0 pictures[][]里面的列
	 * @param row1
	 * @param col1
	 * @param addToLines 是否记录
	 * @return 可以消除返回true，否则返回false
	 */
	private boolean canBeDelete(int row0, int col0, int row1, int col1,boolean addToLines) {
		if (col0 == col1 && row0 == row1) {
			return false;
		}
		//一条直线消除
		if (row0 == row1 || col0 == col1) {
			if(isInLine(row0 + 1, col0 + 1, row1 + 1, col1 + 1))
			{
				if (addToLines) {
					lines.add(getPointXY(row0, col0));
					lines.add(getPointXY(row1, col1));
				}
				return true;
			}
		}
		//两条直线消除，一个矩形，其他两个点都与这两个点可以直线消除
		if (pictures[row0][col1].getCleaned()) {
			if (isInLine(row0 + 1, col1 + 1, row1 + 1, col1 + 1) && isInLine(row0 + 1, col0 + 1, row0 + 1, col1 + 1)) {
				if (addToLines) {
					lines.add(getPointXY(row0, col0));
					lines.add(getPointXY(row0, col1));
					lines.add(getPointXY(row0, col1));
					lines.add(getPointXY(row1, col1));
				}
				return true;
			}
		}
		if (pictures[row1][col0].getCleaned()) {
			if (isInLine(row0 + 1, col0 + 1, row1 + 1, col0 + 1) && isInLine(row1 + 1, col0 + 1, row1 + 1, col1 + 1)) {
				if (addToLines) {
					lines.add(getPointXY(row0, col0));
					lines.add(getPointXY(row1, col0));
					lines.add(getPointXY(row1, col0));
					lines.add(getPointXY(row1, col1));
				}
				return true;
			}
		}
		
		//三条直线消除
		if(threeLines(row0,col0,row1,col1,addToLines))
			return true;
		return false;
	}
	/**
	 * 获取坐标x，y
	 * @param r pictures[][]里面的行
	 * @param c pictures[][]里面的列
	 * @return PointXY对象
	 */
	private PointXY getPointXY(int r, int c) {
		int x = picWidth * COLUMNEXTENSION / 2 + c * picWidth + picWidth * COLUMNEXTENSION / 2;
		int y = picHeight * (ROWEXTENSION - 2) / 2 + r * picHeight + picHeight * (ROWEXTENSION - 2) / 2;
		//修正x,y，当x,y靠近边缘时，所绘的直线可能会被挡住，所以修正
		if (x <= 5) {
			x = 5;
		}
		else if (c == COLUMN) {
			x = x - 5;
		}
		if (y <= 5) {
			y = 5;
		}
		else if (r == ROW) {
			y = y - 5;
		}
		
		return new PointXY(x, y);
	}
	/**
	 * 判断是否在同一条直线上
	 * @param row0 ids中的row
	 * @param col0
	 * @param row1
	 * @param col1
	 * @return 在就返回true，不在返回false
	 */
	private boolean isInLine(int row0, int col0, int row1, int col1) {
		if (row0 == row1) {
			if (col0 > col1) {
				return isInLine(row1, col1, row0, col0);
			}
			else {
				for (int i = col0 + 1; i < col1; i++) {
					if (ids[row0][i] != -1) {
						return false;
					}
				}
				return true;
			}
		}
		if (col0 == col1) {
			if (row0 > row1) {
				return isInLine(row1, col1, row0, col0);
			}
			else {
				for (int i = row0 + 1; i < row1; i++) {
					if (ids[i][col0] != -1) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * 通过三条折线连起来
	 * @return 可以连起来返回true，否则返回false
	 */
	private boolean threeLines(int row0, int col0, int row1, int col1,boolean addToLines) {
		List<Integer> horizontalList = new LinkedList<Integer>();//记录row[0]行的cols,ids中的col
		List<Integer> horizontalList2 = new LinkedList<Integer>();
		List<Integer> verticalList = new LinkedList<Integer>();//记录col[0]行的rows
		List<Integer> verticalList2 = new LinkedList<Integer>();
		//往右找
		for (int i = col0 + 2; i < COLUMN + 2; i++) {
			if (ids[row0 + 1][i] == -1) {
				horizontalList.add(i);
			}
			else {
				break;
			}
		}
		//往左找
		for (int i = col0; i >= 0; i --) {
			if (ids[row0 + 1][i] == -1) {
				horizontalList.add(i);
			}
			else {
				break;
			}
		}
		// 往右找
		for (int i = col1 + 2; i < COLUMN + 2; i++) {
			if (ids[row1 + 1][i] == -1) {
				horizontalList2.add(i);
			} else {
				break;
			}
		}
		for (int i = col1; i >= 0; i --) {
			if (ids[row1 + 1][i] == -1) {
				horizontalList2.add(i);
			}
			else {
				break;
			}
		}
		for (Integer t_col0 : horizontalList) {
			for (Integer t_col1 : horizontalList2) {
				if (t_col0 == t_col1) {
					if (isInLine(row0 + 1, t_col0, row1 + 1, t_col1) ) {
						if (addToLines) {
							lines.add(getPointXY(row0, col0));
							
							lines.add(getPointXY(row0, t_col0 - 1));
							lines.add(getPointXY(row0, t_col0 - 1));
							lines.add(getPointXY(row1, t_col1 - 1));
							lines.add(getPointXY(row1, t_col1 - 1));
							
							lines.add(getPointXY(row1, col1));
						}
						return true;
					}
				}
			}
		}
		// 往下找
		for (int i = row0 + 2; i < ROW + 2; i ++) {
			if (ids[i][col0 + 1] == -1) {
				verticalList.add(i);
			} else {
				break;
			}
		}
		for (int i = row0; i >= 0; i --) {
			if (ids[i][col0 + 1] == -1) {
				verticalList.add(i);
			} else {
				break;
			}
		}
		//往下找
		for (int i = row1 + 2; i < ROW + 2; i ++) {
			if (ids[i][col1 + 1] == -1) {
				verticalList2.add(i);
			} else {
				break;
			}
		}
		for (int i = row1; i >= 0; i --) {
			if (ids[i][col1 + 1] == -1) {
				verticalList2.add(i);
			} else {
				break;
			}
		}
		for (Integer t_row0 : verticalList) {
			for (Integer t_row1 : verticalList2) {
				if (t_row0 == t_row1) {
					if (isInLine(t_row0, col0 + 1, t_row1, col1 + 1)) {
						if (addToLines) {
							lines.add(getPointXY(row0, col0));						
							lines.add(getPointXY(t_row0 - 1, col0));
							lines.add(getPointXY(t_row0 - 1, col0));
							lines.add(getPointXY(t_row1 - 1, col1));
							lines.add(getPointXY(t_row1 - 1, col1));
							lines.add(getPointXY(row1, col1));
						}
						return true;
					}
				}
			}
		}
		return false;
	}
	/**
	 * 重新开始游戏
	 */
	private void restart() {
		pairs = ROW * COLUMN / 2;
		clickCount = 0;
		paused = false;
		lines.clear();
		row[0] = row[1] = -1;
		col[0] = col[1] = -1;
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COLUMN; j++) {
				pictures[i][j] = null;
			}
		}
		generatePoints();	
		gameView.invalidate();
		initialID();		
		pauseButton.setBackgroundResource(0);
		pauseButton.setBackgroundResource(R.drawable.pause);
		progressBar.setMax(MAX_TIME);
        progressBar.setProgress(MAX_TIME);
        progress = progressBar.getMax();
		progressHandler.sendEmptyMessage(1);
	}
	/**
	 * 刷新剩下的组合
	 */
	@SuppressWarnings("unchecked")
	@SuppressLint("UseSparseArrays") 
	private void refresh() {
		clickCount = 0;
		paused = false;
		row[0] = row[1] = -1;
		col[0] = col[1] = -1;
		map = new HashMap<Integer, Integer>();
		//得到所有未clean掉的point
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COLUMN; j++) {
				if (!pictures[i][j].getCleaned()) {
					int id = pictures[i][j].getPicID();
					if (map.get(id) == null || map.get(id) <= 0) {
						map.put(id, 1);
					}
					else {
						int temp = map.get(id); 
						map.put(id, temp + 1);
					}
				}
			}
		}
		//重新分配图片
		List<Integer> list = new ArrayList<Integer>();
		for (Integer integer : map.keySet()) {
			list.add(integer);
		}
		pictureLists = new List[SRC.length];
		for (int i = 0; i < SRC.length; i++) {
			pictureLists[i] = new ArrayList<Picture>();
		}
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COLUMN; j++) {
				if (!pictures[i][j].getCleaned()) {
					int index = new Random().nextInt(list.size());
					int id = list.get(index);
					while (map.get(id) <= 0) {
						list.remove(index);
						index = new Random().nextInt(list.size());
						id = list.get(index);
					}
					pictures[i][j].setPicID(id);
					//得到points[i][j]的行列
					int x = pictures[i][j].getLocation() / COLUMN;
					int y = pictures[i][j].getLocation() % COLUMN;
					ids[x + 1][y + 1] = pictures[i][j].getPicID();
					
//					points[i][j].setBackgroundResource(0);
//					points[i][j].setBackgroundResource(SRC[id]);
					pictureLists[id].add(pictures[i][j]);
					map.put(id, map.get(id) - 1);
				}
			}
		}
		gameView.invalidate();
	}
	/**
	 * 提示
	 */
	private void hint() {
		if(judgeAndDelete(true)){
			clickCount = 0;
			row[0] = row[1] = -1;
			col[0] = col[1] = -1;
			gameView.invalidate();
			return ;
		}
//		refresh();
	}
	/**
	 * 判断是否还可以消除
	 * @param flag 当还有可以消除的，若flag为true，则消除，否则不消除
	 * @return 还有可以消除的返回true
	 */
	private boolean judgeAndDelete(boolean flag) {
		for (int i = 0; i < pictureLists.length; i++) {
			if (pictureLists[i] != null && pictureLists[i].size() >= 2) {
				for (int j = 0; j < pictureLists[i].size(); j ++) {
					int row0 = pictureLists[i].get(j).getLocation() / COLUMN;
					int col0 = pictureLists[i].get(j).getLocation() % COLUMN;
					for (int j2 = j + 1; j2 < pictureLists[i].size(); j2 ++) {						
						int row1 = pictureLists[i].get(j2).getLocation() / COLUMN;
						int col1 = pictureLists[i].get(j2).getLocation() % COLUMN;
						boolean temp = canBeDelete(row0,col0,row1,col1,flag);						
						if(temp){
							if (flag) {
								deletePair(row0,col0,row1,col1);
							}
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	/**
	 * <p>内部类</p>
	 * 用于绘画所有的图片，被选中图片变大，以及消除图片的连线
	 * <p>之所以使用内部类是为了方便访问Main里面的成员</p>
	 * @author tzliang
	 *
	 */
	public class GameView extends View {

		private Paint paint;
		
		public GameView(Context context) {
			super(context);
		}
		
		public GameView(Context context, int width, int height) {
			// TODO Auto-generated constructor stub
			super(context);
			paint = new Paint();		
			paint.setColor(Color.YELLOW);
			// 设置抗锯齿
			paint.setAntiAlias(true);
			// 设置线宽
			paint.setStrokeWidth(3);
			// 设置非填充
			paint.setStyle(Style.STROKE);
		}
		@Override
		protected void onDraw(Canvas canvas) {
			if (lines.size() > 0) {
				for (int i = 0; i < lines.size(); i +=2) {
					int startX = lines.get(i).getX();
					int startY = lines.get(i).getY();
					int stopX = lines.get(i + 1).getX();
					int stopY = lines.get(i + 1).getY();
					canvas.drawLine(startX, startY, stopX, stopY, paint);
				}
				lines.clear();
				//开启定时器
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				invalidate();
			}
			for (int i = 0; i < ROW; i++) {
				for (int j = 0; j < COLUMN; j++) {
					int x = picWidth * COLUMNEXTENSION / 2 + j * picWidth;
					int y = picHeight * (ROWEXTENSION - 2) / 2 + i * picHeight;
					Log.i("GameView.onDraw", "x = " + x + " y = " + y);
					if (!pictures[i][j].getCleaned()) {
						Log.i("onDraw", "drawBitmap:" + pictures[i][j].getPicID());
						canvas.drawBitmap(bitmaps[pictures[i][j].getPicID()], x, y, null);
					}
				}
			}
			//被选中的图片放大
			if (row[0] >= 0 && col[0] >= 0 && !pictures[row[0]][col[0]].getCleaned()) {
				int left = picWidth * COLUMNEXTENSION / 2 + col[0] * picWidth;
				int top = picHeight * (ROWEXTENSION - 2) / 2 + row[0] * picHeight;
				int right = left + picWidth;
				int bottom = top + picHeight;
				int margin = 5;
				
				canvas.drawBitmap(bitmaps[pictures[row[0]][col[0]].getPicID()], null, 
						new Rect(left - margin, top - margin, right + margin, bottom + margin), null);
			}
			if (row[1] >= 0 && col[1] >= 0 && !pictures[row[1]][col[1]].getCleaned()) {
				int left = picWidth * COLUMNEXTENSION / 2 + col[1] * picWidth;
				int top = picHeight * (ROWEXTENSION - 2) / 2 + row[1] * picHeight;
				int right = left + picWidth;
				int bottom = top + picHeight;
				int margin = 5;
				
				canvas.drawBitmap(bitmaps[pictures[row[1]][col[1]].getPicID()], null, 
						new Rect(left - margin, top - margin, right + margin, bottom + margin), null);
			}
			super.onDraw(canvas);
		}
		@SuppressLint("ClickableViewAccessibility") 
		public boolean onTouchEvent(MotionEvent event){
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				int x = (int) event.getX() - picWidth * COLUMNEXTENSION / 2;
				int y = (int) event.getY() - picHeight * (ROWEXTENSION - 2) / 2;
				if ((x >= 0 && x <= COLUMN * picWidth)&& (y >= 0 && y <= ROW * picHeight))
				{
					int c = x / picWidth;
					int r = y / picHeight;
					Log.i("onTouchEvent", String.valueOf(r) + String.valueOf(c));
					if (!pictures[r][c].getCleaned()) {
						row[clickCount] = r;
						col[clickCount] = c;
						gameView.invalidate();
						clickCount++;
						if (clickCount == 2) {
							if ((pictures[row[0]][col[0]].getPicID() == pictures[row[1]][col[1]]
									.getPicID())
									&& (ids[row[0] + 1][col[0] + 1] != -1)
									&& (ids[row[1] + 1][col[1] + 1] != -1)) {
								// 判断两个是否可以消去，可以的话则消去，clickCount =
								// 0，否则clickCount = 1；
								clickCount = 0;
								boolean temp = canBeDelete(row[0], col[0],row[1], col[1],true);
								if (temp) {
									deletePair(row[0], col[0], row[1], col[1]);
									row[0] = row[1] = -1;
									col[0] = col[1] = -1;
								} else {
									clickCount = 1;
									row[0] = row[1];
									col[0] = col[1];
									row[1] = -1;
									col[1] = -1;
								}
							} else {
								clickCount = 1;
								row[0] = row[1];
								col[0] = col[1];
								row[1] = -1;
								col[1] = -1;
							}
						}
					}
				}
				
			}
			return super.onTouchEvent(event);
		}
	}
}
