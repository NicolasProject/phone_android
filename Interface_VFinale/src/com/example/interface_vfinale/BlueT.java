package com.example.interface_vfinale;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.*;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;



public class BlueT {
	public static final int DEMANDE_AUTH_ACT_BT = 1;
	public static final int N_DEMANDE_AUTH_ACT_BT = 0;
	private static final String TAG = "BTT";
	
	private BluetoothAdapter mbtAdapt; //adapt BT du tel
	private Activity mActivity; //activité principale
	private boolean mbtActif = false;	//etat activation BT
	
	private Set<BluetoothDevice> mDevices; //liste des mDevices
	private BluetoothDevice[]mPairedDevices;//tableau des periph connus
	
	private int mDeviceSelected = -1; //periph choisi
	private String[] mstrDeviceName;
	private int miBlc = 0;				//variable utile à la connection
	//private int miTaille = 0;
	private boolean mbtConnected = false;
	
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");  // voir profil spp
	private BluetoothSocket mSocket;
	private OutputStream mOutStream;
	private InputStream mInStream;
	
	//int miDeviceDelected = 0;

	// utilise pour la reception -----------------------
	public Handler mHandler;
	private Thread mThreadReception =null;	//variable de réception
	private String mstrData;
	private String m_strTramComplete;
	
	private boolean firstConnection;
	
	public String m_strRecu;
	
	int miDeviceDelected = 0;
	byte mbBuffer[] = new byte[200]; // on prévoit grand
	int iPos=0;
	// -------------------------------------------------
	
	public BlueT(Activity Activity, Handler Handler)
	{
		this.mActivity = Activity;
		this.mHandler = Handler;
		m_strTramComplete = new String("");
		m_strRecu = new String("");
		this.Verif();
		firstConnection = false;
		
		mThreadReception = new Thread(new Runnable() { //création du thread de réception	
			@Override
			public void run()
			{
				int iPosCar;
				boolean carNotFound = true;
				
				// TODO Auto-generated method stub
				while(true)
				{
					//Log.i(TAG, "etat="+mbtActif);
					if(mbtAdapt != null)
					{   //Log.i(TAG, "etat="+mbtAdapt);
						if(mbtAdapt.isEnabled())
						{
							mbtActif = true;
							//Log.i(TAG, "etat="+mbtActif);
						}
						else
						{
							mbtActif = false;
							//Log.i(TAG, "etat="+mbtActif);
						}
					}
					
					if(mbtConnected == true) // Recupère les données que si on est connecté
					{
						// on lit une trame --------------------------------------------------------
						carNotFound = true;
						
						while(carNotFound)
						{							
							if ((iPosCar = m_strRecu.indexOf('\0')) != -1)
							{
								m_strTramComplete = m_strRecu.substring(0, iPosCar);
								m_strRecu = m_strRecu.substring(iPosCar + 1);
								carNotFound = false;
						    }
							else
								m_strRecu += reception();
						}
						// ------------------------------------------------------------------------
						
						if (!m_strTramComplete.equals("")) { // envoi message si chaine non vide
							Message msg = mHandler.obtainMessage();
							msg.obj = m_strTramComplete;
							mHandler.sendMessage(msg);
							//Log.i("Recu", mstrRecu);
						}
						//else
							//Log.i("mstrRecu", "vide");
					}
					try {
						Thread.sleep(20, 0);
					} 
					catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						//Log.i("IT", "mstrRecu");
					}
				}
			}
		});
		
