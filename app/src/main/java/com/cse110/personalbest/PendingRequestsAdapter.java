package com.cse110.personalbest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PendingRequestsAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;

    public PendingRequestsAdapter(Context context, Data[] data) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.item_pending_request, null);
        TextView text = (TextView) vi.findViewById(R.id.tv_pending_email);
        text.setText(data[position]);
        return vi;
    }
}
