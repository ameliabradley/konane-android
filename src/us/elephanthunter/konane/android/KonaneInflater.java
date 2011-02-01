package us.elephanthunter.konane.android;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater.Factory;
import android.view.View;

public class KonaneInflater implements Factory {

	// private final static String TAG = "TicInflater";
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		// TODO Auto-generated method stub

		// Log.v(TAG, ""+name);
		// Log.v(TAG, ""+attrs.getIdAttribute());
		if (name.equals("SurfaceView")) {
			// Log.v(TAG, "It worked!");
			return new KonaneBoardView(context, attrs);
		}

		return null;
	}
}
