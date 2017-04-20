package com.wangyue.luckyday;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.ini4j.Ini;
import org.w3c.dom.Text;

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

    Locale curLocale = Locale.US;//默认语言是英语
    String langStr = "en_US";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
 

            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);


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

        showLucky = (TextView)findViewById(R.id.showLucky);
        showUnLucky = (TextView)findViewById(R.id.showUnLucky);

        backToToday = (TextView)findViewById(R.id.backToTodayText);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            curLocale = getResources().getConfiguration().getLocales().get(0);
        }else
        {
            curLocale = MainActivity.this.getResources().getConfiguration().locale;
        }



        //
        //langStr = "zh_CN";
        if(curLocale.getLanguage().equalsIgnoreCase("zh")){
            //如果是zh,显示中文界面
            langStr = "zh_CN";
        }else{
            //否则默认是英语,需要把界面设置为三行,然后显示省略号
            showLucky.setMaxLines(3);
            showLucky.setEllipsize(TextUtils.TruncateAt.END);
            showUnLucky.setMaxLines(3);
            showUnLucky.setEllipsize(TextUtils.TruncateAt.END);
        }


        // TODO: 2017/4/20  点击"宜",弹出具体解释
        showLucky.setOnClickListener(showLuckyDetailClickListener);

        // TODO: 2017/4/20  点击"忌",弹出具体解释
        showUnLucky.setOnClickListener(showLuckyDetailClickListener);


        //回到今日按钮的事件
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

        //设置点击日历标题栏,弹出日历选择框
        calendarTitleClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
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
       
    }


    //显示标题栏上的信息图标
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //点击显示详细信息
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionbar_info:
                //Toast.makeText(this, "Compose", Toast.LENGTH_SHORT).show();
                //显示DetailInfoActivity



                Intent intent = new Intent();
                intent.setClass(MainActivity.this, DetailInfoActivity.class);
                MainActivity.this.startActivity(intent);

                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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


        Map<String, List<String>> map = getByDate(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH),langStr);
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




    View.OnClickListener showLuckyDetailClickListener = new View.OnClickListener() {


        @Override
        public void onClick(View v) {

            TextView toShow = (TextView) v;

            //// TODO: 2017/4/20 要把显示改为一个一个标签,为每一个标签设施点击事件,后续再做
            //现在先做成英文状态下展开和收缩
            String lang = "en_US";
            // TODO: 2017/4/20 等英文翻译下来了要取消这一行
            //lang = "zh_CN";
            if (curLocale.getLanguage().equalsIgnoreCase("zh")) {
                //如果是zh,显示中文界面
                lang = "zh_CN";

            } else {//英文才需要伸缩
                if (TextUtils.TruncateAt.END.equals(toShow.getEllipsize())) {

                    //详情展开
                    toShow.setSingleLine(false);
                    toShow.setEllipsize(null);

                } else {

                    //已经展开了,要收缩
                    toShow.setMaxLines(3);
                    toShow.setEllipsize(TextUtils.TruncateAt.END);

                }


            }

        }
    };

    View.OnClickListener showUnLuckyDetailClickListener = showLuckyDetailClickListener;

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
