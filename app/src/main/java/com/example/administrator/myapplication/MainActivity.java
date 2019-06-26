package com.example.administrator.myapplication;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private ClickVerifyCode cf;
    private Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);
        cf = findViewById(R.id.cf);
        bt = findViewById(R.id.bt);

        //设置静态资源背景图
        cf.setBackground(ContextCompat.getDrawable(this,R.drawable.douyu));

        String str = "请点选   " + cf.getVerifyText();
        tv.setText(str);

        cf.setOnVerifyListener(new ClickVerifyCode.VerifyListener() {
            @Override
            public void success() {
                Toast.makeText(MainActivity.this,"验证成功",Toast.LENGTH_LONG).show();
            }

            @Override
            public void fail() {
                Toast.makeText(MainActivity.this,"验证失败",Toast.LENGTH_LONG).show();
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cf.reSet();
                String str = "请点选   " + cf.getVerifyText();
                tv.setText(str);
            }
        });
    }
}
