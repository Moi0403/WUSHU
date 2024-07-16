package fpoly.anhntph36936.bamdiem_app.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class bdiemModel implements Serializable {
    private String _id;
    private String vitri;
    private int diemdo;
    private int diemxanh;
    private ArrayList<String> lichsudo;
    private ArrayList<String> lichsuxanh;

    public bdiemModel() {
    }

    public bdiemModel(String _id, String vitri, int diemdo, int diemxanh, ArrayList<String> lichsudo, ArrayList<String> lichsuxanh) {
        this._id = _id;
        this.vitri = vitri;
        this.diemdo = diemdo;
        this.diemxanh = diemxanh;
        this.lichsudo = lichsudo;
        this.lichsuxanh = lichsuxanh;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getVitri() {
        return vitri;
    }

    public void setVitri(String vitri) {
        this.vitri = vitri;
    }

    public int getDiemdo() {
        return diemdo;
    }

    public void setDiemdo(int diemdo) {
        this.diemdo = diemdo;
    }

    public int getDiemxanh() {
        return diemxanh;
    }

    public void setDiemxanh(int diemxanh) {
        this.diemxanh = diemxanh;
    }

    public ArrayList<String> getLichsudo() {
        return lichsudo;
    }

    public void setLichsudo(ArrayList<String> lichsudo) {
        this.lichsudo = lichsudo;
    }

    public ArrayList<String> getLichsuxanh() {
        return lichsuxanh;
    }

    public void setLichsuxanh(ArrayList<String> lichsuxanh) {
        this.lichsuxanh = lichsuxanh;
    }
}
