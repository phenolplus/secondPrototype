package control.stage;

import second.prototype.R;

public class DrawableIndex {
	
	public static final int TOTAL = 8;
	
	public static int BACK_GROUND = R.drawable.bgd;
	
	public static void setDrawables(int set){
		switch(set){
		case 0:
			BACK_GROUND = R.drawable.bgd;
			break;
		case 1:
			BACK_GROUND = R.drawable.bgc;
			break;
		case 2:
			BACK_GROUND = R.drawable.bgp;
			break;
		case 3:
			BACK_GROUND = R.drawable.bgb;
			break;
		case 4:
			BACK_GROUND = R.drawable.bgw;
			break;
		case 5:
			BACK_GROUND = R.drawable.bgg;
			break;
		case 6:
			BACK_GROUND = R.drawable.bgr;
			break;
		case 7:
			BACK_GROUND = R.drawable.bgy;
			break;
		default:
			BACK_GROUND = R.drawable.bgd;
		}
	}
}
