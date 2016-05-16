package hk.edu.cuhk.ie.iems5722.group4_our_days;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by jiang on 2016/2/4.
 */
public class MsgAdapter extends ArrayAdapter<item> {
    private int resourceId;

    public MsgAdapter(Context context, ArrayList<item> objects) { super(context, 0, objects);}
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        item msg = getItem(position);
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.roomlist,parent, false);

        if(convertView!=null){
            TextView allmsg = (TextView) convertView.findViewById(R.id.roomlist);
            allmsg.setText(msg.getContent());
        }
        return convertView;
    }
}
