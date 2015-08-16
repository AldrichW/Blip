package com.apesinspace.blip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by samnwosu on 8/14/15.
 */
public class UserAdapter extends ArrayAdapter<User> {

    protected Context mContext;
    protected List<User> mUsers;

    public UserAdapter(Context context,List<User>users){
        super(context, R.layout.user_item,users);
        mContext = context;
        mUsers = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.senderLabel);
            holder.review = (TextView)convertView.findViewById(R.id.review);
            holder.mRatingBar = (RatingBar)convertView.findViewById(R.id.ratingBar);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        User user = mUsers.get(position);
        holder.review.setText(user.getReview());
        holder.nameLabel.setText(user.getName());
        holder.mRatingBar.setNumStars(user.getRating());
        Picasso.with(mContext).load(user.getImageUrl()).into(holder.iconImageView);
        return convertView;
    }

    public void refill(List<User> users){
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        TextView review;
        RatingBar mRatingBar;
    }
}
