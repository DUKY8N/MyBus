package com.example.project_mybus;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class BusStopClick extends AppCompatActivity implements View.OnClickListener {

    String serviceKey = "%2FnU0vVe9yEqaJ2vRtCPpJZHv%2Bef81aaG8G2pMXgYpYhJGqpcVzsFP2pqQ62JPlcfY54It2FZeXgN3p8nItuu9Q%3D%3D";
    int cityCode;
    String nodeId;
    Document doc;
    int idc;
    LinearLayout dynamicLayout;
    LinearLayout dynamicHori;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_stop_click);
        overridePendingTransition(0, 0);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView stopnm = (TextView)findViewById(R.id.busstopnm);

        Intent it = getIntent();
        nodeId = it.getStringExtra("it_stopid");
        String str_stopnm = it.getStringExtra("it_stopnm");
        cityCode = it.getIntExtra("it_citycode", 0);

        stopnm.setEllipsize(TextUtils.TruncateAt.END);
        stopnm.setSingleLine(true);
        stopnm.setText(str_stopnm);

        new GetXMLTask().execute();
    }

    private class GetXMLTask extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL("http://openapi.tago.go.kr/openapi/service/ArvlInfoInqireService/getSttnAcctoArvlPrearngeInfoList"+
                        "?serviceKey=" + serviceKey +
                        "&cityCode="+ cityCode +
                        "&nodeId=" + nodeId);
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

            for(int i = 0; i< nodeList.getLength(); i++){
                String s1 = "";
                String s2 = "";

                Node node = nodeList.item(i);
                Element fstElmnt = (Element) node;

                if(cityCode == 37020 || cityCode == 31230){
                    NodeList routeno = fstElmnt.getElementsByTagName("routeno");
                    s1 = routeno.item(0).getChildNodes().item(0).getNodeValue() + "번 버스";
                }
                else{
                    NodeList routeno = fstElmnt.getElementsByTagName("routeno");
                    s1 = routeno.item(0).getChildNodes().item(0).getNodeValue();
                }

                NodeList arrprevstationcnt = fstElmnt.getElementsByTagName("arrprevstationcnt");
                s2 = "(" + arrprevstationcnt.item(0).getChildNodes().item(0).getNodeValue() + "정거장 남음)";

                idc++;
                AddText(s1, s2, idc);
            }
            nullText();

            super.onPostExecute(doc);
        }
    }

    public void nullText(){
        if(idc == 0){
            dynamicLayout = (LinearLayout)findViewById(R.id.dynamicLayout);
            TextView null_tv = new TextView(this);
            null_tv.setText("현재 버스가 없습니다.");
            dynamicLayout.addView(null_tv);
        }
    }

    public void AddText(String s1, String s2, int id) {
        dynamicLayout = (LinearLayout)findViewById(R.id.dynamicLayout);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 2.0f);
        param.width = MATCH_PARENT;
        param.height = 200;
        param.leftMargin = 50;
        param.rightMargin = 50;
        param.topMargin = 40;
        param.bottomMargin = 10;
        TextView busnum_tv = new TextView(this);
        TextView busarcnt_tv = new TextView(this);
        ImageView plusicon_v = new ImageView(this);
        busnum_tv.setText(s1);
        busnum_tv.setTextSize(25);
        busnum_tv.setEllipsize(TextUtils.TruncateAt.END);
        busnum_tv.setSingleLine(true);
        busarcnt_tv.setText(s2);
        busarcnt_tv.setTextColor(Color.RED);
        plusicon_v.setImageResource(R.drawable.plus_icon);
        plusicon_v.setAdjustViewBounds(true);
        plusicon_v.setScaleType(ImageView.ScaleType.FIT_CENTER);
        plusicon_v.setPadding(60, 30, 0, 30);
        plusicon_v.setId(id);
        plusicon_v.setOnClickListener(this);
        LinearLayout layout_tv = new LinearLayout(this);
        layout_tv.setOrientation(LinearLayout.VERTICAL);
        layout_tv.setPadding(50, 30,0,0);
        layout_tv.setGravity(Gravity.CENTER);
        layout_tv.addView(busnum_tv);
        layout_tv.addView(busarcnt_tv);
        dynamicHori = new LinearLayout(this);
        dynamicHori.setBackgroundResource(R.drawable.search_menu_shape);
        dynamicHori.setLayoutParams(param);
        //dynamicHori.addView(plusicon_v);
        dynamicHori.addView(layout_tv);
        dynamicLayout.addView(dynamicHori);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();


    }

    public void Clickref (View v) {
        dynamicLayout = (LinearLayout)findViewById(R.id.dynamicLayout);
        dynamicLayout.removeAllViews();
        new GetXMLTask().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(0,0);
    }
}