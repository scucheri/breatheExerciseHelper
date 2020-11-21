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

import plugin.android.ss.com.breatheexercisehelper.ComplexGraph;

public class QueryActivity  extends Activity {
	private static final double DateInterval = 20;//不同天的数据相隔20

	private static final double haleDisplayXLabelInterval = 5;//呼吸周期曲线每5个点显示时间标签
	private static final double haleDateDisplayPeriod=haleDisplayXLabelInterval*5;//每5个显示的时间标签中有一个显示了日期

	private static final double abnormalDisplayXLabelInterval =3;//异常呼吸曲线每10个点显示时间标签
	private static final double abnormalDateDisplayPeriod=abnormalDisplayXLabelInterval*5;//每5个显示的时间标签中有一个显示了日期

	private int haleDisplayCount,abnormalDisplayCount;

	private ComplexGraph haleDataView,abnormalDataView;
	private  XYMultipleSeriesRenderer haleDataRenderer,abnormalDataRenderer;
	private  XYSeriesRenderer haleFrequencyRenderer,haleIntensityRenderer,haleRatioRenderer,abnormalFrequencyRenderer,abnormalIntensityRenderer;
	private XYSeries haleFrequencySeries,haleIntensitySeries,haleRatioSeries,abnormalIntensitySeries,abnormalFrequencySeries;
	private String haleDataFile="呼吸周期数据.txt";
	private String abnormalDataFile="异常呼吸数据.txt";

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
//		setContentView(R.layout.activity_query);
		setContentView(R.layout.query_activity);


		haleDataView=(ComplexGraph)findViewById(R.id.haleDataView);
		abnormalDataView=(ComplexGraph)findViewById(R.id.abnormalDataView);



		//呼吸周期数据图像
		haleDataRenderer = haleDataView.getRenderer();
		haleDataRenderer.setApplyBackgroundColor(true); // 设置是否显示背景色
		haleDataRenderer.setBackgroundColor(Color.BLACK); // 设置背景色
		haleDataRenderer.setMargins(new int[]{10, 40, 40, 10}); // 设置图表的外边框(上/左/下/右)
		haleDataRenderer.setChartTitleTextSize(40);
		haleDataRenderer.setAxisTitleTextSize(30); // 设置轴标题文字的大小
		haleDataRenderer.setLabelsTextSize(20); // 设置刻度显示文字的大小(XY轴都会被设置)
		haleDataRenderer.setLegendTextSize(20); // 图例文字大小
		haleDataRenderer.setPointSize(10); // 设置点的大小(图上显示的点的大小和图例中点的大小都会被设置)
		haleDataRenderer.setShowGrid(true); // 是否显示网格
		haleDataRenderer.setSelectableBuffer(10); // 设置点的缓冲半径值(在某点附件点击时,多大范围内都算点击这个点)
//			haleDataRenderer.setXTitle(MainActivity.this.getString(R.string.potential) + "(" +
//					MainActivity.this.getResources().getStringArray(R.array.potential_unit)[AppGlobal.DEFAULT_POTENTIAL_UNIT] + ")"); // X轴说明
//			haleDataRenderer.setYTitle(MainActivity.this.getString(R.string.current) + "(" +
//					MainActivity.this.getResources().getStringArray(R.array.current_unit)[AppGlobal.DEFAULT_CURRENT_UNIT] + ")"); // Y轴说明
		haleDataRenderer.setLegendHeight(100); // 设置图例文字位置高度
		haleDataRenderer.setAxesColor(Color.argb(255, 250, 250, 250)); // 轴颜色
		haleDataRenderer.setGridColor(Color.argb(150, 200, 200, 200)); // 网格颜色
		haleDataRenderer.setLabelsColor(Color.argb(180, 180, 180, 180)); // 标注颜色
		haleDataRenderer.setXLabels(0); // X轴刻度数量
		haleDataRenderer.setYLabels(10); // Y轴刻度数量
		haleDataRenderer.setXLabelsColor(Color.argb(255, 255, 255, 255)); // X轴标签文字颜色
		haleDataRenderer.setYLabelsColor(0, Color.argb(255, 255, 255, 255)); // Y轴标签文字颜色

		haleDataRenderer.setChartTitle("呼吸周期数据");
		haleDataRenderer.setXTitle("时间");
		haleDataRenderer.setYTitle("频率/强度/呼吸比");



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



		haleIntensitySeries = new XYSeries("呼吸强度");
		haleDataView.addSeriesAndRenderer( haleIntensitySeries, haleIntensityRenderer);

		for(int i=0;i<haleData_IntensityList.size();i++)
		{
			haleIntensitySeries.add(Double.parseDouble(haleData_XList.get(i)), Double.parseDouble(haleData_IntensityList.get(i)));
//				  haleIntensitySeries.add(i, Double.parseDouble(haleData_IntensityList.get(i)));

		}


		haleFrequencySeries = new XYSeries("呼吸频率");
		haleDataView.addSeriesAndRenderer( haleFrequencySeries, haleFrequencyRenderer);

