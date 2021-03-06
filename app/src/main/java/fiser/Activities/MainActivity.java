/*
 * Copyright (c) 2016 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fiser.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;

import com.fiser.sites.R;

import fiser.BO.Sitio;
import fiser.background.EventoBackground;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private ListView mListView;
    private ArrayList<Sitio> sitioList;
    private SitioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sitioList = Sitio.getSitio(this);
        adapter = new SitioAdapter(this, sitioList);
        mListView = (ListView) findViewById(R.id.sitio_list_view);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Serializable selectedSitio = sitioList.get(position);
                Intent detailIntent = new Intent(MainActivity.this, SitioDetailActivity.class);
                detailIntent.putExtra("sitio", selectedSitio);
                startActivityForResult(detailIntent, PICK_CONTACT_REQUEST);
            }
        });
        Intent i= new Intent(this, EventoBackground.class);
        this.startService(i);

    }

    public static final int PICK_CONTACT_REQUEST = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                sitioList.clear();
                for (Sitio sitio : Sitio.getSitio(this))
                    sitioList.add(sitio);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    private void listaRecargar(boolean cercanos) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sitioList.clear();
        if (!cercanos) {
            for (Sitio sitio : Sitio.getSitio(this))
                sitioList.add(sitio);
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                for (Sitio sitio : Sitio.getSitio(this))
                    sitioList.add(sitio);
            } else {
                Location location = locationManager.getLastKnownLocation(provider);
                for (Sitio sitio : Sitio.getSitio(this)) {
                    Location loc = new Location("temp");
                    loc.setLatitude(sitio.latitud);
                    loc.setLongitude(sitio.longitud);
                    float distance = location.distanceTo(loc);
                    int cercaniaMinimaInt = sharedPref.getInt(getString(R.string.cercaniaMinima), getResources().getInteger(R.integer.cercaniaMinimaDefault));
                    if (cercaniaMinimaInt * 1000 > distance)
                        sitioList.add(sitio);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.crear:
                Intent detailIntent = new Intent(this, SitioDetailActivity.class);
                Sitio sitio = new Sitio();
                sitio.id = new Random().nextInt(Integer.MAX_VALUE);
                detailIntent.putExtra("sitio", sitio);
                startActivityForResult(detailIntent, PICK_CONTACT_REQUEST);
                return true;
            case R.id.settings:
                detailIntent = new Intent(this, SitioSettingsActivity.class);
                startActivity(detailIntent);
                return true;
            case R.id.cerca:
                item.setChecked(!item.isChecked());
                listaRecargar(item.isChecked());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
