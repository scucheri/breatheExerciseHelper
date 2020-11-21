package plugin.android.ss.com.breatheexercisehelper;

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
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

/**
 * ����ͼ��ؼ���
 * ����һ������achartengine��GraphicalView�Լ�һЩ���������ؼ�
 */
public class ComplexGraph extends LinearLayout
{
	static private float MAX_ZOOM_RATE = 1.5f; // ���ͼ��������
	
	// �ؼ�
	private RelativeLayout mRelativeLayout = null;
	private GraphicalView graphView = null;
	private Button buttonFullScreen = null;
	private Button buttonLock = null;
	private SeekBar seekBarX = null;
	//private VerticalSeekBar seekBarY = null;
	
	private PopupWindow mPopupWindow = null; // PopupWindow����ʵ��ȫ��Ч��
	
	private boolean isFullScreen = false; // �Ƿ�ȫ����־
	private boolean isLock = false; // �Ƿ������������ű�־
	
	private boolean enableGraphViewSingleTouch = true; // graphView���㴥��ʹ�ܣ����ڷ�ֹԤ����Ĳ�������Scale������ͬʱ����LongPress����
	
	private Zoom mZoomIn = null; // ���ڷŴ�
	private Zoom mZoomOut = null; // ������С
	
	private final XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset(); // ͼ�����ݼ������ڻ�������
	private final XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer(); // ��Ⱦ������ָ����ʽ���ƶ�Ӧ����
	
//	private String graphFileName = ""; // ��ǰ��ͼ���ļ���
	
	/**
	 * ���캯��
	 * 
	 * @param context �����Ļ���
	 */
	public ComplexGraph(Context context)
	{
        super(context);
        
        initGraphView();
        initAuxiliaryWidget();
    }
	
	/**
	 * ���캯��
	 * ʹ��xml�ļ����ֿؼ�ʱ�Զ�����
	 */
	public ComplexGraph(Context context, AttributeSet attrs)
	{
        super(context, attrs);
        
        initGraphView();
        initAuxiliaryWidget();
    }
	
	/**
     * ��ʼ��ͼ��ؼ�
     */
	private void initGraphView()
	{
		graphView = ChartFactory.getLineChartView(getContext(), mDataset, mRenderer); // ����GraphicalView
		
		XYChart chart = ((XYChart)graphView.getChart());
		mZoomIn = new Zoom(chart, true, mRenderer.getZoomRate()); // �����Լ���������
		mZoomOut = new Zoom(chart, false, mRenderer.getZoomRate()); // �����Լ���������
		
		// �������ƴ�����
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
		        	//	ComplexGraph.this.seekBarY.setVisibility(View.VISIBLE + View.INVISIBLE - ComplexGraph.this.seekBarY.getVisibility());
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
		
		// ���˫�����ƴ���
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
				case 1: // ���㴥��
					gesturedetector.onTouchEvent(arg1); // ���㴥�����Զ�������ƴ���������
					break;
				default: // ��㴥��
					ComplexGraph.this.enableGraphViewSingleTouch = false;
				}
				return false;
			}
		});
		