		for(int i=0;i<haleData_FrequencyList.size();i++)
		{
			haleFrequencySeries.add(Double.parseDouble(haleData_XList.get(i)), Double.parseDouble(haleData_FrequencyList.get(i)));
//				  haleFrequencySeries.add(i, Double.parseDouble(haleData_FrequencyList.get(i)));
		}



		haleRatioSeries = new XYSeries("呼气吸气时间比");
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

//			  haleIntensitySeries = new XYSeries("呼吸强度");
//			  haleDataView.addSeriesAndRenderer( haleIntensitySeries, haleIntensityRenderer);
//			  for(int i=0;i<100;i++)
//			  {
//			  haleIntensitySeries.add(i, i-5);
//			  }
//
//
//			  haleRatioSeries = new XYSeries("呼气吸气时间比");
//			  haleDataView.addSeriesAndRenderer( haleRatioSeries, haleRatioRenderer);
//			  for(int i=0;i<100;i++)
//			  {
//			  haleRatioSeries.add(i, i-10);
//			  }

		haleDataView.fitGraph();






		//异常呼吸数据图像
		abnormalDataRenderer = abnormalDataView.getRenderer();
		abnormalDataRenderer.setApplyBackgroundColor(true); // 设置是否显示背景色
		abnormalDataRenderer.setBackgroundColor(Color.BLACK); // 设置背景色
		abnormalDataRenderer.setMargins(new int[]{10, 40, 40, 10}); // 设置图表的外边框(上/左/下/右)
		abnormalDataRenderer.setChartTitleTextSize(40);
		abnormalDataRenderer.setAxisTitleTextSize(30); // 设置轴标题文字的大小
		abnormalDataRenderer.setLabelsTextSize(20); // 设置刻度显示文字的大小(XY轴都会被设置)
		abnormalDataRenderer.setLegendTextSize(20); // 图例文字大小
		abnormalDataRenderer.setPointSize(10); // 设置点的大小(图上显示的点的大小和图例中点的大小都会被设置)
		abnormalDataRenderer.setShowGrid(true); // 是否显示网格
		abnormalDataRenderer.setSelectableBuffer(10); // 设置点的缓冲半径值(在某点附件点击时,多大范围内都算点击这个点)
//				abnormalDataRenderer.setXTitle(MainActivity.this.getString(R.string.potential) + "(" +
//						MainActivity.this.getResources().getStringArray(R.array.potential_unit)[AppGlobal.DEFAULT_POTENTIAL_UNIT] + ")"); // X轴说明
//				abnormalDataRenderer.setYTitle(MainActivity.this.getString(R.string.current) + "(" +
//						MainActivity.this.getResources().getStringArray(R.array.current_unit)[AppGlobal.DEFAULT_CURRENT_UNIT] + ")"); // Y轴说明
		abnormalDataRenderer.setLegendHeight(100); // 设置图例文字位置高度
		abnormalDataRenderer.setAxesColor(Color.argb(255, 250, 250, 250)); // 轴颜色
		abnormalDataRenderer.setGridColor(Color.argb(150, 200, 200, 200)); // 网格颜色
		abnormalDataRenderer.setLabelsColor(Color.argb(180, 180, 180, 180)); // 标注颜色
		abnormalDataRenderer.setXLabels(0); // X轴刻度数量
		abnormalDataRenderer.setYLabels(10); // Y轴刻度数量
		abnormalDataRenderer.setXLabelsColor(Color.argb(255, 255, 255, 255)); // X轴标签文字颜色
		abnormalDataRenderer.setYLabelsColor(0, Color.argb(255, 255, 255, 255)); // Y轴标签文字颜色

