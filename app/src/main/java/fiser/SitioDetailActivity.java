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

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.fiser.sites.R;
import com.squareup.picasso.Picasso;

public class SitioDetailActivity extends AppCompatActivity {
  public static final String TAG = SitioDetailActivity.class.getSimpleName();
  private EditText description;
  private EditText titulo;
  private ImageView imagen;
  private Button boton;
  private Sitio sitio;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      ActionBar actionBar = getSupportActionBar();
      actionBar.setDisplayHomeAsUpEnabled(true);
      setContentView(R.layout.activity_sitio_detail);
      sitio = (Sitio)this.getIntent().getExtras().getSerializable("sitio");
      setTitle(sitio.title);
      description = (EditText) findViewById(R.id.editDescripcion);
      description.setText(sitio.description);
      titulo = (EditText) findViewById(R.id.editTitulo);
      titulo.setText(sitio.title);
      imagen = (ImageView) findViewById(R.id.imageView);
      Picasso.with(this).load(sitio.imageUrl).placeholder(R.mipmap.ic_launcher).into(imagen);
      boton = (Button) findViewById(R.id.buttonGuardar);
      boton.setOnClickListener(new View.OnClickListener() {
          public void onClick(View v) {
              sitio.description = description.getText().toString();
              sitio.title = titulo.getText().toString();
              new SitiosManager(SitioDetailActivity.this).putSitio(sitio);
              finish();
          }
      });
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

}
