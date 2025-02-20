package com.ludiostrix.organise_mois;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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


/*

Activitl prinipale.
Se lance à l'ouverture de l'application.
Ouvre premièrement les conditions générales si elles n'ont pas encore été accetées.
Sinon, Active les notifications et offre la possibilité d'aller au profil ou à l'agenda.
Le logo change en fonction de l'activité en cours.

 */

public class MainActivity extends AppCompatActivity {

    public static final String USER_PROFILE = "USER_PROFILE";
    private static Profile userProfile = new Profile();
    private String CHANNEL_ID = "CHANNEL_ID";
    public static ArrayList<PendingIntent> intentArray;
    private static AlarmManager mgrAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        setContentView(R.layout.activity_main);

        readFromFile(this);

        mgrAlarm = (AlarmManager) getSystemService(ALARM_SERVICE);

        //setLogo();
        //setAlarmOfTheDay(this);

        //planningCalculation();

        if (userProfile == null){
            userProfile = new Profile();
        }

        if (!userProfile.licenceAccepted){
            File file = new File(getFilesDir(), userProfile.LastFileName);
            file.delete();
            Intent intent = new Intent(MainActivity.this, GeneralTermsOfUseActivity.class);
            intent.putExtra(ProfileActivity.USER_PROFILE, userProfile);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        readFromFile(this);
        userProfile.convertInPastDay();

        saveToFile();
        readFromFile(this); // pas très opti, mais permet de généré aussi le pastAgenda et newEventPastAgenda

        setLogo();
        setAlarmOfTheDay(this);
    }

