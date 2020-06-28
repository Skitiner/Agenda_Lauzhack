package com.ludiostrix.organise_mois;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.ludiostrix.organise_mois.AgendaActivity.conversionDayIndice;

public class ProfileActivity extends AppCompatActivity {

    public static final String USER_PROFILE = "USER_PROFILE";
    public static final String FIXED_WORK = "FIXED_WORK";
    public static final String LUNCH_TIME = "LUNCH_TIME";

    private List<Integer> dayIds = Arrays.asList(R.id.monday, R.id.tuesday, R.id.wednesday, R.id.thursday,
            R.id.friday, R.id.saturday, R.id.sunday);

    private List<Integer> sportIds = Arrays.asList(R.id.sloth, R.id.rabbit, R.id.cheetah);

    Profile userProfile = new Profile();

    private int positionSport;
    private boolean[] NewFreeDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        readFromFile();

        if (savedInstanceState != null) {
            if (savedInstanceState.getSerializable("nbworkHours") != null) {
                userProfile.nbWorkHours = (String) savedInstanceState.getSerializable("nbworkHours");
            }
            if (savedInstanceState.getSerializable("wakeUp") != null) {
                userProfile.wakeUp = (String) savedInstanceState.getSerializable("wakeUp");
            }
            if (savedInstanceState.getSerializable("freeDay") != null) {
                NewFreeDay = (boolean[]) savedInstanceState.getSerializable("freeDay");
            }
            if (savedInstanceState.getSerializable("sportRoutine") != null) {
                positionSport = (int) savedInstanceState.getSerializable("sportRoutine");
            }
            if (savedInstanceState.getSerializable("OptWorkTime") != null) {
                userProfile.optWorkTime = (String) savedInstanceState.getSerializable("OptWorkTime");
            }
        }
        else {
            positionSport = userProfile.sportRoutine;
            NewFreeDay = userProfile.freeDay;
        }

        if (userProfile != null) {
            setProfileInfo();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        TextView NBWorkEditText = findViewById(R.id.NBWorkEditText);
        TextView NBOptWorkEditText = findViewById(R.id.NBOptWorkEditText);
        TextView WakeUpEditText = findViewById(R.id.WakeupEditText);
        userProfile.stringWorkTimeToSlot(NBWorkEditText.getText().toString());
        userProfile.stringOptWorkTimeToSlot(NBOptWorkEditText.getText().toString());
        userProfile.stringTimewakeUpToFloat(WakeUpEditText.getText().toString());
        state.putSerializable("nbWorkHours", userProfile.nbWorkHours);
        state.putSerializable("wakeUp", userProfile.wakeUp);
        state.putSerializable("freeDay", NewFreeDay);
        state.putSerializable("sportRoutine", positionSport);
        state.putSerializable("OptWorkTime", userProfile.optWorkTime);
    }

    private void setProfileInfo() {
        TextView NBWorkEditText = findViewById(R.id.NBWorkEditText);
        NBWorkEditText.setText(userProfile.slotStringToTimeString(userProfile.nbWorkHours));

        TextView NBOptWorkEditText = findViewById(R.id.NBOptWorkEditText);
        NBOptWorkEditText.setText(userProfile.slotStringToTimeString(userProfile.optWorkTime));

        setFreeDay();

        TextView WakeUpEditText = findViewById(R.id.WakeupEditText);
        WakeUpEditText.setText(userProfile.floatStringToTimeString(userProfile.wakeUp));

        setSport();

    }

    private void setDayColor(Button dayButton, boolean red){
        if (!red) {
            dayButton.setTextColor(getResources().getColor(R.color.red, null));
        }
        else {
            dayButton.setTextColor(getResources().getColor(R.color.green, null));
        }
    }

