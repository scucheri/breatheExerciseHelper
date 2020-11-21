package plugin.android.ss.com.breatheexercisehelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.achartengine.chart.PointStyle;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;

public class QueryActivity  extends Activity {
	private static final double DateInterval = 20;//��ͬ����������20

	private static final double haleDisplayXLabelInterval = 5;//������������ÿ5������ʾʱ���ǩ
	private static final double haleDateDisplayPeriod=haleDisplayXLabelInterval*5;//ÿ5����ʾ��ʱ���ǩ����һ����ʾ������
	
	private static final double abnormalDisplayXLabelInterval =3;//�쳣��������ÿ10������ʾʱ���ǩ
	private static final double abnormalDateDisplayPeriod=abnormalDisplayXLabelInterval*5;//ÿ5����ʾ��ʱ���ǩ����һ����ʾ������
	
	private int haleDisplayCount,abnormalDisplayCount;
	
	private ComplexGraph haleDataView,abnormalDataView;
	private  XYMultipleSeriesRenderer haleDataRenderer,abnormalDataRenderer;
	private  XYSeriesRenderer haleFrequencyRenderer,haleIntensityRenderer,haleRatioRenderer,abnormalFrequencyRenderer,abnormalIntensityRenderer;
	private XYSeries haleFrequencySeries,haleIntensitySeries,haleRatioSeries,abnormalIntensitySeries,abnormalFrequencySeries;
	private String haleDataFile="������������.txt";
	private String abnormalDataFile="�쳣��������.txt";
	
	ArrayList<String> haleData_TimeList=new ArrayList<String>();
	ArrayList<String> haleData_FrequencyList=new ArrayList<String>();
	ArrayList<String> haleData_IntensityList=new ArrayList<String>();
	ArrayList<String> haleData_RatioList=new ArrayList<String>();
	ArrayList<String> haleData_XLabelList=new ArrayList<String>();
	ArrayList<String> haleData_XList=new ArrayList<String>();
	
	ArrayList<String> abnormalData_TimeList=new ArrayList<String>();
	ArrayList<String> abnormalData_FrequencyList=new ArrayList<String>();
	ArrayList<String> abnormalData_IntensityList=new ArrayList<String>();
	ArrayList<String> abnormalData_XLabelList=new ArrayList<String>();
	ArrayList<String> abnormalData_XList=new ArrayList<String>();
	ArrayList<String> abnormalData_ReasonList=new ArrayList<String>();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query);
		
		haleDataView=(ComplexGraph)findViewById(R.id.haleDataView);
		abnormalDataView=(ComplexGraph)findViewById(R.id.abnormalDataView);
		
		
		
		//������������ͼ��
		    haleDataRenderer = haleDataView.getRenderer();
	        haleDataRenderer.setApplyBackgroundColor(true); // �����Ƿ���ʾ����ɫ
			haleDataRenderer.setBackgroundColor(Color.BLACK); // ���ñ���ɫ
			haleDataRenderer.setMargins(new int[]{10, 40, 40, 10}); // ����ͼ�����߿�(��/��/��/��)
			haleDataRenderer.setChartTitleTextSize(40);
	        haleDataRenderer.setAxisTitleTextSize(30); // ������������ֵĴ�С
	        haleDataRenderer.setLabelsTextSize(20); // ���ÿ̶���ʾ���ֵĴ�С(XY�ᶼ�ᱻ����)
	        haleDataRenderer.setLegendTextSize(20); // ͼ�����ִ�С
	        haleDataRenderer.setPointSize(10); // ���õ�Ĵ�С(ͼ����ʾ�ĵ�Ĵ�С��ͼ���е�Ĵ�С���ᱻ����)
	        haleDataRenderer.setShowGrid(true); // �Ƿ���ʾ����
	        haleDataRenderer.setSelectableBuffer(10); // ���õ�Ļ���뾶ֵ(��ĳ�㸽�����ʱ,���Χ�ڶ����������)
