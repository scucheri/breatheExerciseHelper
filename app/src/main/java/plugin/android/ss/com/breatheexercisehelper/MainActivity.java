package plugin.android.ss.com.breatheexercisehelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;



import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{
	/**
	 * ��������ͨ��UUID
	 */
	static final public String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	
	// ͨ�������Ӽ���ǽ��յ���������
	/**
	 * ������
	 */
	static final public int REV_BEAT_PACKET	= 0x1000;
	
	// ͨ�����������Ƿ��͵���������
	/**
	 * �������ӳɹ�ȷ��
	 */
	static final public int SEND_CONNECT_OK = 0;
	
	/**
	 * ��ʼ�������
	 */
	static final public int SEND_START_DETECTION = 1;
	
	/**
	 * �Ͽ�������������
	 */
	static final public int SEND_DISCONNECT_BTH	= 2;
	
	/**
	 * ֹͣ�������
	 */
	static final public int SEND_STOP_DETECTION = 3;
	
	static final public int MAXX=500;
	
	static final public double TIME_BETWEEN_TWO=0.0207914724252083;
	
	static final public double MaxVoltage=2.5;
	 
	
//	static final public int displayDotNumber=10;
	static final public int A=1;
	
	static final public int N=20;
	
	static final public double PositiveK=0.002;
	
	static final public double NegativeK=-0.002;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
	
	static final public int TimerInterval=7;
	
	public static final String ENCODING = "UTF-8";
	
	public static final int SaveTimeInterval=2000;
	
	public static final long MaxInhaleTime=15000,MaxExhaleTime=20000;
	
	
	

	
//	static final public double STANTARD_INHALE_TIME;
//	
//	static final public double STANTARD_EXHALE_TIME;
	
	private double kArray[];
	
	private double stantardArray[];
//	private double stantardDisplayArray[]=new double[]{0.76,0.73,0.71,0.7,0.68,0.67,0.65,0.64,0.62,0.61,0.6,0.59,0.58,0.56,0.55,0.53,0.52,0.51,0.5,0.49,0.48,0.49,0.49,0.47,0.47,0.46,0.44,0.4,0.39,0.37,0.35,0.35,0.34,0.33,0.32,0.3,0.29,0.28,0.26,0.24,0.22,0.21,0.19,0.17,0.16,0.16,0.15,0.14,0.13,0.12,0.11,0.1,0.09,0.09,0.09,0.09,0.08,0.08,0.08,0.09,0.2,0.31,0.55,0.78,1.01,1.24,1.39,1.49,1.55,1.61,1.65,1.68,1.7,1.7,1.68,1.65,1.63,1.61,1.59,1.56,1.54,1.51,1.47,1.43,1.4,1.36,1.31,1.26,1.21,1.16,1.12,1.08,1.05,1.02,0.99,0.96,0.94,0.91,0.89,0.87,0.84,0.82,0.79,0.78,0.77,0.75,0.74,0.73,0.72,0.7,0.69,0.68,0.66,0.65,0.64,0.63,0.62,0.62,0.61,0.6,0.6,0.59,0.56,0.55,0.54,0.52,0.52,0.5,0.49,0.49,0.49,0.49,0.48,0.47,0.47,0.46,0.45,0.44,0.42,0.42,0.43,0.43,0.44,0.43,0.41,0.41,0.4,0.41,0.43,0.49,0.58,0.77,0.95,1.14,1.29,1.41,1.49,1.58,1.64,1.69,1.73,1.73,1.71,1.64,1.59,1.56,1.56,1.59,1.66,1.7,1.72,1.72,1.73,1.72,1.71,1.7,1.67,1.65,1.63,1.6,1.55,1.51,1.47,1.41,1.34,1.29,1.28,1.25,1.22,1.2,1.18,1.12,1.06,1.0,0.96,0.92,0.89,0.87,0.85,0.84,0.83,0.81,0.81,0.79,0.77,0.77,0.76,0.74,0.73,0.71,0.68,0.67,0.65,0.63,0.6,0.58,0.55,0.54,0.53,0.5,0.5,0.48,0.47,0.47,0.46,0.45,0.45,0.44,0.44,0.43,0.44,0.47,0.57,0.74,0.9,1.05,1.24,1.36,1.49,1.56,1.6,1.64,1.67,1.67,1.66,1.68,1.69,1.68,1.67,1.68,1.67,1.69,1.7,1.72,1.71,1.69,1.69,1.67,1.64,1.57,1.46,1.39,1.33,1.27,1.22,1.18,1.14,1.1,1.06,1.0,0.95,0.92,0.91,0.89,0.87,0.87,0.86,0.85,0.84,0.84,0.81,0.79,0.77,0.76,0.76,0.76,0.76,0.75,0.75,0.75,0.76,0.74,0.72,0.71,0.7,0.67,0.63,0.61,0.59,0.57,0.56,0.55,0.53,0.52,0.51,0.5,0.48,0.47,0.45,0.43,0.42,0.41,0.41,0.41,0.4,0.39,0.39,0.39,0.38,0.37,0.35,0.35,0.34,0.35,0.36,0.36,0.36,0.36,0.35,0.34,0.33,0.31,0.29,0.28,0.27,0.26,0.26,0.25,0.24,0.24,0.24,0.23,0.24,0.28,0.36,0.48,0.59,0.7,0.85,1.03,1.17,1.27,1.35,1.44,1.51,1.57,1.6,1.62,1.63,1.65,1.68,1.68,1.66,1.64,1.64,1.64,1.62,1.62,1.64,1.64,1.64,1.64,1.6,1.56,1.52,1.48,1.45,1.41,1.38,1.34,1.32,1.29,1.27,1.25,1.22,1.17,1.13,1.09,1.05,1.03,1.02,0.99,0.98,0.97,0.97,0.98,0.96,0.95,0.92,0.9,0.89,0.88,0.85,0.83,0.81,0.79,0.78,0.77,0.76,0.74,0.73,0.7,0.69,0.68,0.67,0.66,0.65,0.63,0.61,0.6,0.59,0.58,0.57,0.55,0.55,0.54,0.53,0.52,0.51,0.51,0.5,0.49,0.47,0.47,0.45,0.44,0.45,0.44,0.43,0.42,0.42,0.41,0.4,0.39,0.39,0.38,0.38,0.38,0.37,0.36,0.35,0.36,0.35,0.34,0.34,0.33,0.32,0.33,0.34,0.38,0.41,0.46,0.54,0.65,0.77,0.88,1.01,1.13,1.27,1.39,1.53,1.59,1.6,1.62,1.67,1.68,1.68,1.67,1.66,1.65,1.65,1.65,1.62,1.59,1.56,1.55,1.52,1.5,1.48,1.49,1.48,1.45,1.43,1.4,1.35,1.25,1.19,1.1,1.0,0.91,0.84,0.78,0.74,0.71,0.67,0.63,0.61,0.58,0.58,0.57,0.57,0.58,0.58,0.58,0.58,0.58,0.57,0.58,0.57,0.57,0.58,0.57,0.57,0.57,0.57,0.57,0.57,0.57,0.57,0.56,0.56,0.56,0.55,0.55,0.55,0.54,0.53,0.52,0.52,0.51,0.51,0.51,0.5,0.49,0.49,0.48,0.47,0.47,0.47,0.46,0.45,0.45,0.45,0.44,0.44,0.42,0.42,0.41,0.41,0.4,0.39,0.38,0.37,0.37,0.37,0.36,0.35,0.35,0.34,0.34,0.33,0.33,0.32,0.32,0.31,0.31,0.31,0.33,0.37,0.5,0.68,0.84,1.03,1.22,1.34,1.44,1.49,1.57};
	private int dataIndex;
	
	private double dataArrayLeftToRight[] ;
	private int newDataIndex;
		
	private int  haleFlag;
	
		
	// �¼�����ö��
	static private enum EventType { BLUETOOTH_SOCKET_UNCONNECTED, // ����δ�����¼�
									BLUETOOTH_SOCKET_CONNECTING, // �������������¼�
									BLUETOOTH_SOCKET_CONNECTED,	// �����������¼�
									BLUETOOTH_SOCKET_DISCONNECTING,	// �������ڶϿ��¼�
//									INHALE_TIME_START,
//									INHALE_TIME_STOP,
//									EXHALE_TIME_START,
//									EXHALE_TIME_STOP,
									DISPLAY_INHALE_TIME,
									DISPLAY_EXHALE_TIME,
									START_TO_INHALE,
									START_TO_EXHALE,
									DISPLAY_SOC,
									UPDATE_SCORE
									};
		
	// �ؼ�
	private ComplexGraph graphView = null;

	private Button buttonConnect = null,buttonSet = null,buttonUpdatePower=null,buttonQueryData=null;
	
	private Handler eventsHandler = null; // �¼�Handler��������Ӧ�����¼�
	
	public boolean enableCommunicate = false; // ����ͨ���߳�ʹ�ܿ���
	public boolean isCommunicateThreadRunning = false; // ����ͨ���߳����б�־
	public BluetoothDevice bluetoothDeviceSelected = null; // ��ѡ�����ӵ������豸
	public BluetoothSocket bluetoothSocket = null; // �����׽��֣��������ⲿ�����豸(���������ģ��)ͨ��
	
	public boolean isDetecting = false; // ���ڼ���־
	
	private XYSeries dataSeries,stantardSeriesTime,stantardSeriesY; // ����
	private XYSeriesRenderer dataRenderer,stantardTimeRenderer,stantardRenderer; // ��Ⱦ��
	
	
	private int graphlength ;
	
	
	private Chronometer testTime ;
	
	private TextView inhaleTimeView,exhaleTimeView,haleFrequencyView,haleIntensityView,haleRatioView,SOCView;
    private long time = 0,inhaleTime,exhaleTime;
	private long startTime,endTime;
	private long periodStartTime,periodEndTime,periodTime;
	private int timerCount=0;
	private double haleFrequency,SOC;
	private double haleIntensity,haleIntensityMin,haleIntensityMax,haleRatio,maxHaleRatio;
	
	private   DecimalFormat df,df1;

    
	private int dataNumber;
	
	
	
	
	double[] dataArayRightToLeft=new double[MAXX];
	
	
	static public final double PI=Math.PI;
	
	static public double stantardPosition,signalPosition;
	private double stantardIntensity,stantardFrequency,stantardRatio;
	private double stantardInhaleT,stantardExhaleT,stantardYT,stantardInhaleW,stantardExhaleW,stantardExhaleT1,stantardYT1,stantardExhaleW1;
	double[] stantardY;
	double[] stantardYSignalPosition;
	private int redFlag;
	
    private double displayTime;
	private int displayDotNumber;
	private double sampleOneOf;
	private int DOT_OF_ONE_MINUTE; 
	
	private String allDataFile="ȫ����������.txt";
	private String halePeriodDataFile="������������.txt";
	private String abnormalDataFile="�쳣��������.txt";
	
	Context mContext;
	
	private long saveTimeStart1,saveTimeStop1,saveTime1;
	
	private long saveTimeStart2,saveTimeStop2,saveTime2;
	
	
	private double maxHaleFrequency,minHaleIntensity;
	
	private String SOCString,dataString;
	
	private int receiveFlag=3;
	
	private double xSum,ySum,xySum,x2Sum,y2Sum,xyRelation,xyRelationAverage,periodNumber,score,averageScore;
	private TextView scoreView;
	
	private double magnifyN;
	private double magnifyTime;
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		System.out.println("dfhhbgre");
		
		super.onRestart();
//		if(setFlag==1)
//        {
//        	setFlag=0;
//		 Intent setIntent = getIntent();
//			String stantardFrequencyStr = setIntent.getStringExtra("stantardFrequency");
//			System.out.println(stantardFrequencyStr+"dfhhb");
//			
//			stantardFrequency=Double.parseDouble(stantardFrequencyStr);
//
//			String stantardRatioStr = setIntent.getStringExtra("stantardRatio");
//			stantardRatio=Double.parseDouble(stantardRatioStr);
//			
//			String stantardIntensityStr = setIntent.getStringExtra("stantardIntensity");
//			stantardIntensity=Double.parseDouble(stantardIntensityStr);
//			
//			String stantardPositionStr = setIntent.getStringExtra("stantardPosition");
//			stantardPosition=Double.parseDouble(stantardPositionStr);
//        }
		
		
		
	}

	
	@Override        
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mContext=MainActivity.this;
        
