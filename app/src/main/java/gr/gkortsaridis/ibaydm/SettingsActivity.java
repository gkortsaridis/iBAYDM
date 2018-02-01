package gr.gkortsaridis.ibaydm;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    CheckBox notify;
    TextView text;
    SeekBar seekbar;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedpreferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE);

        notify = findViewById(R.id.notify);
        text = findViewById(R.id.min_text);
        seekbar = findViewById(R.id.seekBar);

        text.setEnabled(sharedpreferences.getBoolean("notify",true));
        seekbar.setEnabled(sharedpreferences.getBoolean("notify",true));

        notify.setChecked(sharedpreferences.getBoolean("notify",true));
        seekbar.setMax(30);
        seekbar.setProgress(sharedpreferences.getInt("notify_minutes",15));

        text.setText(sharedpreferences.getInt("notify_minutes",15)+" λεπτά νωρίτερα");

        notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                text.setEnabled(b);
                seekbar.setEnabled(b);
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                text.setText(i+" λεπτά νωρίτερα");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("notify",notify.isChecked());
        editor.putInt("notify_minutes",seekbar.getProgress());
        editor.commit();
        Toast.makeText(this,"Αποθηκεύτηκαν οι ρυθμίσεις σου",Toast.LENGTH_SHORT).show();
    }
}
