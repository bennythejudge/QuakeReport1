package android.example.com.quakereport1;

/**
 * Created by neo on 09/03/2018.
 */

public class EarthQuake {
    private double mMagn;
    private String mLoc;
    private Long mTime;
    private String mUrl;

    public EarthQuake (double magnitude, String location, Long time, String url) {
        mMagn = magnitude;
        mLoc = location;
        mTime = time;
        mUrl = url;
    }

    public Double getMagnitude () {
        return mMagn;
    }
    public String getLocation () {
        return mLoc;
    }
    public Long getTime() {
        return mTime;
    }
    public String getUrl() {
        return mUrl;
    }
}