    private void setFreeDay() {
        Button mondayButton = findViewById(R.id.monday);
        Button tuesdayButton = findViewById(R.id.tuesday);
        Button wednesdayButton = findViewById(R.id.wednesday);
        Button thursdayButton = findViewById(R.id.thursday);
        Button fridayButton = findViewById(R.id.friday);
        Button saturdayButton = findViewById(R.id.saturday);
        Button sundayButton = findViewById(R.id.sunday);

        setDayColor(mondayButton, NewFreeDay[0]);
        setDayColor(tuesdayButton, NewFreeDay[1]);
        setDayColor(wednesdayButton, NewFreeDay[2]);
        setDayColor(thursdayButton, NewFreeDay[3]);
        setDayColor(fridayButton, NewFreeDay[4]);
        setDayColor(saturdayButton, NewFreeDay[5]);
        setDayColor(sundayButton, NewFreeDay[6]);
    }

    private void setSport() {
        ImageButton slothSportButton = findViewById(R.id.sloth);
        ImageButton rabbitSportButton = findViewById(R.id.rabbit);
        ImageButton cheetahSportButton = findViewById(R.id.cheetah);
        TextView lightSportText = findViewById(R.id.light);
        TextView moderatedSportText = findViewById(R.id.moderated);
        TextView intenseSportText = findViewById(R.id.intense);

        if (positionSport==0){
            slothSportButton.setBackgroundColor(getResources().getColor(R.color.green, null));
            rabbitSportButton.setBackgroundColor(getResources().getColor(R.color.lightBlue, null));
            cheetahSportButton.setBackgroundColor(getResources().getColor(R.color.lightBlue, null));
            lightSportText.setTextColor(getResources().getColor(R.color.green, null));
            moderatedSportText.setTextColor(getResources().getColor(R.color.black, null));
            intenseSportText.setTextColor(getResources().getColor(R.color.black, null));
        }
        else if (positionSport==1) {
            slothSportButton.setBackgroundColor(getResources().getColor(R.color.lightBlue, null));
            rabbitSportButton.setBackgroundColor(getResources().getColor(R.color.green, null));
            cheetahSportButton.setBackgroundColor(getResources().getColor(R.color.lightBlue, null));
            lightSportText.setTextColor(getResources().getColor(R.color.black, null));
            moderatedSportText.setTextColor(getResources().getColor(R.color.green, null));
            intenseSportText.setTextColor(getResources().getColor(R.color.black, null));
        }
        else {
            slothSportButton.setBackgroundColor(getResources().getColor(R.color.lightBlue, null));
            rabbitSportButton.setBackgroundColor(getResources().getColor(R.color.lightBlue, null));
            cheetahSportButton.setBackgroundColor(getResources().getColor(R.color.green, null));
            lightSportText.setTextColor(getResources().getColor(R.color.black, null));
            moderatedSportText.setTextColor(getResources().getColor(R.color.black, null));
            intenseSportText.setTextColor(getResources().getColor(R.color.green, null));
        }

    }

    public void clickedDayButtonXmlCallback(View view) {

        Button day = (Button) view;
        final int dayID = day.getId(); // dayID is the id of the tapped Button
        final int position = dayIds.indexOf(dayID); // position is the position of the Button (in the ArrayList dayIds)

        Button dayButton = findViewById(dayID);

        NewFreeDay[position] = !NewFreeDay[position];

        int indice = ((7-conversionDayIndice()) + position)%7;

        for(int i = 0; i < userProfile.agenda.get(indice).size(); i++) {
            if(userProfile.agenda.get(indice).get(i) != timeSlot.currentTask.NEWEVENT) {
                userProfile.agenda.get(indice).set(i, timeSlot.currentTask.FREE);
            }
        }

        int pos;
        for(int i = 0; i < 96; i +=4 ) {
            pos = i/4;
            userProfile.fullAgenda.get(indice).get(pos).task_1 =  userProfile.agenda.get(indice).get(i);
            userProfile.fullAgenda.get(indice).get(pos).task_2 =  userProfile.agenda.get(indice).get(i+1);
            userProfile.fullAgenda.get(indice).get(pos).task_3 =  userProfile.agenda.get(indice).get(i+2);
            userProfile.fullAgenda.get(indice).get(pos).task_4 =  userProfile.agenda.get(indice).get(i+3);
        }

        userProfile.freeDay = NewFreeDay;

        setDayColor(dayButton, NewFreeDay[position]);

    }

