package com.maxtech.common;

import com.maxtech.common.gps.GpsTask;
import com.maxtech.common.gps.GpsTaskCallBack;
import com.maxtech.common.gps.GpsTask.GpsData;
import com.maxtech.common.gps.IAddressTask.MLocation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class GpsActivity extends Activity implements OnClickListener {

	private TextView gps_tip = null;
	private AlertDialog dialog = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		gps_tip = (TextView) findViewById(R.id.gps_tip);
		findViewById(R.id.do_gps).setOnClickListener(GpsActivity.this);
		findViewById(R.id.do_apn).setOnClickListener(GpsActivity.this);
		findViewById(R.id.do_wifi).setOnClickListener(GpsActivity.this);

		dialog = new ProgressDialog(GpsActivity.this);
		dialog.setTitle("���Ե�...");
		dialog.setMessage("���ڶ�λ...");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		gps_tip.setText("");
		switch (v.getId()) {
		case R.id.do_apn:
			do_apn();
			break;
		case R.id.do_gps:
			GpsTask gpstask = new GpsTask(GpsActivity.this,
					new GpsTaskCallBack() {

						@Override
						public void gpsConnectedTimeOut() {
							gps_tip.setText("��ȡGPS��ʱ��");
						}

						@Override
						public void gpsConnected(GpsData gpsdata) {
							do_gps(gpsdata);
						}

					}, 30 * 1000);
			gpstask.execute();
			break;
		case R.id.do_wifi:
			do_wifi();
			break;
		}
	}

	private void do_apn() {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				MLocation location = null;
				try {
					location = new AddressTask(GpsActivity.this,
							AddressTask.DO_APN).doApnPost();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(location == null)
					return null;
				return location.toString();
			}

			@Override
			protected void onPreExecute() {
				dialog.show();
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(String result) {
				if(result == null){
					gps_tip.setText("��վ��λʧ����...");					
				}else {
					gps_tip.setText(result);
				}
				dialog.dismiss();
				super.onPostExecute(result);
			}
			
		}.execute();
	}

	private void do_gps(final GpsData gpsdata) {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				MLocation location = null;
				try {
					Log.i("do_gpspost", "��γ�ȣ�" + gpsdata.getLatitude() + "----" + gpsdata.getLongitude());
					location = new AddressTask(GpsActivity.this,
							AddressTask.DO_GPS).doGpsPost(gpsdata.getLatitude(),
									gpsdata.getLongitude());
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(location == null)
					return "GPS��Ϣ��ȡ����";
				return location.toString();
			}

			@Override
			protected void onPreExecute() {
				dialog.show();
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(String result) {
				gps_tip.setText(result);
				dialog.dismiss();
				super.onPostExecute(result);
			}
			
		}.execute();
	}

	private void do_wifi() {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				MLocation location = null;
				try {
					location = new AddressTask(GpsActivity.this,
							AddressTask.DO_WIFI).doWifiPost();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(location == null)
					return null;
				return location.toString();
			}

			@Override
			protected void onPreExecute() {
				dialog.show();
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(String result) {
				if(result != null){
					gps_tip.setText(result);
				}else {
					gps_tip.setText("WIFI��λʧ����...");
				}
				
				dialog.dismiss();
				super.onPostExecute(result);
			}
			
		}.execute();
	}

}