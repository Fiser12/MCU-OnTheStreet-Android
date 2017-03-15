package fiser.widget;

/**
 * Created by Fiser on 15/03/2017.
 */
import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.fiser.sites.R;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import fiser.BO.Sitio;

import static android.content.Context.LOCATION_SERVICE;

/**
 * WidgetDataProvider acts as the adapter for the collection view widget,
 * providing RemoteViews to the widget in the getViewAt method.
 */
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = "WidgetDataProvider";

    List<Sitio> mCollection = new ArrayList<>();
    Context mContext = null;
    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }
    @Override
    public int getCount() {
        return mCollection.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.list_row);
        Sitio listItem = mCollection.get(position);
        remoteView.setTextViewText(R.id.heading, listItem.title);
        remoteView.setTextViewText(R.id.content, listItem.coordenadas);
        Bitmap bitmap = getImageFromInternalStorage("img"+listItem.id+".png");
        if (bitmap != null)
            remoteView.setImageViewBitmap(R.id.imageView, bitmap);
        return remoteView;
    }
    public Bitmap getImageFromInternalStorage(String filename) {
        Bitmap thumbnail = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_image);
        try {
            File filePath = mContext.getFileStreamPath(filename);
            FileInputStream fi = new FileInputStream(filePath);
            thumbnail = BitmapFactory.decodeStream(fi);
        } catch (Exception ex) {

        }
        return thumbnail;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        mCollection.clear();
        LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            Location location = locationManager.getLastKnownLocation(provider);
            for (Sitio sitio : Sitio.getSitio(mContext)) {
                Location loc = new Location("temp");
                loc.setLatitude(sitio.latitud);
                loc.setLongitude(sitio.longitud);
                float distance = location.distanceTo(loc);
                int cercaniaMinimaInt = sharedPref.getInt(mContext.getString(R.string.cercaniaMinima), mContext.getResources().getInteger(R.integer.cercaniaMinimaDefault));
                if (cercaniaMinimaInt * 1000 > distance)
                    mCollection.add(sitio);
            }
        }

    }

}
