package com.safeatwork.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.safeatwork.R;
import com.safeatwork.model.NotificationModel;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<NotificationModel> _data;
    private Context _c;
    private OnItemClickListener mItemClickListener;

    public NotificationAdapter(Context applicationContext, List<NotificationModel> list) {

        _data = list;
        _c = applicationContext;
        //Log.e("test", String.valueOf(origList.size()));
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout Clayout;
        TextView title;
        TextView desc;
        TextView timestamp;
        public ImageView icon;

        ViewHolder(View itemView) {
            super(itemView);
            Clayout = itemView.findViewById(R.id.cardview_layout);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            timestamp = itemView.findViewById(R.id.timestamp);
            icon = itemView.findViewById(R.id.icon);
            Clayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(itemView, getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
		/*if(viewType==0)
		{
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_layout_variety, parent, false);

			//View child = _c.getApplicationContext().getLayoutInflater().inflate(R.layout.ad_layout_variety, null);

			AdView mAdView = (AdView) view.findViewById(R.id.adView);
			AdRequest adRequest = new AdRequest.Builder()
					.build();
			mAdView.loadAd(adRequest);
		}
		else*/
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        NotificationModel m = _data.get(position);

        holder.title.setText(m.getTitle());
        holder.desc.setText(m.getMessage());
        holder.timestamp.setText(m.getTimestamp());
    }

	/*@Override
	public Object getItemId(int position) {
		return  _data.get(position);
	}*/

    @Override
    public int getItemCount() {
        return _data.size();
    }


}
