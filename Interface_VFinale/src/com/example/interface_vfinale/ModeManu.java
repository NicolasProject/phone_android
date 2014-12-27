package com.example.interface_vfinale;

import com.example.interface_vfinale.BlueT;





import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class ModeManu extends Activity implements View.OnClickListener {

	
	int iMotDroit=0;
	int iMotGauche=0;
	boolean bAvGauche;
	boolean bAvDroit;
	public  Button Connect=null;
	public BlueT mBluetooth;
	private Thread mThreadEnvoi = null;
	public 	int miCpt=0;
	public String mstrTrame = "";
	
	

   /** Called when the activity is first created. */ 
   @Override 
   public void onCreate(Bundle savedInstanceState)
   { 
	   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
       super.onCreate(savedInstanceState); 
       setContentView(R.layout.activity_mode_manu); 
       
       
       SeekBar Barre1 = (SeekBar)findViewById(R.id.seekBar1);
       SeekBar Barre2 = (SeekBar)findViewById(R.id.seekBar2);
       final TextView seekBarValue1 = (TextView)findViewById(R.id.Affarr);
       final TextView seekBarValue2 = (TextView)findViewById(R.id.textView2);
       seekBarValue1.setText(String.valueOf(iMotGauche)); 
       seekBarValue2.setText(String.valueOf(iMotDroit));
       
       
       this.Connect = (Button) findViewById(R.id.button1);
       this.Connect.setOnClickListener((OnClickListener) this);
       mBluetooth= new BlueT(this);
       this.mThreadEnvoi = new Thread(new Runnable()
       {
       @Override
   			public void run()
   			{
   				// TODO Auto-generated method stub
   				while(true)
   				{
   					if(mBluetooth.mbtConnected == true)
   					{
   						//mstrTrame = mDeplacement.Deplacement1(iGauche, iDroit);
   						mstrTrame= Integer.toString(miCpt)+"\0";
   						//Log.i("Tx trame", mstrTrame);			
   						mBluetooth.envoi(mstrTrame);
   						miCpt++;
   					}
   					try
   					{
   						Thread.sleep(20, 0);
   					} 
   					catch (InterruptedException e)
   					{
   						// TODO Auto-generated catch block
   						e.printStackTrace();
   					}
   				}
   			}
       });
   		mThreadEnvoi.start();
       
     
       Barre1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
       {    	   
		   @Override 
		   public void onProgressChanged(SeekBar seekBar, int progress1, 
		     boolean fromUser) 
		   { 
		    // TODO Auto-generated method stub 
			iMotGauche = progress1 - 10;
			seekBarValue1.setText(String.valueOf(iMotGauche)); 		    
		    if(iMotGauche<0)
		    {
		    	iMotGauche= -iMotGauche;
		    	bAvGauche = false;
		    }
		    else  if(iMotGauche<0)
		    {
			    	bAvGauche = true;
		    }
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
		   public void onProgressChanged(SeekBar seekBar, int progress2, 
		     boolean fromUser) 
		   { 
		    // TODO Auto-generated method stub		  
		   iMotDroit = progress2 - 10;
		   seekBarValue2.setText(String.valueOf(iMotDroit)); 
		    
		    if(iMotDroit<0)
		    {
		    	iMotDroit= -iMotDroit;
		    	bAvDroit = false;
		    }
		    else  if(iMotDroit<0)
		    {		    	
			    	bAvDroit = true;			    
		    }
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
		    //Bouton On/Off Bluetooth	
		    /*case R.id.toggleButton1:
		    	if(((CompoundButton) OnOff).isChecked()){	//Bouton activer ==> Bluetooth ON	    		
		    		mBluetooth.Verif();
		    		mBluetooth.Activation(BlueT.DEMANDE_AUTH_ACT_BT);
		    	}
		    	else{
		    		mBluetooth.desactivation();				//Bouton désactiver ==> Bluetooth OFF
		    	}
		    break;
		    */
		    //Bouton Connexion Bluetooth
		    case R.id.button1:
		    	this.mBluetooth.connexion();
		    	
		    break;
		    
		    
		 }
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//mBluetooth.desactivation(); //désactivation complete du BT
	}
}
