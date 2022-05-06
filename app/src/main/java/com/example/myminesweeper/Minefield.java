package com.example.myminesweeper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.Arrays;
import java.util.Random;

public class Minefield {
    private int pointsFinishedCount = 0;

    private Context context;

    private String playTime;

    private int minesOnScreen, width, height, minesCount, pointCount;
    private double koefMines;
    public Point[] points;
    private String[] pData;

    public Minefield(Context context){
        this(context, 8, 13, 0.17);
    }
    public Minefield(Context context, int width, int height, double koefMines){
        this.context = context;
        this.width = width;
        this.height = height;
        this.koefMines = koefMines;
        this.pointCount = (width*height);
        this.points = new Point[getPointCount()];
        this.pData = new String[getPointCount()];
        this.minesCount = (int) (Math.ceil(koefMines*getPointCount()));
        this.minesOnScreen = this.minesCount;
        fillEmptyData();
    }

    public void generateNewMinefield(){
        Log.d("myLog", getPointCount()+"");
        this.points = new Point[getPointCount()];
        this.pData = new String[getPointCount()];
        this.minesCount = (int) (Math.ceil(koefMines*getPointCount()));
        fillEmptyData();
        fillMinefield();
        Log.d("myLog", "generateNewMinefield");
    }

    //Отримання загальної кількості позицій
    public int getPointCount() {
        return width*height;
    }
    //отримання коефіцієнту мін
    public double getKoefMines() {
        return koefMines;
    }
    //Отримання масиву обєктів Point
    public Point[] getPoints() {
        return points;
    }
    //Отримання кількості позицій по ширині
    public int getWidth() {
        return width;
    }
    //Отримання загальної кількості мін
    public int getMinesCount() {
        return minesCount;
    }
    //Отримання теперішнього значення знешкоджених мін
    public int getMinesOnScreen() {
        return minesOnScreen;
    }
    //Оновлення кількості мін які були вже знешкоджені
    public void setMinesOnScreen(int minesOnScreen) {
        this.minesOnScreen = minesOnScreen;
    }
    //Отримання часу встановленого часу
    public String getPlayTime() {
        return playTime;
    }
    //Встановлення часу гри
    public void setPlayTime(String playTime) {
        this.playTime = playTime;
    }
    //Заповнення масиву даних тимчасовими пустими значеннями щоб не виникло помилки під час заповнення масиву даними
    public void fillEmptyData(){
        Arrays.fill(pData, "");
    }
    //Отримання даних масиву
    public String[] getData() {
        return pData;
    }
    //Висота
    public int getHeight() {
        return height;
    }
    //Отримання кількості правильно натиснутих пунктів
    public int getPointsFinishedCount() {
        return pointsFinishedCount;
    }
    //Встановити ширину
    public void setWidth(int width) {
        this.width = width;
    }
    //Встановити висоту
    public void setHeight(int height) {
        this.height = height;
    }
    //Встановлює кінцеву кількість пунктів
    public void setPointsFinishedCount(int pointsFinishedCount) {
        this.pointsFinishedCount = pointsFinishedCount;
    }
    //Встановлює коефіцієнт мін на полі
    public void setKoefMines(double koefMines) {
        this.koefMines = koefMines;
    }
    public void setPoints(Point[] points) {
        this.points = points;
    }

    //Метод для заповнення Point[] даними
    public void fillMinefield(){
        int minesPut = 0;
        Random r = new Random();
        do {
            int pos = r.nextInt(getPointCount()+1);
            if(pos== pData.length||pData[pos].equals("*")){
                continue;
            }
            pData[pos] = "*";
            minesPut++;
        }while (minesPut!=minesCount);

        fillAllData();
    }
    //Допоміжний метод для метода fillMinefield знаходить кількість мін навколо точки і вставляє значення в масив
    private void fillAllData(){
        for(int i = 0; i<pData.length; i++){
            if(pData[i].equals("*")){
                continue;
            }
            int countMinesAround = 0;
            int cursor = i-1;
            if(cursor>=0&&(cursor+1)%width!=0)countMinesAround = definingCountMinesAround(cursor, countMinesAround);

            cursor = i+1;
            if(cursor<getPointCount()&&cursor%width!=0)countMinesAround = definingCountMinesAround(cursor, countMinesAround);

            cursor = i-width-1;
            if(cursor>=0&&(cursor+1)%width!=0)countMinesAround = definingCountMinesAround(cursor, countMinesAround);

            cursor = i-width;
            if(cursor>=0)countMinesAround = definingCountMinesAround(cursor, countMinesAround);

            cursor = i-width+1;
            if(cursor>=0&&cursor%width!=0)countMinesAround = definingCountMinesAround(cursor, countMinesAround);

            cursor = i+width-1;
            if(cursor<getPointCount()&&(cursor+1)%width!=0)countMinesAround = definingCountMinesAround(cursor, countMinesAround);

            cursor = i+width;
            if(cursor<getPointCount())countMinesAround = definingCountMinesAround(cursor, countMinesAround);

            cursor = i+width+1;
            if(cursor<getPointCount()&&cursor%width!=0)countMinesAround = definingCountMinesAround(cursor, countMinesAround);

            pData[i]=""+countMinesAround+"";
            if(countMinesAround==0){
                pData[i] = "";
            }
        }
        fillPoints();
    }
    //допоміжний метод для fillAllData для перевірки значення на ""
    private int definingCountMinesAround(int cursor, int countMinesAround){
        if(pData[cursor].equals("*"))countMinesAround++;
        return countMinesAround;
    }

