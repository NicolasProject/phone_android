package com.example.interface_vfinale;


import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
//import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


public class ModeManu extends Activity implements View.OnClickListener
{
	
	private int iMotDroit = 0;
	private int iMotGauche = 0;
	private boolean bAvGauche = true;
	private boolean bAvDroit = true;
	private Button Connect = null;
	private Robot m_robot;
	private boolean m_moteurAvantGOld = true;
	private boolean m_moteurAvantDOld = true;
	private boolean m_moteurOnG = false;
	private boolean m_moteurOnD = false;
	//private BlueT mBluetooth;
	//private Thread mThreadEnvoi = null;
	//private	int miCpt = 0;
	//private String mstrTrame = "";
	
	private SQLiteDatabase m_DB;
	
	

   /** Called when the activity is first created. */ 
   @Override 
   public void onCreate(Bundle savedInstanceState)
   { 
	   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_mode_manu);
       
       //Bundle bd = new Bundle();
       //bd = getIntent().getExtras();
       //m_robot = (Robot) bd.getSerializable("bundleobj");
       
       m_robot = new Robot(this);
       //m_robot = (Robot)intent.getSerializableExtra("robot_obj");
       m_moteurAvantGOld = true;
       m_moteurAvantDOld = true;
       
       
       m_DB = openOrCreateDatabase("moteur", Context.MODE_PRIVATE, null);

       m_DB.execSQL("CREATE TABLE IF NOT EXISTS moteur(Motdroit INT,Motgauche INT,Sensgauche BOOLEAN, Sensdroite BOOLEAN);");
       
       
       SeekBar Barre1 = (SeekBar)findViewById(R.id.seekBar1);
       SeekBar Barre2 = (SeekBar)findViewById(R.id.seekBar2);
       final TextView seekBarValue1 = (TextView)findViewById(R.id.Affarr);
       final TextView seekBarValue2 = (TextView)findViewById(R.id.textView2);
       seekBarValue1.setText(String.valueOf(iMotGauche)); 
       seekBarValue2.setText(String.valueOf(iMotDroit));
       
       
       this.Connect = (Button) findViewById(R.id.button1);
       this.Connect.setOnClickListener((OnClickListener) this);
       
       
     
       Barre1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
       {    	   
    	   @Override
    	   public void onProgressChanged(SeekBar seekBar, int progress1, boolean fromUser) 
		   { 
    		   // TODO Auto-generated method stub
    		   
    		   iMotGauche = progress1 - 10;
    		   seekBarValue1.setText(String.valueOf(iMotGauche));
    		   
    		   if (!m_moteurOnG)
    			   m_robot.moteurOnG();
    		   
    		   if (iMotGauche >= 0)
    		   {
    			   if(!m_moteurAvantGOld)
    			   {
    				   m_robot.moteurAvantG();
    				   m_moteurAvantGOld = true;
    			   }
    			   
    			   m_robot.moteurVitesseG(iMotGauche);
    			   //bAvGauche = false;
    		   }
    		   else
    		   {
    			   if(m_moteurAvantGOld)
    			   {
    				   m_robot.moteurAvantG(false);
    				   m_moteurAvantGOld = false;
    			   }
    			   
    			   m_robot.moteurVitesseG(-iMotGauche);
    			   //bAvGauche = true;
    		   }
    		   
    		   onAdd();
		   } 
		
		   @Override 
		   public void onStartTrackingTouch(SeekBar seekBar) 
		   { 
		    // TODO Auto-generated method stub 
		   } 
		
		   @Override 
		   public void onStopTrackingTouch(SeekBar seekBar) 
		   { 
		    // TODO Auto-generated method stub 
		   } 
       });
       
       Barre2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
       { 

		   @Override 
		   public void onProgressChanged(SeekBar seekBar, int progress2, boolean fromUser) 
		   {
			   // TODO Auto-generated method stub
			   
			   iMotDroit = progress2 - 10;
			   seekBarValue2.setText(String.valueOf(iMotDroit));
			   
			   if (!m_moteurOnD)
    			   m_robot.moteurOnD();
			   
			   if(iMotDroit >= 0)
			   {
				   if(!m_moteurAvantDOld)
    			   {
    				   m_robot.moteurAvantD();
    				   m_moteurAvantDOld = true;
    			   }
    			   
    			   m_robot.moteurVitesseD(iMotDroit);
				   //bAvDroit = false;
			   }
			   else
			   {
				   if(m_moteurAvantDOld)
    			   {
    				   m_robot.moteurAvantD(false);
    				   m_moteurAvantDOld = false;
    			   }
    			   
    			   m_robot.moteurVitesseD(-iMotDroit);
			    	//bAvDroit = true;
			   }
			   
			   onAdd();
		   }
		   
		   @Override 
		   public void onStartTrackingTouch(SeekBar seekBar) 
		   { 
		    // TODO Auto-generated method stub 
		   } 
		
		   @Override 
		   public void onStopTrackingTouch(SeekBar seekBar) 
		   { 
		    // TODO Auto-generated method stub 
		   } 		   
       });
       
   }

   @Override
   public void onClick(View v) 
	{
	  // On récupère l'identifiant de la vue, et en fonction de cet identifiant…
		 switch(v.getId()) 
		 {
		    //Bouton Connexion Bluetooth
		    case R.id.button1:
		    	this.m_robot.connexion();
		    	
		    break;
		    
		 }
	}
   
   
public void onAffiche(View v){
	   
	   onShow();
	   
   }
   
  
   
   public void onAdd(){

        // Inserting record
           m_DB.execSQL("INSERT INTO moteur VALUES('"+iMotDroit+"','"+iMotGauche+"','" + m_moteurAvantDOld + "','" + m_moteurAvantGOld + "');");

        
   }
   
   public void onSuppr(View view){
	   
   	m_DB.execSQL("DROP DATABASE IF EXISTS moteur");
   	
   }
   
   
   public void onShow(){
   	 
       // Retrieving all records
           Cursor c=m_DB.rawQuery("SELECT * FROM moteur", null);
           
       // Checking if no records found
           if(c.getCount()==0)
           {
               showMessage("Erreur", "Rien");
               return;
           }
           
       // Appending records to a string buffer
           StringBuffer buffer=new StringBuffer();
           while(c.moveToNext())
           {
               buffer.append("Valeur Moteur Droit: "+c.getString(0)+"\n");
               buffer.append("Valeur Moteur Gauche : "+c.getString(1)+"\n");
               buffer.append("Sens Du Moteur Droit : "+c.getString(2)+"\n");
               buffer.append("Sens Du Moteur Gauche : "+c.getString(3)+"\n\n");
               
           }
           
       // Displaying all records
           showMessage("Etat Moteur", buffer.toString());
       
   }
   
   public void showMessage(String title,String message)
   {
       Builder builder=new Builder(this);
       builder.setCancelable(true);
       builder.setTitle(title);
       builder.setMessage(message);
       builder.show();
   }
   
   
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		m_robot.destructor();
		
		super.onDestroy();
	}
}
