package com.vmd.webViewControl.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import com.vmd.webViewControl.ActivityMain;
// Need the following import to get access to the app resources, since this
// class is in a sub-package.
import com.vmd.webViewControl.R;

public class Provider extends AppWidgetProvider {
	// log tag
	private static final String TAG = "webViewControlWidget";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.d(TAG, "onUpdate");
		// For each widget that needs an update, get the text that we should display:
		//   - Create a RemoteViews object for it
		//   - Set the text in the RemoteViews object
		//   - Tell the AppWidgetManager to show that views object for the widget.
		final int N = appWidgetIds.length;
		for (int i=0; i<N; i++) {
			int appWidgetId = appWidgetIds[i];
			String titlePrefix = Settings.loadTitlePref(context, appWidgetId);
			updateAppWidget(context, appWidgetManager, appWidgetId, titlePrefix);
		}
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.d(TAG, "onDeleted");
		// When the user deletes the widget, delete the preference associated with it.
		final int N = appWidgetIds.length;
		for (int i=0; i<N; i++) {
			Settings.deleteTitlePref(context, appWidgetIds[i]);
		}
	}

	@Override
	public void onEnabled(Context context) {
		Log.d(TAG, "onEnabled");
		// When the first widget is created, register for the TIMEZONE_CHANGED and TIME_CHANGED
		// broadcasts.  We don't want to be listening for these if nobody has our widget active.
		// This setting is sticky across reboots, but that doesn't matter, because this will
		// be called after boot if there is a widget instance for this provider.
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(
			new ComponentName("com.example.android.apis", ".appwidget.ExampleBroadcastReceiver"),
			PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
			PackageManager.DONT_KILL_APP
		);
	}

	@Override
	public void onDisabled(Context context) {
		// When the first widget is created, stop listening for the TIMEZONE_CHANGED and
		// TIME_CHANGED broadcasts.
		Log.d(TAG, "onDisabled");
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(
			new ComponentName("com.example.android.apis", ".appwidget.ExampleBroadcastReceiver"),
			PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
			PackageManager.DONT_KILL_APP
		);
	}

	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String titlePrefix) {
		
		Log.d(TAG, "updateAppWidget appWidgetId=" + appWidgetId + " titlePrefix=" + titlePrefix);

		// Getting the string this way allows the string to be localized.  The format
		// string is filled in using java.util.Formatter-style format strings.
		CharSequence text = context.getString(R.string.appwidget_text_format,
			Settings.loadTitlePref(context, appWidgetId),
			"0x" + Long.toHexString(SystemClock.elapsedRealtime())
		);

		// Construct the RemoteViews object.  It takes the package name (in our case, it's our
		// package, but it needs this because on the other side it's the widget host inflating
		// the layout from our package).
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
//		views.setTextViewText(R.id.appwidget_text, text);

		
		// Create an Intent to launch ExampleActivity
		Intent intent = new Intent(context, ActivityMain.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

		// Get the layout for the App Widget and attach an on-click listener
		// to the button
//		views.setOnClickPendingIntent(R.id.appwidget_layout, pendingIntent);

		// Tell the widget manager
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}
}
