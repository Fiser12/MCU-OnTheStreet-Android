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

import android.content.Context;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Sitio implements Serializable {

  public static final String TAG = Sitio.class.getSimpleName();

  public String title;
  public String description;
  public String imageUrl;
  public String instructionUrl;
  public String coordenadas;
  public ArrayList<String> contactos = new ArrayList<>();

  public static ArrayList<Sitio> getRecipesFromFile(String filename, Context context){
    final ArrayList<Sitio> sitioList = new ArrayList<>();

    try {
      String jsonString = loadJsonFromAsset(filename, context);
      JSONObject json = new JSONObject(jsonString);
      JSONArray recipes = json.getJSONArray("recipes");
      for(int i = 0; i < recipes.length(); i++){
        Sitio sitio = new Sitio();
        sitio.title = recipes.getJSONObject(i).getString("title");
        sitio.description = recipes.getJSONObject(i).getString("description");
        sitio.imageUrl = recipes.getJSONObject(i).getString("image");
        sitio.instructionUrl = recipes.getJSONObject(i).getString("url");
        sitio.coordenadas = recipes.getJSONObject(i).getString("coordenadas");
        JSONArray st = recipes.getJSONObject(i).getJSONArray("contactos");
        for(int j=0;j<st.length();j++)
          sitio.contactos.add(st.getString(j));
        sitioList.add(sitio);
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return sitioList;
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