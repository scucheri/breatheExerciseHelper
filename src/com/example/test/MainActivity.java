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
	
	
	static final public int MAXX=50;
		
		
	// �¼�����ö��
	static private enum EventType { BLUETOOTH_SOCKET_UNCONNECTED, // ����δ�����¼�
									BLUETOOTH_SOCKET_CONNECTING, // �������������¼�
									BLUETOOTH_SOCKET_CONNECTED,	// �����������¼�
									BLUETOOTH_SOCKET_DISCONNECTING,	// �������ڶϿ��¼�
									};
		
	// �ؼ�
	private ComplexGraph graphView = null;
	private TextView textViewInfo = null;
	private Button buttonStart = null;
	private Button buttonConnect = null;
	
	private Handler eventsHandler = null; // �¼�Handler��������Ӧ�����¼�
	
	public boolean enableCommunicate = false; // ����ͨ���߳�ʹ�ܿ���
	public boolean isCommunicateThreadRunning = false; // ����ͨ���߳����б�־
	public BluetoothDevice bluetoothDeviceSelected = null; // ��ѡ�����ӵ������豸
	public BluetoothSocket bluetoothSocket = null; // �����׽��֣��������ⲿ�����豸(���������ģ��)ͨ��
	
	public boolean isDetecting = false; // ���ڼ���־
	
	private XYSeries dataSeries; // ����
	private XYSeriesRenderer dataRenderer; // ��Ⱦ��
	
	
	private int graphlength ;

	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // ��ȡ�ؼ�
        graphView = (ComplexGraph)findViewById(R.id.graphicView);
        textViewInfo = (TextView)findViewById(R.id.textViewInfo);
        buttonStart = (Button)findViewById(R.id.buttonStart);
        buttonConnect = (Button)findViewById(R.id.buttonConnect);
        
        XYMultipleSeriesRenderer renderer = graphView.getRenderer();
        renderer.setApplyBackgroundColor(true); // �����Ƿ���ʾ����ɫ
		renderer.setBackgroundColor(Color.BLACK); // ���ñ���ɫ
		renderer.setMargins(new int[]{10, 40, 40, 10}); // ����ͼ�����߿�(��/��/��/��)
        renderer.setAxisTitleTextSize(25); // ������������ֵĴ�С
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
		renderer.setXLabels(7); // X��̶�����
		renderer.setYLabels(7); // Y��̶�����
		renderer.setXLabelsColor(Color.argb(255, 255, 255, 255)); // X���ǩ������ɫ
		renderer.setYLabelsColor(0, Color.argb(255, 255, 255, 255)); // Y���ǩ������ɫ
		
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
    					dataSeries.clear(); // ����վ�����
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
				connectBTHDevice(); // �����豸
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
				MainActivity.this.sendDataToDetector(MainActivity.SEND_DISCONNECT_BTH); // �����Ƿ��ͶϿ�������Ϣ
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
					break;
				case BLUETOOTH_SOCKET_DISCONNECTING: // �������ڶϿ��¼�
					MainActivity.this.buttonConnect.setText(R.string.disconnecting);
					MainActivity.this.buttonConnect.setEnabled(false);
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
				
				double [] databufferx = new double[length];
				double [] databuffery = new double[length];
				
				double[] xv=new double[MAXX];
				double[] yv=new double[MAXX];
				
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
	    						
	    					databuffery[graphlength] = Double.parseDouble(new String(bytey)); // ΢��������ֵ
							graphlength++; // ������յ�ͼ�����ݸ���
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
	    				
	    				
	    				

	    				double newy=databuffery[0];
    					double newx=MAXX;
    					
    					
    				
    					int l = dataSeries.getItemCount();
    					
    					
    					if ( (l>=MAXX+1)&&(graphlength>0) ){  
    						
//    						graphView.clearSeries();
    			        
    					for (int i = 0; i <MAXX; i++) {  
//    		          xv[i] =(double) dataSeries.getX(i);  
    		           yv[i] = (double) dataSeries.getY(i+1);  
    		        }  
    		          
    		      
    					   //�㼯����գ�Ϊ�������µĵ㼯��׼��  
        		        dataSeries.clear(); 
    		        //���²����ĵ����ȼ��뵽�㼯�У�Ȼ����ѭ�����н�����任���һϵ�е㶼���¼��뵽�㼯��  
    		        //�����������һ�°�˳��ߵ�������ʲôЧ������������ѭ���壬������²����ĵ�  
    		         
    		        for (int k = 0; k < MAXX; k++) {  
    		            dataSeries.add(k, yv[k]);  
    		        }  
    		          
    		        dataSeries.add(newx, newy);
    		        //�����ݼ�������µĵ㼯  
//    		        graphView.addSeries(dataSeries);  
    		          
    		        //��ͼ���£�û����һ�������߲�����ֶ�̬  
    		        //����ڷ�UI���߳��У���Ҫ����postInvalidate()������ο�api  
    		   
    		        MainActivity.this.graphView.fitGraph();
    					}
	    				
	    				
	    				
	    				
    					else if ( (l<1)&&(graphlength>0) )// && isDetecting)
						{
	    				
							// ������������������½�������ͼ��
    						
    						
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
}
