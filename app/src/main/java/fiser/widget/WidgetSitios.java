package fiser.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.GridView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.fiser.sites.R;

import java.util.Random;

import fiser.Activities.MainActivity;
import fiser.Activities.SitioDetailActivity;
import fiser.BO.Sitio;

import static fiser.Activities.MainActivity.PICK_CONTACT_REQUEST;


/**
 * Implementation of App Widget functionality.
 */
public class WidgetSitios extends AppWidgetProvider {

    private RemoteViews widget;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_sitios);
        Intent svcIntent = new Intent(context, WidgetService.class);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(R.id.widget_list, svcIntent);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }
    public static String YOUR_AWESOME_ACTION = "com.my.assignmentmanager.YourAwesomeAction";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int i = 0; i < appWidgetIds.length; i++){
            Intent svcIntent = new Intent(context, WidgetService.class);
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
            widget = new RemoteViews(context.getPackageName(), R.layout.widget_sitios);
            widget.setRemoteAdapter(appWidgetIds[i], R.id.widget_list, svcIntent);
            Intent toastIntent = new Intent(context, WidgetSitios.class);
            toastIntent.setAction(WidgetSitios.YOUR_AWESOME_ACTION);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            widget.setPendingIntentTemplate(R.id.widget_list, toastPendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
    @Override
    public void onReceive(Context context, Intent intent)
    {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(YOUR_AWESOME_ACTION)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            int id = intent.getIntExtra("KEY", 0);
            for (Sitio sitio : Sitio.getSitio(context)) {
                if(id==sitio.id){
                    Log.e("ERR", ""+sitio.id);
                    Intent i = new Intent();
                    i.putExtra("sitio", sitio);
                    i.setClass(context, SitioDetailActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                    break;
                }
            }
        }
        super.onReceive(context, intent);
    }

}

