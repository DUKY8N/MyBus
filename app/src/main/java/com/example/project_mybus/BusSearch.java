package com.example.project_mybus;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class BusSearch extends AppCompatActivity implements View.OnClickListener {

    String serviceKey = "%2FnU0vVe9yEqaJ2vRtCPpJZHv%2Bef81aaG8G2pMXgYpYhJGqpcVzsFP2pqQ62JPlcfY54It2FZeXgN3p8nItuu9Q%3D%3D";
    int cityCode;
    int routeNo;

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2, fab3;

    Document doc;
    LinearLayout dynamicLayout;
    LinearLayout dynamicHori;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_search);
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
                        routeNo = Integer.parseInt(searchEditText.getText().toString());
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
                url = new URL("http://openapi.tago.go.kr/openapi/service/BusRouteInfoInqireService/getRouteNoList"+
                        "?serviceKey=" + serviceKey +
                        "&cityCode="+ cityCode +
                        "&routeNo=" + routeNo);
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

            for(int i = 0; i< nodeList.getLength(); i++){
                String s = "";

                Node node = nodeList.item(i);
                Element fstElmnt = (Element) node;

                NodeList routeno = fstElmnt.getElementsByTagName("routeno");
                s += "버스번호: "+ routeno.item(0).getChildNodes().item(0).getNodeValue() +"\n";
                NodeList routetp = fstElmnt.getElementsByTagName("routetp");
                s += "버스유형: "+ routetp.item(0).getChildNodes().item(0).getNodeValue() +"\n";

                AddText(s);
            }

            super.onPostExecute(doc);
        }
    }

    public void AddText(String s) {
        dynamicLayout = (LinearLayout)findViewById(R.id.dynamicLayout);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 2.0f);
        param.width = MATCH_PARENT;
        param.height = 250;
        param.leftMargin = 50;
        param.rightMargin = 50;
        param.topMargin = 40;
        param.bottomMargin = 10;
        TextView busnum_tv = new TextView(this);
        TextView bustype_tv = new TextView(this);
        ImageView busicon_v = new ImageView(this);
        busnum_tv.setText(s);
        busnum_tv.setPadding(30, 30, 30 ,30);
        busicon_v.setImageResource(R.drawable.bus_icon2);
        dynamicHori = new LinearLayout(this);
        dynamicHori.setBackgroundResource(R.drawable.search_menu_shape);
        dynamicHori.setLayoutParams(param);
        dynamicHori.addView(busnum_tv);
        dynamicHori.addView(bustype_tv);
        dynamicHori.addView(busicon_v);
        dynamicLayout.addView(dynamicHori);
    }

    public void SearchBus (View v) {
        EditText searchEditText = (EditText)findViewById(R.id.search);
        routeNo = Integer.parseInt(searchEditText.getText().toString());
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
                Toast.makeText(getApplicationContext(), "현재화면 입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fab3:
                anim();
                Intent intent3 = new Intent(this, BusStopSearch.class);
                intent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent3);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(event.getAction() == KeyEvent.ACTION_DOWN) {
            if(keyCode == KeyEvent.KEYCODE_BACK) {
                Toast.makeText(this, "System back 버튼 눌림", Toast.LENGTH_SHORT).show();
                count++;
                if(count > 1) {
                    finish();
                }
            }
        }
        return true;
    }
}