//		addView(graphView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	/**
	 * ��ʼ�������ؼ�
	 */
	private void initAuxiliaryWidget()
	{
		Context c = getContext();
		mRelativeLayout = new RelativeLayout(c);
		mRelativeLayout.addView(graphView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		// �Ӳ����ļ����ظ����ؼ�
//		LayoutInflater.from(getContext()).inflate(R.layout.view_complex_graph, mRelativeLayout, true);
//		buttonFullScreen = (Button)mRelativeLayout.findViewById(R.id.buttonFullScreen);
//		buttonLock = (Button)mRelativeLayout.findViewById(R.id.buttonLock);
//		seekBarX = (SeekBar)mRelativeLayout.findViewById(R.id.seekBarX);
//		seekBarY = (VerticalSeekBar)mRelativeLayout.findViewById(R.id.verticalSeekBarY);
		
		// ��̬���ɸ����ؼ�
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
	//	seekBarY = new VerticalSeekBar(c);
		//seekBarY.setLayoutParams(rl);
		
		mRelativeLayout.addView(buttonFullScreen);
		mRelativeLayout.addView(buttonLock);
		mRelativeLayout.addView(seekBarX);
		//mRelativeLayout.addView(seekBarY);
		
		// ���пؼ��������һ����Բ����У����������ComplexGraph����������ʵ��ȫ��Ч��
		addView(mRelativeLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		// �����ؼ���ʼ��Ϊ���ɼ�
		buttonFullScreen.setVisibility(View.INVISIBLE);
		buttonLock.setVisibility(View.INVISIBLE);
		seekBarX.setVisibility(View.VISIBLE);
		//seekBarY.setVisibility(View.INVISIBLE);
		
		buttonFullScreen.setBackgroundResource(R.drawable.ic_full_screen);
		buttonFullScreen.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
        	{
        		if (ComplexGraph.this.isFullScreen)
        		{
        			// �˳�ȫ��
        			ComplexGraph.this.exitFullScreen();
        		}
        		else
        		{
        			// ����ȫ��
        			ComplexGraph.this.fullScreen();
        		}
        	}
		});
		
//		buttonLock.setBackgroundResource(R.drawable.ic_lock);
//		buttonLock.setOnClickListener(new View.OnClickListener()
//		{
//			@Override
//			public void onClick(View arg0)
//        	{
//        		if (ComplexGraph.this.isLock)
//        		{
//        			buttonLock.setBackgroundResource(R.drawable.ic_unlock);
//        			ComplexGraph.this.isLock = false;
//        		}
//        		else
//        		{
//        			buttonLock.setBackgroundResource(R.drawable.ic_lock);
//        			ComplexGraph.this.isLock = true;
//        		}
//        	}
//		});
		
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
            			// ������������������ͬʱ����Y��
        				//axis = Zoom.ZOOM_AXIS_XY;
        			//	seekBarY.setProgress(progress);
        			}
        			
        			// ���м�Ϊ׼������λ��ȷ����С�Ŵ�������
        			if (distance > 0)
        			{
        				// λ��Ϊ��(����)����Ŵ�
        				ComplexGraph.this.mZoomIn.setZoomRate(distance * (MAX_ZOOM_RATE - 1) / half + 1);
        				ComplexGraph.this.mZoomIn.apply(axis);
        			}
        			if (distance < 0)
        			{
        				// λ��Ϊ��(����)������С
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
				// �������Ų�����ص��м�λ��
				seekBarX.setProgress(seekBarX.getMax()/2);
//				seekBarY.setProgress(seekBarY.getMax()/2);
			}
		});
		
