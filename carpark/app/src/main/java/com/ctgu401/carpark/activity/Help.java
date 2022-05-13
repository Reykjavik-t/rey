package com.ctgu401.carpark.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.ctgu401.carpark.MainActivity;
import com.ctgu401.carpark.R;

public class Help extends AppCompatActivity implements View.OnClickListener
{
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.getWindow().setBackgroundDrawableResource(R.drawable.background);
        setContentView(R.layout.activity_help);
        init();
    }

    private void init()
    {
        back = findViewById(R.id.back_main);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        Intent intent = new Intent(Help.this,MainActivity.class);
        startActivity(intent);
    }

}
