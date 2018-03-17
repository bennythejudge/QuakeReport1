package android.example.com.quakereport1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by neo on 09/03/2018.
 */

public class EarthQuakeAdapter extends ArrayAdapter<EarthQuake> {
    public EarthQuakeAdapter(@NonNull Context context, List<EarthQuake> earthQuakes) {
        super(context, 0, earthQuakes);
    }

    private String[] splitLocation(String location) {
        String[] aloc = new String[2];
        if (location.contains(",")) {
            aloc = location.split("[,]");
        } else {
            aloc[0] = "Near";
            aloc[1] = location;
        }
        Log.v("splitLocation", "returning [" + aloc[0] + "][" + aloc[1] + "]");
        return aloc;
    }

    private int getMagnitudeColor(double mag) {
        int magnitudeFloor = (int) Math.floor(mag);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                color = R.color.magnitude1;
            case 2:
                color = R.color.magnitude2;
            case 3:
                color = R.color.magnitude1;
            case 4:
                color = R.color.magnitude1;
            case 5:
                color = R.color.magnitude1;
            case 6:
                color = R.color.magnitude1;
        }
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        String location;
        String[] aloc;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.earthquake_list_item, parent, false);
        }

        EarthQuake currentEarthquake = getItem(position);

        // format the magnitude
        DecimalFormat formatter = new DecimalFormat("0.0");
        double mag = currentEarthquake.getMagnitude();
        String sMag = formatter.format(mag);

        TextView magnitudeView = (TextView) listItemView.findViewById(R.id.magnitude);
        magnitudeView.setText(sMag);
        // set the color of the magnitude
        int color = getMagnitudeColor(mag);




        location = currentEarthquake.getLocation();
        aloc = splitLocation(location);


        TextView loc1 = (TextView) listItemView.findViewById(R.id.location1);

        TextView loc2 = (TextView) listItemView.findViewById(R.id.location2);

        loc1.setText(aloc[0]);
        loc2.setText(aloc[1]);

        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        TextView timeView = (TextView) listItemView.findViewById(R.id.time);

        Date date = new Date(currentEarthquake.getTime());
        DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");

        dateformat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String dateFormatted = dateformat.format(date);
        String timeFormatted = timeFormat.format(date);

        dateView.setText(dateFormatted);
        timeView.setText(timeFormatted);

        return listItemView;
    }
}
