package cn.andy.textflickerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cn.andy.textflickerview.view.TextFlickerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextFlickerView textFlickerView = findViewById(R.id.tv);
        textFlickerView.start();
    }
}
