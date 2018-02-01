package gr.gkortsaridis.ibaydm;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class LaPixDeMounActivity extends AppCompatActivity {

    CheckBox pm6,pm9,am0,am2,am3,am4,am6;
    Spinner efodoi;
    ArrayList<Long> eventIDs;
    SharedPreferences sharedpreferences;
    int minutesBeforeNotification;
    boolean shouldNotify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_la_pix_de_moun);

        sharedpreferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        minutesBeforeNotification = sharedpreferences.getInt("notify_minutes",15);
        shouldNotify = sharedpreferences.getBoolean("notify",true);

        eventIDs = new ArrayList<>();

        String events = sharedpreferences.getString("eventIDS","");

        if(!(events.equals("") || events.equals("[]"))){
            events = events.substring(1,events.length()-1);
            events = events.replace(" ","");
            Log.i("EVENTS",events);
            String[] temp  = events.split(",");
            Log.i("EVENTS LENGTH",temp.length+"");
            for(int i=0; i<temp.length; i++) {
                eventIDs.add(Long.parseLong(temp[i]));
            }
        }else{
            Log.i("EMPTY","EVENTS");
        }


        efodoi = findViewById(R.id.efodoi);
        pm6 = findViewById(R.id.pm6);
        pm9 = findViewById(R.id.pm9);
        am0 = findViewById(R.id.am0);
        am2 = findViewById(R.id.am2);
        am3 = findViewById(R.id.am3);
        am4 = findViewById(R.id.am4);
        am6 = findViewById(R.id.am6);

        pm6.setChecked(sharedpreferences.getBoolean("pm6",false));
        pm9.setChecked(sharedpreferences.getBoolean("pm9",false));
        am0.setChecked(sharedpreferences.getBoolean("am0",false));
        am2.setChecked(sharedpreferences.getBoolean("am2",false));
        am3.setChecked(sharedpreferences.getBoolean("am3",false));
        am4.setChecked(sharedpreferences.getBoolean("am4",false));
        am6.setChecked(sharedpreferences.getBoolean("am6",false));

        List<String> categories = new ArrayList<String>();
        categories.add("16:00-18:00 & 12:00-02:00");
        categories.add("18:00-20:00 & 02:00-04:00");
        categories.add("20:00-22:00 & 04:00-06:00");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        efodoi.setAdapter(dataAdapter);
    }

    public void settings(View view){
        startActivity(new Intent(LaPixDeMounActivity.this, SettingsActivity.class));
    }

    public void submit(View view){
        //setAlarm();
        remindMe();
    }

    public void refresh(View view){

        String events = sharedpreferences.getString("eventIDS","");

        if(!(events.equals("") || events.equals("[]"))){
            events = events.substring(1,events.length()-1);
            events = events.replace(" ","");
            Log.i("EVENTS",events);
            String[] temp  = events.split(",");
            Log.i("EVENTS LENGTH",temp.length+"");
            for(int i=0; i<temp.length; i++) {
                Uri deleteUri = null;
                if(!temp[i].equals("")){
                    deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Long.parseLong(String.valueOf(temp[i])));
                    int rows = getContentResolver().delete(deleteUri, null, null);
                }
            }
            Toast.makeText(this, "Μια νέα υπηρεσία ξεκινάει", Toast.LENGTH_SHORT).show();

        }else{
            Log.i("EMPTY","EVENTS");
        }

        pm6.setChecked(false);
        pm9.setChecked(false);
        am0.setChecked(false);
        am2.setChecked(false);
        am3.setChecked(false);
        am4.setChecked(false);
        am6.setChecked(false);
        eventIDs.clear();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("pm6",false);
        editor.putBoolean("pm9",false);
        editor.putBoolean("am0",false);
        editor.putBoolean("am2",false);
        editor.putBoolean("am3",false);
        editor.putBoolean("am4",false);
        editor.putBoolean("am6",false);
        editor.putString("eventIDS",eventIDs.toString());
        editor.commit();

    }

    private Long rem(String dateStr){
        long timeInMilliseconds;
        String givenDateString = dateStr;//"Jan 30 2018 10:37:00 GMT+02:00";
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss z");
        Date mDate = null;
        try {
            mDate = sdf.parse(givenDateString);
            timeInMilliseconds = mDate.getTime();

            final ContentValues event = new ContentValues();
            event.put(CalendarContract.Events.CALENDAR_ID, 1);
            event.put(CalendarContract.Events.TITLE, "Αλλαγή Σκοπών");
            event.put(CalendarContract.Events.DESCRIPTION, "Κουνήσου");
            event.put(CalendarContract.Events.DTSTART, timeInMilliseconds);
            event.put(CalendarContract.Events.DTEND, timeInMilliseconds);
            event.put(CalendarContract.Events.ALL_DAY, 0);   // 0 for false, 1 for true
            event.put(CalendarContract.Events.HAS_ALARM, 1); // 0 for false, 1 for true
            event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

            Uri baseUri;
            if (Build.VERSION.SDK_INT >= 8) {
                baseUri = Uri.parse("content://com.android.calendar/events");
            } else {
                baseUri = Uri.parse("content://calendar/events");
            }

            Uri eventUri = getContentResolver().insert(baseUri, event);

            Long eventID = Long.parseLong(eventUri.getLastPathSegment());
            String reminderUriString = "content://com.android.calendar/reminders";
            ContentValues reminderValues = new ContentValues();
            reminderValues.put("event_id", eventID);
            reminderValues.put("minutes", minutesBeforeNotification); // Default value
            //set time in min which occur before event start
            reminderValues.put("method", 1); // Alert Methods: Default(0),
            // Alert(1), Email(2),SMS(3)
            getApplicationContext().getContentResolver().insert(Uri.parse(reminderUriString), reminderValues);
            return eventID;
        } catch (ParseException e) {
            e.printStackTrace();
            return Long.parseLong("-1");
        }

    }

    private void remindMe(){
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        String todayStr  = DateFormat.format("MMM dd yyyy", today.getTime()).toString();
        String tomorrowStr  = DateFormat.format("MMM dd yyyy", tomorrow.getTime()).toString();

        if(pm6.isChecked()){ String dateStr = todayStr+" 18:00:00 GMT+02:00"; if(shouldNotify)eventIDs.add(rem(dateStr)); }
        if(pm9.isChecked()){ String dateStr = todayStr+" 21:00:00 GMT+02:00"; if(shouldNotify)eventIDs.add(rem(dateStr)); }
        if(am0.isChecked()){ String dateStr = tomorrowStr+" 00:00:00 GMT+02:00"; if(shouldNotify)eventIDs.add(rem(dateStr)); }
        if(am2.isChecked()){ String dateStr = tomorrowStr+" 02:00:00 GMT+02:00"; if(shouldNotify)eventIDs.add(rem(dateStr)); }
        if(am3.isChecked()){ String dateStr = tomorrowStr+" 03:00:00 GMT+02:00"; if(shouldNotify)eventIDs.add(rem(dateStr)); }
        if(am4.isChecked()){ String dateStr = tomorrowStr+" 04:00:00 GMT+02:00"; if(shouldNotify)eventIDs.add(rem(dateStr)); }
        if(am6.isChecked()){ String dateStr = tomorrowStr+" 06:00:00 GMT+02:00"; if(shouldNotify)eventIDs.add(rem(dateStr)); }

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("pm6",pm6.isChecked());
        editor.putBoolean("pm9",pm9.isChecked());
        editor.putBoolean("am0",am0.isChecked());
        editor.putBoolean("am2",am2.isChecked());
        editor.putBoolean("am3",am3.isChecked());
        editor.putBoolean("am4",am4.isChecked());
        editor.putBoolean("am6",am6.isChecked());
        editor.putString("eventIDS",eventIDs.toString());
        editor.commit();
        Toast.makeText(this, "Θα πήξεις και σήμερα", Toast.LENGTH_LONG).show();
    }

    private void setAlarm() {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis());

        int day = cal.get(Calendar.DAY_OF_WEEK);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        i.putExtra(AlarmClock.EXTRA_HOUR, hour + Integer.parseInt("10"));
        i.putExtra(AlarmClock.EXTRA_MINUTES, minute + Integer.parseInt("0"));
        i.putExtra(AlarmClock.EXTRA_SKIP_UI, false);
        startActivity(i);
    }
}
