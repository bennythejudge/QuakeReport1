package android.example.com.quakereport1;

/**
 * Created by neo on 09/03/2018.
 */

public class EarthQuake {
    private String mMagn;
    private String mLoc;
    private String mDate;

    public EarthQuake (String magnitude, String location, String date) {
        mMagn = magnitude;
        mLoc = location;
        mDate = date;
    }

    public String getMagnitude () {
        return mMagn;
    }
    public String getLocation () {
        return mLoc;
    }
    public String getDate () {
        return mDate;
    }
}
