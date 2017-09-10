package co.tink.movies.ui.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Cantador on 10.09.17.
 */

public class NetworkCheck {

  public boolean connected = false;

  public boolean isNetworkAvailable(Context context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    connected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    return connected;
  }
}