    private void saveToFile(){

        try {
            File file = new File(getFilesDir(), userProfile.FileName);
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

    private void setLogo() {
        int setting_day = userProfile.settingDay.get(Calendar.DAY_OF_YEAR);
        int actual_day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        int year_offset =  Calendar.getInstance().get(Calendar.YEAR) - userProfile.settingDay.get(Calendar.YEAR);
        int offset = (365*year_offset + actual_day - setting_day + (int) (0.25*(year_offset + 3)))%7;

        while (offset < 0){
            offset += 7;
        }
        offset %=7;

        int slot_indice = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)*4 + Calendar.getInstance().get(Calendar.MINUTE)/15;

        ImageView logo = findViewById(R.id.logo_image);


        if (userProfile.canceled_slots.get(offset).get(slot_indice) == Boolean.TRUE) {
            logo.setImageDrawable(getApplicationContext().getDrawable(R.drawable.main_logo_def));
            return;
        }

        switch (userProfile.agenda.get(offset).get(slot_indice)) {
            case EAT:
                logo.setImageDrawable(getApplicationContext().getDrawable(R.drawable.main_logo_eat));
                break;

            case WORK_CATCH_UP:
            case WORK:
                logo.setImageDrawable(getApplicationContext().getDrawable(R.drawable.main_logo_work));
                break;

            case SPORT_CATCH_UP:
            case SPORT:
                logo.setImageDrawable(getApplicationContext().getDrawable(R.drawable.main_logo_sport));
                break;

            case WORK_FIX:
                logo.setImageDrawable(getApplicationContext().getDrawable(R.drawable.main_logo_work));
                break;

            default:
                logo.setImageDrawable(getApplicationContext().getDrawable(R.drawable.main_logo_def));
                break;
        }
    }

    private void planningCalculation() {
        int setting_day = userProfile.settingDay.get(Calendar.DAY_OF_YEAR);
        int actual_day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        int year_offset =  Calendar.getInstance().get(Calendar.YEAR) - userProfile.settingDay.get(Calendar.YEAR);
        int offset = 365*year_offset + actual_day - setting_day + (int) (0.25*(year_offset + 3));

        if(offset >= 7) {
            AgendaInitialisation agendaInitialisation = new AgendaInitialisation(getApplicationContext());
            agendaInitialisation.slotCalculation();
        }
    }

    private static void removeAlarms() {
        for(PendingIntent intent : intentArray) {
            mgrAlarm.cancel(intent);
        }

        intentArray.clear();
    }

    protected static void setAlarmOfTheDay(Context context) {
        if(intentArray != null)
            removeAlarms();
        else
            intentArray = new ArrayList<PendingIntent>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // ******* RELEASE MODE *****
        int hour = 0;
        int minutes = 0;
        int k = 0;
        int setting_day = userProfile.settingDay.get(Calendar.DAY_OF_YEAR);
        int actual_day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        int year_offset =  Calendar.getInstance().get(Calendar.YEAR) - userProfile.settingDay.get(Calendar.YEAR);
        int offset = 365*year_offset + actual_day - setting_day + (int) (0.25*(year_offset + 3));

        while (offset < 0){
            offset += 7;
        }

        int offset_indice = offset%7;

        TimeSlot.CurrentTask task = userProfile.agenda.get(offset_indice).get(0);
        Boolean canceled = userProfile.canceled_slots.get(offset_indice).get(0);

        for(int i = 0; i < (userProfile.agenda).size(); i++){
            int indice = (i + offset_indice)%7;

            for(int j = 0; j < (userProfile.agenda.get(indice)).size(); j++) {
                k++;
                hour = j/4;
                minutes = 15*(j%4);

                if(userProfile.agenda.get(indice).get(j) != task || userProfile.canceled_slots.get(indice).get(j) != canceled) {

                    // No difference between WORK and WORK_FIX for the notifications
                    if((task == TimeSlot.CurrentTask.WORK || task == TimeSlot.CurrentTask.WORK_FIX)
                            && (userProfile.agenda.get(indice).get(j) == TimeSlot.CurrentTask.WORK || userProfile.agenda.get(indice).get(j) == TimeSlot.CurrentTask.WORK_FIX)
                            && userProfile.canceled_slots.get(indice).get(j) == canceled) {
                        task = userProfile.agenda.get(indice).get(j);
                        continue;
                    }
                    // No difference between PAUSE and FREE for the notifications
                    if((task == TimeSlot.CurrentTask.PAUSE || task == TimeSlot.CurrentTask.FREE)
                            && (userProfile.agenda.get(indice).get(j) == TimeSlot.CurrentTask.PAUSE || userProfile.agenda.get(indice).get(j) == TimeSlot.CurrentTask.FREE)) {
                        task = userProfile.agenda.get(indice).get(j);
                        continue;
                    }
                    canceled = userProfile.canceled_slots.get(indice).get(j);
                    task = userProfile.agenda.get(indice).get(j);
                    Intent intentForService;

                    switch (task) {
                        case WORK:
                            intentForService = new Intent(context, Broadcast_work.class);
                            break;
                        case WORK_FIX:
                            intentForService = new Intent(context, Broadcast_work.class);
                            break;
                        case SPORT:
                            intentForService = new Intent(context, Broadcast_sport.class);
                            break;
                        case WORK_CATCH_UP:
                            intentForService = new Intent(context, Broadcast_work_catch_up.class);
                            break;
                        case SPORT_CATCH_UP:
                            intentForService = new Intent(context, Broadcast_sport_catch_up.class);
                            break;
                        case EAT:
                            intentForService = new Intent(context, Broadcast_eat.class);
                            break;
                        case SLEEP:
                            intentForService = new Intent(context, Broadcast_sleep.class);
                            break;
                        case MORNING_ROUTINE:
                            intentForService = new Intent(context, Broadcast_wake_up.class);
                            break;
                        case FREE:
                            intentForService = new Intent(context, Broadcast_break.class);
                            break;
                        case PAUSE:
                            intentForService = new Intent(context, Broadcast_break.class);
                            break;
                        case NEWEVENT:
                            intentForService = new Intent(context, Broadcast_new_event.class);
                            break;
                        default:
                            intentForService = new Intent(context, RemindBroadcast.class);
                            break;
                    }


                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, k,
                            intentForService, PendingIntent.FLAG_CANCEL_CURRENT);

                    calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());

                    // Condition to not set notification before the actual time
                    if(i == 0 && !(hour > calendar.get(Calendar.HOUR_OF_DAY) || (hour == calendar.get(Calendar.HOUR_OF_DAY) && minutes > calendar.get(Calendar.MINUTE)))) {
                        continue;
                    }

                    calendar.add(Calendar.DAY_OF_MONTH, i);
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minutes);
                    calendar.set(Calendar.SECOND, 0);

                    Log.w("calendar", calendar.toString());


                    mgrAlarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    intentArray.add(pendingIntent);

                }


            }
        }


    }

    public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public static void readFromFile(Context context) {
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

    public void goToAgendaActivity(View view) {
        Intent agenda = new Intent(this, AgendaActivity.class);
        startActivity(agenda);
        //finish();
    }
  
    public void clickedProfileButtonXmlCallback(View view) {

        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        //intent.putExtra(ProfileActivity.USER_PROFILE, userProfile);
        startActivity(intent);
        //finish();

    }

}
