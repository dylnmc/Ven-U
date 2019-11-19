package edu.fsu.cs.ven_u;

public class TimelineItem {
    private String mTitle;
    private String mVisibility;
    private String mCreator;
    private String mDescription;
    private String mStart;
    private String mEnd;
    private double mLatitude;
    private double mLongitude;

    public TimelineItem(String title, String vis, String creator, String desc, String start, String end, double lat, double lon) {
        mTitle = title;
        mVisibility = vis;
        mCreator = creator;
        mDescription = desc;
        mStart = start;
        mEnd = end;
        mLatitude = lat;
        mLongitude = lon;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getVisibility() {
        return mVisibility;
    }

    public String getCreator() {
        return mCreator;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getStart() {
        return mStart;
    }

    public String getEnd() {
        return mEnd;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }
}