//		seekBarY.setMax(1000);
//		seekBarY.setProgress(seekBarY.getMax()/2);
//		seekBarY.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
//		{
//			@Override
//			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
//        	{
//        		if (fromUser)
//        		{
//        			int axis = Zoom.ZOOM_AXIS_Y;
//        			int half = seekBarY.getMax()/2;
//        			int distance = progress - half;
//        			
//        			if (ComplexGraph.this.isLock)
//        			{
//        				// ������������������ͬʱ����X��
//        				axis = Zoom.ZOOM_AXIS_XY;
//        				seekBarX.setProgress(progress);
//        			}
//        			
//        			// ���м�Ϊ׼������λ��ȷ����С�Ŵ�������
//        			if (distance > 0)
//        			{
//        				// λ��Ϊ��(����)����Ŵ�
//        				ComplexGraph.this.mZoomIn.setZoomRate(distance * (MAX_ZOOM_RATE - 1) / half + 1);
//        				ComplexGraph.this.mZoomIn.apply(axis);
//        			}
//        			if (distance < 0)
//        			{
//        				// λ��Ϊ��(����)������С
//        				ComplexGraph.this.mZoomOut.setZoomRate(-distance * (MAX_ZOOM_RATE - 1) / half + 1);
//        				ComplexGraph.this.mZoomOut.apply(axis);
//        			}
//        			
//            		ComplexGraph.this.rePaintGraph();
//        		}
//        	}
//
//			@Override
//			public void onStartTrackingTouch(SeekBar seekBar)
//			{}
//
//			@Override
//			public void onStopTrackingTouch(SeekBar seekBar)
//			{
//				// �������Ų�����ص��м�λ��
//				seekBarX.setProgress(seekBarX.getMax()/2);
//				seekBarY.setProgress(seekBarY.getMax()/2);
//			}
//		});
	}
	
	/**
	 * ������Ⱦ��
	 */
	public XYMultipleSeriesRenderer getRenderer()
	{
		return mRenderer;
	}
	
	/**
     * �Ƿ�ȫ����
     */
	public boolean isFullScreen()
	{
		return isFullScreen;
	}
	
	/**
     * �ػ�ͼ��
     */
	public void rePaintGraph()
	{
		if (graphView != null)
		{
			graphView.repaint();
		}
	}
	
	/**
     * ����ȫ��
     */
	public void fullScreen()
	{
		Context context = getContext();
		if (context instanceof Activity)
		{
			((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			((Activity)context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

			DisplayMetrics metrics = getResources().getDisplayMetrics(); // ��ȡ��Ļ�ֱ���
			removeView(mRelativeLayout); // �����пؼ��ӱ�ComplexGraph�Ƴ�
			
			// �½�ȫ����PopupWindow����������пؼ�
			mPopupWindow = new PopupWindow(mRelativeLayout, metrics.widthPixels, metrics.heightPixels);
			mPopupWindow.showAtLocation(this, Gravity.CENTER, 0, 0);
			
			buttonFullScreen.setBackgroundResource(R.drawable.ic_return_from_full_screen); // ����ȫ����ťͼ��
			isFullScreen = true; // ����ȫ����־
		}
	}
	
	/**
     * �˳�ȫ��
     */
	public void exitFullScreen()
	{
		Context context = getContext();
		if (context instanceof Activity)
		{
			((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			((Activity)context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			
			mPopupWindow.dismiss(); // �ر�ȫ����PopupWindow
			addView(mRelativeLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)); // �����пؼ���ӻر�ComplexGraph
			
			buttonFullScreen.setBackgroundResource(R.drawable.ic_full_screen); // ����ȫ����ťͼ��
			isFullScreen = false; // ����ȫ����־
		}
	}
	
	/**
     * ����µ������������Ӧ��Ⱦ��
     * 
     * @param series ��������
     * @param renderer ��Ⱦ��
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
     * ���ͼ�����ݻ���
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
     * ��ȡ��Ⱦ��
     */
	public XYMultipleSeriesRenderer getXYMultipleSeriesRenderer()
	{
		return mRenderer;
	}
	
	/**
     * ��ȡX����Сֵ
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
     * ��ȡX�����ֵ
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
     * ��ȡY����Сֵ
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
		
		for (int i = 1; i < count; i++)
		{
			miny = Math.min(miny, mDataset.getSeriesAt(i).getMinY());
		}
		
		return miny;
	}
	
	/**
     * ��ȡY�����ֵ
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
     * ͼ����󻯳����ɼ�����
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
		
		
		
		if(getMaxY()<=20)
		{
		mRenderer.setRange(new double[]{getMinX() - margin, getMaxX() + margin,  getMinY()- margin,  20 + margin});    
		}
		
		else if((getMaxY()>20)&&(getMaxY()<=30))
		{
			mRenderer.setRange(new double[]{getMinX() - margin, getMaxX() + margin, getMinY() - margin,  30 + margin});    	
			
		}
		
		else if((getMaxY()>30)&&(getMaxY()<=40))
		{
			mRenderer.setRange(new double[]{getMinX() - margin, getMaxX() + margin, getMinY() - margin,  40 + margin});    	
			
		}
		else if((getMaxY()>40)&&(getMaxY()<=50))
		{
			mRenderer.setRange(new double[]{getMinX() - margin, getMaxX() + margin, getMinY() - margin,  50 + margin});    	
			
		}
		else if((getMaxY()>50)&&(getMaxY()<=65))
		{
			mRenderer.setRange(new double[]{getMinX() - margin, getMaxX() + margin, getMinY() - margin,  65 + margin});    	
			
		}
		else if((getMaxY()>65)&&(getMaxY()<=80))
		{
			mRenderer.setRange(new double[]{getMinX() - margin, getMaxX() + margin, getMinY() - margin,  80 + margin});    	
			
		}
		else if((getMaxY()>80)&&(getMaxY()<=100))
		{
			mRenderer.setRange(new double[]{getMinX() - margin, getMaxX() + margin,  getMinY()- margin,  100 + margin});    	
			
		}
		else 
		{
			mRenderer.setRange(new double[]{getMinX() - margin, getMaxX() + margin, getMinY() - margin,  getMaxY() + margin}); 	
		}
		rePaintGraph();
	}
	
	
	
	
//	public void mainActivity_FitGraph(double singalPosition ) {
//		// TODO Auto-generated method stub
//		
//		double margin = Math.min((getMaxX() - getMinX()) / 500, (getMaxY() - singalPosition) / 500);
//		if (margin <= 0)
//		{
//			margin = 0.001;
//		}
//	//	mRenderer.setRange(new double[]{getMinX() - margin, getMaxX() + margin, singalPosition - margin, getMaxY() + margin});
////		mRenderer.setRange(new double[]{getMinX() - margin, 100+ margin, singalPosition - margin, getMaxY() + margin});
//		
//		
//		
//		if(getMaxY()<=20)
//		{
//		mRenderer.setRange(new double[]{getMinX() - margin, getMaxX() + margin,  singalPosition- margin,  20 + margin});    
//		}
//		
//		else if((getMaxY()>20)&&(getMaxY()<=30))
//		{
//			mRenderer.setRange(new double[]{getMinX() - margin, getMaxX() + margin, singalPosition - margin,  30 + margin});    	
//			
//		}
//		
//		else if((getMaxY()>30)&&(getMaxY()<=40))
//		{
//			mRenderer.setRange(new double[]{getMinX() - margin, getMaxX() + margin, singalPosition - margin,  40 + margin});    	
//			
//		}
//		else if((getMaxY()>40)&&(getMaxY()<=50))
//		{
//			mRenderer.setRange(new double[]{getMinX() - margin, getMaxX() + margin, singalPosition - margin,  50 + margin});    	
//			
//		}
//		else if((getMaxY()>50)&&(getMaxY()<=65))
//		{
//			mRenderer.setRange(new double[]{getMinX() - margin, getMaxX() + margin, singalPosition - margin,  65 + margin});    	
//			
//		}
//		else if((getMaxY()>65)&&(getMaxY()<=80))
//		{
//			mRenderer.setRange(new double[]{getMinX() - margin, getMaxX() + margin, singalPosition - margin,  80 + margin});    	
//			
//		}
//		else if((getMaxY()>80)&&(getMaxY()<=100))
//		{
//			mRenderer.setRange(new double[]{getMinX() - margin, getMaxX() + margin,  singalPosition- margin,  100 + margin});    	
//			
//		}
//		else 
//		{
//			mRenderer.setRange(new double[]{getMinX() - margin, getMaxX() + margin, singalPosition - margin,  getMaxY() + margin}); 	
//		}
//		rePaintGraph();
//		
//	}
	
	/**
     * �ȱ���ʾ��X����Y�����ű�������Ϊ��ͬ
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
     * ���������Ի���
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
				case 0: // ����ԭ��
					ComplexGraph.this.mRenderer.setRange(new double[]{0, ComplexGraph.this.getMaxX(), 0, ComplexGraph.this.getMaxY()});
					ComplexGraph.this.rePaintGraph();
					break;
				case 1: // �ȱ���ʾ
					sameZoomXY();
					break;
				case 2: // ���ͼ��
					ComplexGraph.this.clearSeriesAndRenderer();
					break;
//				case 3: // �л���λ
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
//				case 3: // ����ͼ��
//					// �Զ��������ļ���
//					Context context = ComplexGraph.this.getContext();
//					String newfilename = (context.getString(R.string.new_graph_file) + AppGlobal.getCurrentTime() + AppGlobal.DOT + AppGlobal.GRAPH_FILE_SUFFIX); // new_graph_file��������ʱ������չ��
//					
//					OpenFileDialog savefiledialog = new OpenFileDialog(context, newfilename, new String[]{AppGlobal.GRAPH_FILE_SUFFIX}, true);
//					savefiledialog.setOnClickOK(new OpenFileDialog.OnClickOK()
//					{
//						@Override
//						public void onClickOK(String filepath, String filename)
//						{
//							ComplexGraph.this.graphFileName = filename; // �������ļ���
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
//				case 4: // ����ͼ��
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
//							// �ж���չ���Ƿ�Ϊͼ���ļ�
//							if (0 != suffix.compareTo(graphfilesuffix))
//							{
//								// ������ʽ����չ������ʾ�޷�ʶ��
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
	
	public double getStantardPosition()
	{
		
		return MainActivity.stantardPosition;
		
	}

	
}