package com.example.ourprojecttest;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(LoginActivity.this,"账号或密码错误",Toast.LENGTH_SHORT).show();
                    default:
                        break;
            }
        }
    };



    RadioButton radioButton_doc,radioButton_stu;

    EditText userName,passWord;
    private boolean isHide=true;
    private ImageView imageView;
    Drawable drawableEyeOpen,drawableEyeClose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();//隐藏actionBar
        setContentView(R.layout.activity_login);

        //获取radiobutton
        radioButton_doc = findViewById(R.id.doctor);
        radioButton_stu = findViewById(R.id.student);

        //获取文本框
        userName = (EditText) findViewById(R.id.user_name);
        passWord = (EditText) findViewById(R.id.user_psw);

        //获取眼睛图片资源
        drawableEyeClose = getResources().getDrawable(R.drawable.biyan);
        drawableEyeOpen = getResources().getDrawable(R.drawable.zhengyan);

        //注册叉号的点击事件
        userName.setOnTouchListener(new View.OnTouchListener() {
            Drawable drawable = userName.getCompoundDrawables()[2];

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                //获取点击焦点
                if (event.getX() > userName.getWidth() - userName.getPaddingRight() - drawable.getIntrinsicWidth()) {
                    //其他活动无响应
                    if (event.getAction() != MotionEvent.ACTION_UP)
                        return false;
                    //清空用户名
                    userName.setText("");
                }
                return false;
            }
        });

        //注册小眼睛的点击事件
        passWord.setOnTouchListener(new View.OnTouchListener() {

            final Drawable[] drawables = passWord.getCompoundDrawables();//获取密码框的drawable数组
            final int eyeWidth = drawables[2].getBounds().width();// 眼睛图标的宽度

            Drawable drawable = passWord.getCompoundDrawables()[2];

            public boolean onTouch(View view, MotionEvent event) {
                if (event.getX() > passWord.getWidth() - passWord.getPaddingRight() - drawable.getIntrinsicWidth()) {
                    if (event.getAction() != MotionEvent.ACTION_UP)
                        return false;

                    //如果当前密码框是密文
                    if (isHide) {
                        drawableEyeOpen.setBounds(drawables[2].getBounds());//设置睁开眼睛的界限

                        passWord.setCompoundDrawables(drawables[0], null, drawableEyeOpen, null);
                        Log.d("loginfalse", String.valueOf(isHide));
                        passWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        isHide = false;
                    }
                    //如果当前密码框是明文
                    else {
                        drawableEyeClose.setBounds(drawables[2].getBounds());//设置闭眼的界限
                        passWord.setCompoundDrawables(drawables[0], null, drawableEyeClose, null);

                        Log.d("logintrue", String.valueOf(isHide));
                        passWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        isHide = true;
                    }

                }
                return false;
            }
        });

        //登陆
        Button mEmailSignInButton = (Button) findViewById(R.id.btn_login);//找到按钮
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        TextView userName = (findViewById(R.id.user_name));
                        TextView userPass = findViewById(R.id.user_psw);
                        String name = userName.getText().toString().trim();
                        String pass = userPass.getText().toString().trim();
                        String url="http://139.196.103.219:8080/IM1/servlet/Login?no=" + name + "&pwd=" + pass;

                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(url)
                                .build();
                        try {

                            Response response = client.newCall(request).execute();

                            String responseData = response.body().string();

                            parseJSONWithJSONObject(responseData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
        });


    }

    private void parseJSONWithJSONObject(String jsonData){
        try{

            JSONObject jsonObject=new JSONObject(jsonData);
            String code=jsonObject.getString("code");
            Message msg = Message.obtain();

            if(code.equals("0")){
                msg.what = 0;

            }
            else{
                msg.what =-1;
            }
            handler.sendMessage(msg);

        }catch (Exception e){
            e.printStackTrace();
        }
    }







}
