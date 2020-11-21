package plugin.android.ss.com.breatheexercisehelper;



import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SetActivity extends Activity {
	
	private Button setButton;
	private String signalPositionStr,minHaleIntensityStr,maxHaleFrequencyStr,stantardPositionStr,stantardFrequencyStr,stantardIntensityStr,stantardRatioStr,displayPeriodStr,sampleTimeStr,magnifyNStr;
	private EditText signalPositionEdit,maxHaleFrequencyEdit,minHaleIntensityEdit,stantardPositionEdit,stantardFrequencyEdit,stantardIntensityEdit,stantardRatioEdit,displayPeriodEdit,sampleTimeEdit,magnifyNEdit;
    private double signalPosition,minHaleIntensity,maxHaleFrequency,stantardFrequency,stantardRatio,stantardPosition,stantardIntensity,displayPeriod,sampleOneOf,magnifyN;
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	//	 setContentView(R.layout.activity_set);
		setContentView(R.layout.set_activity);
		 
//		 loadSet();
		
		setButton=(Button) findViewById(R.id.setButton);
		stantardPositionEdit=(EditText) findViewById(R.id.stantardPositionData);
		stantardFrequencyEdit=(EditText) findViewById(R.id.stantardFrequencyData);
		stantardRatioEdit=(EditText) findViewById(R.id.stantardRatioData);
		stantardIntensityEdit=(EditText) findViewById(R.id.stantardIntensityData);
		displayPeriodEdit=(EditText) findViewById(R.id.displayPeriodData);
		sampleTimeEdit=(EditText) findViewById(R.id.sampleTimeData);
		maxHaleFrequencyEdit=(EditText) findViewById(R.id.maxHaleFrequencyData);
		minHaleIntensityEdit=(EditText) findViewById(R.id.minHaleIntensityData);
		magnifyNEdit=(EditText) findViewById(R.id.magnifyNData);
		signalPositionEdit=(EditText) findViewById(R.id.signalPositionData);
		
		Intent setIntentReceive = getIntent();
		Bundle stantardInfoSet = setIntentReceive.getExtras();
		stantardFrequency=stantardInfoSet.getDouble("stantardFrequency");
         stantardRatio=stantardInfoSet.getDouble("stantardRatio");
		 stantardPosition=stantardInfoSet.getDouble("stantardPosition");
	     stantardIntensity=stantardInfoSet.getDouble("stantardIntensity");
	     sampleOneOf=stantardInfoSet.getDouble("sampleOneOf");
	     maxHaleFrequency=stantardInfoSet.getDouble("maxHaleFrequency");
	     minHaleIntensity=stantardInfoSet.getDouble("minHaleIntensity");
	     magnifyN=stantardInfoSet.getDouble("magnifyN");
	     signalPosition=stantardInfoSet.getDouble("signalPosition");
	     displayPeriod=stantardInfoSet.getDouble("displayPeriod");
	     
//	     System.out.println(stantardIntensity+"");
//	     System.out.println(stantardRatio+"");
//	     System.out.println(stantardPosition+"");
//	     
//	     System.out.println(displayPeriod+"displayPeriod");
		
		stantardPositionEdit.setText(stantardPosition+"");
		stantardFrequencyEdit.setText(stantardFrequency+"");
		stantardRatioEdit.setText(stantardRatio+"");
		stantardIntensityEdit.setText(stantardIntensity+"");
		displayPeriodEdit.setText(displayPeriod+"");
	
		sampleTimeEdit.setText(sampleOneOf+"");
		maxHaleFrequencyEdit.setText(maxHaleFrequency+"");
		minHaleIntensityEdit.setText(minHaleIntensity+"");
		magnifyNEdit.setText(magnifyN+"");
		signalPositionEdit.setText(signalPosition+"");
		
		setButton.setOnClickListener(new ClickSetButton());//�����������������MyButton������
	}

	 class ClickSetButton implements OnClickListener{

			@Override
			public void onClick(View v) {
				
//				saveSet();
				
				// TODO Auto-generated method stub
				stantardPositionStr=stantardPositionEdit.getText().toString();
				stantardRatioStr=stantardRatioEdit.getText().toString();
				stantardFrequencyStr=stantardFrequencyEdit.getText().toString();
				stantardIntensityStr=stantardIntensityEdit.getText().toString();
				displayPeriodStr=displayPeriodEdit.getText().toString();
				sampleTimeStr=sampleTimeEdit.getText().toString();
				maxHaleFrequencyStr=maxHaleFrequencyEdit.getText().toString();
				minHaleIntensityStr=minHaleIntensityEdit.getText().toString();
				magnifyNStr=magnifyNEdit.getText().toString();
				signalPositionStr=signalPositionEdit.getText().toString();
				
				
				stantardPosition=Double.parseDouble(stantardPositionStr);
				stantardRatio=Double.parseDouble(stantardRatioStr);
				stantardFrequency=Double.parseDouble(stantardFrequencyStr);
				stantardIntensity=Double.parseDouble(stantardIntensityStr);
				displayPeriod=Double.parseDouble(displayPeriodStr);
				sampleOneOf=Double.parseDouble(sampleTimeStr);
				maxHaleFrequency=Double.parseDouble(maxHaleFrequencyStr);
				minHaleIntensity=Double.parseDouble(minHaleIntensityStr);
				magnifyN=Double.parseDouble(magnifyNStr);
				signalPosition=Double.parseDouble(signalPositionStr);
				
				
				Intent setIntentSend=new Intent(SetActivity.this,MainActivity.class);
				Bundle sendBundle=new Bundle();
				sendBundle.putDouble("stantardPosition", stantardPosition);
				sendBundle.putDouble("stantardRatio", stantardRatio);
				sendBundle.putDouble("stantardFrequency", stantardFrequency);
				sendBundle.putDouble("stantardIntensity", stantardIntensity);
				sendBundle.putDouble("displayPeriod", displayPeriod);
				sendBundle.putDouble("sampleOneOf", sampleOneOf);
				sendBundle.putDouble("maxHaleFrequency", maxHaleFrequency);
				sendBundle.putDouble("minHaleIntensity", minHaleIntensity);
				sendBundle.putDouble("magnifyN", magnifyN);
				sendBundle.putDouble("signalPosition", signalPosition);
				
				setIntentSend.putExtras(sendBundle);
				SetActivity.this.setResult(RESULT_OK, setIntentSend);
				finish();
				

			}
	 }
	 
//	 void loadSet(){
//		 SharedPreferences settings = getSharedPreferences("setting", 0);
//		 
//		 maxHaleFrequency=Double.parseDouble(settings.getString("maxHaleFrequency", "40"));
//	  minHaleIntensity=Double.parseDouble(settings.getString("minHaleIntensity", "5"));
//		
//	 }  
//	 
//	 void saveSet(){
//		 SharedPreferences settings = getSharedPreferences("setting", 0);
//		 SharedPreferences.Editor editor = settings.edit();
//		 editor.putString("maxHaleFrequency",maxHaleFrequency+"");
//		 editor.putString("minHaleIntensity",minHaleIntensity+"");
//		 editor.commit();
//	 }
	 
}