//			haleDataRenderer.setXTitle(MainActivity.this.getString(R.string.potential) + "(" + 
//					MainActivity.this.getResources().getStringArray(R.array.potential_unit)[AppGlobal.DEFAULT_POTENTIAL_UNIT] + ")"); // X��˵��
//			haleDataRenderer.setYTitle(MainActivity.this.getString(R.string.current) + "(" + 
//					MainActivity.this.getResources().getStringArray(R.array.current_unit)[AppGlobal.DEFAULT_CURRENT_UNIT] + ")"); // Y��˵��
			haleDataRenderer.setLegendHeight(100); // ����ͼ������λ�ø߶�
			haleDataRenderer.setAxesColor(Color.argb(255, 250, 250, 250)); // ����ɫ
			haleDataRenderer.setGridColor(Color.argb(150, 200, 200, 200)); // ������ɫ
			haleDataRenderer.setLabelsColor(Color.argb(180, 180, 180, 180)); // ��ע��ɫ
			haleDataRenderer.setXLabels(0); // X��̶�����
			haleDataRenderer.setYLabels(10); // Y��̶�����
			haleDataRenderer.setXLabelsColor(Color.argb(255, 255, 255, 255)); // X���ǩ������ɫ
			haleDataRenderer.setYLabelsColor(0, Color.argb(255, 255, 255, 255)); // Y���ǩ������ɫ
			
			haleDataRenderer.setChartTitle("������������");
			 haleDataRenderer.setXTitle("ʱ��");
			 haleDataRenderer.setYTitle("Ƶ��/ǿ��/������");
			
			
			 
			 haleFrequencyRenderer = new XYSeriesRenderer();
	          haleFrequencyRenderer.isHighlighted();
	          haleFrequencyRenderer.setFillBelowLineColor(Color.YELLOW);
			 haleFrequencyRenderer.setColor(0xFF00FF00);
			 haleFrequencyRenderer.setPointStyle(PointStyle.TRIANGLE);
			 haleFrequencyRenderer.setFillPoints(true);
		
			
			
			 
			 haleIntensityRenderer = new XYSeriesRenderer();
			 haleIntensityRenderer.isHighlighted();
			 haleIntensityRenderer.setFillBelowLineColor(Color.YELLOW);
			 haleIntensityRenderer.setColor(Color.RED);
			 haleIntensityRenderer.setPointStyle(PointStyle.SQUARE);
			 haleIntensityRenderer.setFillPoints(true);
			 
			 
			 haleRatioRenderer = new XYSeriesRenderer();
			 haleRatioRenderer.isHighlighted();
			 haleRatioRenderer.setFillBelowLineColor(Color.YELLOW);
			 haleRatioRenderer.setColor(Color.YELLOW);
			 haleRatioRenderer.setPointStyle(PointStyle.CIRCLE);
			 haleRatioRenderer.setFillPoints(true);
			 
//			haleIntensityRenderer = new XYSeriesRenderer();
//			haleIntensityRenderer.setColor(Color.RED);
//			haleIntensityRenderer.setPointStyle(PointStyle.POINT);
//			haleIntensityRenderer.setFillPoints(true);
//			haleIntensityRenderer.setShowLegendItem(true);
//			haleIntensityRenderer.setLineWidth(4);
			
			 
			  readFromDataFile(haleDataFile);
			  
