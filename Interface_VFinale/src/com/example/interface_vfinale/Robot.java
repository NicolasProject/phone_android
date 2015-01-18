package com.example.interface_vfinale;

import java.io.Serializable;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


public class Robot implements Serializable
{
	static public Handler m_handler = new Handler() { //refresh aff ou/et recup des données bt pour utilisation
		public void handleMessage(Message msg)
		{
			String myString=(String) msg.obj;
			//Log.i("TextView", myString);
			//if ( myString !=""){
			   //TextView1.setText(myString);
			   //Log.i("TextView", myString);
			//}
			
			//Toast.makeText(m_activity.getApplicationContext(), myString, Toast.LENGTH_SHORT).show();
			m_com.traiteTrame(myString);
		}
	};
	
	
	public Robot(Activity myActivity)
	{
		m_activity = myActivity;
		m_Bluetooth= new BlueT(m_activity, m_handler);
		m_com = new Communication(this, m_activity);
		
		//this.connexion();
	}
	
	public void connexion()
	{
		if (!isConnected())
		{
			m_Bluetooth.connexion();
		}
		/*else
			Toast.makeText(m_activity.getApplicationContext(), "Déjà connecté !", Toast.LENGTH_SHORT).show();*/
		else // la déconnection ne fonctionne pas tres bien : problemes, exceptions
		{
			m_Bluetooth.destructor();
			Toast.makeText(m_activity.getApplicationContext(), "Déconnecté !", Toast.LENGTH_SHORT).show();
		}
	}
	
	public boolean isConnected()
	{
		
		return m_Bluetooth.isConnected();
	}
	
	
	public void moteurOn ()
	{
		moteurOn (true, true);
	}
	
	public void moteurOn (boolean bGauche, boolean bDroite)
	{
		String trame = m_mo;
		
		if (!(bGauche && bDroite))
		{
			if (bGauche)
			{
				trame += m_sep + m_true;
				trame += m_sep + m_false;
			}
			else if (bDroite)
			{
				trame += m_sep + m_false;
				// le deuxieme param est facultatif
			}
			else
			{
				trame += m_sep + m_false;
				trame += m_sep + m_false;
			}
		}
		
		// on pourrait ajouter un toast pour pimenter le tout, mais pas ici !! (ce serait trop fort)
		// cette classe n'est pas censé le faire
		// de plus il faudrait connaitre l'activité dans laquelle on le fait
		m_Bluetooth.envoi(trame);
	}
	
	public void moteurOnG()
	{
		moteurOnG (true);
	}
	
	public void moteurOnG (boolean bGauche)
	{
		String trame = m_mog;
		
		if (!(bGauche))
			trame += m_sep + m_false;
		
		m_Bluetooth.envoi(trame);
	}
	
	
	public void moteurOnD()
	{
		moteurOnD (true);
	}
	
	public void moteurOnD (boolean bDroit)
	{
		String trame = m_mod;
		
		if (!(bDroit))
			trame += m_sep + m_false;
		
		m_Bluetooth.envoi(trame);
	}
	
	public void moteurVitesse (int iVitesse)
	{
		moteurVitesse (iVitesse, iVitesse);
	}
	
	public void moteurVitesse (int iVitesseG, int iVitesseD)
	{
		String trame = m_mv;
		
		if ((iVitesseG <= m_iVitesseMax) && (iVitesseG >= 0) && (iVitesseD <= m_iVitesseMax) && (iVitesseD >= 0))
		{
			trame += m_sep + String.valueOf(iVitesseG) + m_sep + String.valueOf(iVitesseD);
		}
		else
		{
			 assert false : "speed error - in moteurVitesse(int, int) function";
		}
		
		m_Bluetooth.envoi(trame);
	}
	
	public void moteurVitesseG (int iVitesse)
	{
		String trame = m_mvg;
		
		if ((iVitesse <= m_iVitesseMax) && (iVitesse >= 0))
		{
			trame += m_sep + String.valueOf(iVitesse);
		}
		else
		{
			assert false : "speed error - in moteurVitesseG (int) function";
		}
		
		m_Bluetooth.envoi(trame);
	}
	
	public void moteurVitesseD (int iVitesse)
	{
		String trame = m_mvd;
		
		if ((iVitesse <= m_iVitesseMax) && (iVitesse >= 0))
		{
			trame += m_sep + String.valueOf(iVitesse);
		}
		else
		{
			assert false : "speed error - in moteurVitesseD (int) function";
		}
		
		m_Bluetooth.envoi(trame);
	}
	
	
	public void moteurAvant()
	{
		moteurAvant (true, true);
	}
	
	public void moteurAvant (boolean bGauche, boolean bDroite)
	{
		String trame = m_ma;
		
		if (!(bGauche && bDroite))
		{
			if (bGauche)
			{
				trame += m_sep + m_true;
				trame += m_sep + m_false;
			}
			else if (bDroite)
			{
				trame += m_sep + m_false;
				// le deuxieme param est facultatif
			}
			else
			{
				trame += m_sep + m_false;
				trame += m_sep + m_false;
			}
		}
		
		m_Bluetooth.envoi(trame);
	}
	
	public void moteurAvantG()
	{
		moteurAvantG (true);
	}
	
	public void moteurAvantG (boolean bGauche)
	{
		String trame = m_mag;
		
		if (!(bGauche))
			trame += m_sep + m_false;
		
		m_Bluetooth.envoi(trame);
	}
	
	
	public void moteurAvantD()
	{
		moteurAvantD (true);
	}
	