//      if(saveSet())
//        saveSet();
        loadSet();
        magnifyTime=(100/MaxVoltage)*magnifyN;
      
    
        // ��ȡ�ؼ�
        graphView = (ComplexGraph)findViewById(R.id.graphicView);
   
//        buttonStart = (Button)findViewById(R.id.buttonStart);
        buttonConnect = (Button)findViewById(R.id.buttonConnect);
        buttonSet = (Button)findViewById(R.id.buttonSet);
        buttonUpdatePower = (Button)findViewById(R.id.buttonUpdatePower);
        buttonQueryData = (Button)findViewById(R.id.buttonQueryData);
        
        
        testTime = (Chronometer) findViewById(R.id.testTime);
//       inhaleTime = (Chronometer) findViewById(R.id.inhaleTime);
//       exhaleTime = (Chronometer) findViewById(R.id.exhaleTime);
       
       inhaleTimeView = (TextView) findViewById(R.id.inhaleTimeView);
       exhaleTimeView = (TextView) findViewById(R.id.exhaleTimeView);
       haleFrequencyView = (TextView) findViewById(R.id.haleFrequencyView);
       haleIntensityView = (TextView) findViewById(R.id.haleIntensityView);
       haleRatioView = (TextView) findViewById(R.id.haleRatioView);
      SOCView = (TextView) findViewById(R.id.SOCView);
      scoreView = (TextView) findViewById(R.id.scoreView);
       inhaleTimeView.setText("����ʱ��");
       exhaleTimeView.setText("����ʱ��");
       haleFrequencyView.setText("����Ƶ��:");
       haleIntensityView.setText("����ǿ��:");
       haleRatioView.setText("����ʱ��/����ʱ��:");	
       SOCView.setText("ʣ�����:"+" "+(int)SOC+"%");
       scoreView.setText("�����÷�:"+" "+(int)score+"��");
       
        kArray=new double[N];
     
        
        haleFlag=1;
        
       df =new DecimalFormat("#####0.00");
       df1 =new DecimalFormat("#####0.0");
        
        testTime.setFormat("����ʱ��:%s");
//        inhaleTime.setFormat("����ʱ��:%s");
//        exhaleTime.setFormat("����ʱ��:%s");
        
        
        
        
		
		
       
        
        
        

   //Ϊ0 ��ʾ������Ϊ1��ʾ����
        
        XYMultipleSeriesRenderer renderer = graphView.getRenderer();
        renderer.setApplyBackgroundColor(true); // �����Ƿ���ʾ����ɫ
		renderer.setBackgroundColor(Color.BLACK); // ���ñ���ɫ
		renderer.setMargins(new int[]{10, 40, 40, 10}); // ����ͼ�����߿�(��/��/��/��)
		renderer.setChartTitleTextSize(40);
        renderer.setAxisTitleTextSize(40); // ������������ֵĴ�С
        renderer.setLabelsTextSize(20); // ���ÿ̶���ʾ���ֵĴ�С(XY�ᶼ�ᱻ����)
        renderer.setLegendTextSize(20); // ͼ�����ִ�С
        renderer.setPointSize(2); // ���õ�Ĵ�С(ͼ����ʾ�ĵ�Ĵ�С��ͼ���е�Ĵ�С���ᱻ����)
        renderer.setShowGrid(true); // �Ƿ���ʾ����
        renderer.setSelectableBuffer(10); // ���õ�Ļ���뾶ֵ(��ĳ�㸽�����ʱ,���Χ�ڶ����������)
//		renderer.setXTitle(MainActivity.this.getString(R.string.potential) + "(" + 
//				MainActivity.this.getResources().getStringArray(R.array.potential_unit)[AppGlobal.DEFAULT_POTENTIAL_UNIT] + ")"); // X��˵��
//		renderer.setYTitle(MainActivity.this.getString(R.string.current) + "(" + 
//				MainActivity.this.getResources().getStringArray(R.array.current_unit)[AppGlobal.DEFAULT_CURRENT_UNIT] + ")"); // Y��˵��
		renderer.setLegendHeight(100); // ����ͼ������λ�ø߶�
		renderer.setAxesColor(Color.argb(255, 250, 250, 250)); // ����ɫ
		renderer.setGridColor(Color.argb(150, 200, 200, 200)); // ������ɫ
		renderer.setLabelsColor(Color.argb(180, 180, 180, 180)); // ��ע��ɫ
		renderer.setXLabels(10); // X��̶�����
		renderer.setYLabels(10); // Y��̶�����
		renderer.setXLabelsColor(Color.argb(255, 255, 255, 255)); // X���ǩ������ɫ
		renderer.setYLabelsColor(0, Color.argb(255, 255, 255, 255)); // Y���ǩ������ɫ
		
		renderer.setChartTitle("��������");
		 renderer.setXTitle("ʱ�䣨s��");
		 renderer.setYTitle("ǿ��");
		 
		 
		 dataRenderer = new XYSeriesRenderer();
			
         dataRenderer.isHighlighted();
         dataRenderer.setFillBelowLineColor(Color.YELLOW);
	        
		dataRenderer.setColor(0xFF00FF00);
		dataRenderer.setPointStyle(PointStyle.CIRCLE);
		dataRenderer.setFillPoints(true);
		
		
		
		
		 
		stantardRenderer = new XYSeriesRenderer();
		stantardRenderer.setColor(Color.RED);
		stantardRenderer.setPointStyle(PointStyle.POINT);
		stantardRenderer.setFillPoints(true);
		stantardRenderer.setShowLegendItem(true);
		stantardRenderer.setLineWidth(4);
		
		
		stantardTimeRenderer = new XYSeriesRenderer();
		stantardTimeRenderer.setColor(Color.LTGRAY);
		stantardTimeRenderer.setPointStyle(PointStyle.POINT);
		stantardTimeRenderer.setFillPoints(true);
		stantardTimeRenderer.setShowLegendItem(true);
		stantardTimeRenderer.setLineWidth(1);
		
		
	        
	        stantardSeriesY = new XYSeries("��׼����");
	        graphView.addSeriesAndRenderer(stantardSeriesY, stantardRenderer);
	        
	       dataSeries = new XYSeries("��������");
	 		graphView.addSeriesAndRenderer(dataSeries, dataRenderer);
	 		
	 		
	 		 stantardSeriesTime = new XYSeries("");
