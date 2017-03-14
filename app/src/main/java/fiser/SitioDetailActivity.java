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
package fiser;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fiser.sites.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;

public class SitioDetailActivity extends AppCompatActivity {
    public static final String TAG = SitioDetailActivity.class.getSimpleName();
    private EditText description;
    private EditText titulo;
    private TextView ubicacion;

    private ImageView imagen;
    private Button botonGuardar;
    private Button botonBorrar;

    private Sitio sitio;
    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private boolean accesoDenegadoGPS = false;
    Location location = null;
    private Bitmap bitmap;
    private FloatingActionButton fab;
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_sitio_detail);
        sitio = (Sitio) this.getIntent().getExtras().getSerializable("sitio");
        setTitle(sitio.title);
        ubicacion = (TextView) findViewById(R.id.textUbicacion);
        ubicacion.setText(sitio.coordenadas);
        description = (EditText) findViewById(R.id.editDescripcion);
        description.setText(sitio.description);
        titulo = (EditText) findViewById(R.id.editTitulo);
        titulo.setText(sitio.title);
        imagen = (ImageView) findViewById(R.id.imageView);
        imagen.setImageBitmap(getImageFromInternalStorage("img"+sitio.id+".png"));
        botonGuardar = (Button) findViewById(R.id.buttonGuardar);
        botonBorrar = (Button) findViewById(R.id.buttonBorrar);
        fab = (FloatingActionButton) findViewById(R.id.add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("content://contacts");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            accesoDenegadoGPS = true;
        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sitio.description = description.getText().toString();
                sitio.title = titulo.getText().toString();
                if(location!=null&&!accesoDenegadoGPS)
                    sitio.coordenadas = getAddress(location.getLatitude(),location.getLongitude());
                if(changeImage)
                    saveImageToInternalStorage(bitmap);
                new SitiosManager(SitioDetailActivity.this).putSitio(sitio);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("sitio", sitio);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        botonBorrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new SitiosManager(SitioDetailActivity.this).deleteSitio(sitio);
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        imagen.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(SitioDetailActivity.this);
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
            }
        });
    }
    private boolean changeImage = false;
    private static final int PICK_IMAGE_ID = 234;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case PICK_IMAGE_ID:
                bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                imagen.setImageBitmap(bitmap);
                changeImage = true;
                break;
            case REQUEST_CODE:
                if(resultCode== RESULT_OK)
                {
                    Uri uri = data.getData();
                    String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };
                    Cursor cursor = getContentResolver().query(uri, projection,
                            null, null, null);
                    cursor.moveToFirst();
                    int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(numberColumnIndex);
                    int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    String name = cursor.getString(nameColumnIndex);
                    sitio.contactos.add(name+":"+number);
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
    public String saveImageToInternalStorage(Bitmap image) {
        try {
            FileOutputStream fos = this.openFileOutput("img"+sitio.id+".png", Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return "img"+sitio.id+".png";
        } catch (Exception e) {
            Log.e("saveToInternalStorage()", e.getMessage());
            return "";
        }
    }
    public Bitmap getImageFromInternalStorage(String filename) {
        Bitmap thumbnail = null;
        try {
            File filePath = this.getFileStreamPath(filename);
            FileInputStream fi = new FileInputStream(filePath);
            thumbnail = BitmapFactory.decodeStream(fi);
        } catch (Exception ex) {

        }
        return thumbnail;
    }


    private String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            return obj.getAddressLine(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            location = loc;
            ubicacion.setText(getAddress(loc.getLatitude(), loc.getLongitude()));
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

}
