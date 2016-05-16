package hk.edu.cuhk.ie.iems5722.group4_our_days;

/**
 * Created by jiang on 2016/2/4.
 */
public class item {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;
    private String content;
    private int type;

    public item(String content, int type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }
}



