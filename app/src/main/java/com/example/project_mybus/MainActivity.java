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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2, fab3;

    TextView tv;
    String test = "";

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
        String api = "http://openapi.tago.go.kr/openapi/service/ArvlInfoInqireService/getCtyCodeList";
        String key = "%2FnU0vVe9yEqaJ2vRtCPpJZHv%2Bef81aaG8G2pMXgYpYhJGqpcVzsFP2pqQ62JPlcfY54It2FZeXgN3p8nItuu9Q%3D%3D";
        String cityCode = "25";
        String nodeId = "DJB8001793";
//        String url = api + "?serviceKey=" + key + "&cityCode=" + cityCode + "&nodeId=" + nodeId;
        String url = api + "?serviceKey=" + key;

        // API를 이용한 데이터 다운로드 객체
        DownloadWebpageTask task = new DownloadWebpageTask();
        // 데이터 다운로드 및 처리
        task.execute(url);
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
            boolean bSet_itemCode = false;
            boolean bSet_city = false;

            String itemCode = "";
            String pollution_degree = "";
            String tag_name = "";

            int cnt = 0;
            int city_no = 0;

            try {
                // XML Pull Parser 객체 생성
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                // 파싱할 문서 설정
                xpp.setInput(new StringReader(result));

                // 현재 이벤트 유형 반환(START_DOCUMENT, START_TAG, TEXT, END_TAG, END_DOCUMENT
                int eventType = xpp.getEventType();

                // 이벤트 유형이 문서 마지막이 될 때까지 반복
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    // 문서의 시작인 경우
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;

                        // START_TAG이면 태그 이름 확인
                    } else if (eventType == XmlPullParser.START_TAG) {
                        tag_name = xpp.getName();
                        if (bSet_itemCode == false && tag_name.equals("citycode"))
                            bSet_itemCode = true;
                        if (itemCode.equals("군포시") && (tag_name.equals("울산시") || tag_name.equals("busan")))
                            bSet_city = true;

                        // 태그 사이의 문자 확인
                    } else if (eventType == XmlPullParser.TEXT) {
                        if (bSet_itemCode) {
                            itemCode = xpp.getText();

                            if (itemCode.equals("PM10")) {
                                cnt++;
                                bSet_itemCode = false;
                            }
                        }
                        if (bSet_city) {
                            pollution_degree = xpp.getText();

                            // 도시와 미세먼지 농도 화면 출력
                            tv.append("" + cnt + ": " + tag_name + ", " + pollution_degree + "\n");
                            bSet_city = false;
                        }

                        // 마침 태그인 경우
                    } else if (eventType == XmlPullParser.END_TAG) {
                        ;
                    }

                    // 다음 이벤트 유형 할당
                    eventType = xpp.next();
                }
            } catch (Exception e) {
            }
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
                    test += line;
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
