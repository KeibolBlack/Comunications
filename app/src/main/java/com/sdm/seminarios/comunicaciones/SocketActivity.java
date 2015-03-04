package com.sdm.seminarios.comunicaciones;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class SocketActivity extends Activity {

	private ServerSocket server;
	TextView displayMessage;
	ServerTask task;

	Uri imageUri;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_socket);

		TabHost tabHost = (TabHost) findViewById(R.id.thSocket);
		tabHost.setup();

		TabSpec spec = tabHost.newTabSpec(getResources().getString(R.string.exchange_images_server));
		spec.setIndicator(getResources().getString(R.string.exchange_images_server));
		spec.setContent(R.id.llServerSocket);
		tabHost.addTab(spec);

		spec = tabHost.newTabSpec(getResources().getString(R.string.exchange_images_client));
		spec.setIndicator(getResources().getString(R.string.exchange_images_client));
		spec.setContent(R.id.llClientSocket);
		tabHost.addTab(spec);

        displayMessage = (TextView) findViewById(R.id.tvServerSocketMessages);
	}

	private boolean isConnected() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		return ((info != null) && info.isConnected());
	}

    /** SERVIDOR **/
	
	public void toggleServer(View v) {
		ToggleButton button = (ToggleButton) v;
		if (button.isChecked()) {
			if (isConnected()) {
				task = new ServerTask();
				task.execute();
			}
			else {
				Toast.makeText(this, getResources().getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
				button.setChecked(false);
			}
		}
		else {
			task.cancel(true);
			try {
				if (server.isBound()) {
					server.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

    /** A COMPLETAR **
     *  Posiciona correctamente los tipos que debe utilizar la tarea asíncrona
     *  en base a la definición de sus métodos
     */
	private class ServerTask extends AsyncTask<?, ?, ?> {

		Bitmap bitmap;

        @Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Socket socket;
            try {
				server = new ServerSocket(9999);
				while (!isCancelled()) {
                    /** A COMPLETAR
                     *  El trabajo a realizar consta de 3 pasos.
                     *  Paso 0: activar el hilo principal (código del onProgressUpdate) para que muestre la información relativa al estado del servidor
                     */
					publishProgress(0);
                    socket = server.accept();
                    /** A COMPLETAR
                     *  Paso 1: activar el hilo principal (código del onProgressUpdate) para que notifique al usuario de que se ha recibido una petición
                     *  de conexión y ésta se está tratando
                     */
					publishProgress(1);
                    bitmap = BitmapFactory.decodeStream(socket.getInputStream());
					FileOutputStream fos = openFileOutput("file_received.png", Context.MODE_PRIVATE);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
					fos.flush();
					fos.close();
					socket.close();
                    /** A COMPLETAR
                     *  Paso 2: activar el hilo principal (código del onProgressUpdate) para que muestre la imagen recibida
                     */
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if (!isCancelled()) {
                    /** A COMPLETAR
                     * El paso 3 sólo se activa en caso de error
                     *  Paso 3: activar el hilo principal (código del onProgressUpdate) para que muestre que se ha producido un error
                     *  en la comunicación
                     */
				}
				return null;
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			switch(values[0]) {
			case 0:
                ((TextView) findViewById(R.id.tvServerSocketAddress)).setText(getResources().getString(R.string.exchange_images_address) + getIpAddress() );
                ((TextView) findViewById(R.id.tvServerSocketPort)).setText(getResources().getString(R.string.exchange_images_port) + server.getLocalPort());
                displayMessage.append(getResources().getString(R.string.message_server_on));
				break;
			case 1:
				displayMessage.append(getResources().getString(R.string.message_server_receiving_image));
				break;
			case 2:
				((ImageView) findViewById(R.id.ivServerSocketImage)).setImageBitmap(bitmap);
				break;
			case 3:
				displayMessage.append(getResources().getString(R.string.message_server_error));
				break;
			}
		}


		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			displayMessage.append(getResources().getString(R.string.message_server_off));
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			displayMessage.append(getResources().getString(R.string.message_server_off));
		}

	}

    public String getIpAddress() {
        String ret = null;
        try {
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()&&inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("Socket exception in GetIP Address of Utilities", ex.toString());
        }
        return null;
    }

	public void selectImage(View v) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == Activity.RESULT_OK) {
			imageUri = data.getData();
			ImageView image = (ImageView) findViewById(R.id.ivClientSocketImage);
			try {
				InputStream is = getContentResolver().openInputStream(imageUri);
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				is.close();
				image.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (resultCode == Activity.RESULT_CANCELED) {
		}
	}

    public void sendImage(View v) {
        if (isConnected()) {
            ClientTask client = new ClientTask();
            client.execute();
        }
        else {
            Toast.makeText(this, getResources().getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
        }
    }


	private class ClientTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				Socket socket = new Socket();
				socket.connect(
						new InetSocketAddress(
								InetAddress.getByName(((EditText) findViewById(R.id.etClientSocketAddress)).getText().toString()),
								Integer.parseInt(((EditText) findViewById(R.id.etClientSocketPort)).getText().toString())
								)
						);

                ImageView image = (ImageView) findViewById(R.id.ivClientSocketImage);
                Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] imageInByte = stream.toByteArray();
                OutputStream writer = socket.getOutputStream();
                writer.write(stream.toByteArray());

                /* OutputStream writer = socket.getOutputStream();
                InputStream reader = getContentResolver().openInputStream(imageUri);
				byte[] buffer = new byte[1024];
				while (reader.read(buffer) != -1) {
					writer.write(buffer);
				}
				reader.close();
				*/
				writer.flush();
				writer.close();
				socket.close();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "Image successfully sent", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "Sending image...", Toast.LENGTH_SHORT).show();
		}

	}

}