// 			graphView.addSeriesAndRenderer(stantardSeries, stantardTimeRenderer);
 			graphView.addSeriesAndRenderer(stantardSeriesTime, stantardTimeRenderer);
// 			
	        
			
	        refreshStantardCurve( stantardFrequency, stantardRatio, stantardPosition, stantardIntensity,displayTime,sampleOneOf);
		        
		        
		        
		       
		
		
//		buttonStart.setOnClickListener(new OnClickListener()
//    	{
//    		public void onClick(View v)
//        	{
//    			if (null != bluetoothSocket)
//    			{
//    				if (isDetecting)
//    				{
//    					MainActivity.this.buttonStart.setText(R.string.start);
//    					MainActivity.this.sendDataToDetector(MainActivity.SEND_STOP_DETECTION);
//    				}
//    				else
//    				{
//    					dataSeries.clear(); // ����վ�����
//    					MainActivity.this.buttonStart.setText(R.string.stop);
//    					MainActivity.this.sendDataToDetector(MainActivity.SEND_START_DETECTION);
//    				}
//    				isDetecting = !isDetecting;
//    			}
//        	}
//        });
		
		buttonConnect.setOnClickListener(new OnClickListener()
    	{
    		public void onClick(View v)
        	{
    			if (null == bluetoothSocket)
    			{
    				Intent intent = new Intent();
            		intent.setClass(MainActivity.this, BTHOperationActivity.class);
            		MainActivity.this.startActivityForResult(intent, 0); // requestCodeΪ0��ʾBTHOperationActivity
    			}
    			else
    			{
    				MainActivity.this.enableCommunicate = false; // Ҫ��ֹͣͨ���߳�
    				MainActivity.this.bluetoothDeviceSelected = null;
    				MainActivity.this.sendEventsBundle(MainActivity.EventType.BLUETOOTH_SOCKET_DISCONNECTING);
    			}
        	}
        });
		
		
		buttonSet.setOnClickListener(new OnClickListener()
    	{
    		public void onClick(View v)
        	{
    			Intent setIntent = new Intent(MainActivity.this, SetActivity.class);
    			Bundle stantardInfo=new Bundle();
    			stantardInfo.putDouble("stantardFrequency", stantardFrequency);
    			stantardInfo.putDouble("stantardRatio", stantardRatio);
    			stantardInfo.putDouble("stantardPosition", stantardPosition);
    			stantardInfo.putDouble("stantardIntensity", stantardIntensity);
    			stantardInfo.putDouble("displayTime", displayTime);
    			stantardInfo.putDouble("sampleOneOf", sampleOneOf);
    			stantardInfo.putDouble("maxHaleFrequency", maxHaleFrequency);
    			stantardInfo.putDouble("minHaleIntensity", minHaleIntensity);
    			stantardInfo.putDouble("magnifyN", magnifyN);
    			stantardInfo.putDouble("signalPosition", signalPosition);
    			
				setIntent.putExtras(stantardInfo);
				
				
				MainActivity.this.startActivityForResult(setIntent,1);
    			
    	
        	}
        });
		
		
		buttonUpdatePower.setOnClickListener(new OnClickListener()
    	{
    		public void onClick(View v)
        	{
    			//��Ƭ����������
    			sendMessage("1");
        	}
        });
		
		buttonQueryData.setOnClickListener(new OnClickListener()
    	{
    		public void onClick(View v)
        	{
    			 Intent queryIntent=new Intent();  
    			 
    			 queryIntent.setClass(MainActivity.this,QueryActivity.class);
    				MainActivity.this.startActivity(queryIntent);
    			  
        	}
        });
		
		setEventsHandler();
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
		switch (requestCode)
		{
		case 0: // return from BTHOperationActivity
			System.out.println(resultCode+"");
			if (resultCode == RESULT_OK)
	    	{
				bluetoothDeviceSelected = data.getParcelableExtra("BluetoothDevice");
				connectBTHDevice(); // �����豸
	    	}
			break;
			
		case 1:
			System.out.println(resultCode+"");
			
			if (resultCode == RESULT_OK)
			{
			Bundle stantardInfoSet = data.getExtras();
			 stantardFrequency=stantardInfoSet.getDouble("stantardFrequency");
		    stantardRatio=stantardInfoSet.getDouble("stantardRatio");
			 stantardPosition=stantardInfoSet.getDouble("stantardPosition");
			 stantardIntensity=stantardInfoSet.getDouble("stantardIntensity");
			 displayTime=stantardInfoSet.getDouble("displayTime");
			 sampleOneOf=stantardInfoSet.getDouble("sampleOneOf");
			 maxHaleFrequency=stantardInfoSet.getDouble("maxHaleFrequency");
			 minHaleIntensity=stantardInfoSet.getDouble("minHaleIntensity");
			 magnifyN=stantardInfoSet.getDouble("magnifyN");
			 signalPosition=stantardInfoSet.getDouble("signalPosition");
			 
			 System.out.println(stantardFrequency+"");
			 
			 magnifyTime=(100/MaxVoltage)*magnifyN;
			 refreshStantardCurve( stantardFrequency, stantardRatio, stantardPosition, stantardIntensity,displayTime,sampleOneOf);
			}
			break;
			
		default:
			break;
		}
	}
    
    

	private void refreshStantardCurve(double stantardFrequency1,
			double stantardRatio1, double stantardPosition1,
			double stantardIntensity1, double displayTime1, double sampleOneOf1) {
		stantardFrequency=stantardFrequency1;
		stantardRatio=stantardRatio1;
		stantardPosition=stantardPosition1;
		stantardIntensity=stantardIntensity1;
		displayTime=displayTime1;
		sampleOneOf=sampleOneOf1;
		
	displayDotNumber=(int) (displayTime/(sampleOneOf*TIME_BETWEEN_TWO));
	 DOT_OF_ONE_MINUTE=(int) (60/(sampleOneOf*TIME_BETWEEN_TWO));
		
		stantardY=new double[displayDotNumber];
		stantardYSignalPosition=new double[displayDotNumber];
		 stantardArray=new double[displayDotNumber];
		   dataArrayLeftToRight=new  double[displayDotNumber] ;
	        for(int k=0;k<displayDotNumber;k++)
	        {
	        	dataArrayLeftToRight[k]=signalPosition/magnifyTime;
	        }
		
		 stantardInhaleT=(DOT_OF_ONE_MINUTE*4)/((1+stantardRatio)*stantardFrequency);  
	        stantardInhaleW=2*PI/stantardInhaleT;
	        
	        stantardExhaleT=(DOT_OF_ONE_MINUTE*4*stantardRatio)/((1+stantardRatio)*stantardFrequency); 
	        stantardExhaleW=2*PI/stantardExhaleT;
	        
	        stantardExhaleT1=(DOT_OF_ONE_MINUTE*2*stantardRatio)/((1+stantardRatio)*stantardFrequency); 
	        stantardExhaleW1=2*PI/stantardExhaleT;
	        stantardYT1=stantardInhaleT/4+stantardExhaleT1/2;
	        
//	        stantardYT=stantardInhaleT/4+stantardExhaleT/2;
	        stantardYT=stantardInhaleT/4+stantardExhaleT/4;
	        
	        stantardSeriesTime.clear();
	        
	        
	        for(int k=0;k<displayDotNumber;k++)
	        {
	        	if((k%stantardYT>=0)&&(k%stantardYT<=stantardInhaleT/4))
	        	{
	        		
	        	stantardY[k]=stantardIntensity*Math.sin(stantardInhaleW*(int)(k%stantardYT));
	        	if(redFlag==0)
	        	{
	        		redFlag=1;
//	            stantardSeriesTime.add((k-1)*TIME_BETWEEN_TWO*sampleOneOf, 0);
	        	stantardSeriesTime.add(k*TIME_BETWEEN_TWO*sampleOneOf, stantardPosition+stantardY[k]);
	        	}
	        	
	        	}
	        	
	        	if((k%stantardYT>stantardInhaleT/4)&&(k%stantardYT<stantardYT))
	        	{
//	        		if((k%stantardYT>stantardInhaleT/4)&&(k%stantardYT<stantardInhaleT/4+0))
//	        		{
//	        		
////	        	stantardY[k]=stantardIntensity*Math.sin(stantardExhaleW*((int)(k%stantardYT)+stantardExhaleT/4-stantardInhaleT/4));
//	        	stantardY[k]=stantardIntensity/2+stantardIntensity/2*Math.cos(stantardExhaleW1*((int)(k%stantardYT1)-stantardInhaleT/4));
//	        		
//	        		}
//	        		else
//	        		{
	        			stantardY[k]=stantardIntensity+stantardIntensity*Math.sin(stantardExhaleW*((int)(k%stantardYT)+stantardExhaleT/2-stantardInhaleT/4));
	        		
//	        		}
	        		if(redFlag==1)
	        	{
	        		redFlag=0;
//	        		stantardSeriesTime.add((k-1)*TIME_BETWEEN_TWO*sampleOneOf, 0);
	        	stantardSeriesTime.add(k*TIME_BETWEEN_TWO*sampleOneOf, stantardPosition+stantardY[k]);
	        	}
	        	}
	        	
	        	
	        }
	        
	        stantardSeriesY.clear();
	       
	        for (int k = 0; k < displayDotNumber; k++) {  
	            stantardSeriesY.add(k*TIME_BETWEEN_TWO*sampleOneOf, stantardPosition+stantardY[k]);  
	            
	            //ȡyv[]��������6������ΪkArray�ĳ�Ա
	        }  
	        
	        for (int k = 0; k < displayDotNumber; k++) {  
	        stantardYSignalPosition[k]=signalPosition;
	            stantardSeriesTime.add(k*TIME_BETWEEN_TWO*sampleOneOf, stantardYSignalPosition[k]);  
	            
	        }  
	        
	        
	        MainActivity.this.graphView.fitGraph();
		
		// TODO Auto-generated method stub
	        
	}


	@Override
	public void onBackPressed()
	{

		// Toast.makeText(this,"���ε÷�"+xyRelationAverage+"", Toast.LENGTH_LONG).show();
		
		AlertDialog.Builder finishbuilder = new AlertDialog.Builder(this);
		
		finishbuilder.setTitle(R.string.sure_to_exit);
		
	
			finishbuilder.setMessage("����ƽ���÷֣�"+(int)averageScore).setIcon(R.drawable.breath1);
	

		finishbuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				MainActivity.this.sendDataToDetector(MainActivity.SEND_DISCONNECT_BTH); // �����Ƿ��ͶϿ�������Ϣ
				MainActivity.this.enableCommunicate = false;
				
				saveSet();
				MainActivity.this.finish();
			}
		});
		
		finishbuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		
		Dialog finishdialog = finishbuilder.create();
		finishdialog.setCanceledOnTouchOutside(true);
		finishdialog.show();
	}
	
	@Override
    protected void onDestroy()
	{
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
    }
    
	/**
     * ���������豸
     */
    public void connectBTHDevice()
    {
    	if (bluetoothDeviceSelected != null)
    	{
    		new Thread()
    		{
    			public void run()
    			{
    				// ����״̬Ϊ�������� 
    				MainActivity.this.sendEventsBundle(MainActivity.EventType.BLUETOOTH_SOCKET_CONNECTING);
    				
					// ����
    				BluetoothSocket tempsocket = null;
    				try
    				{
						// ����һ��Socket���ӣ�SPP_UUID��ʾ����ģʽ��UUID��
    					tempsocket = bluetoothDeviceSelected.createRfcommSocketToServiceRecord(UUID.fromString(MainActivity.SPP_UUID));
    					tempsocket.connect();
    					bluetoothSocket = tempsocket;
    					
    					startToCommunicate();
						
    					// �����ӳɹ�����BLUETOOTH_SOCKET_CONNECTED��Ϣ
    					MainActivity.this.sendEventsBundle(MainActivity.EventType.BLUETOOTH_SOCKET_CONNECTED);
    				}
					catch (IOException e)
    				{
    					// ���������Թر��׽���
    					try {
    						tempsocket.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						} finally {
							bluetoothSocket = null;
							MainActivity.this.sendEventsBundle(MainActivity.EventType.BLUETOOTH_SOCKET_UNCONNECTED);
    						e.printStackTrace();
						}
    				}
				}
    		}.start();
    	}
    }
    
	/**
	 * ������Ϣ����Handler
     */
	private void setEventsHandler()
	{
    	eventsHandler = new Handler()
    	{
			@Override
			public void handleMessage(Message msg)
			{
				Bundle databundle = msg.getData();
				// ȡ���¼����ͽ��ж�Ӧ����
				switch((EventType)databundle.getSerializable("EventType"))
				{
				case BLUETOOTH_SOCKET_UNCONNECTED: // ����δ�����¼�
					bluetoothSocket = null;
					MainActivity.this.buttonConnect.setText(R.string.connect);
					MainActivity.this.buttonConnect.setEnabled(true);
					break;
				case BLUETOOTH_SOCKET_CONNECTING: // �������������¼�
					MainActivity.this.buttonConnect.setText(R.string.connecting);
					MainActivity.this.buttonConnect.setEnabled(false);
					break;
				case BLUETOOTH_SOCKET_CONNECTED: // �����������¼�
					// �����Ƿ������ӳɹ���Ϣ
					if (MainActivity.this.sendDataToDetector(MainActivity.SEND_CONNECT_OK))
					{
						MainActivity.this.buttonConnect.setText(R.string.disconnect);
						
					}
					else
					{
						bluetoothSocket = null;
						MainActivity.this.buttonConnect.setText(R.string.connect);
					}                                                                                                            
					MainActivity.this.buttonConnect.setEnabled(true);
					
					testTime.setBase(SystemClock.elapsedRealtime());
					// ��ʼ��ʱ
					testTime.start();
					startTime=new Date().getTime();
					
					  saveTimeStart1=new Date().getTime();
					  periodNumber=0;
						
					  
//					loadFromFile();
					
//					inhaleTime.setBase(SystemClock.elapsedRealtime());
//					// ��ʼ��ʱ
//					inhaleTime.start();
					
					break;
				case BLUETOOTH_SOCKET_DISCONNECTING: // �������ڶϿ��¼�
					MainActivity.this.buttonConnect.setText(R.string.disconnecting);
					MainActivity.this.buttonConnect.setEnabled(false);
					
					testTime.stop();
//					inhaleTime.stop();
//					exhaleTime.stop();
//					
					
					break;
					
					
//				case INHALE_TIME_START:
//					
//					inhaleTime.setBase(SystemClock.elapsedRealtime());
//					inhaleTime.start();
//				
//					break;
//				
//				case INHALE_TIME_STOP:
//					inhaleTime.stop();
//					break;
//					
//				case EXHALE_TIME_START:
//					
//	        	exhaleTime.setBase(SystemClock.elapsedRealtime());
//				exhaleTime.start();
//			
//					break;
//					
//				case EXHALE_TIME_STOP:
//					exhaleTime.stop();
//					break;
					
				case DISPLAY_INHALE_TIME:
					inhaleTime=time;
					inhaleTimeView.setText("����ʱ��"+getFormatTime(time));
					
//					saveString2File("ʱ�䣺"+dateToString(new Date())+"����ʱ��  ",getFormatTime(time));
					
					break;
					
				case DISPLAY_EXHALE_TIME:
					exhaleTime=time;
					exhaleTimeView.setText("����ʱ��"+getFormatTime(time));
					if(inhaleTime==0)
					{
						haleRatio=-1;
					}
					else
					{
					haleRatio=(double)exhaleTime/(double)inhaleTime;
					}
					
					haleRatioView.setText("����ʱ��/����ʱ��:"+df1.format(haleRatio));
					
					break;
					
				case START_TO_INHALE://������ʼʱ����
					haleFrequencyView.setText("����Ƶ��:"+df1.format(haleFrequency)+"��/����");
					
//					saveTimeStop2=new Date().getTime();
//					saveTime2=saveTimeStop2-saveTimeStart2;
//					if(saveTime2>=SaveTimeInterval)
//					{
//						saveTime2=0;
//						saveTimeStart2=saveTimeStop2;
					
					//ÿ�ο�ʼ������ʱ���¼�������ĵ�
						saveString2File("\r\n",halePeriodDataFile);
						
						saveString2File("ʱ��"+dateToString(new Date()),halePeriodDataFile);
						saveDouble2File("����Ƶ��:  ",Double.parseDouble(df1.format(haleFrequency)),"��/����",halePeriodDataFile);
						
						saveDouble2File("����ǿ��:  ",Double.parseDouble(df1.format(haleIntensity)),"",halePeriodDataFile);
						
//						saveString2File("����ʱ��  "+getFormatTime(inhaleTime));
//						
//						saveString2File("����ʱ��  "+getFormatTime(exhaleTime));
					
						saveDouble2File("����/����:  ",Double.parseDouble(df1.format(haleRatio)),"",halePeriodDataFile);
						
						maxHaleRatio=Double.parseDouble(df1.format(haleRatio));
						
						
//					}
				
					
//					saveFrequencyTemp++;
//					if(saveFrequencyTemp==SaveOneOf)
//					{
//						saveFrequencyTemp=0;
//						saveDouble2File("ʱ�䣺"+dateToString(new Date())+"����Ƶ�ʣ�  ",Double.parseDouble(df1.format(haleFrequency)));
//						saveDouble2File("ʱ�䣺"+dateToString(new Date())+"����/����  ",Double.parseDouble(df1.format(haleRatio)));
//					}
					
					
					break;
					
				case START_TO_EXHALE://������ʼʱ����
					haleIntensityView.setText("����ǿ��:"+df.format(haleIntensity));
//					saveIntensityTemp++;
//					if(saveIntensityTemp==SaveOneOf)
//					{
//						saveIntensityTemp=0;
//						saveDouble2File("ʱ�䣺"+dateToString(new Date())+"����ǿ�ȣ�  ",Double.parseDouble(df1.format(haleIntensity)));
//					}
//					
					break;
					
				case DISPLAY_SOC:
					SOCView.setText("ʣ�����:"+" "+(int)SOC+"%");
					break;
					
				case UPDATE_SCORE:
					scoreView.setText("�����÷�:"+" "+(int)score+"��");
					break;
				}
			}
    	};
	}
	
	/**
	 * ��eventsHandler�����¼���Ϣ
	 * 
	 * @param eventtype �¼����ͣ�������
     * {@link #BLUETOOTH_SOCKET_UNCONNECTED}����δ�����¼�
     * {@link #BLUETOOTH_SOCKET_CONNECTING}�������������¼�
     * {@link #BLUETOOTH_SOCKET_CONNECTED}�����������¼�
     * {@link #BLUETOOTH_SOCKET_DISCONNECTING}�������ڶϿ��¼�
	 */
    public void sendEventsBundle(MainActivity.EventType eventtype)
    {
    	Bundle databundle = new Bundle();
    	databundle.putSerializable("EventType", eventtype);
		
    	Message msg = eventsHandler.obtainMessage();
		msg.setData(databundle);
		eventsHandler.sendMessage(msg);
	}
    
    /**
     * �����Ƿ�������
     * 
     * @param type ��ʾ���ݵ����ͣ�������
     * {@link #SEND_CONNECT_OK}
     * {@link #SEND_START_DETECTION}
     * {@link #SEND_DISCONNECT_BTH}
     * {@link #SEND_STOP_DETECTION}
     * 
     * @return ���ɹ������򷵻�true����ʧ�ܷ���false
     */
    public boolean sendDataToDetector(int type)
    {
		String data = "";
		
		switch (type)
		{
		case SEND_CONNECT_OK:
			data = "OK";
			break;
		case SEND_START_DETECTION:
			data = "START";
			break;
		case SEND_DISCONNECT_BTH:
			data = "DISBTH";
			break;
		case SEND_STOP_DETECTION:
			data = "STOP";
			break;
		}
		
		byte [] databuffer = data.getBytes();
		boolean result = false;
		int datalength = databuffer.length;
		if( (null != bluetoothSocket) && (null != databuffer) && (datalength >= 0))
		{
			datalength++; // ����һ���ֽڣ���ʾͷ����type
			
			byte[] intbyte = new byte[4];
			for (int i = 0; i < 4; i++)
			{
				intbyte[i] = (byte)((datalength >> (8 * (3 - i))) & 0xFF);
			}
			
			try
			{
				OutputStream outputstream = bluetoothSocket.getOutputStream();
				outputstream.write(intbyte, 0, 4); // �������ݳ���
				outputstream.write(new byte[]{(byte)type}, 0, 1); // �������ͱ�־
				outputstream.write(databuffer, 0, (datalength - 1)); // �������ݱ���
				outputstream.flush();
				
				result = true;
			}
			catch(IOException e)
			{
				// ���׳��쳣��ر���������Ϊnull
				try {
					bluetoothSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					bluetoothSocket = null;
					result = false;
					e.printStackTrace();
				}
			}
		}
		
		return result;
    }
    
    
    
    private void sendMessage(String message) {
    	
    	boolean result = false;
        //û�������豸�����ܷ���
        if (!MainActivity.this.sendDataToDetector(MainActivity.SEND_CONNECT_OK))
        		{
            Toast.makeText(this, R.string.pleaseConnectDevice, Toast.LENGTH_SHORT).show();
            return;
        }
		
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            
            try
			{
            OutputStream outputstream = bluetoothSocket.getOutputStream();
            outputstream.write(send, 0, 1); //
			}
            catch(IOException e)
			{
				// ���׳��쳣��ر���������Ϊnull
				try {
					bluetoothSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					bluetoothSocket = null;
					result = false;
					e.printStackTrace();
				}
			}
            
        }
    }
    
    
