package hk.edu.cuhk.ie.iems5722.group4_our_days.account;

/**
 * Created by xuxiaohong on 18/4/16.
 */
public class User {
    String operation;
    String userid;
    String username;
    String userbirthday;
    String usergender;
    String password;

    int avatar;
    String trends;
    //String sex;
    int age;
    int lev;
    String time;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getUserid(){return userid;}


    public void setUserid(String userid) {
        this.userid = userid;
    }


    public void setUserName(String username) {
        this.username = username;
    }

    public String getUserName() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getTrends() {
        return trends;
    }

    public void setTrends(String trends) {
        this.trends = trends;
    }

    public String getUserbirthday() {return userbirthday;}

    public void setUserbirthday(String userbirthday){ this.userbirthday = userbirthday;}

    public String getUsergender(){return usergender;}

    public void setUsergender(String usergender){this.usergender = usergender;}

    //public String getSex() {
    //    return sex;
    //}

    //public void setSex(String sex) {
    //    this.sex = sex;
    //}

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getLev() {
        return lev;
    }

    public void setLev(int lev) {
        this.lev = lev;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
