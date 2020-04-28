package com.example.agenda_lauzhack;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.agenda_lauzhack.MainActivity.setAlarmOfTheDay;

public class Postpone_broadcast extends BroadcastReceiver {

    private Context context;
    private Profile userProfile = new Profile();

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        int id = intent.getIntExtra("ID", 0);
        NotificationManagerCompat.from(context).cancel(id);

        postponeTask();
    }

    private void postponeTask() {
        readFromFile();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        int startI = calendar.get(Calendar.HOUR_OF_DAY)*4 + calendar.get(Calendar.MINUTE)/15;
        int converted_indice = convertedIndice();

        ArrayList<timeSlot.currentTask> dailyTasks = new ArrayList<>(userProfile.agenda.get(converted_indice));
        Log.w("DAILY", dailyTasks.toString());
        Log.w("DAILY", converted_indice + " ");
        // Determinate the time boudaries of the block to postpone
        int endI = startI;

        while ((dailyTasks.get(endI) == dailyTasks.get(startI) || dailyTasks.get(endI) != timeSlot.currentTask.FREE) && endI < (dailyTasks.size() - 1)) {
            endI++;
        }

        for(int i = startI; i < (endI + 1); i++) {
            userProfile.agenda.get(converted_indice).set(i, dailyTasks.get(i-1));
        }
        saveToFile();
        setAlarmOfTheDay(context);
    }

    private int convertedIndice() {
        int setting_day = userProfile.settingDay.get(Calendar.DAY_OF_YEAR);
        int actual_day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        int year_offset =  Calendar.getInstance().get(Calendar.YEAR) - userProfile.settingDay.get(Calendar.YEAR);
        int offset = 365*year_offset + actual_day - setting_day + (int) (0.25*(year_offset + 3));
        Log.w("OFFSET", offset + " ");

        return offset%7;
    }

    private void saveToFile(){
        try {
            File file = new File(context.getFilesDir(), userProfile.FileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

            userProfile.Save(bufferedWriter);

            bufferedWriter.flush();
            bufferedWriter.close();
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFromFile() {
        try {
            FileInputStream fileInputStream = context.openFileInput(userProfile.FileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String lineData = bufferedReader.readLine();

            userProfile.decode(lineData);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
