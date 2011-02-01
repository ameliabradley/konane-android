package us.elephanthunter.konane.android;

import us.elephanthunter.konane.android.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class KonaneMenu extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}
	
	public void buttonClicked(View view) {
		switch(view.getId()){
		case R.id.playGame:
			setResult(KonaneCode.OP_NEW_GAME.ID);
			break;
		case R.id.exit:
			setResult(KonaneCode.OP_EXIT.ID);
			break;
		
		}
		finish();
	}

}