		abnormalDataRenderer.setChartTitle("异常呼吸数据");
		abnormalDataRenderer.setXTitle("时间");
		abnormalDataRenderer.setYTitle("频率/强度");


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
			if((abnormalData_ReasonList.get(i-1).contains("无呼吸信号"))&(abnormalData_ReasonList.get(i).contains("无呼吸信号"))&(abnormalData_ReasonList.get(i+1).contains("无呼吸信号")))

//			if(abnormalData_ReasonList.get(i).contains("无呼吸信号"))
			{
				abnormalDataRenderer.addTextLabel(Double.parseDouble(abnormalData_XList.get(i-1)), abnormalData_XLabelList.get(i-1));
				abnormalDataRenderer.addTextLabel(Double.parseDouble(abnormalData_XList.get(i+1)), abnormalData_XLabelList.get(i+1));
				abnormalDataRenderer.removeXTextLabel(Double.parseDouble(abnormalData_XList.get(i)));


				abnormalData_IntensityList.set(i-1, "0");
				abnormalData_IntensityList.set(i+1, "0");
				abnormalData_FrequencyList.set(i-1, "0");
				abnormalData_FrequencyList.set(i+1, "0");//讲无呼吸信号的曲线归零

				abnormalData_ReasonList.remove(i);
				abnormalData_IntensityList.remove(i);
				abnormalData_FrequencyList.remove(i);
				abnormalData_TimeList.remove(i);
				abnormalData_XList.remove(i);
				abnormalData_XLabelList.remove(i);

				i--;
			}
		}


		abnormalIntensitySeries = new XYSeries("呼吸强度");
		abnormalDataView.addSeriesAndRenderer( abnormalIntensitySeries, abnormalIntensityRenderer);

		for(int i=0;i<abnormalData_IntensityList.size();i++)
		{
			abnormalIntensitySeries.add(Double.parseDouble(abnormalData_XList.get(i)), Double.parseDouble(abnormalData_IntensityList.get(i)));
//					  abnormalIntensitySeries.add(i, Double.parseDouble(abnormalData_IntensityList.get(i)));

		}


		abnormalFrequencySeries = new XYSeries("呼吸频率");
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

		String local_file = Environment.getExternalStorageDirectory().getAbsolutePath()+"/呼吸数据/";
		File fDir = new File(local_file);
		if(fDir.exists())
		{
			//如果存在这个文件，则读取出来
			if(fileName==haleDataFile)
			{
				try{
					haleDatafr=new FileReader(local_file+fileName);
					haleDatabr=new BufferedReader(haleDatafr);

					while(true)
					{
						String line=haleDatabr.readLine();
						if(line.contains("时间"))
						{
							//XLabelList编辑
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

							//XList编辑
							//加入第一个元素
							if(haleData_XList.size()==0)
							{
								haleData_XList.add("1");
							}


							if(haleData_TimeList.size()!=0)
							{
								//将每一天的测试曲线归类到一起，不是同一天的曲线之间间隔10
								if(getStringTime(line).substring(4,8).equals(haleData_TimeList.get(haleData_TimeList.size()-1).substring(5, 9)))
								{

									double temp=Double.parseDouble(haleData_XList.get(haleData_XList.size()-1))+1;
									haleData_XList.add(temp+"");
								}

								else{
									double temp=Double.parseDouble(haleData_XList.get(haleData_XList.size()-1))+DateInterval;
									haleData_XList.add(temp+"");
									haleData_XLabelList.set(haleData_XLabelList.size()-1, getStringTime(line).substring(4));//把新的日期的数据的时间轴标签加上日期显示

									haleDataRenderer.addTextLabel(Double.parseDouble(haleData_XList.get(haleData_XList.size()-1)), haleData_XLabelList.get(haleData_XLabelList.size()-1));
								}
							}


							haleData_TimeList.add(getStringNum(line));//在赋值XList之后才赋值TimeList




							//更新XList坐标

							// System.out.println(getStringTime(line).substring(8));

							//Integer.parseInt(getStringNum(line));

							//HaleData haleData=new HaleData();
						}


						if(line.contains("呼吸频率"))
						{
//					 System.out.println(line);
//					 System.out.println(getStringNum(line));

							haleData_FrequencyList.add(getStringNum(line));
						}


						if(line.contains("呼吸强度"))
						{
//					 System.out.println(line);
//					 System.out.println(getStringNum(line));

							haleData_IntensityList.add(getStringNum(line));
						}


						if(line.contains("呼气/吸气"))
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
						if(line.contains("时间"))
						{

							//abnormalData_XLabelList编辑
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


							//abnormalData_XList编辑
							if(abnormalData_XList.size()==0)
							{
								abnormalData_XList.add("1");
							}


							if(abnormalData_TimeList.size()!=0)
							{
								//将每一天的测试曲线归类到一起，不是同一天的曲线之间间隔10
								if(getStringTime(line).substring(4,8).equals(abnormalData_TimeList.get(abnormalData_TimeList.size()-1).substring(5, 9)))
								{

									double temp=Double.parseDouble(abnormalData_XList.get(abnormalData_XList.size()-1))+1;
									abnormalData_XList.add(temp+"");
								}

								else{
									double temp=Double.parseDouble(abnormalData_XList.get(abnormalData_XList.size()-1))+DateInterval;
									abnormalData_XList.add(temp+"");
									abnormalData_XLabelList.set(abnormalData_XLabelList.size()-1, getStringTime(line).substring(4));//把新的日期的数据的时间轴标签加上日期显示

									abnormalDataRenderer.addTextLabel(Double.parseDouble(abnormalData_XList.get(abnormalData_XList.size()-1)), abnormalData_XLabelList.get(abnormalData_XLabelList.size()-1));
								}
							}


							abnormalData_TimeList.add(getStringNum(line));
						}



						if(line.contains("异常原因"))
						{
//					 System.out.println(line);
//					 System.out.println(getStringNum(line));

							abnormalData_ReasonList.add(line);
							System.out.println(line);
						}


						if(line.contains("呼吸频率"))
						{
//					 System.out.println(line);
//					 System.out.println(getStringNum(line));

							abnormalData_FrequencyList.add(getStringNum(line));
						}


						if(line.contains("呼吸强度"))
						{
//					 System.out.println(line);
//					 System.out.println(getStringNum(line));

							abnormalData_IntensityList.add(getStringNum(line));
						}



//				 if(line.contains("呼气/吸气"))
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
