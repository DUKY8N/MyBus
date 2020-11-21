package com.example.project_mybus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2, fab3;

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(0,0);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);

        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);

        tv = (TextView)findViewById(R.id.tv);

        // 대기오염 정보 API
        String api = "http://openapi.tago.go.kr/openapi/service/ArvlInfoInqireService/getSttnAcctoSpcifyRouteBusArvlPrearngeInfoList?serviceKey=%2FnU0vVe9yEqaJ2vRtCPpJZHv%2Bef81aaG8G2pMXgYpYhJGqpcVzsFP2pqQ62JPlcfY54It2FZeXgN3p8nItuu9Q%3D%3D&cityCode=25&nodeId=DJB8001793&routeId=DJB30300002";

        // API를 이용한 데이터 다운로드 객체
        DownloadWebpageTask task = new DownloadWebpageTask();
        // 데이터 다운로드 및 처리
        task.execute(api);
    }

    // 데이터 다운로드 클래스 정의
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        // 문서 다운로드(백그라운드 실행)
        @Override
        protected String doInBackground(String... urls) {
            try {
                // API에 해당하는 문서 다운로드
                String txt =  (String) downloadUrl((String) urls[0]);
                return txt;
            } catch (IOException e) {
                return "다운로드 실패";
            }
        }

        // 문서 다운로드 후 자동 호출: MXL 문서 파싱
        protected void onPostExecute(String result) {
            tv.setText(result);
        }

        // 전달받은 API에 해당하는 문서 다운로드
        private String downloadUrl(String api) throws IOException {
            HttpURLConnection conn = null;
            try {
                // 문서를 읽어 텍스트 단위로 버퍼에 저장
                URL url = new URL(api);
                conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8"));

                // 줄 단위로 읽어 문자로 저장
                String line = null;
                String page = "";
                while ((line = bufreader.readLine()) != null) {
                    page += line;
                }

                // 다운로드 문서 반환
                return page;
            } finally {
                conn.disconnect();
            }
        }
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                anim();
                Toast.makeText(this, "Floating Action Button", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fab1:
                anim();
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                Toast.makeText(this, "Button1", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.fab2:
                anim();
                Intent intent2 = new Intent(this, BusSearch.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent2);
                Toast.makeText(this, "Button2", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.fab3:
                anim();
                Intent intent3 = new Intent(this, BusStopSearch.class);
                intent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent3);
                Toast.makeText(this, "Button3", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    private void anim() {
        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(0,0);
    }
}
