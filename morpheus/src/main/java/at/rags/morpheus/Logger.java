package at.rags.morpheus;

import android.util.Log;

/**
 * Created by raphaelseher on 07/03/16.
 */
public class Logger {
  private static final String TAG = "Morpheus";
  private static boolean debug = false;

  public static void debug(String message) {
    if (debug) {
      Log.d(TAG, message);
    }
  }

  public static void setDebug(boolean debug) {
    Logger.debug = debug;
  }
}
