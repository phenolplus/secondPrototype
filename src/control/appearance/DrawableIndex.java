package control.appearance;

import second.prototype.R;

public class DrawableIndex {
	
	public static final int TOTAL = 8;
	
	public static int BACK_GROUND = R.drawable.bgd;
	public static int LIST_BACK_GROUND = R.drawable.lbgd;
	
	public static void setDrawables(int set){
		switch(set){
		case 0:
			BACK_GROUND = R.drawable.bgd;
			LIST_BACK_GROUND = R.drawable.lbgd;
			break;
		case 1:
			BACK_GROUND = R.drawable.bgc;
			LIST_BACK_GROUND = R.drawable.lbgc;
			break;
		case 2:
			BACK_GROUND = R.drawable.bgp;
			LIST_BACK_GROUND = R.drawable.lbgp;
			break;
		case 3:
			BACK_GROUND = R.drawable.bgb;
			LIST_BACK_GROUND = R.drawable.lbgb;
			break;
		case 4:
			BACK_GROUND = R.drawable.bgw;
			LIST_BACK_GROUND = R.drawable.lbgw;
			break;
		case 5:
			BACK_GROUND = R.drawable.bgg;
			LIST_BACK_GROUND = R.drawable.lbgg;
			break;
		case 6:
			BACK_GROUND = R.drawable.bgr;
			LIST_BACK_GROUND = R.drawable.lbgr;
			break;
		case 7:
			BACK_GROUND = R.drawable.bgy;
			LIST_BACK_GROUND = R.drawable.lbgy;
			break;
		default:
			BACK_GROUND = R.drawable.bgd;
			LIST_BACK_GROUND = R.drawable.lbgd;
		}
	}
}
