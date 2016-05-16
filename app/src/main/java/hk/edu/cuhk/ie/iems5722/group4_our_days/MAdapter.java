package hk.edu.cuhk.ie.iems5722.group4_our_days;

/**
 * Created by jiang on 2016/2/25.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import org.kymjs.kjframe.KJBitmap;
import java.util.ArrayList;

public class MAdapter extends ArrayAdapter<message> {

    private KJBitmap kjb;
    private ArrayList<message> objects;
    TextView allmsg;
    ImageView photo;
    TextView msgtime;
    int a;
    String partner_gender;
    public MAdapter(Context context, ArrayList<message> objects) {
        super(context, 0, objects);
        kjb = new KJBitmap();
    }

    public void refresh(ArrayList<message> objects){
        if (objects == null) {
            objects = new ArrayList<>(0);
        }
        this.objects = objects;
        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final message msg = getItem(position);
        if (msg.getUser_id()==a) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message, parent, false);
            allmsg = (TextView) convertView.findViewById(R.id.msg);
            photo = (ImageView) convertView.findViewById(R.id.imgShow);
            msgtime = (TextView) convertView.findViewById(R.id.msg_time);
        }
        else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
            allmsg = (TextView) convertView.findViewById(R.id.msg);
            photo = (ImageView) convertView.findViewById(R.id.imgShow);
            msgtime = (TextView) convertView.findViewById(R.id.msg_time);
            ImageView usericon = (ImageView) convertView.findViewById(R.id.partner_icon);
            if (partner_gender.equals("female")) {
                usericon.setImageResource(R.drawable.female_icon);
            } else if (partner_gender.equals("male")) {
                usericon.setImageResource(R.drawable.male_icon);
            }
        }
            if (msg.getType() == message.MSG_TYPE_TEXT) {
                photo.setVisibility(View.GONE);
                allmsg.setVisibility(View.VISIBLE);
                msgtime.setVisibility(View.VISIBLE);
                msgtime.setText(msg.getTimestamp());
                //allmsg.setText(msg.getName() + "\n" + msg.getMessage() + "\n" + msg.getTimestamp());
                allmsg.setText(msg.getMessage());
            } else {
                allmsg.setVisibility(View.GONE);
                photo.setVisibility(View.VISIBLE);
                msgtime.setVisibility(View.VISIBLE);
                if (kjb.getMemoryCache(msg.getMessage()) != null && msg.getMessage() != null &&
                        msg.getMessage().equals(photo.getTag())) {
                } else {
                    photo.setImageResource(R.drawable.default_head);
                }
                String url = "http://54.187.237.119/iems5722/image?imagename=";
                String imagename = url + msg.getMessage();
                kjb.display(photo, imagename, 500, 500);
            }

        return convertView;
    }
}