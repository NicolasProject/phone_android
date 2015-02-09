package com.example.interface_vfinale;

import com.example.interface_vfinale.BlueT;



import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ModeAuto extends Activity implements View.OnClickListener{

	private Button Connect = null;
	private Button m_ButtonDemarre = null;
	private Robot m_robot;
	
	private boolean bValDroit = false; 
	private boolean bValGauche = false; 
	private boolean bValArriere = false; 
	boolean bAppuiGo = false;

	
	
	private Thread m_ThreadAuto = null;
	
	
	private int iValUS = 0;

	
	public enum Etat{
    	
		ARRET,
		AVANCE,
		DROITE,
		GAUCHE,
		RECULE
    }
	
	
	Etat State;
	Etat StateOld;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_auto);
        
        m_robot = new Robot(this);
      


        
        this.Connect = (Button) findViewById(R.id.button1);
	    this.Connect.setOnClickListener(this);
	    
	    
	    
		 

	    
	    
	    this.m_ButtonDemarre = (Button) findViewById(R.id.buttonDemarre);
	    this.m_ButtonDemarre.setOnClickListener(this);
	    //mBluetooth= new BlueT(this); // -------------------------------- l'appeler correctement
	    
	    State = Etat.ARRET;
	    StateOld = Etat.ARRET;
		
		
		
		
		m_ThreadAuto = new Thread(new Runnable() { //création du thread de réception	
			
			@Override
			public void run()
			{
				
				// TODO Auto-generated method stub
				while(true)
				{
					readSensor();
					
					GestionEtat();
					GestionSortie();
					
					
				}
			}
		});
    }
        

	
    public void GestionEtat(){
    	
    	
    	switch (State){
    	
			case ARRET :
				
				if(bAppuiGo == true){
	    			State = Etat.AVANCE;
	    		}
				
				break;
		
		
			case AVANCE :
			
				if(bValDroit && (!bValGauche)){
					
					State = Etat.GAUCHE;
					
				}
				
				else if((!bValDroit) && bValGauche){
					
					State = Etat.DROITE;
					
				}
				else if(bValDroit && bValGauche){
					
					State = Etat.RECULE;
					
				}
				
				break;
	
				
			case DROITE :
			
				if(!bValGauche){
					State = Etat.AVANCE;
				}
				else if(bValDroit && bValGauche){
					
					State = Etat.RECULE;
					
				}
				
				break;
			
				
			case GAUCHE :
				
				if(!bValDroit){
					State = Etat.AVANCE;
				}
				else if(bValDroit && bValGauche){
					
					State = Etat.RECULE;
					
				}
				break;
			
				
			case RECULE :
				
				if(!bValDroit && !bValGauche){
					State = Etat.AVANCE;
				}
				else if( bValArriere){
					State = Etat.DROITE;
					
				}
			
				break;
			
	}
		
		
	}


    
    
    public void GestionSortie(){
    	
    	if(StateOld != State){
    		
    	
	    	switch (State){
	    		
	    		case ARRET:
	    		
		    		if(m_robot.isConnected()){
		    			
		    			m_robot.moteurVitesse(8);
		        		m_robot.moteurOnD(false);
		    			m_robot.moteurOnG(false);
		    		}
		    	break;
	    	    
	    	
	    	
	    		case AVANCE:

		    		m_robot.moteurVitesse(8);
		    		m_robot.moteurOn();
		    		m_robot.moteurAvant();
		    		
	    		break;
	    		
	    		
	    		
	    		case DROITE:

					m_robot.moteurVitesse(4,4);
					m_robot.moteurAvantG(true);
					m_robot.moteurAvantD(false);
					m_robot.moteurOn();
				break;
			
			
			
	    		case GAUCHE:
				
					m_robot.moteurVitesse(4,4);
					m_robot.moteurAvantG(false);
					m_robot.moteurAvantD(true);
					m_robot.moteurOn();
				break;
				
			
			
	    		case RECULE:
				
					m_robot.moteurVitesse(2,8);
					m_robot.moteurAvantG(false);
					m_robot.moteurAvantD(false);
					m_robot.moteurOn();
				break;
	    	}
	    	
	    	//placer ici un enregistrement de la bdd.
	    	StateOld = State;
	    	
	    	
	        
	    	
    	}
    }

    @Override
    public void onClick(View v){
    	
	    	switch(v.getId()) 
	    	{	    
				
	    		case R.id.button1:
	    			if(m_robot.isConnected()== false){
	    				m_robot.connexion();
	    			}
	    			break;
	    			
	    			
	    	    
	    		case R.id.buttonDemarre:
	    			
	    			if(bAppuiGo){
	    				bAppuiGo = false;
		    			m_robot.destructor();
	    			}
	    			
	    			if(m_robot.isConnected() && bAppuiGo == false){
	    				bAppuiGo = true;
		    			m_robot.recevoirCaptIR();
		    			m_ThreadAuto.start(); //lancement thread
	    			}

	    			break;
	    	}

	}
    
    private void readSensor()
    {
    	bValDroit = m_robot.getCaptIRD();
    	bValGauche = m_robot.getCaptIRG();
    	bValArriere = m_robot.getCaptIRArr();
    	
    	
    }
    
    
    @Override
	protected void onDestroy() {
    	// TODO Auto-generated method stub
    	m_robot.destructor();
    			
    	super.onDestroy();
	}
}
