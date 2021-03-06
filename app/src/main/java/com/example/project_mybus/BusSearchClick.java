package com.example.project_mybus;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class BusSearchClick extends AppCompatActivity implements View.OnClickListener {

    String serviceKey = "%2FnU0vVe9yEqaJ2vRtCPpJZHv%2Bef81aaG8G2pMXgYpYhJGqpcVzsFP2pqQ62JPlcfY54It2FZeXgN3p8nItuu9Q%3D%3D";
    int cityCode;
    int pageNo = 1;
    int numOfRows = 200;
    String routeId;
    Document doc;
    Document doc2;
    int idc;
    LinearLayout dynamicLayout;
    LinearLayout dynamicHori;
    String[] busNodeidList;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_search_click);
        overridePendingTransition(0, 0);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView busnum = (TextView)findViewById(R.id.busnum);

        Intent it = getIntent();
        routeId = it.getStringExtra("it_busid");
        String str_busnum = it.getStringExtra("it_busno");
        cityCode = it.getIntExtra("it_citycode", 0);

        busnum.setText(str_busnum);

        new GetXMLTask2().execute(); // 현재 버스 위치
        new GetXMLTask().execute();
    }

    private class GetXMLTask extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL("http://openapi.tago.go.kr/openapi/service/BusRouteInfoInqireService/getRouteAcctoThrghSttnList"+
                        "?serviceKey=" + serviceKey +
                        "&numOfRows=" + numOfRows +
                        "&pageNo=" + pageNo +
                        "&cityCode="+ cityCode +
                        "&routeId=" + routeId);
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
            boolean isBus;

            for(int i = 0; i< nodeList.getLength(); i++){
                String s1 = "";
                String s2 = "";
                String nodeId = "";
                isBus = false;

                Node node = nodeList.item(i);
                Element fstElmnt = (Element) node;

                NodeList nodenm = fstElmnt.getElementsByTagName("nodenm");
                s1 = nodenm.item(0).getChildNodes().item(0).getNodeValue();

                NodeList nodeid = fstElmnt.getElementsByTagName("nodeid");
                nodeId = nodeid.item(0).getChildNodes().item(0).getNodeValue();
                s2 = nodeid.item(0).getChildNodes().item(0).getNodeValue();
                for(int j = 0; j < busNodeidList.length; ++j) {
                    if(s2.equals(busNodeidList[j])) isBus = true;
                }

                idc++;
                AddText(s1, idc, isBus, nodeId);
            }

            super.onPostExecute(doc);
        }
    }

    private class GetXMLTask2 extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL("http://openapi.tago.go.kr/openapi/service/BusLcInfoInqireService/getRouteAcctoBusLcList"+
                        "?serviceKey=" + serviceKey +
                        "&cityCode="+ cityCode +
                        "&routeId=" + routeId);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc2 = db.parse(new InputSource(url.openStream()));
                doc2.getDocumentElement().normalize();

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }
            return doc2;
        }

        @Override
        protected void onPostExecute(Document doc2) {
            NodeList nodeList = doc2.getElementsByTagName("item");
            busNodeidList = new String[nodeList.getLength()];
            for(int i = 0; i< nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                Element fstElmnt = (Element) node;

                NodeList nodeid = fstElmnt.getElementsByTagName("nodeid");
                busNodeidList[i] = nodeid.item(0).getChildNodes().item(0).getNodeValue();
            }

            super.onPostExecute(doc2);
        }
    }

    public void AddText(String s1, int id, boolean isBus, String nodeId) {
        dynamicLayout = (LinearLayout)findViewById(R.id.dynamicLayout);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 2.0f);
        param.width = MATCH_PARENT;
        param.height = 150;
        param.leftMargin = 50;
        param.rightMargin = 50;
        param.topMargin = 40;
        param.bottomMargin = 10;
        TextView stopbus_tv = new TextView(this);
        ImageView plusicon_v = new ImageView(this);
        stopbus_tv.setText(s1);
        stopbus_tv.setTextSize(25);
        stopbus_tv.setEllipsize(TextUtils.TruncateAt.END);
        stopbus_tv.setSingleLine(true);
        plusicon_v.setImageResource(R.drawable.plus_icon);
        plusicon_v.setAdjustViewBounds(true);
        plusicon_v.setScaleType(ImageView.ScaleType.FIT_CENTER);
        plusicon_v.setPadding(60, 30, 0, 30);
        plusicon_v.setId(id);
        plusicon_v.setTag(nodeId);
        plusicon_v.setOnClickListener(this);
        LinearLayout layout_tv = new LinearLayout(this);
        layout_tv.setOrientation(LinearLayout.VERTICAL);
        layout_tv.setPadding(50, 30,0,0);
        layout_tv.setGravity(Gravity.CENTER);
        layout_tv.addView(stopbus_tv);
        dynamicHori = new LinearLayout(this);
        dynamicHori.setBackgroundResource(R.drawable.search_menu_shape);
        dynamicHori.setLayoutParams(param);
        dynamicHori.addView(plusicon_v);
        dynamicHori.addView(layout_tv);
        if(isBus == true) {
            stopbus_tv.setTextColor(Color.RED);
            ImageView busticon_v = new ImageView(this);
            busticon_v.setImageResource(R.drawable.bus_icon3);
            busticon_v.setAdjustViewBounds(true);
            busticon_v.setScaleType(ImageView.ScaleType.FIT_CENTER);
            busticon_v.setPadding(30, 30, 0, 30);
            dynamicHori.addView(busticon_v);
        }
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

        for(int j = 1; j <= idc; j++){
            if(id == j){

                Intent it = new Intent(this, AddBookMark.class);
                it.putExtra("cityCode", cityCode);
                it.putExtra("routeId", routeId);
                startActivity(it);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(0,0);
    }

    public void Clickref (View v) {
        dynamicLayout = (LinearLayout)findViewById(R.id.dynamicLayout);
        dynamicLayout.removeAllViews();
        new GetXMLTask2().execute(); // 현재 버스 위치
        new GetXMLTask().execute();
    }
}