package com.apesinspace.blip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by samnwosu on 8/15/15.
 */
public class RouteAdapter extends ArrayAdapter<Routes> {

    protected Context mContext;
    protected List<Routes> mRoutes;

    public RouteAdapter(Context context,List<Routes>routes){
        super(context, R.layout.route_item,routes);
        mContext = context;
        mRoutes = routes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.route_item, null);
            holder = new ViewHolder();
            holder.routeImageView = (ImageView) convertView.findViewById(R.id.imageView);
            holder.routeLabel = (TextView) convertView.findViewById(R.id.textView);
            holder.routeRating = (RatingBar)convertView.findViewById(R.id.ratingBar2);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        Routes route = mRoutes.get(position);
        holder.routeImageView.setImageResource(R.drawable.test);
        holder.routeLabel.setText(route.getName());
        holder.routeRating.setNumStars(3);

        return convertView;
    }

    public void refill(List<Routes> routes){
        mRoutes.clear();
        mRoutes.addAll(routes);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        ImageView routeImageView;
        TextView routeLabel;
        RatingBar routeRating;
    }
}