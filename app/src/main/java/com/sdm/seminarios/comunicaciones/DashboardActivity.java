package com.sdm.seminarios.comunicaciones;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;


public class DashboardActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);

		ListView list = (ListView) findViewById(R.id.lvList);

		HashMap<String, Object> item = null;
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();

		item = new HashMap<String, Object>();
		item.put("icon", android.R.drawable.ic_menu_gallery);
		item.put("action", getResources().getString(R.string.exchange_image_action));
		data.add(item);

		item = new HashMap<String, Object>();
		item.put("icon", android.R.drawable.ic_menu_myplaces);
		item.put("action", getResources().getString(R.string.http_action));
		data.add(item);


        item = new HashMap<String, Object>();
        item.put("icon", android.R.drawable.ic_menu_call);
        item.put("action", getResources().getString(R.string.handler_action_conMensaje));
        data.add(item);


        item = new HashMap<String, Object>();
        item.put("icon", android.R.drawable.ic_menu_call);
        item.put("action", getResources().getString(R.string.handler_action_conRunnable));
        data.add(item);

		SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.dashboard_item, new String[]{"icon", "action"}, new int[]{R.id.ivIcon, R.id.tvAction});
		list.setAdapter(adapter);

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				switch(arg2) {
				case 0:
					startActivity(new Intent(getApplicationContext(), SocketActivity.class));
					break;
				case 1:
					startActivity(new Intent(getApplicationContext(), HTTPWeatherActivity.class));
					break;
                case 2:
                    startActivity(new Intent(getApplicationContext(), ProgressBarActivity_handlerconMensaje.class));
                    break;
                    case 3:
                        startActivity(new Intent(getApplicationContext(), ProgressBarActivity_handlerconRunnable.class));
                        break;
                default:
				return;
				}
			}
		});
	}

}
