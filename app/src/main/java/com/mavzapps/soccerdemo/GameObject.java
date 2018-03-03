package com.mavzapps.soccerdemo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by MariaVirginia on 2/3/2018.
 */

public class GameObject implements Parcelable {

    private String gameName;
    private String gameStatus;
    private String gameLocation;
    private String homeTeamName;
    private String awayTeamName;
    private Date startDate;
    private Date endDate;
    private int homeTeamScore;
    private int awayTeamScore;
    private int viewType;

    GameObject(int viewType) {
        super();
        this.viewType = viewType;
    }

    void setGameName(String name){
        gameName = name;
    }

    void setGameStatus(String status){
        gameStatus = status;
    }

    void setGameLocation(String location){
        gameLocation = location;
    }

    void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }

    void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    void setAwayTeamScore(int awayTeamScore) {
        this.awayTeamScore = awayTeamScore;
    }

    void setHomeTeamScore(int homeTeamScore) {
        this.homeTeamScore = homeTeamScore;
    }

    public String getGameName() {
        return gameName;
    }

    public String getGameLocation() {
        return gameLocation;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public int getAwayTeamScore() {
        return awayTeamScore;
    }

    public int getHomeTeamScore() {
        return homeTeamScore;
    }

    public int getViewType() {
        return viewType;
    }

    protected GameObject(Parcel in) {
        gameName = in.readString();
        gameStatus = in.readString();
        gameLocation = in.readString();
        homeTeamName = in.readString();
        awayTeamName = in.readString();
        long tmpStartDate = in.readLong();
        startDate = tmpStartDate != -1 ? new Date(tmpStartDate) : null;
        long tmpEndDate = in.readLong();
        endDate = tmpEndDate != -1 ? new Date(tmpEndDate) : null;
        homeTeamScore = in.readInt();
        awayTeamScore = in.readInt();
        viewType = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(gameName);
        dest.writeString(gameStatus);
        dest.writeString(gameLocation);
        dest.writeString(homeTeamName);
        dest.writeString(awayTeamName);
        dest.writeLong(startDate != null ? startDate.getTime() : -1L);
        dest.writeLong(endDate != null ? endDate.getTime() : -1L);
        dest.writeInt(homeTeamScore);
        dest.writeInt(awayTeamScore);
        dest.writeInt(viewType);
    }

    @SuppressWarnings("unused")
    public static final Creator<GameObject> CREATOR = new Creator<GameObject>() {
        @Override
        public GameObject createFromParcel(Parcel in) {
            return new GameObject(in);
        }

        @Override
        public GameObject[] newArray(int size) {
            return new GameObject[size];
        }
    };
}