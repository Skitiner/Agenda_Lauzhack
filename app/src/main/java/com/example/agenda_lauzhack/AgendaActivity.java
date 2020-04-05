package com.example.agenda_lauzhack;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AgendaActivity extends AppCompatActivity  {

    private myAdapter adapter;
    private ListView schedule;
    private ArrayList<ArrayList<timeSlot>> week;
    private ArrayList<ArrayList<timeSlot.currentTask>> dailyTasks;
    private int currentDay;
    private Date currentDate;
    private TextView date;
    private int date_offset;

    private boolean fixed_work;
    private boolean lunch_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        fixed_work = intent.getBooleanExtra(ProfileActivity.FIXED_WORK, false);;
        lunch_time = intent.getBooleanExtra(ProfileActivity.LUNCH_TIME, false);

        adapter = new myAdapter(this.getApplicationContext(), R.layout.time_slot);
        setContentView(R.layout.activity_agenda);
        schedule = findViewById(R.id.schedule);

        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd/MM/yyyy", Locale.getDefault());
        date = findViewById(R.id.date);
        currentDate = new Date();
        date.setText(formatter.format(currentDate));
        currentDay = 0;
        date_offset = 0;

        week = new ArrayList<>();
        dailyTasks = new ArrayList<>();

        timeSlot.currentTask task;

        for(int j = 0; j < 7; j++ ) {
            ArrayList<timeSlot> mySlots = new ArrayList<>();
            ArrayList<timeSlot.currentTask> tasks = new ArrayList<>();
            for (int i = 0; i < 24; i++) {

                task = timeSlot.currentTask.FREE;

                timeSlot slot = new timeSlot();
                slot.time = i;
                slot.task_1 = task;
                slot.task_2 = task;
                slot.task_3 = task;
                slot.task_4 = task;

                tasks.add(slot.task_1);
                tasks.add(slot.task_2);
                tasks.add(slot.task_3);
                tasks.add(slot.task_4);

                mySlots.add(slot);

            }
            week.add(mySlots);
            dailyTasks.add(tasks);
        }

        adapter.addAll(week.get(0));
        schedule.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_agenda, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.today_button:
                currentDay = 0;
                adapter.clear();
                adapter.addAll(week.get(currentDay));
                SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd/MM/yyyy", Locale.getDefault());
                date = findViewById(R.id.date);
                currentDate = new Date();
                date.setText(formatter.format(currentDate));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeDay(View view) {
        adapter.clear();
        if(view.getId() == R.id.nextDay){
            currentDay++;
            currentDay %= 7;
            date_offset++;
        }
        else if(view.getId() == R.id.previousDay){
            if(currentDay == 0)
                currentDay = 6;
            else
                currentDay--;

            date_offset--;
        }

        adapter.addAll(week.get(currentDay));

        // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd/MM/yyyy", Locale.getDefault());
        String dt = sdf.format(currentDate);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dt));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, date_offset);  // number of days to add
        dt = sdf.format(c.getTime());

        date.setText(dt);
    }

    private class myAdapter extends ArrayAdapter<timeSlot> {

        private int time_layout;

        public myAdapter(@NonNull Context context, int resource) {
            super(context, resource);
            time_layout = resource;
        }

        @NonNull
        @Override
        public View getView(int position,
                            @Nullable View convertView,
                            @NonNull ViewGroup parent) {

            View row = convertView;

            if (row == null) {
                //Inflate it from layout
                row = LayoutInflater.from(getContext())
                        .inflate(time_layout, parent, false);
            }

            row.findViewById(R.id.t_1).setOnClickListener(new textViewOnClickListener(row, position, 0));
            row.findViewById(R.id.a_1).setOnClickListener(new textViewOnClickListener(row, position, 1));
            row.findViewById(R.id.a_2).setOnClickListener(new textViewOnClickListener(row, position, 2));
            row.findViewById(R.id.a_3).setOnClickListener(new textViewOnClickListener(row, position, 3));
            row.findViewById(R.id.a_4).setOnClickListener(new textViewOnClickListener(row, position, 4));


            ((TextView) row.findViewById(R.id.t_1)).setText(getItem(position).time + "h");
            setItemApparence((TextView) row.findViewById(R.id.a_1), getItem(position).task_1);
            setItemApparence((TextView) row.findViewById(R.id.a_2), getItem(position).task_2);
            setItemApparence((TextView) row.findViewById(R.id.a_3), getItem(position).task_3);
            setItemApparence((TextView) row.findViewById(R.id.a_4), getItem(position).task_4);


            return row;
        }

        private void setItemApparence(TextView textView, timeSlot.currentTask task) {
            switch (task) {
                case SPORT:
                    textView.setText(R.string.Sport);
                    textView.setBackgroundColor(getResources().getColor(R.color.darkBlue, null));
                    textView.setTextColor(getResources().getColor(R.color.lightBlue, null));
                    break;

                case WORK:
                    textView.setText(R.string.work);
                    textView.setBackgroundColor(getResources().getColor(R.color.lightBlue, null));
                    textView.setTextColor(getResources().getColor(R.color.darkBlue, null));
                    break;

                case EAT:
                    textView.setText(R.string.eat);
                    textView.setBackgroundColor(getResources().getColor(R.color.orange, null));
                    textView.setTextColor(getResources().getColor(R.color.darkBlue, null));
                    break;

                case FREE:
                    textView.setText("-");
                    textView.setBackgroundColor(getResources().getColor(R.color.gray, null));
                    textView.setTextColor(getResources().getColor(R.color.darkBlue, null));
                    break;

                case WORK_FIX:
                    textView.setText(R.string.fixed_work);
                    textView.setBackgroundColor(getResources().getColor(R.color.red, null));
                    textView.setTextColor(getResources().getColor(R.color.white, null));
                    break;

            }
        }

        private void updateAgenda() {
            int position;
            for(int i = 0; i < 96; i +=4 ) {
                position = i/4;
                week.get(currentDay).get(position).task_1 =  dailyTasks.get(currentDay).get(i);
                week.get(currentDay).get(position).task_2 =  dailyTasks.get(currentDay).get(i+1);
                week.get(currentDay).get(position).task_3 =  dailyTasks.get(currentDay).get(i+2);
                week.get(currentDay).get(position).task_4 =  dailyTasks.get(currentDay).get(i+3);
            }
            adapter.clear();
            adapter.addAll(week.get(currentDay));
        }


        private class textViewOnClickListener implements View.OnClickListener {

            View row;
            int position;
            int taskNum;
            int daily_task_pos;

            public textViewOnClickListener(View row, int position, int taskNum) {
                this.row = row;
                this.position = position;
                this.taskNum = taskNum;
                daily_task_pos = position*4 + taskNum;
            }

            @Override
            public void onClick(View v) {
                if(fixed_work) {
                    if (v.getId() == R.id.t_1) {
                        if (week.get(currentDay).get(position).task_1 != timeSlot.currentTask.WORK_FIX ||
                                week.get(currentDay).get(position).task_2 != timeSlot.currentTask.WORK_FIX ||
                                week.get(currentDay).get(position).task_3 != timeSlot.currentTask.WORK_FIX ||
                                week.get(currentDay).get(position).task_4 != timeSlot.currentTask.WORK_FIX) {


                            dailyTasks.get(currentDay).set(daily_task_pos, timeSlot.currentTask.WORK_FIX);
                            dailyTasks.get(currentDay).set(daily_task_pos+1, timeSlot.currentTask.WORK_FIX);
                            dailyTasks.get(currentDay).set(daily_task_pos+2, timeSlot.currentTask.WORK_FIX);
                            dailyTasks.get(currentDay).set(daily_task_pos+3, timeSlot.currentTask.WORK_FIX);
                        }
                        else {
                            dailyTasks.get(currentDay).set(daily_task_pos, timeSlot.currentTask.FREE);
                            dailyTasks.get(currentDay).set(daily_task_pos+1, timeSlot.currentTask.FREE);
                            dailyTasks.get(currentDay).set(daily_task_pos+2, timeSlot.currentTask.FREE);
                            dailyTasks.get(currentDay).set(daily_task_pos+3, timeSlot.currentTask.FREE);
                        }

                    } else {
                        if ( dailyTasks.get(currentDay).get(daily_task_pos-1) != timeSlot.currentTask.WORK_FIX)
                            dailyTasks.get(currentDay).set(daily_task_pos-1, timeSlot.currentTask.WORK_FIX);
                        else
                            dailyTasks.get(currentDay).set(daily_task_pos-1, timeSlot.currentTask.FREE);
                    }
                    updateAgenda();
                }

                if(lunch_time) {
                    if (v.getId() == R.id.t_1) {
                        if (week.get(currentDay).get(position).task_1 != timeSlot.currentTask.WORK_FIX ||
                                week.get(currentDay).get(position).task_2 != timeSlot.currentTask.WORK_FIX ||
                                week.get(currentDay).get(position).task_3 != timeSlot.currentTask.WORK_FIX ||
                                week.get(currentDay).get(position).task_4 != timeSlot.currentTask.WORK_FIX) {
                            week.get(currentDay).get(position).task_1 = timeSlot.currentTask.EAT;
                            week.get(currentDay).get(position).task_2 = timeSlot.currentTask.EAT;
                            week.get(currentDay).get(position).task_3 = timeSlot.currentTask.EAT;
                            week.get(currentDay).get(position).task_4 = timeSlot.currentTask.EAT;

                            week.get(currentDay).get(position + 1).task_1 = timeSlot.currentTask.EAT;
                            week.get(currentDay).get(position + 1).task_2 = timeSlot.currentTask.EAT;
                        }
                        else {

                        }

                        adapter.clear();
                        adapter.addAll(week.get(currentDay));
                    }

                }
            }
        }
    }
}

