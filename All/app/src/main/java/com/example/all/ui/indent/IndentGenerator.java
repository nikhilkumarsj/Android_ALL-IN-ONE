package com.example.all.ui.indent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.all.R;

import java.util.Objects;

public class IndentGenerator extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indent_generatior);
          String DateValue= getIntent().getStringExtra("DateValue");
          String HsdValue= getIntent().getStringExtra("HsdValue");
          String HsdAmountValue= getIntent().getStringExtra("HsdAmountValue");
          String VehicleNumberValue= getIntent().getStringExtra("VehicleNumberValue");
          String BunkNameValue= getIntent().getStringExtra("BunkNameValue");
          String DriverNameValue= getIntent().getStringExtra("DriverNameValue");
          String IssuedByValue= getIntent().getStringExtra("IssuedByValue");
          String RemarksValue= getIntent().getStringExtra("RemarksValue");
          String BunkCashValue= getIntent().getStringExtra("BunkCashValue");
        String IndentValue= getIntent().getStringExtra("IndentValue");
        String UniqueID = getIntent().getStringExtra("UniqueID");
        TextView Datevalue = findViewById(R.id.Datevalue);
        Datevalue.setText(DateValue);
        TextView Indentvalue = findViewById(R.id.Indentvalue);
        Indentvalue.setText(IndentValue);
        TextView Unique = findViewById(R.id.Uniquevalue);
        Unique.setText(UniqueID);
        TextView Dieselvalue = findViewById(R.id.Dieselvalue);
        TextView Inwordsvalue = findViewById(R.id.Inwordsvalue);
        if (!Objects.equals(HsdValue, null)){
            Dieselvalue.setText(HsdValue+" liters");
            Inwordsvalue.setText("");

        }else{
            Dieselvalue.setText(HsdAmountValue+" Rs");
            Inwordsvalue.setText(EnglishNumberToWords.convert(Long.parseLong(HsdAmountValue)).toUpperCase()+" Rupees");

        }

        TextView VehicleNOvalue = findViewById(R.id.VehicleNOvalue);
        VehicleNOvalue.setText(VehicleNumberValue);
        TextView Bunkvalue = findViewById(R.id.Bunkvalue);
        Bunkvalue.setText(BunkNameValue);
        TextView Drivervalue = findViewById(R.id.Drivervalue);
        Drivervalue.setText(DriverNameValue);
        TextView IssuedByvalue = findViewById(R.id.IssuedByvalue);
        IssuedByvalue.setText(IssuedByValue);
        TextView Remarksvalue = findViewById(R.id.Remarksvalue);
        Remarksvalue.setText(RemarksValue);
        TextView BunkCashvalue = findViewById(R.id.BunkCashvalue);
        BunkCashvalue.setText(BunkCashValue+" Rs");

        Button Close = findViewById(R.id.Back);

        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndentGenerator.this, IndentFragment.class);

                // Start the MainActivity
                startActivity(intent);

                // Finish the current activity to remove it from the back stack
                finish();
            }
        });
    }

}