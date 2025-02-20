package com.ludiostrix.organise_mois;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*

Classe du Profil.
Contient toutes les informations du profil de l'utilisateur et les fonctions permettant de convertir,
 utiliser et vérifier ces informations, ainsi que les fonctions de sauvegarde, de récupération et de
 conversion en jour passé.

 */

public class Profile implements Serializable {
    private static final String TAG = "Profile";

    protected boolean licenceAccepted;
    protected String nbWorkHours;
    protected String optWorkTime;
    protected boolean[] freeDay;
    protected String wakeUp;
    protected int sportRoutine;
    protected int lateSportSlot;
    protected Float lateWorkSlot;
    protected boolean calculation;
    protected String FileName;
    protected String LastFileName;
    protected ArrayList<ArrayList<TimeSlot.CurrentTask>> agenda;
    protected ArrayList<ArrayList<TimeSlot.CurrentTask>> futurAgenda;
    protected ArrayList<ArrayList<TimeSlot.CurrentTask>> pastAgenda;
    protected ArrayList<ArrayList<String>> newEventAgenda;
    protected ArrayList<ArrayList<String>> newEventFuturAgenda;
    protected ArrayList<ArrayList<String>> newEventPastAgenda;
    protected ArrayList<ArrayList<TimeSlot>> fullAgenda;
    protected ArrayList<ArrayList<TimeSlot>> futurFullAgenda;
    protected int pastAgendaSize;
    protected List<List<TimeSlot>> pastFullAgenda;
    protected List<List<Boolean>> canceled_slots;
    protected List<List<Boolean>> pastCanceledSlots;
    protected String current;
    protected String currentFutur;
    protected String currentPast;
    protected String cancel_current;
    protected String event_current;
    protected List<String> agenda_back;
    protected List<String> past_agenda_back;
    protected List<String> futur_agenda_back;
    protected List<String> cancel_back;
    protected List<String> event_back;
    protected Calendar settingDay;
    protected Calendar lastConnection;
    public List<newEvent> savedEvent;
    public ArrayList<TimeSlot> freeWeekDay;
    protected List<List<List<Integer>>> weight;
    protected Map<String,Integer> Task = new HashMap<>();
    protected List<Integer> sportDayRank;
    protected boolean agendaInit = true;
    protected int workCatchUp = 0;
    protected int sportCatchUp = 0;
    //protected List<StatPerDay> pastStat;
    //protected List<StatPerDay> weekStat;


/*

Section d'initialisation

 */

    public Profile(){
        agenda_back = new ArrayList<>();
        past_agenda_back = new ArrayList<>();
        futur_agenda_back = new ArrayList<>();
        cancel_back = new ArrayList<>();
        event_back = new ArrayList<>();
        //pastStat = new ArrayList<>();
        //weekStat = new ArrayList<>();

        /*for (int i = 0; i < 7; i++){
            StatPerDay newStat = new StatPerDay();
            weekStat.add(newStat);
        }*/

        this.licenceAccepted = false;
        this.nbWorkHours = "168";
        this.optWorkTime = "6";
        this.freeDay = new boolean[] {false, false, false, false, false, false, false};
        this.wakeUp = "8.0";
        this.sportRoutine = 1;
        this.lateSportSlot = 0;
        this.lateWorkSlot = Float.valueOf(0);
        calculation = false;
        this.FileName = "userProfileV2.txt";
        this.LastFileName = "userProfile.txt";
        initAgenda();
        initFullAgenda();
        settingDay = Calendar.getInstance();
        settingDay.setTimeInMillis(System.currentTimeMillis());
        lastConnection = Calendar.getInstance();
        lastConnection.setTimeInMillis(System.currentTimeMillis());

        current = new String();
        currentFutur = new String();
        currentPast = new String();
        cancel_current = new String();
        event_current = new String();

        this.savedEvent = new ArrayList<>();

        this.freeWeekDay = new ArrayList<>();

        TimeSlot.CurrentTask task;

        for (int i = 0; i < 24; i++) {

            task = TimeSlot.CurrentTask.FREE;
            TimeSlot slot = new TimeSlot();
            slot.time = i;
            slot.task_1 = task;
            slot.task_2 = task;
            slot.task_3 = task;
            slot.task_4 = task;

            this.freeWeekDay.add(slot);

        }
        this.pastAgendaSize = 0;

        initWeight();
        this.Task.put("Sport",0);
        this.Task.put("Work",1);
    }

    /*public Profile(Profile that) {

        this(that.getFullAgenda());

    }

    public ArrayList<ArrayList<TimeSlot>> getFullAgenda(){
        return this.fullAgenda;
    }*/

    private void initWeight(){
        List<List<Integer>> Hour;
        List<Integer> dayWeight;
        List<Integer> nightWeight;
        weight = new ArrayList<>();
        for (int j = 0 ; j < 7; j++) {
            Hour = new ArrayList<>();
            for (int i = 0; i < 4 * 24; i++) {
                if (i < 4 * 7 - 1 || i > 4 * 22 - 1) {
                    nightWeight = new ArrayList<Integer>();
                    nightWeight.add(4);
                    nightWeight.add(4);
                    Hour.add(nightWeight);
                } else {
                    dayWeight = new ArrayList<>();
                    dayWeight.add(6);
                    dayWeight.add(6);
                    Hour.add(dayWeight);
                }
            }
            weight.add(Hour);
        }


        sportDayRank = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            sportDayRank.add(1);
        }
        // int test = this.Hour.get(12).get(Task.get("Sport"));
    }

    private void initFullAgenda() {
        fullAgenda = new ArrayList<>();
        futurFullAgenda = new ArrayList<>();
        pastFullAgenda = new ArrayList<>();

        TimeSlot.CurrentTask task;

        for(int j = 0; j < 7; j++ ) {
            ArrayList<TimeSlot> mySlots = new ArrayList<>();
            for (int i = 0; i < 24; i++) {

                task = TimeSlot.CurrentTask.FREE;
                TimeSlot slot = new TimeSlot();
                slot.time = i;
                slot.task_1 = task;
                slot.task_2 = task;
                slot.task_3 = task;
                slot.task_4 = task;

                mySlots.add(slot);

            }
            fullAgenda.add(mySlots);
        }

        for(int j = 0; j < 7; j++ ) {
            ArrayList<TimeSlot> mySlots = new ArrayList<>();
            for (int i = 0; i < 24; i++) {

                task = TimeSlot.CurrentTask.FREE;
                TimeSlot slot = new TimeSlot();
                slot.time = i;
                slot.task_1 = task;
                slot.task_2 = task;
                slot.task_3 = task;
                slot.task_4 = task;

                mySlots.add(slot);

            }
            futurFullAgenda.add(mySlots);
        }

    }

    private void initAgenda() {
        agenda = new ArrayList<>();
        pastAgenda = new ArrayList<>();
        futurAgenda = new ArrayList<>();
        newEventAgenda = new ArrayList<>();
        newEventFuturAgenda = new ArrayList<>();
        canceled_slots = new ArrayList<>();
        pastCanceledSlots = new ArrayList<>();

        TimeSlot.CurrentTask task;
        for(int j = 0; j < 7; j++ ) {
            ArrayList<TimeSlot.CurrentTask> tasks = new ArrayList<>();
            List<Boolean> cancel = new ArrayList<>();
            ArrayList<String> newEvent = new ArrayList<>();

            for (int i = 0; i < 96; i++) {
                task = TimeSlot.CurrentTask.FREE;
                tasks.add(task);
                cancel.add(Boolean.FALSE);
                newEvent.add("");
            }
            agenda.add(tasks);
            canceled_slots.add(cancel);
            newEventAgenda.add(newEvent);
        }

        for(int j = 0; j < 7; j++ ) {
            ArrayList<TimeSlot.CurrentTask> tasks = new ArrayList<>();
            ArrayList<String> newFuturEvent = new ArrayList<>();

            for (int i = 0; i < 96; i++) {
                task = TimeSlot.CurrentTask.FREE;
                tasks.add(task);
                newFuturEvent.add("");
            }
            futurAgenda.add(tasks);
            newEventFuturAgenda.add(newFuturEvent);
        }

    }


