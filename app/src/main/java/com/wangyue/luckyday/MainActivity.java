package com.wangyue.luckyday;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.ini4j.Ini;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.lineSeparator;


public class MainActivity extends AppCompatActivity implements OnDateSelectedListener, OnMonthChangedListener {

  //  @Bind(R.id.calendarView)
    MaterialCalendarView calendarView;

  //  @Bind(R.id.showDateText)
    TextView showDateText;
    TextView showLucky;
    TextView showUnLucky;
    TextView backToToday;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        calendarView.setOnDateChangedListener(this);
        calendarView.setOnMonthChangedListener(this);

        calendarView.setTileHeightDp(30);
        calendarView.setDateSelected(new Date(),true);
        calendarView.state().edit()
                .setMinimumDate(CalendarDay.from(2009, 1, 1))
                .setMaximumDate(CalendarDay.from(2019, 12, 31))
                .commit();
        //Setup initial text
        showDateText = (TextView) findViewById(R.id.showDateText);
        //showDateText.setText(getSelectedDatesString());


        showLucky = (TextView)findViewById(R.id.showLucky);
        showUnLucky = (TextView)findViewById(R.id.showUnLucky);

        backToToday = (TextView)findViewById(R.id.backToTodayText);



        backToToday.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View arg0) {
                Date now = new Date();
                calendarView.clearSelection();
                calendarView.setDateSelected(now,true);
                calendarView.setCurrentDate(now);

                refreshDesc(now);


            }

        });
        refreshDesc(new Date());
    }


    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
       // showDateText.setText(getSelectedDatesString());
        refreshDesc(date.getDate());
        //Map<String, List<String>> map = getByDate(2017,3,23,"zh_CN");


    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

        getSupportActionBar().setTitle("LuckyDay");
    }


    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG);
    private String getSelectedDatesString() {
        CalendarDay date = calendarView.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        return FORMATTER.format(date.getDate());
    }


    public void refreshDesc(Date date){
        //DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG);
        String dateStr = FORMATTER.format(date);
        showDateText.setText(dateStr);

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        Map<String, List<String>> map = getByDate(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH),"zh_CN");
        String luckyStr = map.get(DO).toString();
        luckyStr = luckyStr.substring(1,luckyStr.length()-1);

        String unLuckyStr = map.get(NOT_DO).toString();
        unLuckyStr = unLuckyStr.substring(1,unLuckyStr.length()-1);

        showLucky.setText(luckyStr);
        showUnLucky.setText(unLuckyStr);


        SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd");
        if(fmt.format(date).toString().equals(fmt.format(new Date()).toString())){
            backToToday.setVisibility(View.INVISIBLE);
        }else {
            backToToday.setVisibility(View.VISIBLE);
        }


    }

    final static String DO = "DO";
    final static String NOT_DO = "NOT_DO";

    /**
     * Core algorithm method
     * @param year
     * @param month
     * @param day
     * @param lang language want to return, e.g. zh_CN, en_US
     * @return a map with two keys("DO", "NOT_DO") and their values(List of Strings in determined language)
     */
    public  Map<String, List<String>> getByDate(Integer year, Integer month, Integer day, String lang) {
        try{
            // obtain related files
            String URI = "data/"+year+"/"+year+"-"+month+"-"+day;

            //File dosFile = new File(URI+"/dos.txt");
            //File donsFile = new File(URI+"/dons.txt");

            InputStream dosIS = getAssets().open(URI+"/dos.txt");
            InputStream donsIS = getAssets().open(URI+"/dons.txt");


            // i18n .ini file
            //Ini ini = new Ini(new File("data/"+lang+".ini"));
            InputStream iniIS = getAssets().open("data/"+lang+".ini");
            Ini ini = new Ini(iniIS);


            // translate to determined language
            List<String> dosList = new ArrayList<String>();
            for(String dos : readfile(dosIS).split(",")) {
                dosList.add(ini.get("i18n", dos.trim()));
            }

            List<String> donsList = new ArrayList<String>();
            for(String dons : readfile(donsIS).split(",")) {
                donsList.add(ini.get("i18n", dons.trim()));
            }

            // put to map
            Map<String, List<String>> result = new HashMap<String, List<String>>();
            result.put(DO, dosList);
            result.put(NOT_DO, donsList);

            return result;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Read file
     * @param is
     * @return
     * @throws IOException
     */
    public static String readfile(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                //sb.append(System.lineSeparator());
                sb.append(lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

}
