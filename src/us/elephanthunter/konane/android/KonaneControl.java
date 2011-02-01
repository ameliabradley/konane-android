package us.elephanthunter.konane.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class KonaneControl extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
		intent = new Intent();
		intent.setClassName(getPackageName(),us.elephanthunter.konane.android.KonaneMenu.class.getName());
        startActivityForResult(intent, KonaneCode.MENU.ID);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	//KonaneCode codeEnum = KonaneCode.getCode(requestCode);
    	KonaneCode resultCodeEnum = KonaneCode.getCode(resultCode);
    	switch(resultCodeEnum){
    	case MENU:
    		break;
    	case OP_NEW_GAME:
    		Intent newGame = new Intent();
    		newGame.setClassName(getPackageName(), KonaneGame.class.getName());
    		startActivityForResult(newGame, KonaneCode.OP_NEW_GAME.ID);
    		break;
    	case OP_REPLAY:
    		break;
    	case OP_OPTIONS:
    		break;
    	case OP_EXIT:
    		setResult(RESULT_OK);
    		finish();
    		break;
    	default:
    	}
    }
}