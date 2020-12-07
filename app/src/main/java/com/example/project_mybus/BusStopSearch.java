package com.example.project_mybus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class BusStopSearch extends AppCompatActivity implements View.OnClickListener {

    String serviceKey = "%2FnU0vVe9yEqaJ2vRtCPpJZHv%2Bef81aaG8G2pMXgYpYhJGqpcVzsFP2pqQ62JPlcfY54It2FZeXgN3p8nItuu9Q%3D%3D";
    int cityCode;
    String nodeNm;
    String nodeNo;
    boolean isNum;
    int numOfRows = 30;
    int pageNo = 1;

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2, fab3;

    Document doc;
    LinearLayout dynamicLayout;
    LinearLayout dynamicHori;
    String[] bstop_nm;
    String bstop_no;
    String[] bstop_id;
    int count;
    int idc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_stop_search);
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

        EditText edt = (EditText) findViewById(R.id.search);
        edt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        EditText searchEditText = (EditText)findViewById(R.id.search);
                        if (isDigit(searchEditText.getText().toString())) {
                            nodeNo = searchEditText.getText().toString();
                            isNum = true;
                        } else {
                            nodeNm = searchEditText.getText().toString();
                            isNum = false;
                        }
                        dynamicLayout = (LinearLayout)findViewById(R.id.dynamicLayout);
                        dynamicLayout.removeAllViews();
                        new GetXMLTask().execute();
                        Toast.makeText(getApplicationContext(), "로딩 중...", Toast.LENGTH_LONG).show();
                        break;
                }
                return true;
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    cityCode = 37020;
                }
                else if(position == 1) {
                    cityCode = 31230;
                }
                else if(position == 2) {
                    cityCode = 26;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                cityCode = 37020;
            }
        });
    }

    private class GetXMLTask extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                if(isNum) {
                    url = new URL("http://openapi.tago.go.kr/openapi/service/BusSttnInfoInqireService/getSttnNoList"+
                            "?serviceKey=" + serviceKey +
                            "&cityCode="+ cityCode +
                            "&nodeNo=" + nodeNo +
                            "&numOfRows" + numOfRows +
                            "&pageNo" + pageNo);

                } else {
                        url = new URL("http://openapi.tago.go.kr/openapi/service/BusSttnInfoInqireService/getSttnNoList"+
                            "?serviceKey=" + serviceKey +
                            "&cityCode="+ cityCode +
                            "&nodeNm=" + nodeNm +
                            "&numOfRows" + numOfRows +
                            "&pageNo" + pageNo);
                }
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {

            NodeList nodeList = doc.getElementsByTagName("item");
            idc = 0;
            bstop_nm = new String[nodeList.getLength()];
            bstop_id = new String[nodeList.getLength()];

            for(int i = 0; i< nodeList.getLength(); i++){

                Node node = nodeList.item(i);
                Element fstElmnt = (Element) node;

                NodeList nodenm = fstElmnt.getElementsByTagName("nodenm");
                bstop_nm[i] = nodenm.item(0).getChildNodes().item(0).getNodeValue();
                NodeList nodeno = fstElmnt.getElementsByTagName("nodeno");
                bstop_no = "(" + nodeno.item(0).getChildNodes().item(0).getNodeValue() + ")";
                NodeList nodeid = fstElmnt.getElementsByTagName("nodeid");
                bstop_id[i] = nodeid.item(0).getChildNodes().item(0).getNodeValue();

                idc++;
                AddText(bstop_nm[i], bstop_no, idc);
            }

            super.onPostExecute(doc);
        }
    }

    public void AddText(String s1, String s2, int id) {
        dynamicLayout = (LinearLayout)findViewById(R.id.dynamicLayout);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 2.0f);
        param.width = MATCH_PARENT;
        param.height = 250;
        param.leftMargin = 50;
        param.rightMargin = 50;
        param.topMargin = 40;
        param.bottomMargin = 10;
        TextView stopnm_tv = new TextView(this);
        TextView stopnum_tv = new TextView(this);
        ImageView stopicon_v = new ImageView(this);
        stopnm_tv.setText(s1);
        stopnm_tv.setTextSize(25);
        stopnm_tv.setEllipsize(TextUtils.TruncateAt.END);
        stopnm_tv.setSingleLine(true);
        stopnum_tv.setText(s2);
        stopnum_tv.setTextColor(Color.RED);
        stopicon_v.setImageResource(R.drawable.bus_stop_icon2);
        stopicon_v.setAdjustViewBounds(true);
        stopicon_v.setScaleType(ImageView.ScaleType.FIT_CENTER);
        stopicon_v.setPadding(60, 0, 0, 0);
        LinearLayout layout_tv = new LinearLayout(this);
        layout_tv.setOrientation(LinearLayout.VERTICAL);
        layout_tv.setPadding(50, 40,0,0);
        layout_tv.setGravity(Gravity.CENTER);
        layout_tv.addView(stopnm_tv);
        layout_tv.addView(stopnum_tv);
        dynamicHori = new LinearLayout(this);
        dynamicHori.setBackgroundResource(R.drawable.search_menu_shape);
        dynamicHori.setLayoutParams(param);
        dynamicHori.addView(stopicon_v);
        dynamicHori.addView(layout_tv);
        dynamicHori.setId(id);
        dynamicHori.setOnClickListener(this);
        dynamicLayout.addView(dynamicHori);
    }


    public boolean isDigit (String input) {

        char tmp;
        boolean output = true;    // 결과값을 저장할 변수, 참/거짓밖에 없기 때문에 boolean으로 선언

        for (int i = 0; i < input.length(); i++) {    //입력받은 문자열인 input의 길이만큼 반복문 진행(배열이 아닌 문자열의 길이기 때문에 length가 아닌 length()를 사용해야한다.)
            tmp = input.charAt(i);    //한글자씩 검사하기 위해서 char형 변수인 tmp에 임시저장

            if (Character.isDigit(tmp) == false) {    //문자열이 숫자가 아닐 경우
                output = false;    //output의 값을 false로 바꿈
            }
        }

        return output;
    }

    public void SearchBusStop (View v) {
        EditText searchEditText = (EditText)findViewById(R.id.search);
        if (isDigit(searchEditText.getText().toString()) == true) {
            nodeNo = searchEditText.getText().toString();
            isNum = true;
        } else {
            nodeNm = searchEditText.getText().toString();
            isNum = false;
        }
        dynamicLayout = (LinearLayout)findViewById(R.id.dynamicLayout);
        dynamicLayout.removeAllViews();
        new GetXMLTask().execute();
        Toast.makeText(getApplicationContext(), "로딩 중...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                anim();
                break;
            case R.id.fab1:
                anim();
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                break;
            case R.id.fab2:
                anim();
                Intent intent2 = new Intent(this, BusSearch.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent2);
                finish();
                break;
            case R.id.fab3:
                anim();
                Toast.makeText(getApplicationContext(), "현재화면 입니다.", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(event.getAction() == KeyEvent.ACTION_DOWN) {
            if(keyCode == KeyEvent.KEYCODE_BACK) {
                Toast.makeText(this, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
                count++;
                if(count > 1) {
                    finish();
                }
            }
        }
        return true;
    }
}