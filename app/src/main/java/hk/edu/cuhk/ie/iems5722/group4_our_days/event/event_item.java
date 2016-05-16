package hk.edu.cuhk.ie.iems5722.group4_our_days.event;

/**
 * Created by xuxiaohong on 22/4/16.
 */
public class event_item {
    public static final int TYPE_BIRTHDAY = 0;
    public static final int TYPE_COUNTDOWN = 1;
    public static final int TYPE_COUNT = 2;
    private int event_id;
    private String event_name;
    private String event_time;
    private String event_type;

    public event_item(int event_id,String event_name, String event_time, String  event_type) {
        this.event_id = event_id;
        this.event_name = event_name;
        this.event_time = event_time;
        this.event_type = event_type;
    }

    public int getEvent_id(){return event_id;}
    public String getEvent_name(){return event_name;}
    public String getEvent_time(){
        return event_time;
    }

    public String getEvent_type() {
        return event_type;
    }
}
