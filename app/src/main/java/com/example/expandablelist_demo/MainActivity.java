package com.example.expandablelist_demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity {

    private final String CBC_SPORTS = "https://www.cbc.ca/cmlink/rss-sports-nhl";
    private ArrayList<HockeyNews> hockeyList;
    private ListView listView;
    private Adapter adapter;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Async myAsyncTask = new Async();
        myAsyncTask.execute();

    }

    public static class ExpandableListDataPump {
        private static ArrayList<HockeyNews> items;
        private static String[] titles, authors, links;

        public void setValues(ArrayList<HockeyNews> objects ) {
            this.items = objects;
        }
        public static HashMap<String, List<String>> getData(){
            HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();
            titles = new String[items.size()];
            authors = new String[items.size()];
            links = new String[items.size()];

            // fill the arrays with the data from (above) the passed objects
            for(int i = 0 ; i < items.size() ; i++){
                HockeyNews o = items.get(i);
                titles[i] = o.getTitle();
                authors[i] = o.getAuthor();
                links[i] = o.getUrl();
            }

            // lets only do the first 3 because its a demo
            List<String> first = new ArrayList<String>();
            List<String> second = new ArrayList<String>();
            List<String> third = new ArrayList<String>();

            first.add(titles[0]);
            second.add(titles[1]);
            third.add(titles[2]);

            first.add(authors[0]);
            second.add(authors[1]);
            third.add(authors[2]);

            first.add(links[0]);
            second.add(links[1]);
            third.add(links[2]);

            expandableListDetail.put("THIS IS THE NAME", first);
            expandableListDetail.put(titles[1], second);
            expandableListDetail.put(titles[2], third);
            return expandableListDetail;
        }
    }


    // Adapter
    public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
        private Context context;
        private List<String> expandableListTitle;
        private HashMap<String, List<String>> expandableListDetail;

        public CustomExpandableListAdapter(Context context, List<String> expandableListTitle,
                                           HashMap<String, List<String>> expandableListDetail) {
            this.context = context;
            this.expandableListTitle = expandableListTitle;
            this.expandableListDetail = expandableListDetail;
        }

        @Override
        public Object getChild(int listPosition, int expandedListPosition) {
            return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                    .get(expandedListPosition);
        }

        @Override
        public long getChildId(int listPosition, int expandedListPosition) {
            return expandedListPosition;
        }

        @Override
        public View getChildView(int listPosition, final int expandedListPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            final String expandedListText = (String) getChild(listPosition, expandedListPosition);
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_item, null);
            }
            TextView expandedListTextView = (TextView) convertView
                    .findViewById(R.id.expandedListItem);
            expandedListTextView.setText(expandedListText);
            return convertView;
        }

        @Override
        public int getChildrenCount(int listPosition) {
            return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                    .size();
        }

        @Override
        public Object getGroup(int listPosition) {
            return this.expandableListTitle.get(listPosition);
        }

        @Override
        public int getGroupCount() {
            return this.expandableListTitle.size();
        }

        @Override
        public long getGroupId(int listPosition) {
            return listPosition;
        }

        @Override
        public View getGroupView(int listPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String listTitle = (String) getGroup(listPosition);
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_group, null);
            }
            TextView listTitleTextView = (TextView) convertView
                    .findViewById(R.id.listTitle);
            listTitleTextView.setTypeface(null, Typeface.BOLD);
            listTitleTextView.setText(listTitle);
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int listPosition, int expandedListPosition) {
            return true;
        }
    }


    class Async extends AsyncTask {
        @Override
        protected void onPostExecute(Object o) {
            ExpandableListDataPump pump = new ExpandableListDataPump();
            pump.setValues(hockeyList);

            expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
            expandableListDetail = ExpandableListDataPump.getData();
            expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
            expandableListAdapter = new CustomExpandableListAdapter(MainActivity.this, expandableListTitle, expandableListDetail);
            expandableListView.setAdapter(expandableListAdapter);

            // Expanding the list
            expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(int groupPosition) {
                  // do something on expand
                }
            });

            // Collapsing the list
            expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                @Override
                public void onGroupCollapse(int groupPosition) {
                   // do something on collapse
                }
            });

            // Clicking on the child element
            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    // do something on click
                    return false;
                }
            });
        }

        // EVERYTHING BELOW HERE IS OLD CODE... PULLED FROM RSS ASSIGNMENT ***********************************





        @Override
        protected Object doInBackground(Object[] objects) {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser saxParser = null;
            URL url = null;
            InputStream inputStream = null;
            HockeyHandler handler = new HockeyHandler();

            // Parsing
            try{
                saxParser = spf.newSAXParser();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            try{
                url = new URL(CBC_SPORTS);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try{
                inputStream = url.openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try{
                saxParser.parse(inputStream, handler);
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    // Handler to get the data
    class HockeyHandler extends DefaultHandler {
        //private ArrayList<HockeyNews> hockeyList;
        private StringBuilder stringBuilder;
        private boolean inItem, inTitle, inDescription, inPubDate, inAuthor, inURL;
        private HockeyNews record;
        {
            hockeyList = new ArrayList<HockeyNews>(20);
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            Log.d("Trevor" , "End of Doc, : " + hockeyList.size());
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if(qName.equalsIgnoreCase("item")){
                inItem = true;
                record = new HockeyNews();
            } else if(qName.equalsIgnoreCase("title")){
                inTitle = true;
                stringBuilder = new StringBuilder(40);
            } else if(qName.equalsIgnoreCase("description")){
                inDescription = true;
                stringBuilder = new StringBuilder(40);
            } else if(qName.equalsIgnoreCase("PubDate")){
                inPubDate = true;
                stringBuilder = new StringBuilder(40);
            } else if(qName.equalsIgnoreCase("Author")){
                inAuthor = true;
                stringBuilder = new StringBuilder(40);
            } else if(qName.equalsIgnoreCase("link")){
                inURL = true;
                stringBuilder = new StringBuilder(40);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);

            if(qName.equalsIgnoreCase("item")){
                inItem = false;
                hockeyList.add(record);
            }
            if(inItem){
                if(qName.equalsIgnoreCase("title")){
                    inTitle = false;
                    record.title = stringBuilder.toString();
                } else if(qName.equalsIgnoreCase("description")){
                    inDescription = false;
                    record.description = stringBuilder.toString();
                } else if(qName.equalsIgnoreCase("PubDate")){
                    inPubDate = false;
                    record.pubDate = stringBuilder.toString();
                } else if(qName.equalsIgnoreCase("Author")){
                    inAuthor = false;
                    record.author = stringBuilder.toString();
                } else if(qName.equalsIgnoreCase("link")){
                    inURL = false;
                    record.url = stringBuilder.toString();
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            if(inItem){
                if (inTitle) {
                    stringBuilder.append(ch, start, length);
                } if(inDescription){
                    stringBuilder.append(ch,start,length);
                } if(inAuthor){
                    stringBuilder.append(ch,start,length);
                } if(inPubDate){
                    stringBuilder.append(ch, start, length);
                } if(inURL){
                    stringBuilder.append(ch, start, length);
                }
            }
        }
    }

    // For array list
    class HockeyNews {
        private String title;
        private String url;
        private String description;
        private String pubDate;
        private String author;

        public void HockeyNews(String title, String url, String description, String pubDate, String author){
            this.title = title;
            this.url = url;
            this.description = description;
            this.pubDate = pubDate;
            this.author = author;
        }

        public String getTitle(){
            return title;
        }
        public String getUrl(){
            return url;
        }
        public String getDescription(){
            return description;
        }
        public String getPubDate(){return pubDate;}
        public String getAuthor(){return author;}
    }

}
