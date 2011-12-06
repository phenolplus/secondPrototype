package control.appearance;


import second.prototype.R;
import android.content.Context;
import android.media.MediaPlayer;

public class BackgroundMusic {
	
	public static MediaPlayer player;
	
	public static final int CHANGE = R.raw.change;
	public static final int CHANGER = R.raw.changer;
	
	public static int BGM = CHANGE;
	
	public static void setBGM(int set){
		switch(set){
		case 0:
			BGM = CHANGER;
			break;
		default:
			BGM = CHANGE;
		}
	}
	
	public static void init(Context owner){
		player = MediaPlayer.create(owner,BGM);
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