//    
//    private void updateChart() {  
//        
//        //���ú���һ����Ҫ���ӵĽڵ�  
//        addX = 0;  
//        addY = (int)(Math.random() * 90);  
//          
//        //�Ƴ����ݼ��оɵĵ㼯  
//        mDataset.removeSeries(series);  
//          
//        //�жϵ�ǰ�㼯�е����ж��ٵ㣬��Ϊ��Ļ�ܹ�ֻ������100�������Ե���������100ʱ��������Զ��100  
//        int length = series.getItemCount();  
//        if (length > 100) {  
//            length = 100;  
//        }  
//          
//        //���ɵĵ㼯��x��y����ֵȡ��������backup�У����ҽ�x��ֵ��1�������������ƽ�Ƶ�Ч��  
//        for (int i = 0; i < length; i++) {  
//            xv[i] = (int) series.getX(i) + 1;  
//            yv[i] = (int) series.getY(i);  
//        }  
//          
//        //�㼯����գ�Ϊ�������µĵ㼯��׼��  
//        series.clear();  
//          
//        //���²����ĵ����ȼ��뵽�㼯�У�Ȼ����ѭ�����н�����任���һϵ�е㶼���¼��뵽�㼯��  
//        //�����������һ�°�˳��ߵ�������ʲôЧ������������ѭ���壬������²����ĵ�  
//        series.add(addX, addY);  
//        for (int k = 0; k < length; k++) {  
//            series.add(xv[k], yv[k]);  
//        }  
//          
//        //�����ݼ�������µĵ㼯  
//        mDataset.addSeries(series);  
//          
//        //��ͼ���£�û����һ�������߲�����ֶ�̬  
//        //����ڷ�UI���߳��У���Ҫ����postInvalidate()������ο�api  
//        chart.invalidate();  
//    }  
//}  



    
    /**
     * ��������ͨ���߳�
     */
    public void startToCommunicate()
    {
    	if (isCommunicateThreadRunning && enableCommunicate) // �����߳���ִ�У�ֱ�ӷ���
		{
			return;
		}
    	
		while (isCommunicateThreadRunning); // �߳̿������ڽ����У��ȴ�����

		isCommunicateThreadRunning = true;
		enableCommunicate = true;
    	
		// �����߳�
    	new Thread()
    	{
    		public void run()
	      	{
				int length = 1;
				
		        
				double [] databuffery = new double[length];
				
			
				
    			while(MainActivity.this.enableCommunicate)
    			{
					if(MainActivity.this.bluetoothSocket != null)
					{
						graphlength = 0;
						
						// �������豸��ȡ����
	    				try {
	    					int byteofunit = 4; // �ɵ�Ƭ���������ֽ���ÿbyteofunit����ʾһ������
	    					int nbyte = byteofunit * length;
	    					int nread = 0;
	    					byte [] databuffer = new byte[nbyte];
	    					byte [] tempbuffer = null;
	    					int oldnread = 0;
	    					while (nread < nbyte) // ��ѭ����֤���յ�ָ�����ȵ����ݺ�Ž��к�������
	    					{
	    						tempbuffer = new byte[nbyte - nread];
	    						oldnread = nread;
	    						nread += MainActivity.this.bluetoothSocket.getInputStream().read(tempbuffer, 0, nbyte - oldnread);
	    						System.arraycopy(tempbuffer, 0, databuffer, oldnread, nread - oldnread);
	    					}
	    					
	    					
	    					for(int i=0;i<length;i++)
	    					{
	    					
	    					byte[] bytey=new byte[byteofunit];
	    					System.arraycopy(databuffer, i*byteofunit, bytey, 0, byteofunit);
	    						
	    					 char[] receiveCharArray =(new String(bytey)).toCharArray(); // ΢��������ֵ
	    					 if(receiveCharArray[0]=='%')
	    					 {
	    						 receiveFlag=0;
	    						 SOCString=(new String(bytey)).substring(1);
	    						 SOC= Double.parseDouble(SOCString);
	    					 }
	    					 else if(receiveCharArray[0]=='#')
	    					 {
	    						 receiveFlag=1; 
	    						 dataString=(new String(bytey)).substring(1);
	    						 databuffery[graphlength] =( Double.parseDouble(dataString))/100; // ΢��������ֵ
	 							graphlength++; // ������յ�ͼ�����ݸ���
	 							
	 							dataNumber++;
	    					 }
	    					}
	    					
	    					
	    					
	    					
	    					
	    					
	    					
	    					
	    					
	    					
	    					
	    					
	    					
	    					
	    					
	    					 
	    					
//	    					int tempx = 0;
//	    					int tempy = 0;
//	    					for (int i = 0; i < length; i++)
//							{
//	    						// �����յ��ֽ�������װ��int��������
//	    						tempx = 0;
//	    						tempy = 0;
//	    						for (int j = 0; j < byteofunit/2; j++)
//								{
//	    							tempx = tempx | ((0xFF & databuffer[i * byteofunit + j]) << (8 * j));
//	    							tempy = tempy | ((0xFF & databuffer[i * byteofunit + byteofunit / 2 + j]) << (8 * j));
//								}
//	    					
//	    						
//	    						// tempyΪ-4095(0xF001)��4095(0x0FFF)ʱ��ʾAD����ֵ���������(0x1000-0xF000)����ָʾ��������
//	    						if (tempy != MainActivity.REV_BEAT_PACKET)
//	    						{
//	    							// ����Ϊ����������д���
//	    							if ((tempy >= (-4095)) && (tempy <= 4095)) // [-4095,4095]����Ϊ��Ч�ɼ�����
//	    							{
////	    								if (AppGlobal.CHECK_PROCESS_STEP == DetectionActivity.this.currentProcess)
////	    		    					{
////	    									// ����ǰΪ���ݽ׶��򱣴��µ���ֵ
////	    									datay1 = 1000.0 * ref * (tempy - offset) / 0xFFF / res - lc; // ΢������
////	    		    					}
////	    								
////	    								if (AppGlobal.CHECK_PROCESS_PLUSE == DetectionActivity.this.currentProcess)
////	    		    					{
////	    									// ����ǰΪ����׶���������ֵ����ȥ���ݽ׶εĵ���ֵ�������ͼ�����ݻ�����
////	    									databuffery[graphlength] = 1000.0 * ref * (tempy - offset) / 0xFFF / res - lc - datay1; // ΢��������ֵ
////	    									databufferx[graphlength] = (tempx * wrscale / 0xFFFF + wrmin) / 1000;
////	    									graphlength++; // ������յ�ͼ�����ݸ���
////	    		    					}
//	    								databuffery[graphlength] = tempy; // ΢��������ֵ
//	    								databufferx[graphlength] = tempx;
//	    								graphlength++; // ������յ�ͼ�����ݸ���
//	    							}
//	    						}
//							}
	    				} catch (Exception e) {
	    					// ���׳��쳣���˳�ѭ���������߳�
	    					break;
	    				}
	    				
					
					if(receiveFlag==1)
					{	double newy=databuffery[0];
    					double newx=MAXX;
    					
    				//	kArray[N-1]=newy;
    					
    					for (int i = 0; i <N-1; i++) {  
//    	    		          xv[i] =(double) dataSeries.getX(i);  
    	    		           kArray[i] = kArray[i+1];  
    	    		           
    	    		        }  
    					
    					kArray[N-1]=newy;
    					
    					 if(dataNumber%sampleOneOf==0)
    					 {
    						 
//    						for (int i = 0; i <MAXX-1; i++) {  
//    		    		           dataArayRightToLeft[i] = dataArayRightToLeft[i+1]; //���������ƶ�
//    		    		        }  
//    						
//    						dataArayRightToLeft[MAXX-1]=newy; 
    						
    						
    						
    						
    						if(dataIndex<displayDotNumber)
    						{
    							stantardArray[dataIndex]=newy;
    							dataArrayLeftToRight[dataIndex]=newy;//���������ƶ�
    							
    						    dataIndex++;
    							
    						}
    						
    						if(dataIndex==displayDotNumber)
    						{
    							dataIndex=0;
    							CalculateScore();
								MainActivity.this.sendEventsBundle(MainActivity.EventType.UPDATE_SCORE);
    						//	System.out.println(stantardString);
//    							writeFileData(fileName,stantardString);
//    							 String result = readFileData(fileName);
//    							 System.out.println("readdg"+result);
    						}
    					 }
    				
//    					if(dataNumber>=MAXX*sampleOneOf)  dataNumber=0;
    					
    					
    					
    					int l = dataSeries.getItemCount();
    					
    					
//    					if ( (l>=MAXX)&&(graphlength>0) ){  
    						
//    						graphView.clearSeries();
    			        
    		
    		          
    		      
    					   //�㼯����գ�Ϊ�������µĵ㼯��׼��  
        		        dataSeries.clear(); 
    		        //���²����ĵ����ȼ��뵽�㼯�У�Ȼ����ѭ�����н�����任���һϵ�е㶼���¼��뵽�㼯��  
    		        //�����������һ�°�˳��ߵ�������ʲôЧ������������ѭ���壬������²����ĵ�  
        		        
        		        if( dataIndex<displayDotNumber-displayDotNumber/6)
    		        	{ 
    		        		for ( int  k = dataIndex; k <dataIndex+displayDotNumber/6; k++)
    		        		{ 
    		        			dataArrayLeftToRight[k]=(signalPosition)/magnifyTime;
        		        		
        		        	}	 
    		        	}
//    		         
    		
    		        
    		        
    		        for (int k =0; k < displayDotNumber; k++) {  
//    		            dataSeries.add(k*TIME_BETWEEN_TWO*sampleOneOf, dataArayRightToLeft[k]*magnifyTime);
    		        	dataSeries.add(k*TIME_BETWEEN_TWO*sampleOneOf, dataArrayLeftToRight[k]*magnifyTime);
    		        	
    		        }  
//    		        
    		      
//    		            dataSeries.add(k*TIME_BETWEEN_TWO*sampleOneOf, dataArayRightToLeft[k]*magnifyTime);  
		        	
		            //ȡyv[]��������6������ΪkArray�ĳ�Ա
        		        
     		        //  dataSeries.add(newx, newy);
    		        //�����ݼ�������µĵ㼯  
//    		        graphView.addSeries(dataSeries);  
    		          
    		        //��ͼ���£�û����һ�������߲�����ֶ�̬  
    		        //����ڷ�UI���߳��У���Ҫ����postInvalidate()������ο�api  
    		   
    		        MainActivity.this.graphView.fitGraph();
    		        
    		       
//    		        
//    					}
//	    				
//	    				
//	    				//�տ�ʼû�дﵽMAXX�������źŵ�ʱ��ʵ�ֳ�ʼ�źŵ��ƶ���ͼ
//    					else if ( (l<1)&&(graphlength>0) )// && isDetecting)
//						{
//	    				
//							// ������������������½�������ͼ��
//    						
//    						
////	    					for (int i = 0; i <graphlength; i++)
////	    					{
//////	    						MainActivity.this.dataSeries.add(databufferx[i], databuffery[i]);
//////	    						MainActivity.this.dataSeries.add( l+i, databuffery[i]);
////	    						
////	    						yv[i]= databuffery[i];
////	    					}
////	    					
////	    					dataSeries.clear();
//	    					for (int i = 0; i <MAXX; i++) {  
//	   		    		          MainActivity.this.dataSeries.add( i*TIME_BETWEEN_TWO*sampleOneOf, dataArayRightToLeft[i]*magnifyTime); 
//	    		    		        }  
//	    					 MainActivity.this.dataSeries.add( MAXX*TIME_BETWEEN_TWO*sampleOneOf, databuffery[0]*magnifyTime); 
//	    					MainActivity.this.graphView.fitGraph();
//						}
//    					                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
//    					
    					
    					

    		        saveTimeStop1=new Date().getTime();
					saveTime1=saveTimeStop1-saveTimeStart1;
					if(saveTime1>=SaveTimeInterval)
					{
						
						//ʱ��ɨ���¼����
						saveTime1=0;
						saveTimeStart1=saveTimeStop1;
//						
//						saveString2File("\r\n",allDataFile);
//						
//						saveString2File("ʱ�䣺"+dateToString(new Date()),allDataFile);
//						saveDouble2File("����Ƶ�ʣ�  ",Double.parseDouble(df1.format(haleFrequency)),"��/����",allDataFile);
//						
//						saveDouble2File("����ǿ�ȣ�  ",Double.parseDouble(df1.format(haleIntensity)),"",allDataFile);
////						
//						saveDouble2File("����ʱ��/����ʱ��  ",maxHaleRatio,"",allDataFile);
						
						
						
						
						
//						saveString2File("����ʱ��  "+getFormatTime(inhaleTime),allDataFile);
//						
//						saveString2File("����ʱ��  "+getFormatTime(exhaleTime),allDataFile);
//					
//					 saveDouble2File("����/����  ",Double.parseDouble(df1.format(haleRatio)),"",allDataFile);
						
//						
//						if(haleFlag==1)
//						{
//							saveString2File("����״̬��  "+"����...",allDataFile);
//						}
//						
//						
//						if(haleFlag==0)
//						{
//							saveString2File("����״̬��  "+"����...",allDataFile);
//						}
						
						
						saveAbnormalData();
					}
						
				
				
					
    		        
    		    		
        		        double m[]=new double[N/2];
        		        double M;
        		        double  numerator=0,denominator=0;
        		        
        		        for(int i=0;i<N/2;i++)
        		        {
        		        	m[i]=(kArray[N-1-i]-kArray[i])/(N-1-2*i);
        		        }
        		       
        		        for(int i=1;i<=N/2;i++)
        		        {
        		        	numerator+=Math.pow(N-(2*i-1), 2)*m[i-1];
        		        }
        		        
        		        
        		        for(int i=1;i<=N/2;i++)
        		        {
        		        	denominator+=Math.pow(N-(2*i-1), 2);
        		        }
        		        M=numerator/denominator;
        		        		
        		        
        		        
        		        
        		        if((M>PositiveK)&&(haleFlag==0))
        		        {
        		        	//��ʼ����
//        		        	MainActivity.this.sendEventsBundle(MainActivity.EventType.EXHALE_TIME_STOP);
//        		        	MainActivity.this.sendEventsBundle(MainActivity.EventType.INHALE_TIME_START);
        		        	periodEndTime=new Date().getTime();
        		        	periodTime=periodEndTime-periodStartTime;
        		        	haleFrequency=60000/(double)periodTime;
        		        	haleIntensityMin=MinOfkArray()*magnifyTime;
        		        	MainActivity.this.sendEventsBundle(MainActivity.EventType.START_TO_INHALE);
        		        	
        		        	startTime=new Date().getTime();
        		        	periodStartTime=new Date().getTime();
        		        	haleFlag=1;
        		        	
        		        }
        		        
        		        
        		         if((M<NegativeK)&&(haleFlag==1))
        		        {
        		        	
        		        	//��ʼ����
//        		        	MainActivity.this.sendEventsBundle(MainActivity.EventType.EXHALE_TIME_START);
//        		        	MainActivity.this.sendEventsBundle(MainActivity.EventType.INHALE_TIME_STOP);
        		        	
        		        	startTime=new Date().getTime();
        		        	haleIntensityMax=MaxOfkArray()*magnifyTime;
        		        	haleIntensity=haleIntensityMax-haleIntensityMin;
        		        	MainActivity.this.sendEventsBundle(MainActivity.EventType.START_TO_EXHALE);
        		        	
       		        	   haleFlag=0;

        		        }
        		         
        		         timerCount++;
         				if(timerCount>=TimerInterval) 
         				{   timerCount=0;
         					endTime=new Date().getTime();
         					time=endTime-startTime;
         					
//         					System.out.println("timer");
         					
         					
         					if(haleFlag==1)
         					{
//         					inhaleTimeView.setText(getFormatTime(time));
         					MainActivity.this.sendEventsBundle(MainActivity.EventType.DISPLAY_INHALE_TIME);
         					}
         				
         					if(haleFlag==0)
         					{
//         					exhaleTimeView.setText(getFormatTime(time));
         						MainActivity.this.sendEventsBundle(MainActivity.EventType.DISPLAY_EXHALE_TIME);
         					}
         				}
					}//�������ݴ������
					
					if(receiveFlag==0)
					{
						//SOCView.setText("ʣ�����"+(int)SOC+"%");
						MainActivity.this.sendEventsBundle(MainActivity.EventType.DISPLAY_SOC);
					}
					}
					
					
					
					try {
						Thread.sleep(1); // ��Ϣ1����
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
    			}

    			MainActivity.this.sendDataToDetector(MainActivity.SEND_DISCONNECT_BTH); // ��MSP��Ƭ�����ͶϿ�������Ϣ
				
				// �ر�����ͨ���׽���
    			if (MainActivity.this.bluetoothSocket != null)
    			{
    				try {
    					MainActivity.this.bluetoothSocket.close();
    				} catch (IOException e) {
    					e.printStackTrace();
    				} finally {
    					MainActivity.this.bluetoothSocket = null;
    				}
    			}
    			
    			MainActivity.this.sendEventsBundle(MainActivity.EventType.BLUETOOTH_SOCKET_UNCONNECTED);
    			
    			MainActivity.this.isCommunicateThreadRunning = false;
	      	}
    	}.start();
    	
    	}
    
    private String getFormatTime(long time) {
		long millisecond = time % 1000;
		long second = (time / 1000) % 60;
		long minute = time / 1000 / 60;
		
		//�����µ�ֻ��ʾһλ
		String strMillisecond = "" + (millisecond / 100);
		//����ʾ��λ
		String strSecond = ("00" + second).substring(("00" + second).length() - 2);
		//����ʾ��λ
		String strMinute = ("00" + minute).substring(("00" + minute).length() - 2);
		
		return strMinute + ":" + strSecond + ":" + strMillisecond;
	}
    
    
    public void writeFileData(String fileName,String message){
        try{
            FileOutputStream fout = openFileOutput(fileName, Context.MODE_WORLD_READABLE+Context.MODE_WORLD_WRITEABLE);//���FileOutputStream����
            byte [] bytes = message.getBytes();//��Ҫд����ַ���ת��Ϊbyte����
            fout.write(bytes);//��byte����д���ļ�
            fout.close();//�ر�FileOutputStream����
           }
           catch(Exception e){
            e.printStackTrace();//�����쳣����ӡ
           }

       }   

//       //��������ָ���ļ�����ȡ�����ݣ������ַ�������
//       public String readFileData(String fileName){
//
//    	     String result="";
//    	     try{
//    	      FileInputStream fin = openFileInput(fileName);//���FileInputStream����
//    	      int length = fin.available();//��ȡ�ļ�����
//    	      byte [] buffer = new byte[length];//����byte�������ڶ�������
//    	      fin.read(buffer);//���ļ����ݶ��뵽byte������        
//    	      result = EncodingUtils.getString(buffer, ENCODING);//��byte����ת����ָ����ʽ���ַ���
//    	      fin.close();     //�ر��ļ�������
//    	     }
//    	     catch(Exception e){
//    	      e.printStackTrace();//�����쳣����ӡ
//    	     }
//    	     return result;//���ض����������ַ���
//    	    }   
//       
//       
//       public Properties loadConfig(Context context, String file) {  
//    	   Properties properties = new Properties();  
//    	   try {  
//    	   FileInputStream s = new FileInputStream(file);  
//    	   properties.load(s);  
//    	   } catch (Exception e) {  
//    	   e.printStackTrace();  
//    	   }  
//    	   return properties;  
//    	   }  
//    	     
//    	   public void saveConfig(Context context, String file, Properties properties) {  
//    	   try {  
//    	   FileOutputStream s = new FileOutputStream(file, false);  
//    	   properties.store(s, "");  
//    	   } catch (Exception e){  
//    	   e.printStackTrace();  
//    	   }  
//    	   }  
    	   
    	 void loadSet(){
    		 SharedPreferences settings = getSharedPreferences("setting", 0);
    		String displayTimeStr=settings.getString("displayTime","60");
    		displayTime=Double.parseDouble(displayTimeStr);
    		stantardIntensity=Double.parseDouble(settings.getString("stantardIntensity", "50"));
    		sampleOneOf=Double.parseDouble(settings.getString("sampleOneOf", "2"));
    		stantardRatio=Double.parseDouble(settings.getString("stantardRatio", "3"));
    		stantardFrequency=Double.parseDouble(settings.getString("stantardFrequency", "6"));
    		stantardPosition=Double.parseDouble(settings.getString("stantardPosition", "5"));
    		
    		 maxHaleFrequency=Double.parseDouble(settings.getString("maxHaleFrequency", "40"));
    		  minHaleIntensity=Double.parseDouble(settings.getString("minHaleIntensity", "5"));
    		  magnifyN=Double.parseDouble(settings.getString("magnifyN", "1"));
    		  signalPosition=Double.parseDouble(settings.getString("signalPosition", "1"));
    		  
    		  SOC=Double.parseDouble(settings.getString("SOC", "0"));
    		
    		
    		 System.out.println(displayTimeStr);
    		 System.out.println(stantardIntensity+"");
    	 }  
    	 
    	 void saveSet(){
    		 SharedPreferences settings = getSharedPreferences("setting", 0);
    		 SharedPreferences.Editor editor = settings.edit();
    		 editor.putString("displayTime",displayTime+"");
    		 editor.putString("stantardIntensity",stantardIntensity+"");
    		 editor.putString("sampleOneOf",sampleOneOf+"");
    		 editor.putString("stantardFrequency",stantardFrequency+"");
    		 editor.putString("stantardRatio",stantardRatio+"");
    		 editor.putString("stantardPosition",stantardPosition+"");
    		 
    		 editor.putString("maxHaleFrequency",maxHaleFrequency+"");
    		 editor.putString("minHaleIntensity",minHaleIntensity+"");
    		 editor.putString("magnifyN",magnifyN+"");
    		 editor.putString("signalPosition",signalPosition+"");
    		 
    		 editor.putString("SOC",SOC+"");
    		 editor.commit();
    	 }
    	 
    	 public void saveDouble2File(String name,double data,String unit,String fileName) {

    			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//    				File sdCardDir = Environment.getExternalStorageDirectory();//��ȡSDCardĿ¼
//    				File saveFile = new File(sdCardDir, fileName);
    				
    				 String local_file = Environment.getExternalStorageDirectory().getAbsolutePath()+"/��������/";
    				 File fDir = new File(local_file);  
    				 if(!fDir.exists())
    				 {  
    					 fDir.mkdirs(); 
    				 }
    				 
    				File saveFile=new File(fDir, fileName);
    				
    				try {
    			        FileOutputStream outStream = new FileOutputStream(saveFile,true);
    			        outStream.write((name+data+unit+"\r\n").getBytes());
    			        outStream.close();
    				} catch (FileNotFoundException e) {
    					Log.d("SP", e.toString());
    					return;
    				} catch (IOException e) {
    					Log.d("SP", e.toString());
    					return;
    				}
    			}
    		}
    	 
    	 
    	 public void saveString2File(String str,String fileName) {

 			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
// 				File sdCardDir = Environment.getExternalStorageDirectory();//��ȡSDCardĿ¼
// 				File saveFile = new File(sdCardDir, fileName);
 				 
 				String local_file = Environment.getExternalStorageDirectory().getAbsolutePath()+"/��������/";
				 File fDir = new File(local_file);  
				 if(!fDir.exists())
				 {  
					 fDir.mkdirs(); 
				 }
				 
				File saveFile=new File(fDir, fileName);
 				try {
 			        FileOutputStream outStream = new FileOutputStream(saveFile,true);
 			        outStream.write((str+"\r\n").getBytes());
 			        outStream.close();
 				} catch (FileNotFoundException e) {
 					Log.d("SP", e.toString());
 					return;
 				} catch (IOException e) {
 					Log.d("SP", e.toString());
 					return;
 				}
 			}
 		}
    	 
    	
    	 
    	 public void loadFromFile(String fileName) {
    			try {
    				FileInputStream inStream = this.openFileInput(fileName);
    				ByteArrayOutputStream stream = new ByteArrayOutputStream();
    				byte[] buffer = new byte[1024];
    				int length = -1;
    				while ((length = inStream.read(buffer)) != -1) {
    					stream.write(buffer, 0, length);
    				}
    				stream.close();
    				inStream.close();
    				File sdCardDir = Environment.getExternalStorageDirectory();//��ȡSDCardĿ¼
    				FileInputStream inSdcardStream = new FileInputStream(new File(sdCardDir, "a.txt"));
    				ByteArrayOutputStream sdcardStream = new ByteArrayOutputStream();
    				byte[] buff = new byte[1024];
    				int len = -1;
    				while ((len = inSdcardStream.read(buff)) != -1) {
    					sdcardStream.write(buff, 0, len);
    				}
//    				Toast.makeText(mContext, "Loaded " + stream.toString() + " sdcard/a.txt: " +  sdcardStream.toString(),
    				Toast.makeText(mContext, new String(buff),		
    				Toast.LENGTH_LONG).show();
    				sdcardStream.close();
    				inSdcardStream.close();
    			} catch (FileNotFoundException e) {
    				Log.d("SP", e.toString());
    				e.printStackTrace();
    			} catch (IOException e) {
    				Log.d("SP", e.toString());
    				return;
    			} catch (Exception e) {
    				Log.d("SP", e.toString());
    				return;
    			}
    		}
    	   
    	 
    	 public static String dateToString(Date data) {
    		 String formatType="   yyyy.MM-dd HH:mm:ss  ";
    		  return new SimpleDateFormat(formatType).format(data);
    		  }
    	 
    	 
    	 public double MaxOfkArray()
    	 {
    		 double maxOfkArray=0;
    		 for(int i=0;i<N;i++)
    		 {
    			 if(kArray[i]>maxOfkArray)
    			 {
    				 maxOfkArray=kArray[i];
    				 
    			 }
    		 }
    		 
			return maxOfkArray;
    		 
    	 }
    	 
    	 public double MinOfkArray()
    	 {
    		 double minOfkArray=10;
    		 for(int i=0;i<N;i++)
    		 {
    			 if(kArray[i]<minOfkArray)
    			 {
    				 minOfkArray=kArray[i];
    				 
    			 }
    		 }
    		 
			return minOfkArray;
    		 
    	 }
    	 
    		
    	 public void CalculateScore()
    	 {
    		 periodNumber++;
    		 xyRelation=0;
    		 
//			xySum=0;
//			xSum=0;
//			ySum=0;
//			x2Sum=0;
//			y2Sum=0;
			//String stantardString=new String();
//			for(int i=0;i<displayDotNumber;i++)
//			{
//				//stantardString=stantardString+stantardArray[i]+",";
//				
//				xySum+=(stantardPosition+stantardY[i])*dataArrayLeftToRight[i];
//				xSum+=(stantardPosition+stantardY[i]);
//				ySum+=dataArrayLeftToRight[i];
//				x2Sum+=Math.pow((stantardPosition+stantardY[i]), 2);
//				y2Sum+=Math.pow(dataArrayLeftToRight[i], 2);
//			}
//			
//			double RNumerator=xySum-xSum*ySum/displayDotNumber;
//			double RDenominator=Math.sqrt((x2Sum-Math.pow(xSum, 2)/displayDotNumber)*(y2Sum-Math.pow(ySum, 2)/displayDotNumber));
//			
//			xyRelation=RNumerator/RDenominator;
//			
//			xyRelationAverage=(xyRelationAverage*(periodNumber-1)+xyRelation)/periodNumber;
//			
//			score=100*xyRelation;
			
			for(int i=0;i<displayDotNumber;i++)
			{
				
				xyRelation+=Math.pow(stantardPosition+stantardY[i]-dataArrayLeftToRight[i]*magnifyTime, 2);
			}
			
			xyRelation=Math.sqrt(xyRelation/displayDotNumber);
			
			double scoreTemp=100*Math.pow(0.985,xyRelation);
			
			if(scoreTemp>=92)
			{
				score=scoreTemp;
			}
			
			else if((scoreTemp<92)&&(scoreTemp>=85))
			{
				score=100*Math.pow(0.97,xyRelation);
			}
			
			else if((scoreTemp<85)&&(scoreTemp>=70))
			{
				score=100*Math.pow(0.95,xyRelation);
			}
			
			else 
			{
				score=100*Math.pow(0.9,xyRelation);	
			}
			
			averageScore=(averageScore*(periodNumber-1)+score)/periodNumber;
			
    	 } 
    	 
    	 
    	 void saveAbnormalData()
    	 {

				//�쳣�ж�
				 String abnormalReason=" ";
				
				
				if((inhaleTime>=MaxInhaleTime)||(exhaleTime>=MaxExhaleTime)||(haleFrequency==0)||(haleIntensity==0))
				{
					abnormalReason="Reason1";
					saveString2File("\r\n",abnormalDataFile);
					
					saveString2File("ʱ��"+dateToString(new Date()),abnormalDataFile);
					
					saveString2File("�쳣ԭ��  "+"�豸����������/�޺����ź�",abnormalDataFile);
					
					saveDouble2File("����Ƶ�ʣ�  ",Double.parseDouble(df1.format(haleFrequency)),"��/����",abnormalDataFile);
					
					saveDouble2File("����ǿ�ȣ�  ",Double.parseDouble(df1.format(haleIntensity)),"",abnormalDataFile);
					
					if(haleFlag==1)
					{
						saveString2File("����״̬��  "+"����...",abnormalDataFile);
					}
					
					
					if(haleFlag==0)
					{
						saveString2File("����״̬��  "+"����...",abnormalDataFile);
					}
					
				}
				
				
				if((haleFrequency>=maxHaleFrequency)&&(abnormalReason!="Reason1"))
				{
					if(haleIntensity>minHaleIntensity)
					{
					abnormalReason="Reason2";
					
					saveString2File("\r\n",abnormalDataFile);
					
					saveString2File("ʱ��"+dateToString(new Date()),abnormalDataFile);
					
					saveString2File("�쳣ԭ��  "+"������Ƶ�ʹ���",abnormalDataFile);
					
					saveDouble2File("����Ƶ�ʣ�  ",Double.parseDouble(df1.format(haleFrequency)),"��/����",abnormalDataFile);
					
					saveDouble2File("����ǿ�ȣ�  ",Double.parseDouble(df1.format(haleIntensity)),"",abnormalDataFile);
					
					if(haleFlag==1)
					{
						saveString2File("����״̬��  "+"����...",abnormalDataFile);
					}
					
					
					if(haleFlag==0)
					{
						saveString2File("����״̬��  "+"����...",abnormalDataFile);
					}
					}
				}
//				
				
				if((haleIntensity<=minHaleIntensity)&&(abnormalReason!="Reason1")&&(abnormalReason!="Reason2"))
				{
                    
					
					if(haleFrequency<maxHaleFrequency)
					{
						abnormalReason="Reason3";
						saveString2File("\r\n",abnormalDataFile);
						
						saveString2File("ʱ��"+dateToString(new Date()),abnormalDataFile);
						
						saveString2File("�쳣ԭ��  "+"������ǿ�ȹ�ǳ",abnormalDataFile);
						
						saveDouble2File("����Ƶ�ʣ�  ",Double.parseDouble(df1.format(haleFrequency)),"��/����",abnormalDataFile);
						
						saveDouble2File("����ǿ�ȣ�  ",Double.parseDouble(df1.format(haleIntensity)),"",abnormalDataFile);
						
						if(haleFlag==1)
						{
							saveString2File("����״̬��  "+"����...",abnormalDataFile);
						}
						
						
						if(haleFlag==0)
						{
							saveString2File("����״̬��  "+"����...",abnormalDataFile);
						}
					}
					
				
				else if(haleFrequency>=maxHaleFrequency)
				{
					abnormalReason="Reason4";
					saveString2File("\r\n",abnormalDataFile);
					
					saveString2File("ʱ��"+dateToString(new Date()),abnormalDataFile);
					
					saveString2File("�쳣ԭ��: "+"������ǿ�ȹ�ǳ��������Ƶ�ʹ���",abnormalDataFile);
					
					saveDouble2File("����Ƶ�� :",Double.parseDouble(df1.format(haleFrequency)),"��/����",abnormalDataFile);
					
					saveDouble2File("����ǿ�� : ",Double.parseDouble(df1.format(haleIntensity)),"",abnormalDataFile);
					
					if(haleFlag==1)
					{
						saveString2File("����״̬��  "+"����...",abnormalDataFile);
					}
					
					
					if(haleFlag==0)
					{
						saveString2File("����״̬��  "+"����...",abnormalDataFile);
					}
				}
				}	
    	 }
    	 
    	 
    	 
}
