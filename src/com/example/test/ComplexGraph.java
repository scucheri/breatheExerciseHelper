package com.example.test;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.XYChart;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.Zoom;

import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

/**
 * 复合图表控件类
 * 包括一个基于achartengine的GraphicalView以及一些辅助操作控件
 */
public class ComplexGraph extends LinearLayout
{
	static private float MAX_ZOOM_RATE = 1.5f; // 最大图表缩放率
	
	// 控件
	private RelativeLayout mRelativeLayout = null;
	private GraphicalView graphView = null;
	private Button buttonFullScreen = null;
	private Button buttonLock = null;
	private SeekBar seekBarX = null;
	private VerticalSeekBar seekBarY = null;
	
	private PopupWindow mPopupWindow = null; // PopupWindow用于实现全屏效果
	
	private boolean isFullScreen = false; // 是否全屏标志
	private boolean isLock = true; // 是否锁定横纵缩放标志
	
	private boolean enableGraphViewSingleTouch = true; // graphView单点触控使能，用于防止预期外的操作，如Scale操作会同时引起LongPress操作
	
	private Zoom mZoomIn = null; // 用于放大
	private Zoom mZoomOut = null; // 用于缩小
	
	private final XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset(); // 图表数据集，用于绘制曲线
	private final XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer(); // 渲染器，以指定方式绘制对应曲线
	
//	private String graphFileName = ""; // 当前的图形文件名
	
	/**
	 * 构造函数
	 * 
	 * @param context 上下文环境
	 */
	public ComplexGraph(Context context)
	{
        super(context);
        
        initGraphView();
        initAuxiliaryWidget();
    }
	
	/**
	 * 构造函数
	 * 使用xml文件布局控件时自动调用
	 */
	public ComplexGraph(Context context, AttributeSet attrs)
	{
        super(context, attrs);
        
        initGraphView();
        initAuxiliaryWidget();
    }
	
	/**
     * 初始化图表控件
     */
	private void initGraphView()
	{
		graphView = ChartFactory.getLineChartView(getContext(), mDataset, mRenderer); // 生成GraphicalView
		
		XYChart chart = ((XYChart)graphView.getChart());
		mZoomIn = new Zoom(chart, true, mRenderer.getZoomRate()); // 生成自己的缩放器
		mZoomOut = new Zoom(chart, false, mRenderer.getZoomRate()); // 生成自己的缩放器
		
		// 生成手势处理器
		final GestureDetector gesturedetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener()
		{
			@Override
			public boolean onDown(MotionEvent e)
			{
				ComplexGraph.this.enableGraphViewSingleTouch = true;
				return false;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
			{
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e)
			{
				if (ComplexGraph.this.enableGraphViewSingleTouch)
				{
					showOperatingDialog();
				}
				else
				{
					ComplexGraph.this.enableGraphViewSingleTouch = true;
				}
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
			{
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e)
			{}

			@Override
			public boolean onSingleTapUp(MotionEvent e)
			{
				if (ComplexGraph.this.enableGraphViewSingleTouch)
				{
					SeriesSelection seriesSelection = graphView.getCurrentSeriesAndPoint();
					double[] xy = graphView.toRealPoint(0);
					if (seriesSelection == null)
					{
						ComplexGraph.this.buttonFullScreen.setVisibility(View.VISIBLE + View.INVISIBLE - ComplexGraph.this.buttonFullScreen.getVisibility());
		        		ComplexGraph.this.buttonLock.setVisibility(View.VISIBLE + View.INVISIBLE - ComplexGraph.this.buttonLock.getVisibility());
		        		ComplexGraph.this.seekBarX.setVisibility(View.VISIBLE + View.INVISIBLE - ComplexGraph.this.seekBarX.getVisibility());
		        		ComplexGraph.this.seekBarY.setVisibility(View.VISIBLE + View.INVISIBLE - ComplexGraph.this.seekBarY.getVisibility());
					}
					else
					{
						String ms = "X=" + seriesSelection.getXValue() + ", Y=" + seriesSelection.getValue() + "\r\nreal X=" + (float) xy[0] + ", real Y=" + (float) xy[1];
						Toast.makeText(ComplexGraph.this.getContext(), ms, Toast.LENGTH_LONG).show();
					}
				}
				else
				{
					ComplexGraph.this.enableGraphViewSingleTouch = true;
				}
				
				return false;
			}
		});
		
		// 添加双击手势处理
		gesturedetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener()
		{
			@Override
			public boolean onDoubleTap(MotionEvent e)
			{
				ComplexGraph.this.fitGraph();
				return false;
			}

			@Override
			public boolean onDoubleTapEvent(MotionEvent e)
			{
				return false;
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e)
			{
				return false;
			}
		});
		
		graphView.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1)
			{
				switch (arg1.getPointerCount())
				{
				case 1: // 单点触控
					gesturedetector.onTouchEvent(arg1); // 单点触控由自定义的手势处理器处理
					break;
				default: // 多点触控
					ComplexGraph.this.enableGraphViewSingleTouch = false;
				}
				return false;
			}
		});
		
