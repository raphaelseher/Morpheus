package at.rags.morpheus;

import android.util.Log;

/**
 * Logger you can turn on and off.
 */
public class Logger {
  private static final String TAG = "Morpheus";
  private static boolean debug = false;

  public static void debug(String message) {
    if (debug) {
      Log.d(TAG, message);
    } else {
      System.out.println(message);
    }
  }

  public static void setDebug(boolean debug) {
    at.rags.morpheus.Logger.debug = debug;
  }
}
