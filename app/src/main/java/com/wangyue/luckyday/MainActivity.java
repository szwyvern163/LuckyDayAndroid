package com.wangyue.luckyday;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    CalendarDay currentShowMonth = CalendarDay.from(new Date());
    DatePickerDialog.OnDateSetListener dateSetListener ;
    DatePickerDialog datePickerDialog;

    View.OnClickListener calendarTitleClicked ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        calendarView.setOnDateChangedListener(this);
        calendarView.setOnMonthChangedListener(this);


        calendarView.setTileHeightDp(28);
        calendarView.setDateSelected(new Date(),true);
        SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date minDate = fmt.parse(minDateStr);
            Date maxDate = fmt.parse(maxDateStr);
            calendarView.state().edit()
            .setMinimumDate(minDate)  //只准备了2016年到2019年的数据
                    .setMaximumDate(maxDate)
                    .commit();
        } catch (ParseException e) {
            e.printStackTrace();
        }


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
                setCalendarViewDate(now);
                refreshDesc(now);


            }

        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Log.d("date selected",year +"  "+month+"  "+dayOfMonth);
                CalendarDay day = CalendarDay.from(year,month,dayOfMonth);
                setCalendarViewDate(day.getDate());
                refreshDesc(day.getDate());
            }
        };


        calendarTitleClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Date selectedDate = calendarView.getSelectedDate().getDate();
                //DatePicker datePicker = new DatePicker();
                datePickerDialog = new DatePickerDialog(
                        //MainActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert, dateSetListener , currentShowMonth.getYear(), currentShowMonth.getMonth(), currentShowMonth.getDay()
                        MainActivity.this, 0, dateSetListener , currentShowMonth.getYear(), currentShowMonth.getMonth(), currentShowMonth.getDay()

                );
                SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date minDate = fmt.parse(minDateStr);
                    Date maxDate = fmt.parse(maxDateStr);
                    datePickerDialog.getDatePicker().setMinDate(minDate.getTime());
                    datePickerDialog.getDatePicker().setMaxDate(maxDate.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                datePickerDialog.show();


            }
        };
        calendarView.setOnTitleClickListener(calendarTitleClicked);



        refreshDesc(new Date());
    }


    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

        refreshDesc(date.getDate());


    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        currentShowMonth = date;
        //getSupportActionBar().setTitle("LuckyDay");
    }





    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG);


    public void setCalendarViewDate(Date date){
        calendarView.clearSelection();
        calendarView.setDateSelected(date,true);
        calendarView.setCurrentDate(date);
    }


    public void refreshDesc(Date date){
        //DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG);
        String dateStr = FORMATTER.format(date);
        showDateText.setText(dateStr);

        Calendar c = Calendar.getInstance();
        c.setTime(date);

       /* Log.d("locale","locale kkk");
        Locale localea = Locale.getDefault();
        Log.d("locale",localea.toString());
       // Log.d(locale.getCountry().toString());
        //locale = getResources().getConfiguration().getLocales().get(0);
        Log.d("locale","run api is "+Build.VERSION.SDK_INT);
        Log.d("locale","build api is "+Build.VERSION_CODES.N);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            LocaleList al = getResources().getConfiguration().getLocales();
            Log.d("locale",al.toString());
            localea = getResources().getConfiguration().getLocales().get(0);
            Log.d("locale",localea.toString());
        }else
        {
            localea = MainActivity.this.getResources().getConfiguration().locale;
        }
        localea = MainActivity.this.getResources().getConfiguration().locale;
        Log.d("locale",localea.getCountry());
        Log.d("locale",localea.getLanguage());
        Log.d("locale","locale rrrkkk");
*/


        Map<String, List<String>> map = getByDate(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH),"zh_CN");
        String luckyStr = map.get(DO).toString();
        luckyStr = luckyStr.substring(1,luckyStr.length()-1);

        String unLuckyStr = map.get(NOT_DO).toString();
        unLuckyStr = unLuckyStr.substring(1,unLuckyStr.length()-1);

        showLucky.setText(luckyStr);
        showUnLucky.setText(unLuckyStr);


        SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd");
        if(fmt.format(date).equals(fmt.format(new Date()))){
            backToToday.setVisibility(View.INVISIBLE);
        }else {
            backToToday.setVisibility(View.VISIBLE);
        }


    }


    //下面是读数据的代码段
    final static String DO = "DO";
    final static String NOT_DO = "NOT_DO";

    final static String minDateStr = "2016-01-01";
    final static String maxDateStr = "2019-12-31";
    //final static Date minDate = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");

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