    public void clickedSetFixedWorkButtonXmlCallback(View view) {

        int nb_free_day = 0;
        for(int i = 0; i < 7; i++) {
            if(!NewFreeDay[i])
                nb_free_day++;
        }

        if (nb_free_day == 0) {
            Toast.makeText(this, R.string.allFreeDay, Toast.LENGTH_SHORT).show();
            return;
        }

        TextView NBWorkEditText = findViewById(R.id.NBWorkEditText);
        TextView NBOptWorkEditText = findViewById(R.id.NBOptWorkEditText);
        TextView WakeUpEditText = findViewById(R.id.WakeupEditText);

        if (NBWorkEditText.getText().toString().isEmpty()) {
        }
        else if (NBOptWorkEditText.getText().toString().isEmpty()) {
        }
        else if (WakeUpEditText.getText().toString().isEmpty()) {
        }
        else {
            userProfile.stringWorkTimeToSlot(NBWorkEditText.getText().toString());
            userProfile.stringOptWorkTimeToSlot(NBOptWorkEditText.getText().toString());
            userProfile.stringTimewakeUpToFloat(WakeUpEditText.getText().toString());
            userProfile.sportRoutine = positionSport;
        }
        saveToFile();
        Intent intent = new Intent(this, AgendaActivity.class);
        intent.putExtra(FIXED_WORK, true);
        intent.putExtra(LUNCH_TIME, false);
        startActivity(intent);
    }

    public void clickedSetEatTimeButtonXmlCallback(View view) {

        int nb_free_day = 0;
        for(int i = 0; i < 7; i++) {
            if(!NewFreeDay[i])
                nb_free_day++;
        }

        if (nb_free_day == 0) {
            Toast.makeText(this, R.string.allFreeDay, Toast.LENGTH_SHORT).show();
            return;
        }

        TextView NBWorkEditText = findViewById(R.id.NBWorkEditText);
        TextView NBOptWorkEditText = findViewById(R.id.NBOptWorkEditText);
        TextView WakeUpEditText = findViewById(R.id.WakeupEditText);

        if (NBWorkEditText.getText().toString().isEmpty()) {
        }
        else if (NBOptWorkEditText.getText().toString().isEmpty()) {
        }
        else if (WakeUpEditText.getText().toString().isEmpty()) {
        }
        else {
            userProfile.stringWorkTimeToSlot(NBWorkEditText.getText().toString());
            userProfile.stringOptWorkTimeToSlot(NBOptWorkEditText.getText().toString());
            userProfile.stringTimewakeUpToFloat(WakeUpEditText.getText().toString());
            userProfile.sportRoutine = positionSport;
        }
        saveToFile();
        Intent intent = new Intent(this, AgendaActivity.class);
        intent.putExtra(FIXED_WORK, false);
        intent.putExtra(LUNCH_TIME, true);
        startActivity(intent);
    }

    public void clickedSportButtonXmlCallback(View view) {

        ImageButton sport = (ImageButton) view;
        final int sportID = sport.getId(); // dayID is the id of the tapped Button
        positionSport = sportIds.indexOf(sportID); // position is the position of the Button (in the ArrayList dayIds)

        setSport();

    }