        mThreadReception.start(); //lancement thread
	}
	
	public void Verif() // vérification de la présence du BT
	{
		mbtAdapt = BluetoothAdapter.getDefaultAdapter(); //récupération des info du BT sur le device
		if(mbtAdapt == null) {
			Log.i(TAG, "pas present");
		}
		else {
			Log.i(TAG, "present");
		}
	}
	
	public void connexion() // connexion au device
	{
		firstConnection = true; // on doit selectionner le device avant de pouvoir appeler tryconnect() dans les autres methodes (ecrire, lecture)
		this.Device_Connu(); //récupération des mDevices connus
		AlertDialog.Builder adBuilder = new AlertDialog.Builder(mActivity);//fenêtre "pop up" des périph connus
		adBuilder.setTitle("device");
		miDeviceDelected = mDeviceSelected;
		adBuilder.setSingleChoiceItems(mstrDeviceName, 0, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
					mDeviceSelected = which;
					dialog.dismiss();
					tryconnect(); //essai de connection avec le device cible
			}
		});
		
		AlertDialog adb = adBuilder.create();
		adb.show();
	}
	
	public void Device_Connu() // récuperation des mDevices déjà connus par le tel
	{
		this.mDevices = mbtAdapt.getBondedDevices(); //Récupération de tous les mDevices connus dans un tableau set
		this.miBlc = mDevices.size(); // Récupération du nb de mDevices connus
		this.mstrDeviceName = new String[this.miBlc]; //tableau pour l'affichage des mDevices dans le pop up de connexion
		this.miBlc = 0;
		for(BluetoothDevice dev : this.mDevices)
		{
			this.mstrDeviceName[this.miBlc] = dev.getName();
			this.miBlc = this.miBlc + 1;
		}
		this.mPairedDevices = (BluetoothDevice[]) this.mDevices.toArray(new BluetoothDevice[this.mDevices.size()]); //cast du set en array.
	}
	
	public void tryconnect()
	{
		this.mbtConnected = false;
		
		if (firstConnection)
		{
			try
			{
				this.mSocket =this.mPairedDevices[this.mDeviceSelected].createRfcommSocketToServiceRecord(MY_UUID); //connexion avec le device select grace au mSocket, mUUID: id du BT sur device de la cible
				this.mSocket.connect();
				Toast.makeText(this.mActivity, "Connecté", Toast.LENGTH_SHORT).show();
				this.mbtConnected = true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				Toast.makeText(this.mActivity, "Non connecté", Toast.LENGTH_SHORT).show();
				try
				{
					mSocket.close();
				}
				catch(Exception e2)
				{
					e2.printStackTrace();
				}
			}
		}
		else
		{
			Toast.makeText(this.mActivity, "Choisir le peripherique a connecter", Toast.LENGTH_SHORT).show();
		}
	}
	
	public boolean isConnected()
	{
		return mbtConnected;
	}
	
	public boolean envoi(String strOrdre) // false -> erreur; true -> ok
	{
		boolean bRet = false;
		int iCptCrash = 0;
		
		do
		{
			try
			{
				this.mOutStream = this.mSocket.getOutputStream(); //ouverture du flux de sortie
				
				strOrdre += '\0';
				byte[] trame = strOrdre.getBytes();
				
				this.mOutStream.write(trame); //envoi de la trame via le flux de sortie
				this.mOutStream.flush();
				Log.i(TAG, "envoi");
				bRet = true;
			}
			catch(Exception e2)
			{
				Log.i(TAG, "deco");
				tryconnect();
				iCptCrash++;
				/*try
				{
					this.mSocket.close();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//this.mbtConnected = false;*/
			}
		}while(mbtConnected && iCptCrash < 5 && bRet == false);
		
		return bRet;
	}
	
	private String reception()
	{
		boolean success = false;
		int iNbLu;
		mstrData = new String("");
		int iCptCrash = 0;
		
		do
		{
			try
			{
				this.mInStream = this.mSocket.getInputStream();// mstrRecupération flux entrée
				
				if(this.mInStream.available() > 0 )
				{
					// On lit les données, iNbLu représente le nombre de bytes lu
					iNbLu=mInStream.read(mbBuffer,iPos,20); // attention on ne reçoit pas forcément une trame complète
					this.mstrData = new String(mbBuffer,0,iNbLu); //création d'une chaine de caractères grace au bytes mstrRecus
				}
				success = true;
			}
			catch (Exception e)
			{
				Log.i(TAG, "deco");
				tryconnect();
				iCptCrash++;
				/*Log.i(TAG, "erreur");
				try {
					mSocket.close();
				} 
				catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//this.mbtConnected = false;*/
			}
		}while(mbtConnected && iCptCrash < 5 && success == false);
		
		return mstrData;
	}
}