//			  for(int i=0;i<haleData_TimeList.size();i++)
//			  {
//				  System.out.println(haleData_TimeList.get(i)); 
//			  }
			
			 
			  
			  haleIntensitySeries = new XYSeries("����ǿ��");
			  haleDataView.addSeriesAndRenderer( haleIntensitySeries, haleIntensityRenderer);
			  
			  for(int i=0;i<haleData_IntensityList.size();i++)
			  {
			  haleIntensitySeries.add(Double.parseDouble(haleData_XList.get(i)), Double.parseDouble(haleData_IntensityList.get(i)));
//				  haleIntensitySeries.add(i, Double.parseDouble(haleData_IntensityList.get(i)));
			  
			  }
			  
			  
			  haleFrequencySeries = new XYSeries("����Ƶ��");
			  haleDataView.addSeriesAndRenderer( haleFrequencySeries, haleFrequencyRenderer);
			  
			  for(int i=0;i<haleData_FrequencyList.size();i++)
			  {
			  haleFrequencySeries.add(Double.parseDouble(haleData_XList.get(i)), Double.parseDouble(haleData_FrequencyList.get(i)));
//				  haleFrequencySeries.add(i, Double.parseDouble(haleData_FrequencyList.get(i)));
			  }
			  
			  
			  
			  haleRatioSeries = new XYSeries("��������ʱ���");
			  haleDataView.addSeriesAndRenderer( haleRatioSeries, haleRatioRenderer);
			  
			  for(int i=0;i<haleData_RatioList.size();i++)
			  {
			  haleRatioSeries.add(Double.parseDouble(haleData_XList.get(i)), Double.parseDouble(haleData_RatioList.get(i)));
//				  haleRatioSeries.add(i, Double.parseDouble(haleData_RatioList.get(i)));
			  }
			  
			  
			  for(int i=0;i<haleData_XLabelList.size();i++)
			  {
				  if(haleDisplayCount==0)
				  {
					  haleDataRenderer.addTextLabel(Double.parseDouble(haleData_XList.get(i)), haleData_XLabelList.get(i));  
				  }
				  
				  haleDisplayCount++;
				  if(haleDisplayCount==haleDisplayXLabelInterval)
				  {
					  haleDisplayCount=0;
				  }
//				  haleDataRenderer.addTextLabel(i, haleData_XLabelList.get(i));
			  }
			  
//			  haleIntensitySeries = new XYSeries("����ǿ��");
//			  haleDataView.addSeriesAndRenderer( haleIntensitySeries, haleIntensityRenderer);
//			  for(int i=0;i<100;i++)
//			  {
//			  haleIntensitySeries.add(i, i-5);
//			  }
//			  
//			  
//			  haleRatioSeries = new XYSeries("��������ʱ���");
//			  haleDataView.addSeriesAndRenderer( haleRatioSeries, haleRatioRenderer);
//			  for(int i=0;i<100;i++)
//			  {
//			  haleRatioSeries.add(i, i-10);
//			  }
			  
			  haleDataView.fitGraph();
			 
			  
			  
			  
			  
			  
			//�쳣��������ͼ��
			 abnormalDataRenderer = abnormalDataView.getRenderer();
		        abnormalDataRenderer.setApplyBackgroundColor(true); // �����Ƿ���ʾ����ɫ
				abnormalDataRenderer.setBackgroundColor(Color.BLACK); // ���ñ���ɫ
				abnormalDataRenderer.setMargins(new int[]{10, 40, 40, 10}); // ����ͼ�����߿�(��/��/��/��)
				abnormalDataRenderer.setChartTitleTextSize(40);
		        abnormalDataRenderer.setAxisTitleTextSize(30); // ������������ֵĴ�С
		        abnormalDataRenderer.setLabelsTextSize(20); // ���ÿ̶���ʾ���ֵĴ�С(XY�ᶼ�ᱻ����)
		        abnormalDataRenderer.setLegendTextSize(20); // ͼ�����ִ�С
		        abnormalDataRenderer.setPointSize(10); // ���õ�Ĵ�С(ͼ����ʾ�ĵ�Ĵ�С��ͼ���е�Ĵ�С���ᱻ����)
		        abnormalDataRenderer.setShowGrid(true); // �Ƿ���ʾ����
		        abnormalDataRenderer.setSelectableBuffer(10); // ���õ�Ļ���뾶ֵ(��ĳ�㸽�����ʱ,���Χ�ڶ����������)
