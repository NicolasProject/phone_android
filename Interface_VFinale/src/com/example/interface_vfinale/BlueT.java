package com.example.interface_vfinale;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.*;
import android.content.DialogInterface;
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
	
	//int miDeviceDelected = 0;

	
	public BlueT(Activity Activity)
	{
		this.mActivity = Activity;
		this.Verif();
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
		//miDeviceDelected = mDeviceSelected;
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
		for(BluetoothDevice dev : this.mDevices) {
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
}



