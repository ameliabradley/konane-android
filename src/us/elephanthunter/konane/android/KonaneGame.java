package us.elephanthunter.konane.android;

import us.elephanthunter.konane.android.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

public class KonaneGame extends Activity {
	
	View myView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater layOutInf = getLayoutInflater();
		layOutInf.setFactory(new KonaneInflater());
		myView = layOutInf.inflate(R.layout.board,null);
		setContentView(myView);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		((KonaneBoardView)findViewById(R.id.gameView)).draw();
		return super.onTouchEvent(event);
	}

}