//				abnormalDataRenderer.setXTitle(MainActivity.this.getString(R.string.potential) + "(" + 
//						MainActivity.this.getResources().getStringArray(R.array.potential_unit)[AppGlobal.DEFAULT_POTENTIAL_UNIT] + ")"); // X��˵��
//				abnormalDataRenderer.setYTitle(MainActivity.this.getString(R.string.current) + "(" + 
//						MainActivity.this.getResources().getStringArray(R.array.current_unit)[AppGlobal.DEFAULT_CURRENT_UNIT] + ")"); // Y��˵��
				abnormalDataRenderer.setLegendHeight(100); // ����ͼ������λ�ø߶�
				abnormalDataRenderer.setAxesColor(Color.argb(255, 250, 250, 250)); // ����ɫ
				abnormalDataRenderer.setGridColor(Color.argb(150, 200, 200, 200)); // ������ɫ
				abnormalDataRenderer.setLabelsColor(Color.argb(180, 180, 180, 180)); // ��ע��ɫ
				abnormalDataRenderer.setXLabels(0); // X��̶�����
				abnormalDataRenderer.setYLabels(10); // Y��̶�����
				abnormalDataRenderer.setXLabelsColor(Color.argb(255, 255, 255, 255)); // X���ǩ������ɫ
				abnormalDataRenderer.setYLabelsColor(0, Color.argb(255, 255, 255, 255)); // Y���ǩ������ɫ
				
				abnormalDataRenderer.setChartTitle("�쳣��������");
				 abnormalDataRenderer.setXTitle("ʱ��");
				 abnormalDataRenderer.setYTitle("Ƶ��/ǿ��");
				
				 
				 abnormalFrequencyRenderer = new XYSeriesRenderer();
		          abnormalFrequencyRenderer.isHighlighted();
		          abnormalFrequencyRenderer.setFillBelowLineColor(Color.YELLOW);
				 abnormalFrequencyRenderer.setColor(0xFF00FF00);
				 abnormalFrequencyRenderer.setPointStyle(PointStyle.TRIANGLE);
				 abnormalFrequencyRenderer.setFillPoints(true);
				
				
				 
				 abnormalIntensityRenderer = new XYSeriesRenderer();
				 abnormalIntensityRenderer.isHighlighted();
				 abnormalIntensityRenderer.setFillBelowLineColor(Color.YELLOW);
				 abnormalIntensityRenderer.setColor(Color.RED);
				 abnormalIntensityRenderer.setPointStyle(PointStyle.SQUARE);
				 abnormalIntensityRenderer.setFillPoints(true);
				 
				  readFromDataFile(abnormalDataFile);
				  
