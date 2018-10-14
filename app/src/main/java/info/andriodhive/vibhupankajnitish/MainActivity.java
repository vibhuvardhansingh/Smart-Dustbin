package info.andriodhive.vibhupankajnitish;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    private Boolean dustbinAtRamanujan = false;
    private Boolean dustbinAtAryabhatta = false;
    private Boolean dustbinAtG11 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button location = (Button)findViewById(R.id.location);
        ToggleButton toggleButton1 = (ToggleButton)findViewById(R.id.toggle);
        ToggleButton toggleButton2 = (ToggleButton)findViewById(R.id.toggle1);
        ToggleButton toggleButton3 = (ToggleButton)findViewById(R.id.toggle2);
        location.setBackgroundColor(getResources().getColor(R.color.colorRed));
        toggleButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    dustbinAtRamanujan = true;
                }
                else dustbinAtRamanujan = false;
            }
        });
        toggleButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    dustbinAtAryabhatta = true;
                }
                else dustbinAtAryabhatta = false;
            }
        });
        toggleButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    dustbinAtG11 = true;
                }
                else dustbinAtG11 = false;
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,MapsActivity.class);
                i.putExtra("data",dustbinAtRamanujan);
                i.putExtra("dataOne",dustbinAtAryabhatta);
                i.putExtra("dataTwo",dustbinAtG11);
                startActivity(i);
//                startActivity(new Intent(getApplicationContext(),MapsActivity.class));
            }
        });
    }
}