/*

Section de sauvegarde du profil

 */

    private byte[] floatToByteArray ( final float i ) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeFloat(i);
        dos.flush();
        return bos.toByteArray();
    }

    private float convertByteArrayToFloat(byte[] floatBytes){
        ByteBuffer byteBuffer = ByteBuffer.allocate(Float.BYTES);
        byteBuffer.put(floatBytes);
        byteBuffer.flip();
        return byteBuffer.getFloat();
    }

    public void Save(BufferedWriter bufferedWriter){
        try {
            bufferedWriter.write(String.valueOf(this.licenceAccepted));
            bufferedWriter.write("/");
            bufferedWriter.write(String.valueOf(this.agendaInit));
            bufferedWriter.write("/");
            bufferedWriter.write(this.nbWorkHours);
            bufferedWriter.write("/");
            bufferedWriter.write(this.optWorkTime);
            bufferedWriter.write("/");
            for(int i=0; i < this.freeDay.length; i ++) {
                if (this.freeDay[i]){
                    bufferedWriter.write('1');
                }
                else {
                    bufferedWriter.write('0');
                }
            }
            bufferedWriter.write("/");
            bufferedWriter.write(this.wakeUp);
            bufferedWriter.write("/");
            bufferedWriter.write(String.valueOf(this.sportRoutine));
            bufferedWriter.write("/");
            bufferedWriter.write(String.valueOf(this.lateSportSlot));
            bufferedWriter.write("/");

            /*byte[] lateSport = floatToByteArray(lateWorkSlot);
            for (int i = 0; i < lateSport.length; i++) {
                bufferedWriter.write(String.valueOf(lateSport[i]));
            }*/

            bufferedWriter.write(String.valueOf(this.lateWorkSlot));
            bufferedWriter.write("/");
            bufferedWriter.write(String.valueOf(settingDay.getTimeInMillis()));
            bufferedWriter.write("/");
            bufferedWriter.write(String.valueOf(lastConnection.getTimeInMillis()));
            bufferedWriter.write("/");
            bufferedWriter.write(String.valueOf(pastFullAgenda.size()));
            bufferedWriter.write("/");
            for (int i = 0; i < pastFullAgenda.size() ; i++){
                for (int j = 0; j < pastFullAgenda.get(i).size(); j++){
                    if (pastFullAgenda.get(i).get(j).task_1 == TimeSlot.CurrentTask.NEWEVENT){
                        bufferedWriter.write(pastFullAgenda.get(i).get(j).new_task_1);
                    }
                    else {
                        bufferedWriter.write(pastFullAgenda.get(i).get(j).task_1.toString());
                    }
                    bufferedWriter.write(",");
                    if (pastFullAgenda.get(i).get(j).task_2 == TimeSlot.CurrentTask.NEWEVENT){
                        bufferedWriter.write(pastFullAgenda.get(i).get(j).new_task_2);
                    }
                    else {
                        bufferedWriter.write(pastFullAgenda.get(i).get(j).task_2.toString());
                    }
                    bufferedWriter.write(",");
                    if (pastFullAgenda.get(i).get(j).task_3 == TimeSlot.CurrentTask.NEWEVENT){
                        bufferedWriter.write(pastFullAgenda.get(i).get(j).new_task_3);
                    }
                    else {
                        bufferedWriter.write(pastFullAgenda.get(i).get(j).task_3.toString());
                    }
                    bufferedWriter.write(",");
                    if (pastFullAgenda.get(i).get(j).task_4 == TimeSlot.CurrentTask.NEWEVENT){
                        bufferedWriter.write(pastFullAgenda.get(i).get(j).new_task_4);
                    }
                    else {
                        bufferedWriter.write(pastFullAgenda.get(i).get(j).task_4.toString());
                    }
                    bufferedWriter.write(",");
                }
            }
            bufferedWriter.write("/");
            for(int i=0; i < this.pastCanceledSlots.size(); i ++) {
                for (int j = 0; j < this.pastCanceledSlots.get(i).size(); j ++) {
                    if (this.pastCanceledSlots.get(i).get(j)) {
                        bufferedWriter.write('1');
                    } else {
                        bufferedWriter.write('0');
                    }
                }
            }
            bufferedWriter.write("/");
            for (int i = 0; i < fullAgenda.size() ; i++){
                for (int j = 0; j < fullAgenda.get(i).size(); j++){
                    if (fullAgenda.get(i).get(j).task_1 == TimeSlot.CurrentTask.NEWEVENT){
                        bufferedWriter.write(fullAgenda.get(i).get(j).new_task_1);
                    }
                    else {
                        bufferedWriter.write(fullAgenda.get(i).get(j).task_1.toString());
                    }
                    bufferedWriter.write(",");
                    if (fullAgenda.get(i).get(j).task_2 == TimeSlot.CurrentTask.NEWEVENT){
                        bufferedWriter.write(fullAgenda.get(i).get(j).new_task_2);
                    }
                    else {
                        bufferedWriter.write(fullAgenda.get(i).get(j).task_2.toString());
                    }
                    bufferedWriter.write(",");
                    if (fullAgenda.get(i).get(j).task_3 == TimeSlot.CurrentTask.NEWEVENT){
                        bufferedWriter.write(fullAgenda.get(i).get(j).new_task_3);
                    }
                    else {
                        bufferedWriter.write(fullAgenda.get(i).get(j).task_3.toString());
                    }
                    bufferedWriter.write(",");
                    if (fullAgenda.get(i).get(j).task_4 == TimeSlot.CurrentTask.NEWEVENT){
                        bufferedWriter.write(fullAgenda.get(i).get(j).new_task_4);
                    }
                    else {
                        bufferedWriter.write(fullAgenda.get(i).get(j).task_4.toString());
                    }
                    bufferedWriter.write(",");
                }
            }
            bufferedWriter.write("/");
            for (int i = 0; i < futurFullAgenda.size() ; i++){
                for (int j = 0; j < futurFullAgenda.get(i).size(); j++){
                    if (futurFullAgenda.get(i).get(j).task_1 == TimeSlot.CurrentTask.NEWEVENT){
                        bufferedWriter.write(futurFullAgenda.get(i).get(j).new_task_1);
                    }
                    else {
                        bufferedWriter.write(futurFullAgenda.get(i).get(j).task_1.toString());
                    }
                    bufferedWriter.write(",");
                    if (futurFullAgenda.get(i).get(j).task_2 == TimeSlot.CurrentTask.NEWEVENT){
                        bufferedWriter.write(futurFullAgenda.get(i).get(j).new_task_2);
                    }
                    else {
                        bufferedWriter.write(futurFullAgenda.get(i).get(j).task_2.toString());
                    }
                    bufferedWriter.write(",");
                    if (futurFullAgenda.get(i).get(j).task_3 == TimeSlot.CurrentTask.NEWEVENT){
                        bufferedWriter.write(futurFullAgenda.get(i).get(j).new_task_3);
                    }
                    else {
                        bufferedWriter.write(futurFullAgenda.get(i).get(j).task_3.toString());
                    }
                    bufferedWriter.write(",");
                    if (futurFullAgenda.get(i).get(j).task_4 == TimeSlot.CurrentTask.NEWEVENT){
                        bufferedWriter.write(futurFullAgenda.get(i).get(j).new_task_4);
                    }
                    else {
                        bufferedWriter.write(futurFullAgenda.get(i).get(j).task_4.toString());
                    }
                    bufferedWriter.write(",");
                }
            }
            bufferedWriter.write("/");
            bufferedWriter.write(canceled_slots.toString());
            bufferedWriter.write("/");
            for (int i = 0; i < weight.size() ; i++){
                for (int j = 0; j < weight.get(i).size(); j++){
                    bufferedWriter.write(weight.get(i).get(j).get(0).toString());
                    bufferedWriter.write(weight.get(i).get(j).get(1).toString());
                }
            }
            bufferedWriter.write("/");
            for (int j = 0; j < sportDayRank.size(); j++) {
                bufferedWriter.write(sportDayRank.get(j).toString());
            }
            bufferedWriter.write("/");
            /*for (int i = 0; i < weekStat.size(); i++) {
                bufferedWriter.write(String.valueOf(weekStat.get(i).work));
                bufferedWriter.write(",");
                bufferedWriter.write(String.valueOf(weekStat.get(i).workDone));
                bufferedWriter.write(",");
                bufferedWriter.write(String.valueOf(weekStat.get(i).sport));
                bufferedWriter.write(",");
                bufferedWriter.write(String.valueOf(weekStat.get(i).sportDone));
                bufferedWriter.write(",");
            }
            bufferedWriter.write("/");
            for (int i = 0; i < pastStat.size(); i++) {
                bufferedWriter.write(String.valueOf(pastStat.get(i).work));
                bufferedWriter.write(",");
                bufferedWriter.write(String.valueOf(pastStat.get(i).workDone));
                bufferedWriter.write(",");
                bufferedWriter.write(String.valueOf(pastStat.get(i).sport));
                bufferedWriter.write(",");
                bufferedWriter.write(String.valueOf(pastStat.get(i).sportDone));
                bufferedWriter.write(",");
            }
            bufferedWriter.write("/");*/
            bufferedWriter.write(String.valueOf(this.workCatchUp));
            bufferedWriter.write("/");
            bufferedWriter.write(String.valueOf(this.sportCatchUp));
            bufferedWriter.write("/");
            if (savedEvent != null) {
                for (newEvent e : savedEvent) {
                    bufferedWriter.write(e.name);
                    bufferedWriter.write(",");
                    bufferedWriter.write(e.color.toString());
                    bufferedWriter.write(",");
                    if (e.work) {
                        bufferedWriter.write("1");
                    }
                    else {
                        bufferedWriter.write("0");
                    }
                    bufferedWriter.write(",");
                    if (e.sport) {
                        bufferedWriter.write("1");
                    }
                    else {
                        bufferedWriter.write("0");
                    }
                    bufferedWriter.write(",");
                }
                bufferedWriter.write("/");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Log.w("CANCELED SAVE", canceled_slots.toString());
    }


/*

Section de récupération du profil

 */

    public void decode(String lineData) {
        current = new String();
        currentFutur = new String();
        currentPast = new String();
        cancel_current = new String();
        event_current = new String();
        String lA="";
        String aI="";
        String nW="";
        String oW="";
        Character[] fD= new Character[] {'0','0','0','0','0','0','0'};
        String wU="";
        past_agenda_back = new ArrayList<>();
        String pastCA = "";
        agenda_back = new ArrayList<>();
        futur_agenda_back = new ArrayList<>();
        cancel_back = new ArrayList<>();
        event_back = new ArrayList<>();
        String sR="";
        String lSS="";
        String lWS="";
        String timeMillis = "";
        String lastConnexionTimeMillis = "";
        String W = "";
        String sDR = "";
        String pastAS = "";
        /*String ps = "";
        String s = "";*/
        String wCU = "";
        String sCU = "";

        int j = 0;
        int n = 0;

        for (int i=0;i<lineData.length();i++) {
            if (lineData.charAt(i) !='/'){
                switch (j){
                    case 0 : lA += lineData.charAt(i);
                             break;
                    case 1 : aI += lineData.charAt(i);
                             break;
                    case 2 : nW += lineData.charAt(i);
                             break;
                    case 3 : oW += lineData.charAt(i);
                            break;
                    case 4 : fD[n] = lineData.charAt(i);
                             n++;
                             break;
                    case 5 : wU += lineData.charAt(i);
                             break;
                    case 6 : sR += lineData.charAt(i);
                             break;
                    case 7 : lSS += lineData.charAt(i);
                            break;
                    case 8 : lWS += lineData.charAt(i);
                            break;
                    case 9 : timeMillis += lineData.charAt(i);
                            break;
                    case 10 : lastConnexionTimeMillis += lineData.charAt(i);
                            break;
                    case 11 : pastAS += lineData.charAt(i);
                            break;
                    case 12 : getPastAgenda(lineData.charAt(i));
                            break;
                    case 13 : pastCA += lineData.charAt(i);
                            break;
                    case 14 : getAgenda(lineData.charAt(i));
                            break;
                    case 15 : getFuturAgenda(lineData.charAt(i));
                            break;
                    case 16 : getCanceled(lineData.charAt(i));
                            break;
                    case 17 : W += lineData.charAt(i);
                            break;
                    case 18 : sDR += lineData.charAt(i);
                            break;
                    /*case 19 : s += lineData.charAt(i);
                            break;
                    case 20 : ps += lineData.charAt(i);
                            break;*/
                    case 19 : wCU += lineData.charAt(i);
                            break;
                    case 20 : sCU += lineData.charAt(i);
                            break;
                    case 21 : getSavedEvent(lineData.charAt(i));
                            break;

                }
            }
            else {
                j++;
            }
        }

        this.pastAgendaSize = Integer.parseInt(pastAS);
        writeInPastAgenda();

        List<Boolean> backPCA;
        for (int i = 0; i < pastAgendaSize ; i++){
            backPCA = new ArrayList<>();
            for (int k = 0; k < 96; k++){
                if (pastCA.charAt(i*96 + k) == '1') {
                    backPCA.add(true);
                }
                else {
                    backPCA.add(false);
                }
            }
            pastCanceledSlots.add(backPCA);
        }

        /*for (int i = 0; i < 7; i++){
            int k = 0;
            String w = "";
            String wd = "";
            String sp = "";
            String spd = "";

            for (int l = 0; l < s.length(); l++){
                if (s.charAt(l) == ','){
                    k++;
                }
                else {
                    switch (k){
                        case 0 : w += s.charAt(l);
                            break;
                        case 1 : wd += s.charAt(l);
                            break;
                        case 2 : sp += s.charAt(l);
                            break;
                        case 3 : spd += s.charAt(l);
                            break;
                    }
                }
            }
            weekStat.get(i).work = Integer.parseInt(w);
            weekStat.get(i).workDone = Float.parseFloat(wd);
            weekStat.get(i).sport = Integer.parseInt(sp);
            weekStat.get(i).sportDone = Integer.parseInt(spd);
        }

        for (int i = 0; i < pastAgendaSize; i++){
            StatPerDay initStat = new StatPerDay();
            pastStat.add(initStat);
        }
        for (int i = 0; i < pastAgendaSize; i++){
            int k = 0;
            String w = "";
            String wd = "";
            String sp = "";
            String spd = "";

            for (int l = 0; l < ps.length(); l++){
                if (ps.charAt(l) == ','){
                    k++;
                }
                else {
                    switch (k){
                        case 0 : w += ps.charAt(l);
                                break;
                        case 1 : wd += ps.charAt(l);
                                break;
                        case 2 : sp += ps.charAt(l);
                                break;
                        case 3 : spd += ps.charAt(l);
                                break;
                    }
                }
            }
            pastStat.get(i).work = Integer.parseInt(w);
            pastStat.get(i).workDone = Float.parseFloat(wd);
            pastStat.get(i).sport = Integer.parseInt(sp);
            pastStat.get(i).sportDone = Integer.parseInt(spd);
        }*/

        writeInAgenda();
        writeInFuturAgenda();
        writeCanceled();
        writeSavedEvent();

        this.workCatchUp = Integer.parseInt(wCU);
        this.sportCatchUp = Integer.parseInt(sCU);

        for (int i = 0; i < sDR.length(); i++){
            this.sportDayRank.set(i, Integer.parseInt(String.valueOf(sDR.charAt(i))));
        }

        this.licenceAccepted = Boolean.parseBoolean(lA);
        this.agendaInit = Boolean.parseBoolean(aI);
        this.nbWorkHours = nW;
        this.optWorkTime = oW;
        for(int i=0; i < fD.length; i ++) {
            if (fD[i] == '1') {
                this.freeDay[i] = true;
            }
            else {
                this.freeDay[i] = false;
            }
        }
        for (int i = 0; i < weight.size() ; i++){
            for (int k = 0; k < weight.get(i).size(); k++){
                weight.get(i).get(k).set(0, Integer.parseInt(String.valueOf(W.charAt(2*i*weight.get(0).size() + 2*k))));
                weight.get(i).get(k).set(1, Integer.parseInt(String.valueOf(W.charAt(2*i*weight.get(0).size() + 2*k + 1))));
            }
        }
        this.wakeUp = wU;
        this.sportRoutine = Integer.parseInt(sR);
        this.lateSportSlot = Integer.parseInt(lSS);
        //byte[] lateWork = lWS.getBytes();
        this.lateWorkSlot = Float.parseFloat(lWS); //convertByteArrayToFloat(lateWork);
        this.settingDay = Calendar.getInstance();               //pas nécessaire?
        this.settingDay.setTimeInMillis(Long.parseLong(timeMillis));
        this.lastConnection.setTimeInMillis(Long.parseLong(lastConnexionTimeMillis));

        Log.w("CANCELED READ", canceled_slots.toString());
    }

    private void getSavedEvent(char charAt) {
        if(charAt == ',') {
            event_back.add(event_current);
            event_current = new String();
        }
        else
            event_current = event_current + charAt;

    }

    private void writeSavedEvent() {
        savedEvent.clear();
        newEvent e = new newEvent();
        for (int i = 0; i < event_back.size(); i++) {
            if (i % 4 == 0) {
                e.name = event_back.get(i);
            } else if (i % 4 == 1){
                e.color = Integer.parseInt(event_back.get(i));
            } else if (i % 4 == 2){
                if (event_back.get(i).equals("1")) {
                    e.work = true;
                }
                else {
                    e.work = false;
                }
            } else if (i % 4 == 3){
                if (event_back.get(i).equals("1")) {
                    e.sport = true;
                }
                else {
                    e.sport = false;
                }
                savedEvent.add(e);
                e = new newEvent();
            }
        }
    }

    private void getCanceled(char charAt) {
        if(charAt == '[' || charAt == ' ' || charAt == ']')
            return;
        else if(charAt == ',') {
            cancel_back.add(cancel_current);
            cancel_current = new String();
    }
        else
            cancel_current = cancel_current + charAt;
    }


    private void writeCanceled() {
        for(int i = 0; i < cancel_back.size(); i++) {
            int j = i/96;
            int k = i%96;

            if ("true".equals(cancel_back.get(i))) {
                canceled_slots.get(j).set(k, Boolean.TRUE);

            } else if ("false".equals(cancel_back.get(i))) {
                canceled_slots.get(j).set(k, Boolean.FALSE);
            }
        }
    }

    private void writeInPastAgenda() {

        pastFullAgenda = new ArrayList<>();
        pastAgenda = new ArrayList<>();
        newEventPastAgenda = new ArrayList<>();

        TimeSlot.CurrentTask task;

        for(int j = 0; j < this.pastAgendaSize; j++ ) {
            List<TimeSlot> mySlots = new ArrayList<>();

            for (int i = 0; i < 24; i++) {

                task = TimeSlot.CurrentTask.FREE;
                TimeSlot slot = new TimeSlot();
                slot.time = i;
                slot.task_1 = task;
                slot.task_2 = task;
                slot.task_3 = task;
                slot.task_4 = task;

                mySlots.add(slot);
            }
            pastFullAgenda.add(mySlots);
        }

        for(int j = 0; j < this.pastAgendaSize; j++ ) {
            ArrayList<TimeSlot.CurrentTask> tasks = new ArrayList<>();
            ArrayList<String> newEvent = new ArrayList<>();

            for (int i = 0; i < 96; i++) {
                task = TimeSlot.CurrentTask.FREE;
                tasks.add(task);
                newEvent.add("");
            }
            pastAgenda.add(tasks);
            newEventPastAgenda.add(newEvent);
        }


        for(int i = 0; i < past_agenda_back.size(); i++) {
            int j = i/96;
            int k = i%96;

            if ("SPORT".equals(past_agenda_back.get(i))) {
                this.pastAgenda.get(j).set(k, TimeSlot.CurrentTask.SPORT);

            } else if ("WORK".equals(past_agenda_back.get(i))) {
                this.pastAgenda.get(j).set(k, TimeSlot.CurrentTask.WORK);

            } else if ("EAT".equals(past_agenda_back.get(i))) {
                this.pastAgenda.get(j).set(k, TimeSlot.CurrentTask.EAT);

            } else if ("FREE".equals(past_agenda_back.get(i))) {
                this.pastAgenda.get(j).set(k, TimeSlot.CurrentTask.FREE);

            } else if ("WORK_FIX".equals(past_agenda_back.get(i))) {
                this.pastAgenda.get(j).set(k, TimeSlot.CurrentTask.WORK_FIX);

            } else if ("MORNING_ROUTINE".equals(past_agenda_back.get(i))) {
                this.pastAgenda.get(j).set(k, TimeSlot.CurrentTask.MORNING_ROUTINE);

            } else if ("SLEEP".equals(past_agenda_back.get(i))) {
                this.pastAgenda.get(j).set(k, TimeSlot.CurrentTask.SLEEP);

            } else if ("PAUSE".equals(past_agenda_back.get(i))) {
                this.pastAgenda.get(j).set(k, TimeSlot.CurrentTask.PAUSE);
            } else if ("WORK_CATCH_UP".equals(past_agenda_back.get(i))) {
                this.pastAgenda.get(j).set(k, TimeSlot.CurrentTask.WORK_CATCH_UP);
            } else if ("SPORT_CATCH_UP".equals(past_agenda_back.get(i))) {
                this.pastAgenda.get(j).set(k, TimeSlot.CurrentTask.SPORT_CATCH_UP);
            }
            else{
                this.pastAgenda.get(j).set(k, TimeSlot.CurrentTask.NEWEVENT);
                newEventPastAgenda.get(j).set(k, past_agenda_back.get(i));
                if(k%4 == 0) {
                    pastFullAgenda.get(j).get((int) k / 4).new_task_1 = past_agenda_back.get(i);
                }
                else if(k%4 == 1) {
                    pastFullAgenda.get(j).get((int) k / 4).new_task_2 = past_agenda_back.get(i);
                }
                else if(k%4 == 2) {
                    pastFullAgenda.get(j).get((int) k / 4).new_task_3 = past_agenda_back.get(i);
                }
                else if(k%4 == 3) {
                    pastFullAgenda.get(j).get((int) k / 4).new_task_4 = past_agenda_back.get(i);
                }
            }
            if(k%4 == 0) {
                pastFullAgenda.get(j).get((int) k / 4).task_1 = this.pastAgenda.get(j).get(k);
            }
            else if(k%4 == 1) {
                pastFullAgenda.get(j).get((int) k / 4).task_2 = this.pastAgenda.get(j).get(k);
            }
            else if(k%4 == 2) {
                pastFullAgenda.get(j).get((int) k / 4).task_3 = this.pastAgenda.get(j).get(k);
            }
            else if(k%4 == 3) {
                pastFullAgenda.get(j).get((int) k / 4).task_4 = this.pastAgenda.get(j).get(k);
            }
        }

        Log.w("AGENDA_BACK", past_agenda_back.toString());
    }

    private void writeInAgenda() {
        for(int i = 0; i < agenda_back.size(); i++) {
            int j = i/96;
            int k = i%96;

            if ("SPORT".equals(agenda_back.get(i))) {
                agenda.get(j).set(k, TimeSlot.CurrentTask.SPORT);

            } else if ("WORK".equals(agenda_back.get(i))) {
                agenda.get(j).set(k, TimeSlot.CurrentTask.WORK);

            } else if ("EAT".equals(agenda_back.get(i))) {
                agenda.get(j).set(k, TimeSlot.CurrentTask.EAT);

            } else if ("FREE".equals(agenda_back.get(i))) {
                agenda.get(j).set(k, TimeSlot.CurrentTask.FREE);

            } else if ("WORK_FIX".equals(agenda_back.get(i))) {
                agenda.get(j).set(k, TimeSlot.CurrentTask.WORK_FIX);

            } else if ("MORNING_ROUTINE".equals(agenda_back.get(i))) {
                agenda.get(j).set(k, TimeSlot.CurrentTask.MORNING_ROUTINE);

            } else if ("SLEEP".equals(agenda_back.get(i))) {
                agenda.get(j).set(k, TimeSlot.CurrentTask.SLEEP);

            } else if ("PAUSE".equals(agenda_back.get(i))) {
                agenda.get(j).set(k, TimeSlot.CurrentTask.PAUSE);
            }
            else if ("WORK_CATCH_UP".equals(agenda_back.get(i))) {
                this.agenda.get(j).set(k, TimeSlot.CurrentTask.WORK_CATCH_UP);
            }
            else if ("SPORT_CATCH_UP".equals(agenda_back.get(i))) {
                this.agenda.get(j).set(k, TimeSlot.CurrentTask.SPORT_CATCH_UP);
            }
            else{
                agenda.get(j).set(k, TimeSlot.CurrentTask.NEWEVENT);
                newEventAgenda.get(j).set(k, agenda_back.get(i));
                if(k%4 == 0) {
                    fullAgenda.get(j).get((int) k / 4).new_task_1 = agenda_back.get(i);
                }
                else if(k%4 == 1) {
                    fullAgenda.get(j).get((int) k / 4).new_task_2 = agenda_back.get(i);
                }
                else if(k%4 == 2) {
                    fullAgenda.get(j).get((int) k / 4).new_task_3 = agenda_back.get(i);
                }
                else if(k%4 == 3) {
                    fullAgenda.get(j).get((int) k / 4).new_task_4 = agenda_back.get(i);
                }
            }
            if(k%4 == 0) {
                fullAgenda.get(j).get((int) k / 4).task_1 = agenda.get(j).get(k);
            }
            else if(k%4 == 1) {
                fullAgenda.get(j).get((int) k / 4).task_2 = agenda.get(j).get(k);
            }
            else if(k%4 == 2) {
                fullAgenda.get(j).get((int) k / 4).task_3 = agenda.get(j).get(k);
            }
            else if(k%4 == 3) {
                fullAgenda.get(j).get((int) k / 4).task_4 = agenda.get(j).get(k);
            }
        }

        Log.w("AGENDA_BACK", agenda_back.toString());
        Log.w("AGENDA", agenda.toString());
    }

    private void writeInFuturAgenda() {
        for(int i = 0; i < futur_agenda_back.size(); i++) {
            int j = i/96;
            int k = i%96;

            if ("SPORT".equals(futur_agenda_back.get(i))) {
                futurAgenda.get(j).set(k, TimeSlot.CurrentTask.SPORT);

            } else if ("WORK".equals(futur_agenda_back.get(i))) {
                futurAgenda.get(j).set(k, TimeSlot.CurrentTask.WORK);

            } else if ("EAT".equals(futur_agenda_back.get(i))) {
                futurAgenda.get(j).set(k, TimeSlot.CurrentTask.EAT);

            } else if ("FREE".equals(futur_agenda_back.get(i))) {
                futurAgenda.get(j).set(k, TimeSlot.CurrentTask.FREE);

            } else if ("WORK_FIX".equals(futur_agenda_back.get(i))) {
                futurAgenda.get(j).set(k, TimeSlot.CurrentTask.WORK_FIX);

            } else if ("MORNING_ROUTINE".equals(futur_agenda_back.get(i))) {
                futurAgenda.get(j).set(k, TimeSlot.CurrentTask.MORNING_ROUTINE);

            } else if ("SLEEP".equals(futur_agenda_back.get(i))) {
                futurAgenda.get(j).set(k, TimeSlot.CurrentTask.SLEEP);

            } else if ("PAUSE".equals(futur_agenda_back.get(i))) {
                futurAgenda.get(j).set(k, TimeSlot.CurrentTask.PAUSE);
            }
            else if ("WORK_CATCH_UP".equals(futur_agenda_back.get(i))) {
                this.futurAgenda.get(j).set(k, TimeSlot.CurrentTask.WORK_CATCH_UP);
            }
            else if ("SPORT_CATCH_UP".equals(futur_agenda_back.get(i))) {
                this.futurAgenda.get(j).set(k, TimeSlot.CurrentTask.SPORT_CATCH_UP);
            }
            else{
                futurAgenda.get(j).set(k, TimeSlot.CurrentTask.NEWEVENT);
                newEventFuturAgenda.get(j).set(k, futur_agenda_back.get(i));
                if(k%4 == 0) {
                    futurFullAgenda.get(j).get((int) k / 4).new_task_1 = futur_agenda_back.get(i);
                }
                else if(k%4 == 1) {
                    futurFullAgenda.get(j).get((int) k / 4).new_task_2 = futur_agenda_back.get(i);
                }
                else if(k%4 == 2) {
                    futurFullAgenda.get(j).get((int) k / 4).new_task_3 = futur_agenda_back.get(i);
                }
                else if(k%4 == 3) {
                    futurFullAgenda.get(j).get((int) k / 4).new_task_4 = futur_agenda_back.get(i);
                }
            }
            if(k%4 == 0) {
                futurFullAgenda.get(j).get((int) k / 4).task_1 = futurAgenda.get(j).get(k);
            }
            else if(k%4 == 1) {
                futurFullAgenda.get(j).get((int) k / 4).task_2 = futurAgenda.get(j).get(k);
            }
            else if(k%4 == 2) {
                futurFullAgenda.get(j).get((int) k / 4).task_3 = futurAgenda.get(j).get(k);
            }
            else if(k%4 == 3) {
                futurFullAgenda.get(j).get((int) k / 4).task_4 = futurAgenda.get(j).get(k);
            }
        }

        Log.w("AGENDA_BACK", futur_agenda_back.toString());
        Log.w("AGENDA", futurAgenda.toString());
    }

    private void getPastAgenda(char charAt) {
        if(charAt == '[' || charAt == ']')
            return;
        else if(charAt == ',') {
            past_agenda_back.add(currentPast);
            currentPast = new String();
        }
        else
            currentPast = currentPast + charAt;

    }

    private void getAgenda(char charAt) {
        if(charAt == '[' || charAt == ']')
            return;
        else if(charAt == ',') {
            agenda_back.add(current);
            current = new String();
        }
        else
            current = current + charAt;

    }

    private void getFuturAgenda(char charAt) {
        if(charAt == '[' || charAt == ']')
            return;
        else if(charAt == ',') {
            futur_agenda_back.add(currentFutur);
            currentFutur = new String();
        }
        else
            currentFutur = currentFutur + charAt;

    }


/*

Fonction de conversion en jour passé et recalcul du septième jour.

 */

    public void convertInPastDay(){
        int actual_day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        int daySinceLastConnection = actual_day - this.lastConnection.get(Calendar.DAY_OF_YEAR);

        if (daySinceLastConnection > 0) {
            int offset;

            offset = (convertedIndice() - daySinceLastConnection) % 7;            //- daySinceLastConnection pour avoir le jour d'avant

            for (int i = 0; i < daySinceLastConnection; i++) {
                int val = (i + offset) % 7;
                if (val < 0) {
                    val += 7;
                }
                //ArrayList<ArrayList<TimeSlot>> copy = (ArrayList<ArrayList<TimeSlot>>)this.fullAgenda.clone();
                ArrayList<TimeSlot> copy = new ArrayList<>(fullAgenda.get(val).size());
                //ArrayList<ArrayList<TimeSlot>> copy = fullAgenda.stream().collect(Collectors.toCollection());
                //Collections.copy(copy, fullAgenda);

                for (TimeSlot task : fullAgenda.get(val)) {
                    copy.add(new TimeSlot(task));
                }

                this.pastFullAgenda.add(copy);

                List<Boolean> copyCanceled = new ArrayList<>(canceled_slots.get(val).size());

                for (boolean canceled : canceled_slots.get(val)) {
                    copyCanceled.add(canceled);
                }

                this.pastCanceledSlots.add(copyCanceled);

                int nbWorkDay = 0;
                for (int j = 0; j < freeDay.length; j++) {
                    if (!freeDay[j]) {
                        nbWorkDay++;
                    }
                }
                int[] freedaylist = new int[nbWorkDay];
                int n = 0;
                for (int j = 0; j < this.freeDay.length; j++) {
                    if (!freeDay[j]) {
                        freedaylist[n] = j;
                        n++;
                    }
                }

                boolean freeday = true;
                boolean nextfreeday = true;
                boolean pastfreeday = true;
                for (int k = 0; k < freedaylist.length; k++) {
                    if (freedaylist[k] == (i + conversionDayIndice() - daySinceLastConnection + (daySinceLastConnection / 7) * 7) % 7) { // (daySinceLastConnection / 7) * 7) make it >= 0
                        freeday = false;
                    }
                    if (freedaylist[k] == (i + 1 + conversionDayIndice() - daySinceLastConnection + (daySinceLastConnection / 7) * 7) % 7) {
                        nextfreeday = false;
                    }
                    if (freedaylist[k] == (i - 1 + 7 + conversionDayIndice() - daySinceLastConnection + (daySinceLastConnection / 7) * 7) % 7) {
                        pastfreeday = false;
                    }
                }

                if (!freeday) {
                    this.lateWorkSlot += Float.parseFloat(nbWorkHours) / (float) nbWorkDay;
                }

                if (sportRoutine == 2) {
                    this.lateSportSlot += 4;
                } else {
                    this.lateSportSlot += 2;
                }

                AgendaInitialisation plan = new AgendaInitialisation(this, freeday, nextfreeday, pastfreeday, val, true);


                int dayCalcul = (i - daySinceLastConnection) % 7;
                if (dayCalcul < 0) {
                    dayCalcul += 7;
                }

                DayPlan Agent = new DayPlan(this.weight, this.canceled_slots.get(val), this.sportDayRank, this.lastConnection,
                        this.settingDay, plan.daily_slots_generated, this.agenda.get(val),
                        this.newEventAgenda.get(val), dayCalcul, this.savedEvent, freeday,
                        Integer.parseInt(this.optWorkTime), this.lateWorkSlot, this.workCatchUp,
                        this.sportRoutine, this.lateSportSlot, this.sportCatchUp, this.agendaInit, true, false);
                Agent.planDay();
                this.sportDayRank = Agent.rank;
                this.agenda.set(val, Agent.dailyAgenda);
                this.lateSportSlot = Agent.sportSlot;
                this.lateWorkSlot = Agent.workSlot;
                this.workCatchUp = Agent.workSlotCatchUp;
                this.sportCatchUp = Agent.sportCatchUp;

                int position;
                for (int j = 0; j < 96; j += 4) {
                    position = j / 4;
                    this.fullAgenda.get(val).get(position).task_1 = this.agenda.get(val).get(j);
                    this.fullAgenda.get(val).get(position).task_2 = this.agenda.get(val).get(j + 1);
                    this.fullAgenda.get(val).get(position).task_3 = this.agenda.get(val).get(j + 2);
                    this.fullAgenda.get(val).get(position).task_4 = this.agenda.get(val).get(j + 3);
                }

                for (int j = 0 ; j < this.canceled_slots.get(val).size(); j++) {
                    this.canceled_slots.get(val).set(j, false);
                }

            }
            this.lastConnection = Calendar.getInstance();
            this.lastConnection.setTimeInMillis(System.currentTimeMillis());
        }
        else if (daySinceLastConnection < 0){
            for (int i = pastAgendaSize; i > this.pastAgendaSize + daySinceLastConnection; i--) { //daySinceLastConnection <0
                if (pastFullAgenda.size() > 0) {
                    this.pastFullAgenda.remove(i - 1);
                    this.pastCanceledSlots.remove(i-1);
                }
            }

            /*int offset_indice = convertedIndice() + daySinceLastConnection;
            while (offset_indice < 0){
                offset_indice += 7;
            }

            ArrayList<ArrayList<TimeSlot.CurrentTask>> dailyTasks = new ArrayList<>(this.agenda);
            ArrayList<ArrayList<TimeSlot>> week = new ArrayList<>(this.fullAgenda);
            ArrayList<ArrayList<String>> newEventWeek = new ArrayList<>(this.newEventAgenda);

            for (int i = 0; i < 7; i++) {
                int indice = (i + offset_indice)%7;
                dailyTasks.set(i, this.agenda.get(indice));
                week.set(i,this.fullAgenda.get(indice));
                newEventWeek.set(i,this.newEventAgenda.get(indice));
            }

            remove_canceled_days();

            this.agenda = dailyTasks;
            this.fullAgenda = week;
            this.newEventAgenda = newEventWeek;

            this.settingDay = Calendar.getInstance();*/
            this.lastConnection = Calendar.getInstance();
            this.lastConnection.setTimeInMillis(System.currentTimeMillis());
        }
    }


/*

Fonction utilitaire de mises à jour de variable

 */

    public void remove_canceled_days() {
        for (int i = 0; i < 7; i++) {
            for(int j = 0; j < 96; j++) {
                this.canceled_slots.get(i).set(j, Boolean.FALSE);
            }
        }
    }

    public void updateFullAgenda(int currentDay) {
        int position;
        for(int i = 0; i < 96; i +=4 ) {
            position = i/4;
            this.fullAgenda.get(currentDay).get(position).task_1 =  this.agenda.get(currentDay).get(i);
            this.fullAgenda.get(currentDay).get(position).task_2 =  this.agenda.get(currentDay).get(i+1);
            this.fullAgenda.get(currentDay).get(position).task_3 =  this.agenda.get(currentDay).get(i+2);
            this.fullAgenda.get(currentDay).get(position).task_4 =  this.agenda.get(currentDay).get(i+3);
            this.fullAgenda.get(currentDay).get(position).new_task_1 =  this.newEventAgenda.get(currentDay).get(i);
            this.fullAgenda.get(currentDay).get(position).new_task_2 =  this.newEventAgenda.get(currentDay).get(i+1);
            this.fullAgenda.get(currentDay).get(position).new_task_3 =  this.newEventAgenda.get(currentDay).get(i+2);
            this.fullAgenda.get(currentDay).get(position).new_task_4 =  this.newEventAgenda.get(currentDay).get(i+3);
        }
    }

    public void updateFuturAgenda(int currentDay) {
        int position;
        for(int i = 0; i < 96; i +=4 ) {
            position = i/4;
            this.futurFullAgenda.get(currentDay).get(position).task_1 =  this.futurAgenda.get(currentDay).get(i);
            this.futurFullAgenda.get(currentDay).get(position).task_2 =  this.futurAgenda.get(currentDay).get(i+1);
            this.futurFullAgenda.get(currentDay).get(position).task_3 =  this.futurAgenda.get(currentDay).get(i+2);
            this.futurFullAgenda.get(currentDay).get(position).task_4 =  this.futurAgenda.get(currentDay).get(i+3);
            this.futurFullAgenda.get(currentDay).get(position).new_task_1 =  this.newEventFuturAgenda.get(currentDay).get(i);
            this.futurFullAgenda.get(currentDay).get(position).new_task_2 =  this.newEventFuturAgenda.get(currentDay).get(i+1);
            this.futurFullAgenda.get(currentDay).get(position).new_task_3 =  this.newEventFuturAgenda.get(currentDay).get(i+2);
            this.futurFullAgenda.get(currentDay).get(position).new_task_4 =  this.newEventFuturAgenda.get(currentDay).get(i+3);
        }
    }

    public void updatePastAgenda(int currentDay) {
        int position;
        for(int i = 0; i < 96; i +=4 ) {
            position = i/4;
            this.pastFullAgenda.get(currentDay).get(position).task_1 =  this.pastAgenda.get(currentDay).get(i);
            this.pastFullAgenda.get(currentDay).get(position).task_2 =  this.pastAgenda.get(currentDay).get(i+1);
            this.pastFullAgenda.get(currentDay).get(position).task_3 =  this.pastAgenda.get(currentDay).get(i+2);
            this.pastFullAgenda.get(currentDay).get(position).task_4 =  this.pastAgenda.get(currentDay).get(i+3);
            this.pastFullAgenda.get(currentDay).get(position).new_task_1 =  this.newEventPastAgenda.get(currentDay).get(i);
            this.pastFullAgenda.get(currentDay).get(position).new_task_2 =  this.newEventPastAgenda.get(currentDay).get(i+1);
            this.pastFullAgenda.get(currentDay).get(position).new_task_3 =  this.newEventPastAgenda.get(currentDay).get(i+2);
            this.pastFullAgenda.get(currentDay).get(position).new_task_4 =  this.newEventPastAgenda.get(currentDay).get(i+3);
        }
    }


/*

Fonction de conversion d'indice

 */
    private int convertedIndice() {
        int setting_day = this.settingDay.get(Calendar.DAY_OF_YEAR);
        int actual_day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        int year_offset =  Calendar.getInstance().get(Calendar.YEAR) - this.settingDay.get(Calendar.YEAR);
        int offset = 365*year_offset + actual_day - setting_day + (int) (0.25*(year_offset + 3));

        while (offset < 0){
            offset += 7;
        }

        return offset%7;
    }

    private int conversionDayIndice() {
        int offset = 0;
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.MONDAY:
                offset = 0;
                break;
            case Calendar.TUESDAY:
                offset = 1;
                break;
            case Calendar.WEDNESDAY:
                offset = 2;
                break;
            case Calendar.THURSDAY:
                offset = 3;
                break;
            case Calendar.FRIDAY:
                offset = 4;
                break;
            case Calendar.SATURDAY:
                offset = 5;
                break;
            case Calendar.SUNDAY:
                offset = 6;
                break;

        }

        return offset;
    }

/*

Fonction de control d'heure et de conversion d'heure en tranche horaire.

 */
    public boolean stringWorkTimeToSlot(String time){
        boolean ok;
        boolean entier = true;
        String hour = "0";
        String min = "0";
        float hourt;
        float mint;
        int nbHC = 0;

        for (int i = 0; i < time.length(); i++){
            if(time.charAt(i) != ':' && entier && nbHC < 2) {
                hour += time.charAt(i);
                nbHC++;
            }
            else if (time.charAt(i) != ':'){
                min += time.charAt(i);
            }
            else if (time.charAt(i) == ':'){
                entier = false;
            }
        }
        try {
            int intHourt=0;
            hourt = Float.parseFloat(hour);
            mint = Float.parseFloat(min);
            ok = true;
            if (hourt > 70 || hourt < 0){
                ok = false;
            }
            if (hourt == 70 && mint > 0){
                ok = false;
            }
            if (mint >= 60 || mint < 0){
                ok = false;
            }
            else {
                hourt = hourt + roundFifty(mint/60);
                hourt = hourt*4;
                intHourt = (int)hourt;
            }
            if(ok){
                this.nbWorkHours = String.valueOf(intHourt);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ok=false;
        }
        return ok;
    }

    public boolean stringOptWorkTimeToSlot(String time){
        boolean ok;
        boolean entier = true;
        String hour = "0";
        String min = "0";
        float hourt;
        float mint;
        int nbHC = 0;

        for (int i = 0; i < time.length(); i++){
            if(time.charAt(i) != ':' && entier && nbHC < 2) {
                hour += time.charAt(i);
                nbHC++;
            }
            else if (time.charAt(i) != ':'){
                min += time.charAt(i);
            }
            else if (time.charAt(i) == ':'){
                entier = false;
            }
        }
        try {
            int intHourt=0;
            hourt = Float.parseFloat(hour);
            mint = Float.parseFloat(min);
            ok = true;
            if (hourt > 2 || hourt < 0){
                ok = false;
            }
            if (hourt == 2 && mint > 0){
                ok = false;
            }
            if (mint >= 60 || mint < 0){
                ok = false;
            }
            if (hourt == 0 && mint < 30){
                ok = false;
            }
            else {
                hourt = hourt + roundFifty(mint/60);
                hourt = hourt*4;
                intHourt = (int)hourt;
            }
            if(ok){
                this.optWorkTime = String.valueOf(intHourt);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ok=false;
        }
        return ok;
    }

    public String slotStringToTimeString(String slotTime){
        String slot = "";
        String hour = "";
        String min = "";

        for (int i = 0; i < slotTime.length(); i++){
            slot += slotTime.charAt(i);
        }

        int nbSlot = Integer.parseInt(slot);
        min = String.valueOf((nbSlot % 4)*15);
        hour = String.valueOf((nbSlot - ((nbSlot % 4)))/4);
        hour += ':';
        if (min.length() > 2){
            min = min.substring(0,2);
        }
        if (min.length()<2){
            while(min.length()<2){
                min += '0';
            }
        }
        hour += min;
        return hour;
    }

    public boolean stringTimewakeUpToFloat(String time){
        boolean ok;
        boolean entier = true;
        String hour = "0";
        String min = "0";
        float hourt;
        float mint;
        int nbHC = 0;

        for (int i = 0; i < time.length(); i++){
            if(time.charAt(i) != ':' && entier && nbHC < 2) {
                hour += time.charAt(i);
                nbHC++;
            }
            else if (time.charAt(i) != ':'){
                min += time.charAt(i);
            }
            else if (time.charAt(i) == ':'){
                entier = false;
            }
        }
        try {
            hourt = Float.parseFloat(hour);
            mint = Float.parseFloat(min);
            ok = true;
            if (hourt > 12 || hourt < 0){
                ok = false;
            }
            if (hourt == 12 && mint > 0){
                ok = false;
            }
            if (mint >= 60 || mint < 0){
                ok = false;
            }
            else {
                hourt = hourt + roundFifty(mint/60);
                while (hourt>24){
                    hourt -=24;
                }
            }
            if(ok){
                this.wakeUp = String.valueOf(hourt);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ok=false;
        }
        return ok;
    }

    public String floatStringToTimeString(String floatTime){
        boolean entier = true;
        String hour = "";
        String min = "";

        for (int i = 0; i < floatTime.length(); i++){
            if(floatTime.charAt(i) != '.' && entier) {
                hour += floatTime.charAt(i);
            }
            else if (floatTime.charAt(i) != '.'){
                min += floatTime.charAt(i);
            }
            else if (floatTime.charAt(i) == '.'){
                hour += ':';
                entier = false;
            }
        }

        int mint = Integer.parseInt(min)*60;
        min = String.valueOf(mint);
        if (min.length() > 2){
            min = min.substring(0,2);
        }
        if (min.length()<2){
            while(min.length()<2){
                min += '0';
            }
        }
        hour += min;
        return hour;
    }
    private float roundFifty(float fmin){
        float min = 0;
        final float zero = 0;
        final float one = 1;
        final float two = 2;
        final float three = 3;
        final float four = 4;
        final float five = 5;
        final float seven = 7;
        final float height = 8;

        if (fmin >= zero && fmin < one/height){
            min = 0;
        }
        else if (fmin >= one/height && fmin < three/height){
            min = one/four;
        }
        else if (fmin >= three/height && fmin < five/height){
            min = one/two;
        }
        else if (fmin >= five/height && fmin < seven/height){
            min = three/four;
        }
        else if (fmin >= seven/height && fmin < 1){
            min = 1;
        }

        return min;
    }
}
