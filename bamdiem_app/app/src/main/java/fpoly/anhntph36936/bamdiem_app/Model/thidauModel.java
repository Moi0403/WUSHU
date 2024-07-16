package fpoly.anhntph36936.bamdiem_app.Model;

import java.io.Serializable;

public class thidauModel implements Serializable {
    private String _id;
    private String round;
    private String name_n1;
    private String province_n1;
    private int diem_n1;
    private String name_n2;
    private String province_n2;
    private int diem_n2;
    private int minute;
    private int second;
    private String weight;
    private String group;
    private String sex;

    public thidauModel() {
    }

    public thidauModel(String _id, String round, String name_n1, String province_n1, int diem_n1, String name_n2, String province_n2, int diem_n2, int minute, int second, String weight, String group, String sex) {
        this._id = _id;
        this.round = round;
        this.name_n1 = name_n1;
        this.province_n1 = province_n1;
        this.diem_n1 = diem_n1;
        this.name_n2 = name_n2;
        this.province_n2 = province_n2;
        this.diem_n2 = diem_n2;
        this.minute = minute;
        this.second = second;
        this.weight = weight;
        this.group = group;
        this.sex = sex;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getName_n1() {
        return name_n1;
    }

    public void setName_n1(String name_n1) {
        this.name_n1 = name_n1;
    }

    public String getProvince_n1() {
        return province_n1;
    }

    public void setProvince_n1(String province_n1) {
        this.province_n1 = province_n1;
    }

    public int getDiem_n1() {
        return diem_n1;
    }

    public void setDiem_n1(int diem_n1) {
        this.diem_n1 = diem_n1;
    }

    public String getName_n2() {
        return name_n2;
    }

    public void setName_n2(String name_n2) {
        this.name_n2 = name_n2;
    }

    public String getProvince_n2() {
        return province_n2;
    }

    public void setProvince_n2(String province_n2) {
        this.province_n2 = province_n2;
    }

    public int getDiem_n2() {
        return diem_n2;
    }

    public void setDiem_n2(int diem_n2) {
        this.diem_n2 = diem_n2;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