	public void moteurAvantD (boolean bDroit)
	{
		String trame = m_mad;
		
		if (!(bDroit))
			trame += m_sep + m_false;
		
		m_Bluetooth.envoi(trame);
	}
	
	public void recevoirCaptIR ()
	{
		recevoirCaptIR (true, true, true);
	}
	
	public void recevoirCaptIR (boolean ir1)
	{
		recevoirCaptIR (ir1, true, true);
	}
	
	public void recevoirCaptIR (boolean ir1, boolean ir2)
	{
		recevoirCaptIR (ir1, ir2, true);
	}
	
	public void recevoirCaptIR (boolean ir1, boolean ir2, boolean ir3)
	{
		String trame = m_rir;
		
		if (!(ir1 && ir2 && ir3))
		{
			if (ir1)
				trame += m_sep + m_true;
			else
				trame += m_sep + m_false;
			
			if (!(ir2 && ir3))
			{				
				if (ir2)
				{
					trame += m_sep + m_true;
					trame += m_sep + m_false;
				}
				else if (ir3)
				{
					trame += m_sep + m_false;
					// le troisieme param est facultatif
				}
				else
				{
					trame += m_sep + m_false;
					trame += m_sep + m_false;
				}
			}
		}
		
		m_Bluetooth.envoi(trame);
	}
	
	public void recevoirCaptIRArr()
	{
		recevoirCaptIRArr(true);
	}
	
	public void recevoirCaptIRArr(boolean ir)
	{
		String trame = m_rir1;
		
		if (!ir)
			trame += m_sep + m_false;
		
		m_Bluetooth.envoi(trame);
	}
	
	public void recevoirCaptIRG()
	{
		recevoirCaptIRG(true);
	}
	
	public void recevoirCaptIRG(boolean ir)
	{
		String trame = m_rir2;
		
		if (!ir)
			trame += m_sep + m_false;
		
		m_Bluetooth.envoi(trame);
	}
	
	public void recevoirCaptIRD()
	{
		recevoirCaptIRD(true);
	}
	
	public void recevoirCaptIRD(boolean ir)
	{
		String trame = m_rir3;
		
		if (!ir)
			trame += m_sep + m_false;
		
		m_Bluetooth.envoi(trame);
	}
	
	public void recevoirDistance()
	{
		recevoirDistance(true);
	}
	
	public void recevoirDistance(boolean recevoir)
	{
		String trame = m_rus;
		
		if (!recevoir)
			trame += m_sep + m_false;
		
		m_Bluetooth.envoi(trame);
	}
	
	public void setPosServomoteur(int angle)
	{
		if (angle >= 0 && angle <= 90)
		{
			String trame = m_psm + m_sep + Integer.toString(angle);
			m_Bluetooth.envoi(trame);
		}
		else
			assert false : "erreur d'angle de servomoteur - dans la fonction setPosServomoteur(int)";
	}
	
	
	public boolean getCaptIRArr()
	{
		return m_captIRArr;
	}
	
	public boolean getCaptIRG()
	{
		return m_captIRG;
	}
	
	public boolean getCaptIRD()
	{
		return m_captIRD;
	}
	
	public int getDistance()
	{
		return m_distance;
	}
	
	
	
	public void giveKeyTo(Communication obj)
	{
		obj.receiveKey(new PrivateMethodsFriends());
	}
	
	public class PrivateMethodsFriends
	{
		private PrivateMethodsFriends()
		{}
		
		
		public void setCaptIRArr()
		{
			setCaptIRArr(true);
		}
		
		public void setCaptIRArr(boolean capteur)
		{
			m_captIRArr = capteur;
		}
		
		public void setCaptIRG()
		{
			setCaptIRG(true);
		}
		
		public void setCaptIRG(boolean capteur)
		{
			m_captIRG = capteur;
		}
		
		public void setCaptIRD()
		{
			setCaptIRD(true);
		}
		
		public void setCaptIRD(boolean capteur)
		{
			m_captIRD = capteur;
		}
		
		public void setDistance(int dst)
		{
			if (dst >= -1 && dst < 1000)
				m_distance = dst;
		}
	}
	
	public void destructor()
	{
		m_Bluetooth.destructor();
		//m_Bluetooth= new BlueT(m_activity, m_handler);
	}
	
	
	//declaration des elements de la trame
	
	private final static String m_sep = ","; //virgule de separation dans la trame
	
	private final static String m_mo  = "1";  // "1" moteur on
	private final static String m_mog = "2";  // "2" moteur on gauche
	private final static String m_mod = "3";  // "3" moteur on droit
	
	private final static String m_mv  = "4";
	private final static String m_mvg = "5";
	private final static String m_mvd = "6";
	
	private final static String m_ma  = "7";
	private final static String m_mag = "8";
	private final static String m_mad = "9";
	
	private final static String m_rir = "10";
	private final static String m_rir1 = "11";
	private final static String m_rir2 = "12";
	private final static String m_rir3 = "13";
	
	private final static String m_rus = "14";
	
	private final static String m_psm = "15";
	
	private final static String m_true  = "1";
	private final static String m_false = "0";
	
	//private final static String m_fin = "\0";
	
	private final static int m_iVitesseMax = 10;
	
	
	private boolean m_captIRArr;
	private boolean m_captIRG;
	private boolean m_captIRD;
	
	private int m_distance;
	
	static private Activity m_activity;
	
	
	private BlueT m_Bluetooth;
	static private Communication m_com;
}


