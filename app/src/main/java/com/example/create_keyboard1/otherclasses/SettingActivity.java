
package com.example.create_keyboard1.otherclasses;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;

import com.example.create_keyboard1.R;



public class SettingActivity extends AppCompatActivity {

    public static final String POSITION_AD_PREVIEW ="preview" ;
    public   CheckBox  chkpreview,chkprediction,chkvibrate;
      public CardView   cardbackground,cardenable,cardset,carddisable,customback;

    public static final String PREVIEW_PREF_NAME = "myloginapp";
    public static final String PREVIEW_SHARED_PREF = "groupname";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setTitle("Keyboard Settings");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(SettingActivity.this,
                    R.color.buttondark));
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0099CD")));

        initialize();

        cardbackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),Background.class));

            }
        });
        cardenable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent enableIntent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
                enableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(enableIntent);
            }
        });
        cardset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMydialog();
            }
        });
        carddisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent enableIntent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
                enableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(enableIntent);
            }
        });



        customback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),CamGalleryBackground.class));
            }
        });

        
    }

    private void initialize() {
        cardbackground=findViewById(R.id.t4);
        cardenable=findViewById(R.id.t5);
        cardset=findViewById(R.id.t6);
        carddisable=findViewById(R.id.t7);
        customback=findViewById(R.id.t3);
    }
    private void showMydialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Alert.!")
                .setMessage("Make sure to enable first to see keyboard in the list.If you don't see the keyboard in the list enable it first.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        InputMethodManager imeManager = (InputMethodManager)
                                getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                        imeManager.showInputMethodPicker();
                        finish();
                    }
                })
                .show();
    }


}
