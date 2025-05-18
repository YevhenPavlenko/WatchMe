package com.example.watchme.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.watchme.R;

public class MovieAdapter extends BaseAdapter {
    private Context context;
    private Cursor cursor;

    public MovieAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        cursor.moveToPosition(position);
        return cursor.getString(cursor.getColumnIndexOrThrow("title"));
    }

    @Override
    public long getItemId(int position) {
        cursor.moveToPosition(position);
        return cursor.getLong(cursor.getColumnIndexOrThrow("movie_id"));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        cursor.moveToPosition(position);
        String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        int year = cursor.getInt(cursor.getColumnIndexOrThrow("release_year"));
        float rating = cursor.getFloat(cursor.getColumnIndexOrThrow("rating"));
        String posterName = cursor.getString(cursor.getColumnIndexOrThrow("poster_name"));

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false);
        }

        TextView titleView = convertView.findViewById(R.id.movie_title);
        TextView infoView = convertView.findViewById(R.id.movie_info);
        ImageView posterView = convertView.findViewById(R.id.movie_poster);

        titleView.setText(title);
        infoView.setText("Year: " + year + " | Rating: " + rating);
        int imageId = context.getResources().getIdentifier(posterName, "drawable", context.getPackageName());
        posterView.setImageResource(imageId);

        return convertView;
    }
}
