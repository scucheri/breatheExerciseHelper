package plugin.android.ss.com.breatheexercisehelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.app.AlertDialog;


/**
 * 蓝牙操作界面Activity
 */
public class BTHOperationActivity  extends ListActivity
{
	// 控件
	private TextView textViewBTHSwitch = null;
	private ProgressBar progressBarBTHSwitch = null;
	private ToggleButton toggleBTHSwitch = null;
	private TextView textViewBTHScan = null;
	private ProgressBar progressBarBTHScan = null;
	private Button buttonBTHScan = null;
	
	// 蓝牙适配器 
	private BluetoothAdapter bluetoothAdapter = null;
	
	// 设备列表 
	private List<BluetoothDevice> deviceList = null;
	
	// 当前选中的蓝牙设备
	private BluetoothDevice deviceSelected = null;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_bth);
		
		// 获取控件
		textViewBTHSwitch = (TextView)findViewById(R.id.textviewBTHSwitch);
		progressBarBTHSwitch = (ProgressBar)findViewById(R.id.progressBarBTHSwitch);
		toggleBTHSwitch = (ToggleButton)findViewById(R.id.toggleButtonBTHSwitch);
		textViewBTHScan = (TextView)findViewById(R.id.textviewBTHScan);
		progressBarBTHScan = (ProgressBar)findViewById(R.id.progressBarBTHScan);
		buttonBTHScan = (Button)findViewById(R.id.buttonBTHScan);
		
		deviceList = new ArrayList<BluetoothDevice>();

		// 获取蓝牙适配器
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (null == bluetoothAdapter)
		{
			Toast.makeText(this, R.string.bth_adapter_notfound, Toast.LENGTH_SHORT).show();
			return;
		}
		
		// 设置控件
		if (bluetoothAdapter.isEnabled())
		{
			toggleBTHSwitch.setChecked(true);
			textViewBTHSwitch.setText(R.string.bth_turned_on);
			progressBarBTHSwitch.setVisibility(View.INVISIBLE);

			buttonBTHScan.setBackgroundResource(R.drawable.ic_cancel);
			buttonBTHScan.setEnabled(true);
			textViewBTHScan.setText(R.string.bth_is_scanning);
			progressBarBTHScan.setVisibility(View.VISIBLE);
			// 自动开始扫描
			if (!bluetoothAdapter.isDiscovering())
			{
				bluetoothAdapter.startDiscovery();
			}
		}
		else
		{
			toggleBTHSwitch.setChecked(false);
			textViewBTHSwitch.setText(R.string.bth_turned_off);
			buttonBTHScan.setBackgroundResource(R.drawable.ic_refresh_disable);
			buttonBTHScan.setEnabled(false);
			textViewBTHScan.setText("");
			progressBarBTHScan.setVisibility(View.INVISIBLE);
			
			bluetoothAdapter.enable(); // 若蓝牙关闭，则自动开启
			textViewBTHSwitch.setText(R.string.bth_turning_on);
			progressBarBTHSwitch.setVisibility(View.VISIBLE);
		}
		
		setListener(); // 设置监听器
		registerBroadcastReceiver(); // 注册广播接收器
	}

	/**
	 * 设置监听器
	 */
	private void setListener()
	{
		toggleBTHSwitch.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				BTHOperationActivity.this.toggleBTHSwitch.toggle(); // 这里先将toggle按钮状态设回原先的，由蓝牙广播根据蓝牙开关状态来设置toggle按钮状态
				
				if (bluetoothAdapter.isEnabled())
				{
					if (bluetoothAdapter.isDiscovering())
					{
						bluetoothAdapter.cancelDiscovery();
					}
					buttonBTHScan.setBackgroundResource(R.drawable.ic_refresh_disable);
					buttonBTHScan.setEnabled(false);
					textViewBTHScan.setText("");
					bluetoothAdapter.disable();
					deviceList.clear();
					showDeviceList();
				}
				else
				{
					bluetoothAdapter.enable();
				}
				return;
			}
		});
		
		buttonBTHScan.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				if (bluetoothAdapter.isDiscovering())
				{
					bluetoothAdapter.cancelDiscovery();
					buttonBTHScan.setBackgroundResource(R.drawable.ic_refresh);
					textViewBTHScan.setText("");
				}
				else
				{
					deviceList.clear();
					showDeviceList();
					bluetoothAdapter.startDiscovery();
				}
				return;
			}
		});
	}
	
	/**
	 * 注册各类接收器
	 */
	private void registerBroadcastReceiver()
	{
		// 本地蓝牙适配器状态改变
		registerReceiver(new BroadcastReceiver()
		{
			public void onReceive(Context context, Intent intent)
			{
				switch (bluetoothAdapter.getState())
				{
				case BluetoothAdapter.STATE_OFF:
					toggleBTHSwitch.setChecked(false);
					textViewBTHSwitch.setText(R.string.bth_turned_off);
					progressBarBTHSwitch.setVisibility(View.INVISIBLE);
					buttonBTHScan.setBackgroundResource(R.drawable.ic_refresh_disable);
					buttonBTHScan.setEnabled(false);
					textViewBTHScan.setText("");
					break;
				case BluetoothAdapter.STATE_ON:
					toggleBTHSwitch.setChecked(true);
					textViewBTHSwitch.setText(R.string.bth_turned_on);
					progressBarBTHSwitch.setVisibility(View.INVISIBLE);
					buttonBTHScan.setBackgroundResource(R.drawable.ic_refresh);
					buttonBTHScan.setEnabled(true);
					// 自动开始扫描
					if (!bluetoothAdapter.isDiscovering())
					{
						bluetoothAdapter.startDiscovery();
					}
					break;
				case BluetoothAdapter.STATE_TURNING_OFF:
					textViewBTHSwitch.setText(R.string.bth_turning_off);
					progressBarBTHSwitch.setVisibility(View.VISIBLE);
					break;
				case BluetoothAdapter.STATE_TURNING_ON:
					textViewBTHSwitch.setText(R.string.bth_turning_on);
					progressBarBTHSwitch.setVisibility(View.VISIBLE);
					break;
				}
			}
		}, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
		
		// 设备扫描开始
		registerReceiver(new BroadcastReceiver()
		{
			public void onReceive(Context context, Intent intent)
			{
				buttonBTHScan.setBackgroundResource(R.drawable.ic_cancel);
				textViewBTHScan.setText(R.string.bth_is_scanning);
				progressBarBTHScan.setVisibility(View.VISIBLE);
			}
		}, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
		
		// 设备扫描完成
		registerReceiver(new BroadcastReceiver()
		{
			public void onReceive(Context context, Intent intent)
			{
				buttonBTHScan.setBackgroundResource(R.drawable.ic_refresh);
				textViewBTHScan.setText(R.string.bth_found);
				progressBarBTHScan.setVisibility(View.INVISIBLE);
			}
		}, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
		
		// 发现设备
		registerReceiver(new BroadcastReceiver()
		{
			public void onReceive(Context context, Intent intent)
			{
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				deviceList.add(device); // 将新发现的设备添加到设备列表中
				showDeviceList(); // 更新设备列表显示
			}
		}, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		
		// 配对请求
		registerReceiver(new BroadcastReceiver()
		{
			public void onReceive(Context context, Intent intent)
			{
			}
		}, new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST"));
		
		// 配对状态改变
		registerReceiver(new BroadcastReceiver()
		{
			public void onReceive(Context context, Intent intent)
			{
				showDeviceList();
			}
		}, new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED"));
	}
	
	/**
	 * 响应list选择
	 */
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		deviceSelected = deviceList.get(position);

		// 根据配对状态加载不同的菜单项
		int bondmenuitem = 0;
		switch (deviceSelected.getBondState())
		{
		case BluetoothDevice.BOND_NONE:
			bondmenuitem = R.array.bond_none_menu;
			break;
		case BluetoothDevice.BOND_BONDING:
			bondmenuitem = R.array.bond_bonding_menu;
			break;
		case BluetoothDevice.BOND_BONDED:
			bondmenuitem = R.array.bond_bonded_menu;
			break;
		}

		// 设置弹出式对话框，进行配对操作
		AlertDialog.Builder menubuilder = new AlertDialog.Builder(this).setTitle(deviceSelected.getName() + getString(R.string.bond_operation)).setItems(bondmenuitem, new DialogInterface.OnClickListener()
        {
        	public void onClick(DialogInterface dialog, int which)
        	{
        		BTHOperationActivity.this.bondMenuItemSelect(which); // 直接调用SearchBTHActivity的BondMenuItemSelect
        	}
        });
        
        Dialog menudialog = menubuilder.create();
        menudialog.setCanceledOnTouchOutside(true);
        menudialog.show();
	}
	
	/**
	 * 根据选择的菜单进行配对操作
	 * 
	 * @param itemid 项ID
	 */
	private void bondMenuItemSelect(int itemid)
	{
		try
		{
			switch (deviceSelected.getBondState())
			{
			case BluetoothDevice.BOND_NONE:
				if (itemid == 0)
				{
					BluetoothDevice.class.getDeclaredMethod("setPin", new Class[]{byte[].class}).invoke(deviceSelected, new Object[]{"1234".getBytes()});
					BluetoothDevice.class.getMethod("createBond").invoke(deviceSelected);
					BluetoothDevice.class.getMethod("cancelPairingUserInput").invoke(deviceSelected);
				}
				break;
			case BluetoothDevice.BOND_BONDING:
				if (itemid == 0)
				{
					(BluetoothDevice.class.getMethod("cancelBondProcess")).invoke(deviceSelected);
				}
				break;
			case BluetoothDevice.BOND_BONDED:
				if (itemid == 0)
				{
					(BluetoothDevice.class.getMethod("removeBond")).invoke(deviceSelected);
				}
				else if (itemid == 1)
				{
					bluetoothAdapter.cancelDiscovery();
					connectDevice(deviceSelected);
				}
				break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 连接蓝牙设备
	 * 
	 * @param device 蓝牙设备
	 */
	private void connectDevice(BluetoothDevice device)
	{
		Intent result = new Intent();
		result.putExtra("BluetoothDevice", device);
		setResult(RESULT_OK, result);
		finish();
	}
	
	/**
	 * 显示设备列表
	 */
	private void showDeviceList()
	{
		new Handler().post(new Runnable()
		{
			public void run()
			{
				int [] bondstatestringid = {R.string.bth_bond_none, R.string.bth_bond_bonding, R.string.bth_bond_bonded}; // 配对状态字符串资源
				
				List<Map<String, Object>> devicelist = new ArrayList<Map<String, Object>>();;
				Map<String, Object> map = null;
				BluetoothDevice d = null;
				int bondstate = 0;
				
				for (int i = 0, size = BTHOperationActivity.this.deviceList.size(); i < size; i++)
				{
					d = BTHOperationActivity.this.deviceList.get(i);
					bondstate = d.getBondState() - BluetoothDevice.BOND_NONE;
					
					map = new HashMap<String, Object>();
		        	map.put("BTHDeviceImageID", (bondstate == 2)?R.drawable.ic_bth_bond_bonded:R.drawable.ic_bth_bond_none);
		        	map.put("BTHDeviceName", d.getName());
		        	map.put("BTHDeviceMac", d.getAddress());
		        	map.put("BTHDeviceBondState", getString(bondstatestringid[bondstate]));
		        	
		        	devicelist.add(map);
				}
				
				BTHOperationActivity.this.setListAdapter(new SimpleAdapter(BTHOperationActivity.this, devicelist, R.layout.listitem_bthdevice, 
						new String[]{"BTHDeviceImageID", "BTHDeviceName", "BTHDeviceMac", "BTHDeviceBondState"}, new int[]{R.id.imageViewBTHBondState, R.id.textViewBTHName, R.id.textViewBTHMac, R.id.textViewBTHBondState}));
			}
		});
	}
}