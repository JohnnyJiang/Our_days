package hk.edu.cuhk.ie.iems5722.group4_our_days;

/**
 * Created by jiang on 2016/2/25.
 */
public class message {
    public final static int MSG_TYPE_TEXT = 0;
    public final static int MSG_TYPE_PHOTO = 1;
    private String message;
    private String name;
    private String timestamp;
    private int user_id;
    private int type;

    public message(String message, String name, String timestamp, int user_id, int type) {
        this.message = message;
        this.name = name;
        this.timestamp = timestamp;
        this.user_id = user_id;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public String getName(){
        return name;
    }

    public String getTimestamp(){
        return timestamp;
    }

    public int getUser_id(){
        return user_id;
    }

    public int getType(){
        return type;
    }

    public String getContent() {
        return name+message+timestamp;
    }

}