//				  for(int i=0;i<abnormalData_TimeList.size();i++)
//				  {
//					  System.out.println(abnormalData_TimeList.get(i)); 
//				  }
//				
				  
				  for(int i=1;i<abnormalData_XList.size()-1;i++)
				  {
				 if((abnormalData_ReasonList.get(i-1).contains("�޺����ź�"))&(abnormalData_ReasonList.get(i).contains("�޺����ź�"))&(abnormalData_ReasonList.get(i+1).contains("�޺����ź�")))
				
//			if(abnormalData_ReasonList.get(i).contains("�޺����ź�"))
						 {
					 
					 abnormalData_IntensityList.set(i-1, "0");
					 abnormalData_IntensityList.set(i+1, "0");
					 abnormalData_FrequencyList.set(i-1, "0");
					 abnormalData_FrequencyList.set(i+1, "0");//���޺����źŵ����߹���
					 
					 abnormalData_ReasonList.remove(i);
					 abnormalData_IntensityList.remove(i);
					 abnormalData_FrequencyList.remove(i);
					 abnormalData_TimeList.remove(i);
					 abnormalData_XList.remove(i);
					 abnormalData_XLabelList.remove(i);
					 i--;
				 }
				  }
				  
				  
				  abnormalIntensitySeries = new XYSeries("����ǿ��");
				  abnormalDataView.addSeriesAndRenderer( abnormalIntensitySeries, abnormalIntensityRenderer);
				  
				  for(int i=0;i<abnormalData_IntensityList.size();i++)
				  {
				  abnormalIntensitySeries.add(Double.parseDouble(abnormalData_XList.get(i)), Double.parseDouble(abnormalData_IntensityList.get(i)));
//					  abnormalIntensitySeries.add(i, Double.parseDouble(abnormalData_IntensityList.get(i)));
				  
				  }
				  
				  
				  abnormalFrequencySeries = new XYSeries("����Ƶ��");
				  abnormalDataView.addSeriesAndRenderer( abnormalFrequencySeries, abnormalFrequencyRenderer);
				  
				  for(int i=0;i<abnormalData_FrequencyList.size();i++)
				  {
				  abnormalFrequencySeries.add(Double.parseDouble(abnormalData_XList.get(i)), Double.parseDouble(abnormalData_FrequencyList.get(i)));
//					  abnormalFrequencySeries.add(i, Double.parseDouble(abnormalData_FrequencyList.get(i)));
				  }
				  
				  for(int i=0;i<abnormalData_XLabelList.size();i++)
				  {
					  if(abnormalDisplayCount==0)
					  {
					  abnormalDataRenderer.addTextLabel(Double.parseDouble(abnormalData_XList.get(i)), abnormalData_XLabelList.get(i));  
					  }
					  
					  abnormalDisplayCount++;
					  if(abnormalDisplayCount==abnormalDisplayXLabelInterval)
					  {
						  abnormalDisplayCount=0;
					  }
//					  abnormalDataRenderer.addTextLabel(i, abnormalData_XLabelList.get(i));
				  }
				  
				  abnormalDataView.fitGraph(); 
				
			
	}

	
	private void readFromDataFile(String fileName)
	{ int haleXFlag=0;
	int abnormalXFlag=0;
		
		 FileReader haleDatafr = null;
		BufferedReader haleDatabr = null;
		
		 FileReader abnormalDatafr = null;
			BufferedReader abnormalDatabr = null;
		
		String local_file = Environment.getExternalStorageDirectory().getAbsolutePath()+"/��������/";
		 File fDir = new File(local_file);  
		 if(fDir.exists())
		 {  
			//�����������ļ������ȡ����
			if(fileName==haleDataFile)	
			{ 
			try{
				 haleDatafr=new FileReader(local_file+fileName);
				 haleDatabr=new BufferedReader(haleDatafr);

				 while(true)
				 {
				 String line=haleDatabr.readLine();
				 if(line.contains("ʱ��"))
				 {		
					 //XLabelList�༭
					 if(haleXFlag==0)
					 {
						 haleData_XLabelList.add(getStringDate(line).substring(4));
					 }
					 else
						 {
						 haleData_XLabelList.add(getStringTime(line).substring(8));
						 }
					 
					 haleXFlag++;
					 if(haleXFlag== haleDateDisplayPeriod)
					 {
						 haleXFlag=0;
					 }
					 
					 //XList�༭
					 //�����һ��Ԫ��
					 if(haleData_XList.size()==0)
					 {
						 haleData_XList.add("1");
						 }
					 
					 
					 if(haleData_TimeList.size()!=0)
					 {
						//��ÿһ��Ĳ������߹��ൽһ�𣬲���ͬһ�������֮����10
						 if(getStringTime(line).substring(4,8).equals(haleData_TimeList.get(haleData_TimeList.size()-1).substring(5, 9)))
						 {

							double temp=Double.parseDouble(haleData_XList.get(haleData_XList.size()-1))+1;
							 haleData_XList.add(temp+"");	
						 }
						 
						 else{
							 double temp=Double.parseDouble(haleData_XList.get(haleData_XList.size()-1))+DateInterval;
							 haleData_XList.add(temp+"");
							 haleData_XLabelList.set(haleData_XLabelList.size()-1, getStringTime(line).substring(4));//���µ����ڵ����ݵ�ʱ�����ǩ����������ʾ
							 
							 haleDataRenderer.addTextLabel(Double.parseDouble(haleData_XList.get(haleData_XList.size()-1)), haleData_XLabelList.get(haleData_XLabelList.size()-1));  
						 } 
					 }
					 
					 
					 haleData_TimeList.add(getStringNum(line));//�ڸ�ֵXList֮��Ÿ�ֵTimeList
					 
					
					
					 
					 //����XList����
			
					// System.out.println(getStringTime(line).substring(8));
					 
					 //Integer.parseInt(getStringNum(line));
					 
					 //HaleData haleData=new HaleData();
				 }
				 
			
				 if(line.contains("����Ƶ��"))
				 {
//					 System.out.println(line); 
//					 System.out.println(getStringNum(line));
					 
					 haleData_FrequencyList.add(getStringNum(line));
				 }
				 
				 
				 if(line.contains("����ǿ��"))
				 {
//					 System.out.println(line); 
//					 System.out.println(getStringNum(line));
					 
					 haleData_IntensityList.add(getStringNum(line));
				 }
				 
				 
				 if(line.contains("����/����"))
				 {
//					 System.out.println(line); 
//					 System.out.println(getStringNum(line));
					 
					 haleData_RatioList.add(getStringNum(line));
					 
				 }
				 
				 
				 
				 if(line==null)
				   break;
				// System.out.println(line);

				 }
				 }

				 catch(Exception e)
				 {
				 e.printStackTrace();
				 }


				 finally{
				 try{
				 haleDatabr.close();
				 haleDatafr.close();
				 }
				 catch(Exception e)
				 {
				 System.out.println(e);
				 }
				 }
		 }
		 
		 
		 else if(fileName==abnormalDataFile)
		 {
			 try{
				 abnormalDatafr=new FileReader(local_file+fileName);
				 abnormalDatabr=new BufferedReader(abnormalDatafr);

				 while(true)
				 {
				 String line=abnormalDatabr.readLine();
				 if(line.contains("ʱ��"))
				 {
					 
					 //abnormalData_XLabelList�༭
					 if(abnormalXFlag==0)
					 {
						 abnormalData_XLabelList.add(getStringDate(line).substring(4));
					 }
					 else
						 {
						 abnormalData_XLabelList.add(getStringTime(line).substring(8));
						 }
					 
					 abnormalXFlag++;
					 if(abnormalXFlag==abnormalDateDisplayPeriod)
					 {
						 abnormalXFlag=0;
					 }
					 
					 
					 //abnormalData_XList�༭
					 if(abnormalData_XList.size()==0)
					 {
						 abnormalData_XList.add("1");
						 }
					 
					 
					 if(abnormalData_TimeList.size()!=0)
					 {
						//��ÿһ��Ĳ������߹��ൽһ�𣬲���ͬһ�������֮����10
						 if(getStringTime(line).substring(4,8).equals(abnormalData_TimeList.get(abnormalData_TimeList.size()-1).substring(5, 9)))
						 {

							double temp=Double.parseDouble(abnormalData_XList.get(abnormalData_XList.size()-1))+1;
							 abnormalData_XList.add(temp+"");	
						 }
						 
						 else{
							 double temp=Double.parseDouble(abnormalData_XList.get(abnormalData_XList.size()-1))+DateInterval;
							 abnormalData_XList.add(temp+"");
							 abnormalData_XLabelList.set(abnormalData_XLabelList.size()-1, getStringTime(line).substring(4));//���µ����ڵ����ݵ�ʱ�����ǩ����������ʾ
							 
							 abnormalDataRenderer.addTextLabel(Double.parseDouble(abnormalData_XList.get(abnormalData_XList.size()-1)), abnormalData_XLabelList.get(abnormalData_XLabelList.size()-1));  
						 } 
					 }
					 
					 
					 abnormalData_TimeList.add(getStringNum(line));
				 }
				 
			
				 
				 if(line.contains("�쳣ԭ��"))
				 {
//					 System.out.println(line); 
//					 System.out.println(getStringNum(line));
					 
					 abnormalData_ReasonList.add(line);
					 System.out.println(line); 
				 }
				 
				 
				 if(line.contains("����Ƶ��"))
				 {
//					 System.out.println(line); 
//					 System.out.println(getStringNum(line));
					 
					 abnormalData_FrequencyList.add(getStringNum(line));
				 }
				 
				 
				 if(line.contains("����ǿ��"))
				 {
//					 System.out.println(line); 
//					 System.out.println(getStringNum(line));
					 
					 abnormalData_IntensityList.add(getStringNum(line));
				 }
				 
				
				 
//				 if(line.contains("����/����"))
//				 {
//					 System.out.println(line); 
//					 System.out.println(getStringNum(line));
//					 
//					 abnormalData_RatioList.add(getStringNum(line));
//					 
//				 }
//				 
				 
				 
				 if(line==null)
				   break;
				// System.out.println(line);

				 }
				 }

				 catch(Exception e)
				 {
				 e.printStackTrace();
				 }


				 finally{
				 try{
				 abnormalDatabr.close();
				 abnormalDatafr.close();
				 }
				 catch(Exception e)
				 {
				 System.out.println(e);
				 }
				 }
		 }
		 }
	}
	
	
	
	
