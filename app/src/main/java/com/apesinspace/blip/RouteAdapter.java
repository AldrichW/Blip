package com.apesinspace.blip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by samnwosu on 8/15/15.
 */
public class RouteAdapter extends ArrayAdapter<Routes> implements Filterable{

    protected Context mContext;
    protected List<Routes> mRoutes;
    protected List<Routes>mFiltered;
    protected List<String>urls;
    private Random rand = new Random(System.currentTimeMillis());
    private ItemFilter mFilter = new ItemFilter();

    public RouteAdapter(Context context,List<Routes>routes) {
        super(context, R.layout.route_item, routes);
        mContext = context;
        mRoutes = new ArrayList<>();
        mFiltered = new ArrayList<>();
        mRoutes.addAll(routes);
        mFiltered.addAll(routes);
        urls = new ArrayList<>();
        urls.add("http://node.jrdbnntt.com/img/PC/bridge1.jpg ");
        urls.add("http://node.jrdbnntt.com/img/PC/bridge2.jpg ");
        urls.add("http://node.jrdbnntt.com/img/PC/green1.jpg");
        urls.add("http://node.jrdbnntt.com/img/PC/ice1.jpg");
        urls.add("http://node.jrdbnntt.com/img/PC/mountains1.jpg");
        urls.add("http://node.jrdbnntt.com/img/PC/offroad1.jpg");
        urls.add("http://node.jrdbnntt.com/img/PC/plains1.jpg");
        urls.add("http://node.jrdbnntt.com/img/PC/slippery1.jpg");
        urls.add("http://node.jrdbnntt.com/img/PC/water1.jpg");

    }

    public Filter getFilter() {
        return mFilter;
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

        Routes route = mFiltered.get(position);
        int index = rand.nextInt(mFiltered.size());

        Picasso.with(mContext).load(urls.get(index)).into(holder.routeImageView);
        //holder.routeImageView.setImageResource(R.drawable.test);
        holder.routeLabel.setText(route.getName());
        holder.routeRating.setNumStars(route.getAvgRating());

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

    public int getCount() {
        return mFiltered.size();
    }

    public long getItemId(int position) {
        return position;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            int filterInt;
            FilterResults results = new FilterResults();
            if(!constraint.toString().equals("")) {
                filterInt = Integer.parseInt(constraint.toString());
            }else{
                results.values = mRoutes;
                results.count = mRoutes.size();
                return results;
            }

            final List<Routes> list = mRoutes;

            int count = list.size();
            final ArrayList<Routes> nlist = new ArrayList<Routes>(count);
            int filterableInt ;

            for (int i = 0; i < count; i++) {
                filterableInt = list.get(i).getDistance();
                if (filterableInt <= filterInt) {
                    nlist.add(list.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFiltered = (ArrayList<Routes>) results.values;
            notifyDataSetChanged();
        }

    }
}

