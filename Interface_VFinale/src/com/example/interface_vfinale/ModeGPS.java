package com.example.interface_vfinale;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ModeGPS extends Activity implements View.OnClickListener {

	TextView  textViewLatitude, textViewLongitude, m_Cap, m_Distance, textViewOrient;
	 EditText m_latitudeDest , m_longitudeDest;
	 Button mDebut, mMot;
	 public Robot m_Robot;
	 public  Button Connect=null;
	 private Thread mThreadEtat =null;
	 
	 
	 private double m_currentTime=0;
	 double m_latitudeOrigine = 0;
	 double m_longitudeOrigine = 0;
	 double m_latDestDbl = 0;
	 double m_longDestDbl =0;
	 public static final double pi = 3.14159265359;
	 double dCap = 0;
	 
	 boolean bStopThread = false;
	 
	 
	 char cPos = 'N', cSaR = 'N';
	 char cCap = 'N';
	 int iCpt = 0;
	 
	 float x = 0; 
	 float y = 0; 
	 float z = 0;
	 
	  boolean bEtat=false;
	  boolean bCalc = false; 
	 boolean bLaunch = false;
	 private SensorManager mSensorManager;
	 private Sensor mMagnetic;
		
	 private enum Etat {GAUCHE, DROITE, AVANCE};
	 private Etat State = Etat.AVANCE, StateOld = null;
	
	 //private Button mDebut;
		
	 float[] magneticVector=new float[3];
	   
	 LocationManager myLocationManager;
	 String PROVIDER = LocationManager.GPS_PROVIDER;
	   
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.activity_mode_gps);
	  textViewLatitude 	= (TextView)findViewById(R.id.latitudeactuelle);
	  textViewLongitude = (TextView)findViewById(R.id.longitudeactuelle);
	  textViewOrient	= (TextView)findViewById(R.id.orient);
	  m_latitudeDest	= (EditText)findViewById (R.id.TxtLat);
	  m_longitudeDest 	= (EditText)findViewById (R.id.TxtLong);
	  mDebut    		= (Button)	findViewById(R.id.btDebut);

	  mMot				=  (Button)	findViewById(R.id.btMot);
	  mMot.setBackgroundColor(Color.RED);
	  
	  m_Robot = new Robot(this);
	  

	  
	  
	   
	  
	  
	  mSensorManager 	= (SensorManager)getSystemService(MainActivity1.SENSOR_SERVICE); 
	  
	  mMagnetic 		= mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

	  mSensorManager.registerListener(mSensorListener, mMagnetic,SensorManager.SENSOR_DELAY_NORMAL); 
	 
	  myLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	  Location location = myLocationManager.getLastKnownLocation(PROVIDER); 
	
	  
	  //get last known location, if available
	  
	  
	  /* 
	   * A mettre dans un Handler
	   * textViewOrient.setText( String.valueOf( fTronc) );
	  m_Cap  .setText( String.valueOf(Cap));*/
	  showMyLocation(location);
	  
	  mThreadEtat = new Thread(new Runnable() { //création du thread de réception	
			@Override
			public void run()
			{
				
				
				while(!bStopThread)
				{
					bCalc =	CalculCap( m_latitudeOrigine,m_longitudeOrigine, m_latDestDbl,  m_longDestDbl);
					cPos =  Position(x, y , z, dCap); 
					
					MachineEtat();
					ActionEtat();
				
					Log.i("H", Character.toString(cPos))	;
					try {
						Thread.sleep(100, 0);
					} 
					catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						//Log.i("IT", "mstrRecu");
					}
				}
			}
	  });
	
	 }

	 
	 
	 public void MachineEtat(){
		 

				switch(State){
				

					case GAUCHE:
							
						if((true == VerificationCap(x,y,z,dCap))||(System.currentTimeMillis()> m_currentTime + 2000)){
							State = Etat.AVANCE;
						}
						
					break;
						
					case DROITE:
					
						if((true == VerificationCap(x,y,z,dCap))||(System.currentTimeMillis()> m_currentTime + 2000)){
							State = Etat.AVANCE;
						}
					
					break;
					
					case AVANCE:
						
						if((false == VerificationCap(x,y,z,dCap))&&(System.currentTimeMillis()> m_currentTime + 500)){
							
							if(cPos == 'G'){
								State = Etat.GAUCHE;
							}
							else if(cPos == 'D'){
								State = Etat.DROITE;
							}
						}
						
					break;
				
				}						 
	 }
	 
	 
	 
	 public void ActionEtat(){    //1

		if(State != StateOld){
			
			m_currentTime = System.currentTimeMillis();
			switch(State){
			
				
				
				case GAUCHE:
						
					m_Robot.moteurVitesseD(9);
					m_Robot.moteurVitesseG(0);
					Log.i("H","GAUCHE")	;
					
				break;
					
				case DROITE:
					
					m_Robot.moteurVitesseG(9);
					m_Robot.moteurVitesseD(0);
					Log.i("H","DROITE")	;
					
				break;
				
				
				case AVANCE:
					
					m_Robot.moteurVitesse(8);
					Log.i("H","AVANCE")	;
					
					
					
				break;
			
			}
			
			
			
			StateOld = State;
		
		 }
			
	}
	 public void onClick(View v) {
		 
		 // un bouton pour mettre en route / stopper le robot
		 // un bouton pour valider les coordonnées (si on veut les changer en cours de route)
		 
			switch(v.getId()){
			
				case R.id.btDebut : 
					
					m_Robot.moteurOn();
					m_Robot.moteurAvant();
					m_latDestDbl = Double.parseDouble(m_latitudeDest.getText().toString());
					m_longDestDbl = Double.parseDouble(m_longitudeDest.getText().toString());
					bStopThread = false;
					mThreadEtat.start();
					
				break;
					
				case R.id.btConnect : 
					m_Robot.connexion();

					
					
				break;
				
				
				case R.id.btMot :
					bStopThread = true;
					m_Robot.moteurVitesse(0);
				break;
			}
			
	}	
		
		
		private final SensorEventListener mSensorListener = new SensorEventListener() { //6

	     public void onSensorChanged(SensorEvent se) 
	     { 
	       
	     	 x = se.values[0]; 
	         y = se.values[1]; 
	         z = se.values[2]; 
	       
	     }

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {} 

		};
		
		
	public char Position(float iX, float iY, float iZ, double Cap){ 
		
			char cResult = 'N';
			
			double angleNordCap, angleSudOppCap;
			float fTronc;
			
	 	double angle= Math.atan2(-iX-3, iY+3)/Math.PI*180;
	 	
	 	if (angle < 180){
	 		
		 	angleNordCap = 360 - Cap;
		 	angleSudOppCap = 180 - angleNordCap;
	 	
	 	}
	 	
	 	else{
	 		
	 		angleSudOppCap= 180 + Cap;
	 		
	 	}
	 	
	 	
	 	if (angle<0){
	 		
	 		angle+=360;
	 		
	 	}
	 	
	 	if( (Cap < angle+180) && (Cap< 180)){
	 		
	 			cResult ='G' ;  //GAUCHE
	 			
	 	}
	 	
	 	else if ((Cap > angle+180 )&& (Cap< 180)){
	 			cResult='D';  //DROITE
	 		
	 	}
	 	
	 	if ((Cap > 180 )&& ((angle<angleSudOppCap)|| ((angle >Cap )&& (angle< 360)))){
	 		
	 			cResult ='G' ;
	 			
	 	}else if ((Cap>180) && ((angle <Cap)&& (angle> angleSudOppCap))){
	 			cResult='D';
	 	}
	 	
	 	fTronc =(int)angle*1000;
	 	fTronc = fTronc/1000;
	 	
	 	 
	 	
	 	return cResult;
	 }

	public boolean VerificationCap (float iX, float iY, float iZ, double Cap){    //8
			
		
			boolean bResult = false;
			
			
		 	double angle=Math.atan2(-iX-3, iY+3)/Math.PI*180;
		 	
		 	if(angle <0){
		 		
		 		angle+=360;
		 	}
		 	
		 	if ((angle>(Cap-10)) && (angle < (Cap +10))){
		 		
		 		bResult =true;
		 	}
		 	
		 	else {
		 		
		 		bResult = false;
		 		
		 	}
		 	
		 	
		 	return bResult;
		 
		}

	  
																											//8
	 protected boolean CalculCap(double latOrigine,double longOrigine, double latDest,double longDest){     
		 //DŽclaration des variables
		 double RadConv = 180/pi;
		
		 double Distance = 0;
		 double pole = 0;
		 double a = 0;
		 double b = 0;
		 double c = 0;
		 double COScap = 0;
		 double Ac = 0;
		 float fCapTronc = 0, fDistTronc = 0;
		if (latDest != 0 && longDest != 0 ){ 
		 //Conversion postion en radian
		 latOrigine = latOrigine/RadConv;
		 longOrigine = longOrigine/RadConv;
		 latDest = latDest/RadConv;
		 longDest = longDest/RadConv;
		 
		 //Calcul distance
		 Distance = RadConv *(1000/9)*Math.acos(Math.sin(latOrigine)*Math.sin(latDest)+(Math.cos(latOrigine)*Math.cos(latDest)*Math.cos(longOrigine-longDest)));
		 
		 //Calcul cap
		 pole = pi/2;
		 a = pole - latDest;
		 b = pole - latOrigine;
		 c = Math.acos(Math.sin(latOrigine)*Math.sin(latDest)+(Math.cos(latOrigine)*Math.cos(latDest)*Math.cos(longOrigine-longDest)));
		 
		 COScap = (Math.cos(a)-Math.cos(b)*Math.cos(c))/(Math.sin(b)*Math.sin(c));
		 Ac = Math.acos(COScap);
		 dCap = Ac*RadConv;
		 if(longOrigine > longDest){
			 dCap = 360-dCap;
		 }
		 
		 fCapTronc = (int) dCap*1000;
		 fCapTronc/= 1000;
		 
		 fDistTronc = (int) Distance *1000;
		 fDistTronc /=10000; 
		 

		} 
		 return true;
	 }
	 
	 																				//9
	 public void onCalcul (){
		 
		
		 CalculCap(m_latitudeOrigine,m_longitudeOrigine,m_latDestDbl,m_longDestDbl);
		 
	 }
	 																				//10
	 @Override
	 protected void onPause() {														
	 
	  super.onPause();
	  myLocationManager.removeUpdates(myLocationListener);
	 }
	  
	 @Override
	 protected void onResume() {													//11

	  super.onResume();
	  myLocationManager.requestLocationUpdates(
	    PROVIDER,     
	    0,       
	    0,      
	    myLocationListener); 
	 }
	  
	 private void showMyLocation(Location l){	//12
		 
		 float fLatTronc = 0, fLongTronc = 0;
		 
		 
		 if(l == null){
			 Toast.makeText(this, "impossible" , Toast.LENGTH_SHORT).show();
		 }
		 else{
			 m_latitudeOrigine = l.getLatitude();
			 m_longitudeOrigine=l.getLongitude();
			 
			 fLatTronc = (int)l.getLatitude()*1000;
			 fLongTronc = (int)l.getLongitude() *1000;
			 
			 fLatTronc /= 1000;
			 fLongTronc /= 1000;
			 textViewLatitude.setText(String.valueOf(fLatTronc));
			 textViewLongitude.setText(String.valueOf(fLongTronc));
			 
			 
			 m_longitudeOrigine = l.getLongitude();
			 m_latitudeOrigine = l.getLatitude();
		 }
	    
	 }
	 	
			
	 private LocationListener myLocationListener = new LocationListener(){
	  
		 @Override
		 public void onLocationChanged(Location location) {
			 showMyLocation(location);
		 }
	  
		 @Override
		 public void onProviderDisabled(String provider) {
			 
	     
		 }
	  
		 @Override
		 public void onProviderEnabled(String provider) {
			 
	     
		 }
	  
		 @Override
		 public void onStatusChanged(String provider, int status, Bundle extras) {
			 
	     
		 }
		 
	 };
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mode_g, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		m_Robot.destructor();
		
		super.onDestroy();
	}
}
