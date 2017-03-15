package fiser.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.fiser.sites.R;

public class SitioSettingsActivity extends AppCompatActivity {
    public static final String TAG = SitioSettingsActivity.class.getSimpleName();
    private EditText tituloPorDefecto;
    private EditText cercaniaMinima;
    private EditText lugaresWidget;
    private CheckBox checkBox;
    private Button botonGuardar;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_settings);
        setTitle("Configuración");
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int cercaniaMinimaInt = sharedPref.getInt(getString(R.string.cercaniaMinima), getResources().getInteger(R.integer.cercaniaMinimaDefault));
        int lugaresWidgetInt = sharedPref.getInt(getString(R.string.lugaresWidget), getResources().getInteger(R.integer.lugaresWidgetDefault));
        String tituloPorDefectoDefault = sharedPref.getString(getString(R.string.tituloPorDefecto), getResources().getString(R.string.tituloPorDefectoDefault));
        boolean activadasNotificaciones = sharedPref.getBoolean(getString(R.string.notificacionesActivas), getResources().getBoolean(R.bool.notificacionesActivasDefault));
        cercaniaMinima = (EditText) findViewById(R.id.editTextCercaniaMinima);
        lugaresWidget = (EditText) findViewById(R.id.editTextLugaresWidget);
        tituloPorDefecto = (EditText) findViewById(R.id.editTextTextoTituloPorDefecto);
        checkBox = (CheckBox) findViewById(R.id.checkBoxNotificaciones);
        botonGuardar = (Button) findViewById(R.id.buttonGuardarConfig);

        cercaniaMinima.setText(cercaniaMinimaInt+"", TextView.BufferType.EDITABLE);
        lugaresWidget.setText(lugaresWidgetInt+"", TextView.BufferType.EDITABLE);
        tituloPorDefecto.setText(tituloPorDefectoDefault+"", TextView.BufferType.EDITABLE);
        checkBox.setChecked(activadasNotificaciones);

        botonGuardar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(Integer.parseInt(lugaresWidget.getText().toString())>1&&Integer.parseInt(lugaresWidget.getText().toString())<9) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(getString(R.string.cercaniaMinima), Integer.parseInt(cercaniaMinima.getText().toString()));
                    editor.putInt(getString(R.string.lugaresWidget), Integer.parseInt(lugaresWidget.getText().toString()));
                    editor.putString(getString(R.string.tituloPorDefecto), tituloPorDefecto.getText().toString());
                    editor.putBoolean(getString(R.string.notificacionesActivas), checkBox.isEnabled());
                    editor.commit();
                    finish();
                }else{
                }
            }
        });

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
}
