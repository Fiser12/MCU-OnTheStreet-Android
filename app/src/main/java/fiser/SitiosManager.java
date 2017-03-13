package fiser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class SitiosManager extends SQLiteOpenHelper{

    // Database name and version
    private static final String DATABASE_NAME = "sitiolist.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_SITIOS = "sitio";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_COORDENADAS = "coordenadas";
    private static final String COLUMN_IMG = "img";
    private static final String COLUMN_CONTACTSID = "contactos";

    // SQL sentence to create the tables
    private static final String DATABASE_CREATE = "create table "
            + TABLE_SITIOS + "(" +
            COLUMN_ID + " integer primary key autoincrement" +
            ", " + COLUMN_TITLE + " text not null, " + COLUMN_DESCRIPTION + " text not null" +
            ", " + COLUMN_COORDENADAS + " text" +
            ", " + COLUMN_IMG + " text" +
            ", " + COLUMN_CONTACTSID + " text);";

    public SitiosManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // Executed when creating the DB for first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        insertExample(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SITIOS);
        onCreate(db);
    }
    private void insertExample(SQLiteDatabase db)
    {
        Sitio sitio = new Sitio();
        sitio.id = new Random().nextInt(100000);
        sitio.title = "Guggenheim";
        sitio.description = "Museo más importante y grande de Bilbao, se encuentra al otro lado de la universidad de Deusto y es una de las principales zonas actuales de turismo obligatorio de la ciudad.";
        sitio.imageUrl = "http://www.esvivir.com/uploads/museo_guggenheim_bilbao_1500_23114227.jpg";
        sitio.coordenadas = "43.268606°, -2.934286°";
        sitio.instructionUrl = "https://www.guggenheim-bilbao.eus";
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, sitio.title);
        values.put(COLUMN_DESCRIPTION, sitio.description);
        values.put(COLUMN_COORDENADAS, sitio.coordenadas);
        values.put(COLUMN_IMG, sitio.imageUrl);
        values.put(COLUMN_CONTACTSID, sitio.contactos.toString());
        db.insert(TABLE_SITIOS, null, values);
    }
    public void putSitio(Sitio sitio){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, sitio.id);
        values.put(COLUMN_TITLE, sitio.title);
        values.put(COLUMN_DESCRIPTION, sitio.description);
        values.put(COLUMN_COORDENADAS, sitio.coordenadas);
        values.put(COLUMN_IMG, sitio.imageUrl);
        values.put(COLUMN_CONTACTSID, sitio.contactos.toString());
        db.insertWithOnConflict(TABLE_SITIOS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
    public ArrayList<Sitio> getSitios(){
        ArrayList<Sitio> as = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_SITIOS,
                new String[]{COLUMN_ID, COLUMN_TITLE, COLUMN_DESCRIPTION, COLUMN_COORDENADAS, COLUMN_IMG, COLUMN_CONTACTSID},
                null,
                null,
                null,
                null,
                null);
        cursor.moveToNext();
        while(!cursor.isAfterLast()){
            Sitio sitio = new Sitio();
            sitio.id = cursor.getInt(0);
            sitio.title = cursor.getString(1);
            sitio.description = cursor.getString(2);
            sitio.coordenadas = cursor.getString(3);
            sitio.imageUrl = cursor.getString(4);
            as.add(sitio);
            cursor.moveToNext();
        }
        return as;
    }
}
