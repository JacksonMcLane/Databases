package com.per6.databases;

import android.os.Parcel;
import android.os.Parcelable;

public class Friend implements Parcelable {
    private int clumsiness;
    private double gymFrequency;
    private boolean isAwesome;
    private double moneyOwed;
    private String name;
    private int trustworthiness;

    public Friend(){}

    public String toString(){
        return name + " " + clumsiness;
    }

    public int getClumsiness() {
        return clumsiness;
    }

    public void setClumsiness(int clumsiness) {
        this.clumsiness = clumsiness;
    }

    public double getGymFrequency() {
        return gymFrequency;
    }

    public void setGymFrequency(double gymFrequency) {
        this.gymFrequency = gymFrequency;
    }

    public boolean isAwesome() {
        return isAwesome;
    }

    public void setAwesome(boolean awesome) {
        isAwesome = awesome;
    }

    public double getMoneyOwed() {
        return moneyOwed;
    }

    public void setMoneyOwed(double moneyOwed) {
        this.moneyOwed = moneyOwed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTrustworthiness() {
        return trustworthiness;
    }

    public void setTrustworthiness(int trustworthiness) {
        this.trustworthiness = trustworthiness;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(clumsiness);
        parcel.writeDouble(gymFrequency);
        parcel.writeByte((byte) (isAwesome ? 1 : 0));
        parcel.writeDouble(moneyOwed);
        parcel.writeString(name);
        parcel.writeInt(trustworthiness);
    }

    protected Friend(Parcel in) {
        clumsiness = in.readInt();
        gymFrequency = in.readDouble();
        isAwesome = in.readByte() != 0;
        moneyOwed = in.readDouble();
        name = in.readString();
        trustworthiness = in.readInt();
    }

    public static final Creator<Friend> CREATOR = new Creator<Friend>() {
        @Override
        public Friend createFromParcel(Parcel in) {
            return new Friend(in);
        }

        @Override
        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };

}