//	class HaleData {
//		 private int time;
//		 private double haleFrequency;
//		 private double haleIntensity;
//		 private double haleRatio;
//		 
//		 
//		 public HaleData(int time,double haleFrequency,double haleIntensity){
//		        this.time = time;
//		        this.haleFrequency=haleFrequency;
//		        this.haleIntensity=haleIntensity;
//		   }
//		 
//		   public HaleData(int time,double haleFrequency,double haleIntensity,double haleRatio){
//		        this.time = time;
//		        this.haleFrequency=haleFrequency;
//		        this.haleIntensity=haleIntensity;
//		        this.haleRatio=haleRatio;
//		   }
//		 
//		 public int getTime() {
//		  return time;
//		 }
//		 
//		 public double getHaleFrequency() {
//		  return haleFrequency;
//		 }
//		 
//		 public double getHaleIntensity() {
//			  return haleIntensity;
//			 }
//		 public double getHaleRatio() {
//			  return haleRatio;
//			 }
//		 
//		}
	
	String getStringNum(String string) {
		String str = string.trim();
		String str2="";
		if(str != null && !"".equals(str)){
		for(int i=0;i<str.length();i++){
		if((str.charAt(i)>=48 && str.charAt(i)<=57)||(str.charAt(i)=='.')){
		str2+=str.charAt(i);
		}
		}

		}
		return str2;
		}

		 
	String getStringTime(String string) {
		String str = string.trim();
		String str2="";
		if(str != null && !"".equals(str)){
		for(int i=0;i<str.length();i++){
		if((str.charAt(i)>=48 && str.charAt(i)<=57)||(str.charAt(i)==':')){
		str2+=str.charAt(i);
		}
		}

		}
		return str2;
		}

	String getStringDate(String string) {
		String str = string.trim();
		String str2="";
		if(str != null && !"".equals(str)){
		for(int i=0;i<str.length();i++){
		if((str.charAt(i)>=48 && str.charAt(i)<=57)||(str.charAt(i)==':')||(str.charAt(i)=='-')){
		str2+=str.charAt(i);
		}
		}

		}
		return str2;
		}
}
