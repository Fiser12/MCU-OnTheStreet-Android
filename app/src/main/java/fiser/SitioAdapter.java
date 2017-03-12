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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

import com.raywenderlich.alltherecipes.R;
import com.squareup.picasso.Picasso;
import android.graphics.Typeface;

public class SitioAdapter extends BaseAdapter {

  public static final String TAG = SitioAdapter.class.getSimpleName();
  public static final HashMap<String, Integer> LABEL_COLORS = new HashMap<String, Integer>()
  {{
    put("Low-Carb", R.color.colorLowCarb);
    put("Low-Fat", R.color.colorLowFat);
    put("Low-Sodium", R.color.colorLowSodium);
    put("Medium-Carb", R.color.colorMediumCarb);
    put("Vegetarian", R.color.colorVegetarian);
    put("Balanced", R.color.colorBalanced);
  }};

  private Context mContext;
  private LayoutInflater mInflater;
  private ArrayList<Sitio> mDataSource;

  public SitioAdapter(Context context, ArrayList<Sitio> items) {
    mContext = context;
    mDataSource = items;
    mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  @Override
  public int getCount() {
    return mDataSource.size();
  }

  @Override
  public Object getItem(int position) {
    return mDataSource.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    ViewHolder holder;
    if (convertView == null) {
      convertView = mInflater.inflate(R.layout.list_item_sitio, parent, false);
      holder = new ViewHolder();
      holder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.sitio_list_thumbnail);
      holder.titleTextView = (TextView) convertView.findViewById(R.id.sitio_list_title);
      convertView.setTag(holder);
    }
    else {
      holder = (ViewHolder) convertView.getTag();
    }
    TextView titleTextView = holder.titleTextView;
    ImageView thumbnailImageView = holder.thumbnailImageView;
    Sitio sitio = (Sitio) getItem(position);
    titleTextView.setText(sitio.title);
    Picasso.with(mContext).load(sitio.imageUrl).placeholder(R.mipmap.ic_launcher).into(thumbnailImageView);
    Typeface titleTypeFace = Typeface.createFromAsset(mContext.getAssets(),
        "fonts/JosefinSans-Bold.ttf");
    titleTextView.setTypeface(titleTypeFace);
    return convertView;
  }

  private static class ViewHolder {
    public TextView titleTextView;
    public ImageView thumbnailImageView;
  }
}
