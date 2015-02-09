package com.example.interface_vfinale;

import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony.Sms.Conversations;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ModeServerCo extends Activity implements View.OnClickListener {
	
	public Handler m_handler = new Handler() { //refresh aff ou/et recup des données bt pour utilisation
		public void handleMessage(Message msg)
		{
			String myString=(String) msg.obj;
			
			Toast.makeText(getApplicationContext(), myString, Toast.LENGTH_SHORT).show();
		}
	};
	
	private Button Connect = null;
	private Robot m_robot = null;
	
	private boolean m_robotOn = false;
	private boolean m_sensAvantG = true;
	private boolean m_sensAvantD = true;

	// private Thread m_Thread = null;

	private Socket m_socketIO = null;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mode_server);

		m_robot = new Robot(this);		
		
//		try {
//			SocketIO.setDefaultSSLSocketFactory(SSLContext.getDefault());
//		} catch (NoSuchAlgorithmException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		try {
//			m_socketIO = new SocketIO("http://nico-serverrpi.ddns.net:8080");
//			//m_socketIO = new SocketIO("http://192.168.1.10:8080");
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		m_socketIO.connect(new IOCallback() {
//
//			@Override
//			public void onMessage(JSONObject json, IOAcknowledge ack) {
//				// TODO Auto-generated method stub
//				try {
//					Message msg = m_handler.obtainMessage();
//					msg.obj = "Server said:" + json.toString(2);
//					m_handler.sendMessage(msg);
//					
//					/*Toast.makeText(getApplicationContext(),
//							"Server said:" + json.toString(2),
//							Toast.LENGTH_SHORT).show();*/
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//
//			@Override
//			public void onMessage(String data, IOAcknowledge ack) {
//				// TODO Auto-generated method stub
//				Message msg = m_handler.obtainMessage();
//				msg.obj = "Server said: " + data;
//				m_handler.sendMessage(msg);
//				
//				/*Toast.makeText(getApplicationContext(), "Server said: " + data,
//						Toast.LENGTH_SHORT).show();*/
//			}
//
//			@Override
//			public void onError(SocketIOException socketIOException) {
//				// TODO Auto-generated method stub
//				Message msg = m_handler.obtainMessage();
//				msg.obj = "An Error occured";
//				m_handler.sendMessage(msg);
//				
//				/*Toast.makeText(getApplicationContext(), "An Error occured",
//						Toast.LENGTH_SHORT).show();*/
//				socketIOException.printStackTrace();
//			}
//
//			@Override
//			public void onDisconnect() {
//				// TODO Auto-generated method stub
//				Message msg = m_handler.obtainMessage();
//				msg.obj = "Connection terminated";
//				m_handler.sendMessage(msg);
//				
//				/*Toast.makeText(getApplicationContext(), "Connection terminated",
//						Toast.LENGTH_SHORT).show();*/
//			}
//
//			@Override
//			public void onConnect() {
//				// TODO Auto-generated method stub
//				Message msg = m_handler.obtainMessage();
//				msg.obj = "Connection established";
//				m_handler.sendMessage(msg);
//				
//				/*Toast.makeText(getApplicationContext(), "Connection established",
//						Toast.LENGTH_SHORT).show();*/
//				m_socketIO.emit("message", "hello ! je suis connecté ! ;-)");
//			}
//
//			@Override
//			public void on(String event, IOAcknowledge ack, Object... args) {
//				// TODO Auto-generated method stub
//					if(event.equals("message"))
//					{
//						if (args.length >= 1)
//						{
//							Message msg = m_handler.obtainMessage();
//							msg.obj = "on '" + event + "' : " + args[0];
//							m_handler.sendMessage(msg);
//							
//							/*Toast.makeText(getApplicationContext(), "on '" + event + "' : " + args[0],
//								Toast.LENGTH_SHORT).show();*/
//						}
//					}
//			}
//		});
		
		try {
			m_socketIO = IO.socket("http://nico-serverrpi.ddns.net:8080");
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		m_socketIO.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

		  @Override
		  public void call(Object... args) {
			  m_socketIO.emit("connection_roboter3");
			  //m_socketIO.disconnect();
			  
			  Message msg = m_handler.obtainMessage();
			  msg.obj = "Server said:connect";
			  m_handler.sendMessage(msg);
		  }

		 })
		 .on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

			  @Override
			  public void call(Object... args) {
				  //m_socketIO.emit("disconnect_roboter3");
				  Message msg = m_handler.obtainMessage();
				  msg.obj = "Server said:disconnect !";
				  m_handler.sendMessage(msg);
			  }

		 })
		 .on("android connected", new Emitter.Listener() {

			   @Override
			   public void call(Object... args) {				   
				   Message msg = m_handler.obtainMessage();
				   msg.obj = "phone connected";
				   m_handler.sendMessage(msg);
			   }
	 
		 })
		 .on("vitesseG", new Emitter.Listener() {

		   @Override
		   public void call(Object... args) {
			   JSONObject obj = (JSONObject)args[0];
			   
			   Message msg = m_handler.obtainMessage();
			   try {
				msg.obj = "vitesseG = " + obj.getString("content");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			   m_handler.sendMessage(msg);
			   
			   if (m_robot.isConnected())
			   {
				   int iVitesse = 0;
				   
				   if (!m_robotOn)
				   {
					   m_robot.moteurOn();
					   m_robotOn = true;
				   }
				   
				   try {
					   iVitesse = Integer.parseInt(obj.getString("content"));
				   } catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				   
				   if (iVitesse < 0)
				   {
					   if (m_sensAvantG)
					   {
						   m_robot.moteurAvantG(false);
						   m_sensAvantG = false;
					   }
					   m_robot.moteurVitesseG(-iVitesse);
				   }
				   else
				   {
					   if (!m_sensAvantG)
					   {
						   m_robot.moteurAvantG();
						   m_sensAvantG = true;
					   }
					   m_robot.moteurVitesseG(iVitesse);
				   }
			   }
		   }
 
		 })
		 .on("vitesseD", new Emitter.Listener() {

		   @Override
		   public void call(Object... args) {
			   JSONObject obj = (JSONObject)args[0];
			   
			   Message msg = m_handler.obtainMessage();
			   try {
				msg.obj = "vitesseD = " + obj.getString("content");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			   m_handler.sendMessage(msg);
			   
			   if (m_robot.isConnected())
			   {
				   int iVitesse = 0;
				   
				   if (!m_robotOn)
				   {
					   m_robot.moteurOn();
					   m_robotOn = true;
				   }
				   
				   try {
					   iVitesse = Integer.parseInt(obj.getString("content"));
				   } catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				   
				   if (iVitesse < 0)
				   {
					   if (m_sensAvantD)
					   {
						   m_robot.moteurAvantD(false);
						   m_sensAvantD = false;
					   }
					   m_robot.moteurVitesseD(-iVitesse);
				   }
				   else
				   {
					   if (!m_sensAvantD)
					   {
						   m_robot.moteurAvantD();
						   m_sensAvantD = true;
					   }
					   m_robot.moteurVitesseD(iVitesse);
				   }
			   }
		   }
 
		 })
		 .on("message", new Emitter.Listener() {

			   @Override
			   public void call(Object... args) {
				   JSONObject obj = (JSONObject)args[0];
				   
				   Message msg = m_handler.obtainMessage();
				   try {
					msg.obj = "message : " + obj.getString("content");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				   m_handler.sendMessage(msg);
			   }
	 
		 });
		
		 m_socketIO.connect();
		

		this.Connect = (Button) findViewById(R.id.button1);
		this.Connect.setOnClickListener((OnClickListener) this);

		// m_socketIO = new SocketIO("adresse");

		/*
		 * this.m_Thread = new Thread(new Runnable() {
		 * 
		 * @Override public void run() { // TODO Auto-generated method stub
		 * while(true) { if (m_socketRead != null) { try { if
		 * (m_socketRead.available() > 0) { // il faut lire les données !!!
		 * --------------------------------------------- } } catch (IOException
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); } }
		 * 
		 * try { Thread.sleep(20, 0); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } } } });
		 * m_Thread.start();
		 */
	}

	@Override
	public void onClick(View v) {
		// On récupère l'identifiant de la vue, et en fonction de cet
		// identifiant…
		switch (v.getId()) {
		case R.id.button1:
			this.m_robot.connexion();
			
			break;

		}
	}

	@Override
	protected void onDestroy() {
		/*if (m_socketIO != null)
			m_socketIO.disconnect();*/

		m_robot.destructor();

		super.onDestroy();
	}
}
