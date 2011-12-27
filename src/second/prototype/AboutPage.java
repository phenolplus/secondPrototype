package second.prototype;

import control.stage.StageManager;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class AboutPage extends Activity {
	/** Members */
	private int code = 0;
	private ImageView plot;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.about);
        plot = (ImageView) findViewById(R.id.foo);
        ContainerBox.master = false;
        
        plot.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					code++;
					Log.e("Code",""+code%5);
				}
				return false;
			}
        	
        });
	}
	
	/** Menu Control 
	 *  These are Programmer tasks ...*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		if((code%5) == 0){
			menu.add(0, 0, 0, "Restore Deault Data").setIcon(android.R.drawable.ic_menu_upload);
			menu.add(0, 1, 0, "Master").setIcon(android.R.drawable.ic_menu_edit);
			Log.e("Code","Foo");
			return true;
		} else {
			return false;
		}
		
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch(item.getItemId()){
		case 0:
			StageManager.initFileSettings(this);
			break;
		case 1:
			ContainerBox.master = true;
			plot.setImageResource(R.drawable.posterwwwp);
			break;
		default :		
		}
		return true;
	}
}
