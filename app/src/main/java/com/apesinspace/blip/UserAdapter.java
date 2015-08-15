package com.apesinspace.blip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by samnwosu on 8/14/15.
 */
public class UserAdapter extends ArrayAdapter<User> {

    protected Context mContext;
    protected List<User> mMessages;

    public UserAdapter(Context context,List<User>messages){
        super(context, R.layout.user_item,messages);
        mContext = context;
        mMessages = messages;
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
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        //User message = mMessages.get(position);

        holder.review.setText("This is  my review of the place I really reeally reeaallly liked it");

        holder.nameLabel.setText("PlaceHolder");
        return convertView;
    }

    public void refill(List<User> users){
        mMessages.clear();
        mMessages.addAll(users);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        TextView review;
    }
}
