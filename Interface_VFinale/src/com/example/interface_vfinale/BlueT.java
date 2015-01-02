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
	
	BluetoothAdapter mbtAdapt; //adapt BT du tel
	Activity mActivity; //activité principale
	boolean mbtActif = false;	//etat activation BT
	
	private Set<BluetoothDevice> mDevices; //liste des mDevices
	private BluetoothDevice[]mPairedDevices;//tableau des periph connus
	
	int mDeviceSelected = -1; //periph choisi
	String[] mstrDeviceName;
	int miBlc = 0;				//variable utile à la connection
	int miTaille = 0;
	boolean mbtConnected = false;
	
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
		try {
			this.mSocket =this.mPairedDevices[this.mDeviceSelected].createRfcommSocketToServiceRecord(MY_UUID); //connexion avec le device select grace au mSocket, mUUID: id du BT sur device de la cible
			this.mSocket.connect();
			Toast.makeText(this.mActivity, "ConnectŽ Morray", Toast.LENGTH_SHORT).show();
			this.mbtConnected = true;
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this.mActivity, "Essayer encore Boulay", Toast.LENGTH_SHORT).show();
			try {
				mSocket.close();
			}
			catch(Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	public Boolean envoi(String strOrdre) // false -> erreur; true -> ok
	{
		Boolean bRet = false;
		
		try	{
			this.mOutStream = this.mSocket.getOutputStream(); //ouverture du flux de sortie
			
			strOrdre += '\0';
			byte[] trame = strOrdre.getBytes();
			
			this.mOutStream.write(trame); //envoi de la trame via le flux de sortie
			this.mOutStream.flush();
			Log.i(TAG, "envoi");
		}
		catch(Exception e2) {
			Log.i(TAG, "deco");
			tryconnect();
			try {
				this.mSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.mbtConnected = false;
		}		
		return bRet;
	}
	
	private String reception()
	{
		int iNbLu;
		mstrData = new String("");
		try
		{
			this.mInStream = this.mSocket.getInputStream();// mstrRecupération flux entrée
			
			if(this.mInStream.available() > 0 )
			{
				// On lit les données, iNbLu représente le nombre de bytes lu
				iNbLu=mInStream.read(mbBuffer,iPos,20); // attention on ne reçoit pas forcément une trame complète
				this.mstrData = new String(mbBuffer,0,iNbLu); //création d'une chaine de caractères grace au bytes mstrRecus
			}
		}
		catch (Exception e)
		{
			Log.i(TAG, "erreur");
			try {
				mSocket.close();
			} 
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			this.mbtConnected = false;
		}
		return mstrData;
	}
}



