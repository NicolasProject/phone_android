package com.example.interface_vfinale;



import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


	public class MainActivity1 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity1);
       
        
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
    public void OnClickManu(View view){
    	
    	Intent myIntentManu = new Intent(MainActivity1.this,ModeManu.class); // ceci crée l'objet activité basé sur la classe MainActivity2
		startActivityForResult(myIntentManu,50);// attribue un n° à l'intent
    	
    }
    public void OnClickAuto(View view){
    	
    	Intent myIntentAuto = new Intent(MainActivity1.this,ModeAuto.class); // ceci crée l'objet activité basé sur la classe MainActivity2
		startActivityForResult(myIntentAuto,51);// attribue un n° à l'intent
    	
    }
 	public void OnClickGPS(View view){
 		
 		Intent myIntentGPS = new Intent(MainActivity1.this,ModeGPS.class); // ceci crée l'objet activité basé sur la classe MainActivity2
		startActivityForResult(myIntentGPS,52);// attribue un n° à l'intent
    	
 	
 	}
 	
 	@Override
	   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	       super.onActivityResult(requestCode, resultCode, data);

	     
		   Toast.makeText(getApplicationContext(), "Retour Menu", Toast.LENGTH_LONG).show();
		    
	   }
}