    public void clickedSaveProfileButtonXmlCallback(View view) {

        TextView NBWorkEditText = findViewById(R.id.NBWorkEditText);
        TextView NBOptWorkEditText = findViewById(R.id.NBOptWorkEditText);
        TextView WakeUpEditText = findViewById(R.id.WakeupEditText);

        boolean allFreeDay = true;
        for (int i = 0; i < NewFreeDay.length; i++){
            if (!NewFreeDay[i]){
                allFreeDay = false;
            }
        }
        if (allFreeDay){
            Toast.makeText(ProfileActivity.this, R.string.allFreeDay, Toast.LENGTH_SHORT).show();
        }
        else if (NBWorkEditText.getText().toString().isEmpty()) {
            Toast.makeText(ProfileActivity.this, R.string.forgetWorkHour, Toast.LENGTH_SHORT).show();
        }else if (NBOptWorkEditText.getText().toString().isEmpty()) {
            Toast.makeText(ProfileActivity.this, R.string.forgetOptWorkHour, Toast.LENGTH_SHORT).show();
        }
        else if (WakeUpEditText.getText().toString().isEmpty()) {
            Toast.makeText(ProfileActivity.this, R.string.forgetWakeUp, Toast.LENGTH_SHORT).show();
        }
        else {
            boolean correctnbWork = userProfile.stringWorkTimeToSlot(NBWorkEditText.getText().toString());
            boolean correctnbOptWork = userProfile.stringOptWorkTimeToSlot(NBOptWorkEditText.getText().toString());
            boolean correctwakeUp = userProfile.stringTimewakeUpToFloat(WakeUpEditText.getText().toString());
            if (!correctnbWork) {
                Toast.makeText(ProfileActivity.this, R.string.wrongnbWorkHours, Toast.LENGTH_SHORT).show();
            } else if (!correctnbOptWork) {
                Toast.makeText(ProfileActivity.this, R.string.wrongnbOptWorkHours, Toast.LENGTH_SHORT).show();
            } else if (!correctwakeUp) {
                Toast.makeText(ProfileActivity.this, R.string.wrongwakeUp, Toast.LENGTH_SHORT).show();
            } else {
                userProfile.sportRoutine = positionSport;
                int newOffsetSettings = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - userProfile.settingDay.get(Calendar.DAY_OF_YEAR);
                for (int i = 0; i < newOffsetSettings ; i++) {
                    ArrayList<timeSlot> tempDay = userProfile.fullAgenda.get(0);
                    userProfile.fullAgenda.remove(0);
                    userProfile.fullAgenda.add(tempDay);
                }
                userProfile.settingDay = Calendar.getInstance();
                userProfile.settingDay.setTimeInMillis(System.currentTimeMillis());

                saveToFile();

                DaySlotsCalculation daySlotsCalculation = new DaySlotsCalculation(getApplicationContext());
                int isOK = daySlotsCalculation.slotCalculation();

                if (isOK == 0) {
                    Toast.makeText(ProfileActivity.this, R.string.Saved, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if (isOK == 1){
                    Toast.makeText(ProfileActivity.this, R.string.bad_parameters, Toast.LENGTH_SHORT).show();
                }
                else if (isOK == 2){
                    Toast.makeText(ProfileActivity.this, R.string.too_lots_of_fixedwork, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void saveToFile(){
        try {
            //Context ctx = getApplicationContext();
            //ctx.deleteFile(userProfile.FileName);
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


    public void clickedSeeUtilisationConditionButtonXmlCallback(View view) {
        Intent intent = new Intent(ProfileActivity.this, popupActivity.class);
        intent.putExtra(ProfileActivity.USER_PROFILE, userProfile);
        startActivity(intent);
    }

    public void onBackPressed() {
        DaySlotsCalculation daySlotsCalculation = new DaySlotsCalculation(getApplicationContext());
        daySlotsCalculation.slotCalculation();

        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void readFromFile() {
        try {
            Context ctx = getApplicationContext();
            FileInputStream fileInputStream = ctx.openFileInput(userProfile.FileName);
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

    public void clickedBackToMenuButtonXmlCallback (View view) {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}