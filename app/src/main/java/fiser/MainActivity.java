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

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;
import android.content.Context;

import com.fiser.sites.R;

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
  }
  public static final int PICK_CONTACT_REQUEST = 1;

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == PICK_CONTACT_REQUEST) {
      if(resultCode == Activity.RESULT_OK){
        sitioList.clear();
        for(Sitio sitio: Sitio.getSitio(this))
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
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