    private void fillPoints(){
        for(int i =0; i<pData.length; i++){
                points[i] = new Point(context, pData[i]);
        }
    }

    public class Point{
        private Context context;
        private int pointColor;

        private boolean isEnabled, isFinished, dataVisibility, isEnabledPoint;
        private Drawable currentState;

        public void setDat(String dat) {
            this.dat = dat;
        }

        private String dat = "";
        //Конструктор задає початкові значення пунктам мінного поля
        public Point(Context context, String data){
            this.context = context;
            setPointDuringFillingData(data);
        }

        public void setPointDuringFillingData(String data){
            this.dat = data;
            this.currentState = context.getDrawable(R.drawable.rect_beforestart);
            this.dataVisibility = false;
            this.isEnabled = false;
            this.isFinished = false;
            pointsFinishedCount=0;
            this.pointColor = setPointColor(data);
        }
        //Встановлює початкові значиння при натисканні на Stop
        public void setPointStop(){
            this.currentState = context.getDrawable(R.drawable.rect_beforestart);
            this.dataVisibility = false;
            this.isEnabled = false;
            this.isFinished = false;
            pointsFinishedCount=0;
        }
        //Встановлює початкове поле при натисканні на кнопку START
        public void setPointStart(){
            setDataVisibility(false);
            setCurrentState(context.getDrawable(R.drawable.rect_start));
            setEnabled(true);
            setEnabledPoint(true);
            setFinished(false);
            pointsFinishedCount=0;
            setPointColor(this.dat);
        }
        private int setPointColor(String data){
            int res = 0;
            switch (data){
                case "1":
                    res = context.getColor(R.color.color1);
                    break;
                case "2":
                    res = context.getColor(R.color.color2);
                    break;
                case "3":
                    res = context.getColor(R.color.color3);
                    break;
                case "4":
                    res = context.getColor(R.color.color4);
                    break;
                case "5":
                    res = context.getColor(R.color.color5);
                    break;
                case "6":
                    res = context.getColor(R.color.color6);
                    break;
                case "7":
                    res = context.getColor(R.color.color7);
                    break;
                case "8":
                    res = context.getColor(R.color.color8);
                    break;
                default:
                    res = Color.WHITE;
            }
            return res;
        }

        public int getPointColor() {
            return pointColor;
        }

        //Виконується на натиснутому непрограшному значенні
        public void setPointClicked(){
            setDataVisibility(true);
            setCurrentState(context.getDrawable(R.drawable.rect_clicked));
            setEnabledPoint(false);
            setFinished(true);
            pointsFinishedCount++;
            Log.d("myLog", "Point finished count = "+pointsFinishedCount);
        }
        //Виконується при довгому натисканні(Ставить міну)
        public void setPointLongClicked(){
            setCurrentState(context.getDrawable(R.drawable.rect_keepclicked));
            setEnabledPoint(false);
        }
        //Виконується при скасуванні поставленої міни
        public void setCancelLongClicked(){
            setDataVisibility(false);
            setCurrentState(context.getDrawable(R.drawable.rect_start));
            setEnabledPoint(true);
        }
        //Встановлюється при програші на натиснуту міну
        public void setPointLost(){
            setCurrentState(context.getDrawable(R.drawable.rect_lost));
            setDataVisibility(true);
            setEnabledPoint(false);
            setEnabled(false);
        }
        //Відобразить правильно вибрані міни під час виграшу або програшу
        public void setPointGoodCheckedMines(){
            setCurrentState(context.getDrawable(R.drawable.rect_goodmineschecked));
            setDataVisibility(true);
            setEnabledPoint(false);
            setFinished(true);
            setEnabled(false);
        }
        //Відобразить неправильно вибрані міни під час програшу
        public void setPointWrongCheckedMines(){
            setCurrentState(context.getDrawable(R.drawable.rect_wrongmineschecked));
            setDataVisibility(true);
            setEnabledPoint(false);
            setFinished(true);
            setEnabled(false);
        }

        //Отримання теперішнього значення заднього плану пункта
        public Drawable getCurrentState() {
            return currentState;
        }
        //Встановлення значення заднього плану пункта
        public void setCurrentState(Drawable currentState) {
            this.currentState = currentState;
        }
        //Отримання видимості даних
        public boolean getDataVisibility() {
            return dataVisibility;
        }
        //Встановлення видимості даних
        public void setDataVisibility(boolean dataVisibility) {
            this.dataVisibility = dataVisibility;
        }
        //Отримання даних з масиву
        public String getData() {
            return dat;
        }
        //Встановлення нових даних в масив
        private void setData(String data) {
            this.dat = data;
        }
        //Отримання чи можна клікати на пункти
        public boolean isEnabled() {
            return isEnabled;
        }
        //Встановлення можливості клікання на всі пункти(Використовується на початку
        // для включення і вкінці для виключення)
        public void setEnabled(boolean enabled) {
            isEnabled = enabled;
        }
        //Отриманна чи заморожений пункт
        public boolean isEnabledPoint() {
            return isEnabledPoint;
        }
        //Встановлення пункту замороженим або незамороженим(потрібно для довгого натискання)
        public void setEnabledPoint(boolean enabledPoint) {
            isEnabledPoint = enabledPoint;
        }
        //Зміна замороженості пункту(потрібно при довгому натисканні)
        public void changeEnablePoint(){
            isEnabledPoint = false;
        }
        //чи обєкт натиснутий
        public boolean isFinished() {
            return isFinished;
        }
        //Встановлення пункта натиснутим
        public void setFinished(boolean finished) {
            isFinished = finished;
        }
    }
}