//		addView(graphView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	/**
	 * 初始化辅助控件
	 */
	private void initAuxiliaryWidget()
	{
		Context c = getContext();
		mRelativeLayout = new RelativeLayout(c);
		mRelativeLayout.addView(graphView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		// 从布局文件加载辅助控件
//		LayoutInflater.from(getContext()).inflate(R.layout.view_complex_graph, mRelativeLayout, true);
//		buttonFullScreen = (Button)mRelativeLayout.findViewById(R.id.buttonFullScreen);
//		buttonLock = (Button)mRelativeLayout.findViewById(R.id.buttonLock);
//		seekBarX = (SeekBar)mRelativeLayout.findViewById(R.id.seekBarX);
//		seekBarY = (VerticalSeekBar)mRelativeLayout.findViewById(R.id.verticalSeekBarY);
		
		// 动态生成辅助控件
		RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(64, 64);
		rl.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		buttonFullScreen = new Button(c);
		buttonFullScreen.setLayoutParams(rl);
		
		rl = new RelativeLayout.LayoutParams(64, 64);
		rl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rl.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		buttonLock = new Button(c);
		buttonLock.setId(1000000);
		buttonLock.setLayoutParams(rl);
		
		rl = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		rl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rl.addRule(RelativeLayout.RIGHT_OF, buttonLock.getId());
		seekBarX = new SeekBar(c);
		seekBarX.setLayoutParams(rl);
		
		rl = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		rl.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		rl.addRule(RelativeLayout.ABOVE, buttonLock.getId());
		seekBarY = new VerticalSeekBar(c);
		seekBarY.setLayoutParams(rl);
		
		mRelativeLayout.addView(buttonFullScreen);
		mRelativeLayout.addView(buttonLock);
		mRelativeLayout.addView(seekBarX);
		mRelativeLayout.addView(seekBarY);
		
		// 所有控件先添加至一个相对布局中，再添加至本ComplexGraph，这样便于实现全屏效果
		addView(mRelativeLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		// 辅助控件初始化为不可见
		buttonFullScreen.setVisibility(View.INVISIBLE);
		buttonLock.setVisibility(View.INVISIBLE);
		seekBarX.setVisibility(View.INVISIBLE);
		seekBarY.setVisibility(View.INVISIBLE);
		
		buttonFullScreen.setBackgroundResource(R.drawable.ic_full_screen);
		buttonFullScreen.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
        	{
        		if (ComplexGraph.this.isFullScreen)
        		{
        			// 退出全屏
        			ComplexGraph.this.exitFullScreen();
        		}
        		else
        		{
        			// 进入全屏
        			ComplexGraph.this.fullScreen();
        		}
        	}
		});
		
		buttonLock.setBackgroundResource(R.drawable.ic_lock);
		buttonLock.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
        	{
        		if (ComplexGraph.this.isLock)
        		{
        			buttonLock.setBackgroundResource(R.drawable.ic_unlock);
        			ComplexGraph.this.isLock = false;
        		}
        		else
        		{
        			buttonLock.setBackgroundResource(R.drawable.ic_lock);
        			ComplexGraph.this.isLock = true;
        		}
        	}
		});
		
		seekBarX.setMax(1000);
		seekBarX.setProgress(seekBarX.getMax()/2);
		seekBarX.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        	{
        		if (fromUser)
        		{
        			int axis = Zoom.ZOOM_AXIS_X;
        			int half = seekBarX.getMax()/2;
        			int distance = progress - half;
        			
        			if (ComplexGraph.this.isLock)
        			{
            			// 若横纵缩放锁定，则同时控制Y轴
        				axis = Zoom.ZOOM_AXIS_XY;
        				seekBarY.setProgress(progress);
        			}
        			
        			// 以中间为准，根据位移确定缩小放大及缩放率
        			if (distance > 0)
        			{
        				// 位移为正(向右)，则放大
        				ComplexGraph.this.mZoomIn.setZoomRate(distance * (MAX_ZOOM_RATE - 1) / half + 1);
        				ComplexGraph.this.mZoomIn.apply(axis);
        			}
        			if (distance < 0)
        			{
        				// 位移为负(向左)，则缩小
        				ComplexGraph.this.mZoomOut.setZoomRate(-distance * (MAX_ZOOM_RATE - 1) / half + 1);
        				ComplexGraph.this.mZoomOut.apply(axis);
        			}
        			
            		ComplexGraph.this.rePaintGraph();
        		}
        	}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				// 结束缩放操作后回到中间位置
				seekBarX.setProgress(seekBarX.getMax()/2);
				seekBarY.setProgress(seekBarY.getMax()/2);
			}
		});
		
		seekBarY.setMax(1000);
		seekBarY.setProgress(seekBarY.getMax()/2);
		seekBarY.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        	{
        		if (fromUser)
        		{
        			int axis = Zoom.ZOOM_AXIS_Y;
        			int half = seekBarY.getMax()/2;
        			int distance = progress - half;
        			
        			if (ComplexGraph.this.isLock)
        			{
        				// 若横纵缩放锁定，则同时控制X轴
        				axis = Zoom.ZOOM_AXIS_XY;
        				seekBarX.setProgress(progress);
        			}
        			
        			// 以中间为准，根据位移确定缩小放大及缩放率
        			if (distance > 0)
        			{
        				// 位移为正(向上)，则放大
        				ComplexGraph.this.mZoomIn.setZoomRate(distance * (MAX_ZOOM_RATE - 1) / half + 1);
        				ComplexGraph.this.mZoomIn.apply(axis);
        			}
        			if (distance < 0)
        			{
        				// 位移为负(向下)，则缩小
        				ComplexGraph.this.mZoomOut.setZoomRate(-distance * (MAX_ZOOM_RATE - 1) / half + 1);
        				ComplexGraph.this.mZoomOut.apply(axis);
        			}
        			
            		ComplexGraph.this.rePaintGraph();
        		}
        	}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				// 结束缩放操作后回到中间位置
				seekBarX.setProgress(seekBarX.getMax()/2);
				seekBarY.setProgress(seekBarY.getMax()/2);
			}
		});
	}
	
	/**
	 * 返回渲染器
	 */
	public XYMultipleSeriesRenderer getRenderer()
	{
		return mRenderer;
	}
	
	/**
     * 是否全屏中
     */
	public boolean isFullScreen()
	{
		return isFullScreen;
	}
	
	/**
     * 重绘图形
     */
	public void rePaintGraph()
	{
		if (graphView != null)
		{
			graphView.repaint();
		}
	}
	
	/**
     * 进入全屏
     */
	public void fullScreen()
	{
		Context context = getContext();
		if (context instanceof Activity)
		{
			((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			((Activity)context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

			DisplayMetrics metrics = getResources().getDisplayMetrics(); // 获取屏幕分辨率
			removeView(mRelativeLayout); // 将所有控件从本ComplexGraph移除
			
			// 新建全屏的PopupWindow，并添加所有控件
			mPopupWindow = new PopupWindow(mRelativeLayout, metrics.widthPixels, metrics.heightPixels);
			mPopupWindow.showAtLocation(this, Gravity.CENTER, 0, 0);
			
			buttonFullScreen.setBackgroundResource(R.drawable.ic_return_from_full_screen); // 设置全屏按钮图标
			isFullScreen = true; // 设置全屏标志
		}
	}
	
	/**
     * 退出全屏
     */
	public void exitFullScreen()
	{
		Context context = getContext();
		if (context instanceof Activity)
		{
			((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			((Activity)context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			
			mPopupWindow.dismiss(); // 关闭全屏的PopupWindow
			addView(mRelativeLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)); // 将所有控件添加回本ComplexGraph
			
			buttonFullScreen.setBackgroundResource(R.drawable.ic_full_screen); // 设置全屏按钮图标
			isFullScreen = false; // 设置全屏标志
		}
	}
	
	/**
     * 添加新的曲线数据与对应渲染器
     * 
     * @param series 曲线数据
     * @param renderer 渲染器
     */
	public void addSeriesAndRenderer(XYSeries series, XYSeriesRenderer renderer)
	{
		if ((series != null) && (renderer != null))
		{
			mDataset.addSeries(series);
			mRenderer.addSeriesRenderer(renderer);
		}
	}
		
	
	/**
     * 清空图表数据缓存
     */
	public void clearSeriesAndRenderer()
	{
		int count = mDataset.getSeriesCount();
		for (int i = 0; i < count ; i++)
		{
			mDataset.removeSeries(0);
		}
		
		count = mRenderer.getSeriesRendererCount();
		for (int i = 0; i < count ; i++)
		{
			mRenderer.removeSeriesRenderer(mRenderer.getSeriesRendererAt(0));
		}
		
		rePaintGraph();
	}
	
	
	public void clearSeries()
	{
		int count = mDataset.getSeriesCount();
		for (int i = 0; i < count ; i++)
		{
			mDataset.removeSeries(0);
		}
		
	}
	
	/**
     * 获取渲染器
     */
	public XYMultipleSeriesRenderer getXYMultipleSeriesRenderer()
	{
		return mRenderer;
	}
	
	/**
     * 获取X轴最小值
     */
	public double getMinX()
	{
		double minx;
		int count = mDataset.getSeriesCount();
		if (count > 0)
		{
			minx = mDataset.getSeriesAt(0).getMinX();
		}
		else
		{
			minx = 0;
		}
		
		for (int i = 1; i < count ; i++)
		{
			minx = Math.min(minx, mDataset.getSeriesAt(i).getMinX());
		}
		
		return minx;
	}
	
	/**
     * 获取X轴最大值
     */
	public double getMaxX()
	{
		double maxx;
		int count = mDataset.getSeriesCount();
		if (count > 0)
		{
			maxx = mDataset.getSeriesAt(0).getMaxX();
		}
		else
		{
			maxx = 0;
		}
		
		for (int i = 1; i < count ; i++)
		{
			maxx = Math.max(maxx, mDataset.getSeriesAt(i).getMaxX());
		}
		
		return maxx;
	}
	
	/**
     * 获取Y轴最小值
     */
	public double getMinY()
	{
		double miny;
		int count = mDataset.getSeriesCount();
		if (count > 0)
		{
			miny = mDataset.getSeriesAt(0).getMinY();
		}
		else
		{
			miny = 0;
		}
		
		for (int i = 1; i < count ; i++)
		{
			miny = Math.min(miny, mDataset.getSeriesAt(i).getMinY());
		}
		
		return miny;
	}
	
	/**
     * 获取Y轴最大值
     */
	public double getMaxY()
	{
		double maxy;
		int count = mDataset.getSeriesCount();
		if (count > 0)
		{
			maxy = mDataset.getSeriesAt(0).getMaxY();
		}
		else
		{
			maxy = 0;
		}
		
		for (int i = 1; i < count ; i++)
		{
			maxy = Math.max(maxy, mDataset.getSeriesAt(i).getMaxY());
		}
		
		return maxy;
	}
	
	/**
     * 图形最大化充满可见窗口
     */
	public void fitGraph()
	{
		double margin = Math.min((getMaxX() - getMinX()) / 500, (getMaxY() - getMinY()) / 500);
		if (margin <= 0)
		{
			margin = 0.001;
		}
	//	mRenderer.setRange(new double[]{getMinX() - margin, getMaxX() + margin, getMinY() - margin, getMaxY() + margin});
//		mRenderer.setRange(new double[]{getMinX() - margin, 100+ margin, getMinY() - margin, getMaxY() + margin});
		mRenderer.setRange(new double[]{getMinX() - margin, getMaxX() + margin, getMinY() - margin, 2.5});
		rePaintGraph();
	}
	
	/**
     * 等比显示，X轴与Y轴缩放比例设置为相同
     */
	public void sameZoomXY()
	{
		double minx = getMinX();
		double miny = getMinY();
		double maxx = getMaxX();
		double maxy = getMaxY();
		
		XYChart chart = ((XYChart)graphView.getChart());
		double[] lb = chart.toScreenPoint(new double []{mRenderer.getXAxisMin(), mRenderer.getYAxisMin()});
		double[] rt = chart.toScreenPoint(new double []{mRenderer.getXAxisMax(), mRenderer.getYAxisMax()});
		
		int w = (int)Math.abs(rt[0] - lb[0]);
		int h = (int)Math.abs(rt[1] - lb[1]);
		
		double rx = maxx - minx;
		double ry = maxy - miny;
		rx = Math.max(rx, w * ry / h);
		ry = Math.max(ry, h * rx / w);
		
		mRenderer.setRange(new double[]{minx, minx + rx, miny, miny + ry});
		rePaintGraph();
	}
	
	/**
     * 弹出操作对话框
     */
	public void showOperatingDialog()
	{
		AlertDialog.Builder menubuilder = new AlertDialog.Builder(getContext());
		Dialog menudialog = menubuilder.setTitle(R.string.graphview_menu_title).setItems(R.array.graphview_menu, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				switch (which)
				{
				case 0: // 返回原点
					ComplexGraph.this.mRenderer.setRange(new double[]{0, ComplexGraph.this.getMaxX(), 0, ComplexGraph.this.getMaxY()});
					ComplexGraph.this.rePaintGraph();
					break;
				case 1: // 等比显示
					sameZoomXY();
					break;
				case 2: // 清除图形
					ComplexGraph.this.clearSeriesAndRenderer();
					break;
//				case 3: // 切换单位
//					Context context = ComplexGraph.this.getContext();
//					
//					final RadioButton rb11 = new RadioButton(context);
//					rb11.setText("V");
//					final RadioButton rb12 = new RadioButton(context);
//					rb12.setText("mV");
//					rb11.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//					{
//						@Override
//						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//						{
//							rb12.setChecked(!isChecked);
//						}
//					});
//					rb12.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//					{
//						@Override
//						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//						{
//							rb11.setChecked(!isChecked);
//						}
//					});
//					if (0 == ComplexGraph.this.unitX)
//					{
//						rb11.setChecked(true);
//						rb12.setChecked(false);
//					}
//					if (1 == ComplexGraph.this.unitX)
//					{
//						rb11.setChecked(false);
//						rb12.setChecked(true);
//					}
//					RadioGroup rg1 = new RadioGroup(context);
//					rg1.addView(rb11);
//					rg1.addView(rb12);
//					
//					TextView tx1 = new TextView(context);
//					tx1.setText("X");
//					
//					LinearLayout ll1 = new LinearLayout(context);
//					ll1.setOrientation(LinearLayout.VERTICAL);
//					ll1.addView(tx1);
//					ll1.addView(rg1);
//					
//					final RadioButton rb21 = new RadioButton(context);
//					rb21.setText("mA");
//					final RadioButton rb22 = new RadioButton(context);
//					rb22.setText("uA");
//					rb21.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//					{
//						@Override
//						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//						{
//							rb22.setChecked(!isChecked);
//						}
//					});
//					rb22.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//					{
//						@Override
//						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//						{
//							rb21.setChecked(!isChecked);
//						}
//					});
//					if (0 == ComplexGraph.this.unitY)
//					{
//						rb21.setChecked(true);
//						rb22.setChecked(false);
//					}
//					if (1 == ComplexGraph.this.unitY)
//					{
//						rb21.setChecked(false);
//						rb22.setChecked(true);
//					}
//					RadioGroup rg2 = new RadioGroup(context);
//					rg2.addView(rb21);
//					rg2.addView(rb22);
//					
//					TextView tx2 = new TextView(context);
//					tx2.setText("Y");
//					
//					LinearLayout ll2 = new LinearLayout(context);
//					ll1.setOrientation(LinearLayout.VERTICAL);
//					ll1.addView(tx2);
//					ll1.addView(rg2);
//					
//					LinearLayout ll = new LinearLayout(context);
//					ll.setOrientation(LinearLayout.HORIZONTAL);
//					ll.addView(ll1);
//					ll.addView(ll2);
//					
//					AlertDialog.Builder setunitbuilder = new AlertDialog.Builder(context);
//					setunitbuilder.setView(ll);
//					
//					setunitbuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
//	    			{
//	    				@Override
//	    				public void onClick(DialogInterface dialog, int which)
//	    				{
//	    					if (rb11.isChecked())
//	    					{
//	    						ComplexGraph.this.unitX = 0;
//	    					}
//	    					if (rb12.isChecked())
//	    					{
//	    						ComplexGraph.this.unitX = 1;
//	    					}
//	    					
//	    					if (rb21.isChecked())
//	    					{
//	    						ComplexGraph.this.unitY = 0;
//	    					}
//	    					if (rb22.isChecked())
//	    					{
//	    						ComplexGraph.this.unitY = 1;
//	    					}
//	    					
//	    					dialog.dismiss();
//	    				}
//	    			});
//	    			
//					setunitbuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
//	    			{
//	    				@Override
//	    				public void onClick(DialogInterface dialog, int which)
//	    				{
//	    					dialog.dismiss();
//	    				}
//	    			});
//					
//					Dialog setunitdialog = setunitbuilder.create();
//					setunitdialog.setCanceledOnTouchOutside(true);
//					setunitdialog.show();
//					break;
//				case 3: // 保存图形
//					// 自动生成新文件名
//					Context context = ComplexGraph.this.getContext();
//					String newfilename = (context.getString(R.string.new_graph_file) + AppGlobal.getCurrentTime() + AppGlobal.DOT + AppGlobal.GRAPH_FILE_SUFFIX); // new_graph_file加上日期时间与扩展名
//					
//					OpenFileDialog savefiledialog = new OpenFileDialog(context, newfilename, new String[]{AppGlobal.GRAPH_FILE_SUFFIX}, true);
//					savefiledialog.setOnClickOK(new OpenFileDialog.OnClickOK()
//					{
//						@Override
//						public void onClickOK(String filepath, String filename)
//						{
//							ComplexGraph.this.graphFileName = filename; // 设置新文件名
//							
//							FileOutputStream fos = null;
//							try {
//								fos = new FileOutputStream(new File(filepath, filename));
//
//								int buffersize = graphSeries.getItemCount();
//								if (buffersize > 0)
//								{
//									byte [] sizedata = {(byte)((buffersize&0xFF000000)>>24), 
//														(byte)((buffersize&0x00FF0000)>>16), 
//														(byte)((buffersize&0x0000FF00)>>8), 
//														(byte)(buffersize&0x000000FF)};
//									
//									byte [] bufferdata = new byte[buffersize * 16];
//									long temp = 0;
//									
//									for (int i = 0; i < buffersize ; i++)
//									{
//										temp = Double.doubleToLongBits(graphSeries.getX(i));
//										for (int j = 0; j < 8; j++)
//										{
//											bufferdata[i * 16 + j] = (byte)((temp >> (8 * (7 - j))) & 0xFF);
//										}
//										temp = Double.doubleToLongBits(graphSeries.getY(i));
//										for (int j = 0; j < 8; j++)
//										{
//											bufferdata[i * 16 + j + 8] = (byte)((temp >> (8 * (7 - j))) & 0xFF);
//										}
//									}
//									
//							        try 
//							        {
//							        	fos.write(sizedata);
//							        	fos.write(bufferdata);
//									}
//							        catch (IOException e) {
//										e.printStackTrace();
//										Toast.makeText(ComplexGraph.this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//									}
//								}
//							} catch (FileNotFoundException e) {
//								e.printStackTrace();
//								Toast.makeText(ComplexGraph.this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//							} finally {
//								if (fos != null)
//								{
//									try {
//										fos.close();
//									} catch (IOException e) {
//										e.printStackTrace();
//										Toast.makeText(ComplexGraph.this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//									}
//								}
//					        }
//						}
//					});
//					savefiledialog.show();
//					dialog.dismiss();
//					break;
//				case 4: // 载入图形
//					OpenFileDialog loadfiledialog = new OpenFileDialog(ComplexGraph.this.getContext(), ComplexGraph.this.graphFileName, new String[]{FileScanView.GRAPH_FILE_SUFFIX}, false);
//					loadfiledialog.setOnClickOK(new OpenFileDialog.OnClickOK()
//					{
//						@Override
//						public void onClickOK(String filepath, String filename)
//						{
//							int fl = filename.length();
//							String graphfilesuffix = AppGlobal.DOT + AppGlobal.GRAPH_FILE_SUFFIX;
//							String suffix = filename.substring(fl - graphfilesuffix.length(), fl).toLowerCase();
//							
//							// 判断扩展名是否为图形文件
//							if (0 != suffix.compareTo(graphfilesuffix))
//							{
//								// 其它格式的扩展名，提示无法识别
//								Toast.makeText(ComplexGraph.this.getContext(), R.string.unsupported_graph_file, Toast.LENGTH_LONG).show();
//							}
//							else
//							{
//								ComplexGraph.this.graphFileName = filename;
//								
//								FileInputStream fis = null;
//								try {
//									fis = new FileInputStream(new File(filepath, filename));
//									
//									try {
//										byte [] sizedata = new byte[4];
//										fis.read(sizedata);
//										
//										int buffersize = 0;
//										for (int i = 0; i < 4; i++)
//										{
//											buffersize = buffersize | ((0xFF & sizedata[i]) << (8 * (3 - i)));
//										}
//										byte [] bufferdata = new byte[buffersize * 16];
//										fis.read(bufferdata);
//										
//										long temp = 0;
//										double tempx = 0;
//										double tempy = 0;
//										ComplexGraph.this.clearSeriesAndRenderer();
//										for (int i = 0; i < buffersize ; i++)
//										{
//											temp = 0;
//											for (int j = 0; j < 8; j++)
//											{
//												temp = temp | ((long)(0xFF & bufferdata[i * 16 + j]) << (8 * (7 - j)));
//											}
//											tempx = Double.longBitsToDouble(temp);
//											
//											temp = 0;
//											for (int j = 0; j < 8; j++)
//											{
//												temp = temp | ((long)(0xFF & bufferdata[i * 16 + j + 8]) << (8 * (7 - j)));
//											}
//											tempy = Double.longBitsToDouble(temp);
//											
//											graphSeries.add(tempx, tempy);
//										}
//										ComplexGraph.this.fitGraph();
//									} catch (IOException e) {
//										e.printStackTrace();
//										Toast.makeText(ComplexGraph.this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//									}
//								} catch (FileNotFoundException e) {
//									e.printStackTrace();
//									Toast.makeText(ComplexGraph.this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//								} finally {
//									if (fis != null)
//									{
//										try {
//											fis.close();
//										} catch (IOException e) {
//											e.printStackTrace();
//											Toast.makeText(ComplexGraph.this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//										}
//									}
//								}
//							}
//						}
//					});
//					loadfiledialog.show();
//					dialog.dismiss();
//					break;
				default:
					dialog.dismiss();
					break;
				}
			}
		}).create();
		menudialog.setCanceledOnTouchOutside(true);
		menudialog.show();
	}

	public void addSeries(XYSeries dataSeries) {
		
		mDataset.addSeries(dataSeries);
		// TODO Auto-generated method stub
		
	}
}