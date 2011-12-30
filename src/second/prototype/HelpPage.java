package second.prototype;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class HelpPage extends Activity {
	
	/** Called when the activity is first created. */
	private ImageView image;
	private int state;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.help);
        
        state = 1;
        image = (ImageView) findViewById(R.id.imageHelp);
        
        image.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event){
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					float h = image.getHeight();
			        float w = image.getWidth();
					float x = event.getX();
					float y = event.getY();
					
					if(((w-x)<200)&&(y<100)){
						// next
						switch(state){
						case 1:
							state++;
							image.setImageResource(ContainerBox.isTab?R.drawable.help_large02:R.drawable.help02);
							break;
						case 2:
							state++;
							image.setImageResource(ContainerBox.isTab?R.drawable.help_large03:R.drawable.help03);
							break;
						case 3:
							state++;
							image.setImageResource(ContainerBox.isTab?R.drawable.help_large04:R.drawable.help04);
							break;
						case 4:
							state = 1;
							HelpPage.this.finish();
							break;
						default:
						}
					}
					
					if((x<200)&&((h-y)<100)){
						// previous
						switch(state){
						case 1:
							// do nothing
							break;
						case 2:
							state--;
							image.setImageResource(ContainerBox.isTab?R.drawable.help_large01:R.drawable.help01);
							break;
						case 3:
							state--;
							image.setImageResource(ContainerBox.isTab?R.drawable.help_large02:R.drawable.help02);
							break;
						case 4:
							state--;
							image.setImageResource(ContainerBox.isTab?R.drawable.help_large03:R.drawable.help03);
							break;
						default:
						}
					}
					
					return true;
				}
				
				return false;
			}
        });
	}
	
	/** Key control */
    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
    	if(keyCode==KeyEvent.KEYCODE_BACK){
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }
}
