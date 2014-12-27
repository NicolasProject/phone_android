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

	public  Button Connect=null;
	public BlueT mBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_auto);
        
        m_affCapt1 = (TextView) findViewById(R.id.Affarr);
        m_affCapt2 = (TextView) findViewById(R.id.Affavd);
        m_affCapt3 = (TextView) findViewById(R.id.Affavg);
        m_affUltra = (TextView) findViewById(R.id.Affultras);
        //mImgView = (ImageView) findViewById(R.id.Robotimg);
        
        this.Connect = (Button) findViewById(R.id.button1);
	    this.Connect.setOnClickListener(this);
	    mBluetooth= new BlueT(this);

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
    
    private TextView m_affCapt1;
    private TextView m_affCapt2;
    private TextView m_affCapt3;
    private TextView m_affUltra;


    @Override
    public void onClick(View v) {
    	// On récupère l'identifiant de la vue, et en fonction de cet identifiant…
    	switch(v.getId()) 
    	{	    
					
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
