package com.example.test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity
{
	/**
	 * 蓝牙串行通信UUID
	 */
	static final public String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	
	// 通过蓝牙从检测仪接收的数据类型
	/**
	 * 心跳包
	 */
	static final public int REV_BEAT_PACKET	= 0x1000;
	
	// 通过蓝牙向检测仪发送的数据类型
	/**
	 * 蓝牙连接成功确认
	 */
	static final public int SEND_CONNECT_OK = 0;
	
	/**
	 * 开始检测命令
	 */
	static final public int SEND_START_DETECTION = 1;
	
	/**
	 * 断开蓝牙连接命令
	 */
	static final public int SEND_DISCONNECT_BTH	= 2;
	
	/**
	 * 停止检测命令
	 */
	static final public int SEND_STOP_DETECTION = 3;
	
	
	static final public int MAXX=50;
		
		
	// 事件类型枚举
	static private enum EventType { BLUETOOTH_SOCKET_UNCONNECTED, // 蓝牙未连接事件
									BLUETOOTH_SOCKET_CONNECTING, // 蓝牙正在连接事件
									BLUETOOTH_SOCKET_CONNECTED,	// 蓝牙已连接事件
									BLUETOOTH_SOCKET_DISCONNECTING,	// 蓝牙正在断开事件
									};
		
	// 控件
	private ComplexGraph graphView = null;
	private TextView textViewInfo = null;
	private Button buttonStart = null;
	private Button buttonConnect = null;
	
	private Handler eventsHandler = null; // 事件Handler，用来响应各类事件
	
	public boolean enableCommunicate = false; // 蓝牙通信线程使能控制
	public boolean isCommunicateThreadRunning = false; // 蓝牙通信线程运行标志
	public BluetoothDevice bluetoothDeviceSelected = null; // 被选择连接的蓝牙设备
	public BluetoothSocket bluetoothSocket = null; // 蓝牙套接字，负责与外部蓝牙设备(检测仪蓝牙模块)通信
	
	public boolean isDetecting = false; // 正在检测标志
	
	private XYSeries dataSeries; // 数据
	private XYSeriesRenderer dataRenderer; // 渲染器
	
	
	private int graphlength ;

	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 获取控件
        graphView = (ComplexGraph)findViewById(R.id.graphicView);
        textViewInfo = (TextView)findViewById(R.id.textViewInfo);
        buttonStart = (Button)findViewById(R.id.buttonStart);
        buttonConnect = (Button)findViewById(R.id.buttonConnect);
        
        XYMultipleSeriesRenderer renderer = graphView.getRenderer();
        renderer.setApplyBackgroundColor(true); // 设置是否显示背景色
		renderer.setBackgroundColor(Color.BLACK); // 设置背景色
		renderer.setMargins(new int[]{10, 40, 40, 10}); // 设置图表的外边框(上/左/下/右)
        renderer.setAxisTitleTextSize(25); // 设置轴标题文字的大小
        renderer.setLabelsTextSize(20); // 设置刻度显示文字的大小(XY轴都会被设置)
        renderer.setLegendTextSize(20); // 图例文字大小
        renderer.setPointSize(2); // 设置点的大小(图上显示的点的大小和图例中点的大小都会被设置)
        renderer.setShowGrid(true); // 是否显示网格
        renderer.setSelectableBuffer(10); // 设置点的缓冲半径值(在某点附件点击时,多大范围内都算点击这个点)
//		renderer.setXTitle(MainActivity.this.getString(R.string.potential) + "(" + 
//				MainActivity.this.getResources().getStringArray(R.array.potential_unit)[AppGlobal.DEFAULT_POTENTIAL_UNIT] + ")"); // X轴说明
//		renderer.setYTitle(MainActivity.this.getString(R.string.current) + "(" + 
//				MainActivity.this.getResources().getStringArray(R.array.current_unit)[AppGlobal.DEFAULT_CURRENT_UNIT] + ")"); // Y轴说明
		renderer.setLegendHeight(100); // 设置图例文字位置高度
		renderer.setAxesColor(Color.argb(255, 250, 250, 250)); // 轴颜色
		renderer.setGridColor(Color.argb(150, 200, 200, 200)); // 网格颜色
		renderer.setLabelsColor(Color.argb(180, 180, 180, 180)); // 标注颜色
		renderer.setXLabels(7); // X轴刻度数量
		renderer.setYLabels(7); // Y轴刻度数量
		renderer.setXLabelsColor(Color.argb(255, 255, 255, 255)); // X轴标签文字颜色
		renderer.setYLabelsColor(0, Color.argb(255, 255, 255, 255)); // Y轴标签文字颜色
		
		dataSeries = new XYSeries("data");
		dataRenderer = new XYSeriesRenderer();
		dataRenderer.setColor(0xFF00FF00);
		dataRenderer.setPointStyle(PointStyle.CIRCLE);
		dataRenderer.setFillPoints(true);
		
		graphView.addSeriesAndRenderer(dataSeries, dataRenderer);
		
		buttonStart.setOnClickListener(new OnClickListener()
    	{
    		public void onClick(View v)
        	{
    			if (null != bluetoothSocket)
    			{
    				if (isDetecting)
    				{
    					MainActivity.this.buttonStart.setText(R.string.start);
    					MainActivity.this.sendDataToDetector(MainActivity.SEND_STOP_DETECTION);
    				}
    				else
    				{
    					dataSeries.clear(); // 先清空旧数据
    					MainActivity.this.buttonStart.setText(R.string.stop);
    					MainActivity.this.sendDataToDetector(MainActivity.SEND_START_DETECTION);
    				}
    				isDetecting = !isDetecting;
    			}
        	}
        });
		
		buttonConnect.setOnClickListener(new OnClickListener()
    	{
    		public void onClick(View v)
        	{
    			if (null == bluetoothSocket)
    			{
    				Intent intent = new Intent();
            		intent.setClass(MainActivity.this, BTHOperationActivity.class);
            		MainActivity.this.startActivityForResult(intent, 0); // requestCode为0表示BTHOperationActivity
    			}
    			else
    			{
    				MainActivity.this.enableCommunicate = false; // 要求停止通信线程
    				MainActivity.this.bluetoothDeviceSelected = null;
    				MainActivity.this.sendEventsBundle(MainActivity.EventType.BLUETOOTH_SOCKET_DISCONNECTING);
    			}
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
			if (resultCode == RESULT_OK)
	    	{
				bluetoothDeviceSelected = data.getParcelableExtra("BluetoothDevice");
				connectBTHDevice(); // 连接设备
	    	}
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed()
	{
		AlertDialog.Builder finishbuilder = new AlertDialog.Builder(this);
		
		finishbuilder.setTitle(R.string.sure_to_exit).setIcon(R.drawable.ic_launcher);

		finishbuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				MainActivity.this.sendDataToDetector(MainActivity.SEND_DISCONNECT_BTH); // 向检测仪发送断开连接消息
				MainActivity.this.enableCommunicate = false;
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
     * 连接蓝牙设备
     */
    public void connectBTHDevice()
    {
    	if (bluetoothDeviceSelected != null)
    	{
    		new Thread()
    		{
    			public void run()
    			{
    				// 设置状态为正在连接 
    				MainActivity.this.sendEventsBundle(MainActivity.EventType.BLUETOOTH_SOCKET_CONNECTING);
    				
					// 连接
    				BluetoothSocket tempsocket = null;
    				try
    				{
						// 创建一个Socket连接，SPP_UUID表示串口模式的UUID号
    					tempsocket = bluetoothDeviceSelected.createRfcommSocketToServiceRecord(UUID.fromString(MainActivity.SPP_UUID));
    					tempsocket.connect();
    					bluetoothSocket = tempsocket;
    					
    					startToCommunicate();
						
    					// 若连接成功则发送BLUETOOTH_SOCKET_CONNECTED消息
    					MainActivity.this.sendEventsBundle(MainActivity.EventType.BLUETOOTH_SOCKET_CONNECTED);
    				}
					catch (IOException e)
    				{
    					// 若出错则尝试关闭套接字
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
	 * 设置消息处理Handler
     */
	private void setEventsHandler()
	{
    	eventsHandler = new Handler()
    	{
			@Override
			public void handleMessage(Message msg)
			{
				Bundle databundle = msg.getData();
				// 取出事件类型进行对应操作
				switch((EventType)databundle.getSerializable("EventType"))
				{
				case BLUETOOTH_SOCKET_UNCONNECTED: // 蓝牙未连接事件
					bluetoothSocket = null;
					MainActivity.this.buttonConnect.setText(R.string.connect);
					MainActivity.this.buttonConnect.setEnabled(true);
					break;
				case BLUETOOTH_SOCKET_CONNECTING: // 蓝牙正在连接事件
					MainActivity.this.buttonConnect.setText(R.string.connecting);
					MainActivity.this.buttonConnect.setEnabled(false);
					break;
				case BLUETOOTH_SOCKET_CONNECTED: // 蓝牙已连接事件
					// 向检测仪发送连接成功消息
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
					break;
				case BLUETOOTH_SOCKET_DISCONNECTING: // 蓝牙正在断开事件
					MainActivity.this.buttonConnect.setText(R.string.disconnecting);
					MainActivity.this.buttonConnect.setEnabled(false);
					break;
				}
			}
    	};
	}
	
	/**
	 * 向eventsHandler发送事件消息
	 * 
	 * @param eventtype 事件类型，包括：
     * {@link #BLUETOOTH_SOCKET_UNCONNECTED}蓝牙未连接事件
     * {@link #BLUETOOTH_SOCKET_CONNECTING}蓝牙正在连接事件
     * {@link #BLUETOOTH_SOCKET_CONNECTED}蓝牙已连接事件
     * {@link #BLUETOOTH_SOCKET_DISCONNECTING}蓝牙正在断开事件
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
     * 向检测仪发送数据
     * 
     * @param type 表示数据的类型，包括：
     * {@link #SEND_CONNECT_OK}
     * {@link #SEND_START_DETECTION}
     * {@link #SEND_DISCONNECT_BTH}
     * {@link #SEND_STOP_DETECTION}
     * 
     * @return 若成功发送则返回true，若失败返回false
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
			datalength++; // 增加一个字节，表示头部的type
			
			byte[] intbyte = new byte[4];
			for (int i = 0; i < 4; i++)
			{
				intbyte[i] = (byte)((datalength >> (8 * (3 - i))) & 0xFF);
			}
			
			try
			{
				OutputStream outputstream = bluetoothSocket.getOutputStream();
				outputstream.write(intbyte, 0, 4); // 发送数据长度
				outputstream.write(new byte[]{(byte)type}, 0, 1); // 发送类型标志
				outputstream.write(databuffer, 0, (datalength - 1)); // 发送数据本身
				outputstream.flush();
				
				result = true;
			}
			catch(IOException e)
			{
				// 若抛出异常则关闭蓝牙，置为null
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
    
    
//    
//    private void updateChart() {  
//        
//        //设置好下一个需要增加的节点  
//        addX = 0;  
//        addY = (int)(Math.random() * 90);  
//          
//        //移除数据集中旧的点集  
//        mDataset.removeSeries(series);  
//          
//        //判断当前点集中到底有多少点，因为屏幕总共只能容纳100个，所以当点数超过100时，长度永远是100  
//        int length = series.getItemCount();  
//        if (length > 100) {  
//            length = 100;  
//        }  
//          
//        //将旧的点集中x和y的数值取出来放入backup中，并且将x的值加1，造成曲线向右平移的效果  
//        for (int i = 0; i < length; i++) {  
//            xv[i] = (int) series.getX(i) + 1;  
//            yv[i] = (int) series.getY(i);  
//        }  
//          
//        //点集先清空，为了做成新的点集而准备  
//        series.clear();  
//          
//        //将新产生的点首先加入到点集中，然后在循环体中将坐标变换后的一系列点都重新加入到点集中  
//        //这里可以试验一下把顺序颠倒过来是什么效果，即先运行循环体，再添加新产生的点  
//        series.add(addX, addY);  
//        for (int k = 0; k < length; k++) {  
//            series.add(xv[k], yv[k]);  
//        }  
//          
//        //在数据集中添加新的点集  
//        mDataset.addSeries(series);  
//          
//        //视图更新，没有这一步，曲线不会呈现动态  
//        //如果在非UI主线程中，需要调用postInvalidate()，具体参考api  
//        chart.invalidate();  
//    }  
//}  



    
    /**
     * 启动蓝牙通信线程
     */
    public void startToCommunicate()
    {
    	if (isCommunicateThreadRunning && enableCommunicate) // 已有线程在执行，直接返回
		{
			return;
		}
    	
		while (isCommunicateThreadRunning); // 线程可能正在结束中，等待结束

		isCommunicateThreadRunning = true;
		enableCommunicate = true;
    	
		// 启动线程
    	new Thread()
    	{
    		public void run()
	      	{
				int length = 1;
				
				double [] databufferx = new double[length];
				double [] databuffery = new double[length];
				
				double[] xv=new double[MAXX];
				double[] yv=new double[MAXX];
				
    			while(MainActivity.this.enableCommunicate)
    			{
					if(MainActivity.this.bluetoothSocket != null)
					{
						graphlength = 0;
						
						// 从蓝牙设备读取数据
	    				try {
	    					int byteofunit = 4; // 由单片机发来的字节流每byteofunit个表示一个数据
	    					int nbyte = byteofunit * length;
	    					int nread = 0;
	    					byte [] databuffer = new byte[nbyte];
	    					byte [] tempbuffer = null;
	    					int oldnread = 0;
	    					while (nread < nbyte) // 该循环保证接收到指定长度的数据后才进行后续处理
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
	    						
	    					databuffery[graphlength] = Double.parseDouble(new String(bytey)); // 微安电流差值
							graphlength++; // 计算接收的图表数据个数
	    					}
	    					
	    					
	    					
	    					
	    					
	    					
	    					
	    					
	    					
	    					
	    					
	    					 
	    					
//	    					int tempx = 0;
//	    					int tempy = 0;
//	    					for (int i = 0; i < length; i++)
//							{
//	    						// 将接收的字节数据组装成int类型数据
//	    						tempx = 0;
//	    						tempy = 0;
//	    						for (int j = 0; j < byteofunit/2; j++)
//								{
//	    							tempx = tempx | ((0xFF & databuffer[i * byteofunit + j]) << (8 * j));
//	    							tempy = tempy | ((0xFF & databuffer[i * byteofunit + byteofunit / 2 + j]) << (8 * j));
//								}
//	    					
//	    						
//	    						// tempy为-4095(0xF001)至4095(0x0FFF)时表示AD的数值，其余情况(0x1000-0xF000)用于指示其它数据
//	    						if (tempy != MainActivity.REV_BEAT_PACKET)
//	    						{
//	    							// 若不为心跳包则进行处理
//	    							if ((tempy >= (-4095)) && (tempy <= 4095)) // [-4095,4095]区间为有效采集数据
//	    							{
////	    								if (AppGlobal.CHECK_PROCESS_STEP == DetectionActivity.this.currentProcess)
////	    		    					{
////	    									// 若当前为阶梯阶段则保存下电流值
////	    									datay1 = 1000.0 * ref * (tempy - offset) / 0xFFF / res - lc; // 微安电流
////	    		    					}
////	    								
////	    								if (AppGlobal.CHECK_PROCESS_PLUSE == DetectionActivity.this.currentProcess)
////	    		    					{
////	    									// 若当前为脉冲阶段则计算电流值并减去阶梯阶段的电流值，添加至图表数据缓冲区
////	    									databuffery[graphlength] = 1000.0 * ref * (tempy - offset) / 0xFFF / res - lc - datay1; // 微安电流差值
////	    									databufferx[graphlength] = (tempx * wrscale / 0xFFFF + wrmin) / 1000;
////	    									graphlength++; // 计算接收的图表数据个数
////	    		    					}
//	    								databuffery[graphlength] = tempy; // 微安电流差值
//	    								databufferx[graphlength] = tempx;
//	    								graphlength++; // 计算接收的图表数据个数
//	    							}
//	    						}
//							}
	    				} catch (Exception e) {
	    					// 若抛出异常则退出循环，结束线程
	    					break;
	    				}
	    				
	    				
	    				

	    				double newy=databuffery[0];
    					double newx=MAXX;
    					
    					
    				
    					int l = dataSeries.getItemCount();
    					
    					
    					if ( (l>=MAXX+1)&&(graphlength>0) ){  
    						
//    						graphView.clearSeries();
    			        
    					for (int i = 0; i <MAXX; i++) {  
//    		          xv[i] =(double) dataSeries.getX(i);  
    		           yv[i] = (double) dataSeries.getY(i+1);  
    		        }  
    		          
    		      
    					   //点集先清空，为了做成新的点集而准备  
        		        dataSeries.clear(); 
    		        //将新产生的点首先加入到点集中，然后在循环体中将坐标变换后的一系列点都重新加入到点集中  
    		        //这里可以试验一下把顺序颠倒过来是什么效果，即先运行循环体，再添加新产生的点  
    		         
    		        for (int k = 0; k < MAXX; k++) {  
    		            dataSeries.add(k, yv[k]);  
    		        }  
    		          
    		        dataSeries.add(newx, newy);
    		        //在数据集中添加新的点集  
//    		        graphView.addSeries(dataSeries);  
    		          
    		        //视图更新，没有这一步，曲线不会呈现动态  
    		        //如果在非UI主线程中，需要调用postInvalidate()，具体参考api  
    		   
    		        MainActivity.this.graphView.fitGraph();
    					}
	    				
	    				
	    				
	    				
    					else if ( (l<1)&&(graphlength>0) )// && isDetecting)
						{
	    				
							// 若有新曲线数据则更新进度条与图表
    						
    						
//	    					for (int i = 0; i <graphlength; i++)
//	    					{
////	    						MainActivity.this.dataSeries.add(databufferx[i], databuffery[i]);
////	    						MainActivity.this.dataSeries.add( l+i, databuffery[i]);
//	    						
//	    						yv[i]= databuffery[i];
//	    					}
//	    					
//	    					dataSeries.clear();
	    					for (int i = 0; i <MAXX; i++) {  
	   		    		          MainActivity.this.dataSeries.add( i, yv[i]); 
	    		    		        }  
	    					 MainActivity.this.dataSeries.add( MAXX, databuffery[0]); 
	    					MainActivity.this.graphView.fitGraph();
						}
					}
					
					
					
					try {
						Thread.sleep(1); // 休息1毫秒
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
    			}

    			MainActivity.this.sendDataToDetector(MainActivity.SEND_DISCONNECT_BTH); // 向MSP单片机发送断开连接消息
				
				// 关闭蓝牙通信套接字
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
}
