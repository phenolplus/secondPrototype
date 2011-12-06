package control.appearance;


import second.prototype.R;
import android.content.Context;
import android.media.MediaPlayer;

public class BackgroundMusic {
	
	public static MediaPlayer player;
	
	public static final int BGM_CHANGE = R.raw.change;
	
	public static void init(Context owner, int Res_id){
		player = MediaPlayer.create(owner, Res_id);
		player.setLooping(true);
	}
	
	public static void play(){
		player.start();
	}
	
	public static void pause(){
		if(player.isPlaying()){
			player.pause();
		}
	}
	
	public static void stop(){
		if(player!=null){
			if(player.isPlaying()){
				player.stop();
			}
			player.release();
		}
	}
}
