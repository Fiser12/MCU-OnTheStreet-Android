package fiser;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

import com.fiser.sites.R;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class EventoBackground extends Service {
    private Sitio sitioMasCercano;
    private Timer mTimer;
    private TimerTask timerTask = new TimerTask() {
        public void run() {
            send();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTimer = new Timer();
        mTimer.schedule(timerTask, 10000, 10000);
    }

    @Override
    public void onDestroy() {
        try {
            mTimer.cancel();
            timerTask.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        sendBroadcast(intent);
    }

    private Sitio getSitioMasCercano() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ArrayList<Sitio> sitios = new SitiosManager(getBaseContext()).getSitios();
        Sitio elegido = null;
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }else {
            Location location = locationManager.getLastKnownLocation(provider);
            float distanceMinima = Float.MAX_VALUE;
            for (Sitio sitio : sitios) {
                try {
                    Location loc = new Location("temp");
                    loc.setLatitude(sitio.latitud);
                    loc.setLongitude(sitio.longitud);
                    float distance = location.distanceTo(loc);
                    int cercaniaMinimaInt = sharedPref.getInt(getString(R.string.cercaniaMinima), getResources().getInteger(R.integer.cercaniaMinimaDefault));
                    if (cercaniaMinimaInt * 1000 > distance) {
                        if (distanceMinima > distance) {
                            elegido = sitio;
                            distanceMinima = distance;
                        }
                    }
                }catch(NullPointerException e){
                    e.printStackTrace();
                }
            }
            return elegido;
        }
    }
    public void send(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean notificacionesActivas = sharedPref.getBoolean(getString(R.string.notificacionesActivas), getResources().getBoolean(R.bool.notificacionesActivasDefault));
        if(notificacionesActivas) {
            Sitio nuevo = getSitioMasCercano();
            if(sitioMasCercano==null) {
                sitioMasCercano = new Sitio();
                sitioMasCercano.id = new Random().nextInt(Integer.MAX_VALUE);
            }
            if(nuevo!=null) {
                if (!(nuevo.id == sitioMasCercano.id)) {
                    sitioMasCercano = nuevo;
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction("RSSPullService");
                    Intent notificationIntent = new Intent(getBaseContext(), SitioDetailActivity.class);
                    notificationIntent.putExtra("sitio", sitioMasCercano);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
                    Context context = getApplicationContext();
                    Notification.Builder builder;
                    builder = new Notification.Builder(context)
                            .setContentTitle("Nueva ubicación cercana")
                            .setContentText(sitioMasCercano.title + " en " + sitioMasCercano.coordenadas)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setSmallIcon(R.drawable.ic_stat_name);
                    Notification notification = builder.build();
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(1, notification);
                }
            }
        }
    }
}