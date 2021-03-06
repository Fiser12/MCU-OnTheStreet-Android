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
package fiser.BO;

import android.content.Context;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

import fiser.model.SitiosManager;

public class Sitio implements Serializable {

  public static final String TAG = Sitio.class.getSimpleName();
  public int id;
  public String title;
  public String description;
  public String imageUrl;
  public String instructionUrl;
  public String coordenadas;
  public double latitud;
  public double longitud;
  public ArrayList<String> contactos = new ArrayList<>();

  public static ArrayList<Sitio> getSitio(Context context){
    return (new SitiosManager(context)).getSitios();
  }
  public static void putSitio(Context context, Sitio sitio){
    (new SitiosManager(context)).putSitio(sitio);
  }

  private static String loadJsonFromAsset(String filename, Context context) {
    String json = null;
    try {
      InputStream is = context.getAssets().open(filename);
      int size = is.available();
      byte[] buffer = new byte[size];
      is.read(buffer);
      is.close();
      json = new String(buffer, "UTF-8");
    }
    catch (java.io.IOException ex) {
      ex.printStackTrace();
      return null;
    }

    return json;
  }

}
