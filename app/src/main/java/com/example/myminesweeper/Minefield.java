package com.example.myminesweeper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import java.util.Arrays;
import java.util.Random;

public class Minefield {
    private int pointsFinishedCount = 0;
    private final Context context;
    private String playTime;
    private int minesOnScreen;
    private int width;
    private int height;
    private int minesCount;
    private double koefMines;
    public Point[] points;
    private String[] pData;

    public Minefield(Context context, int width, int height, double koefMines) {
        this.context = context;
        this.width = width;
        this.height = height;
        this.koefMines = koefMines;
        this.points = new Point[getPointCount()];
        this.pData = new String[getPointCount()];
        this.minesCount = (int) (Math.ceil(koefMines * getPointCount()));
        this.minesOnScreen = this.minesCount;
        fillEmptyData();
    }

    public void generateNewMinefield() {
        this.points = new Point[getPointCount()];
        this.pData = new String[getPointCount()];
        this.minesCount = (int) (Math.ceil(koefMines * getPointCount()));
        fillEmptyData();
        fillMinefield();
    }

    public int getPointCount() {
        return width * height;
    }

    public Point[] getPoints() {
        return points;
    }

    public int getWidth() {
        return width;
    }

    public int getMinesCount() {
        return minesCount;
    }

    public int getMinesOnScreen() {
        return minesOnScreen;
    }

    public void setMinesOnScreen(int minesOnScreen) {
        this.minesOnScreen = minesOnScreen;
    }

    public String getPlayTime() {
        return playTime;
    }

    public void setPlayTime(String playTime) {
        this.playTime = playTime;
    }

    public void fillEmptyData() {
        Arrays.fill(pData, "");
    }

    public String[] getData() {
        return pData;
    }

    public int getHeight() {
        return height;
    }

    public int getPointsFinishedCount() {
        return pointsFinishedCount;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setPointsFinishedCount(int pointsFinishedCount) {
        this.pointsFinishedCount = pointsFinishedCount;
    }

    public void setKoefMines(double koefMines) {
        this.koefMines = koefMines;
    }

    public void fillMinefield() {
        int minesPut = 0;
        Random r = new Random();
        do {
            int pos = r.nextInt(getPointCount() + 1);
            if (pos == pData.length || pData[pos].equals("*")) {
                continue;
            }
            pData[pos] = "*";
            minesPut++;
        } while (minesPut != minesCount);

        fillAllData();
    }

    private void fillAllData() {
        for (int i = 0; i < pData.length; i++) {
            if (pData[i].equals("*")) {
                continue;
            }
            int countMinesAround = 0;
            int cursor = i - 1;
            if (cursor >= 0 && (cursor + 1) % width != 0)
                countMinesAround = definingCountMinesAround(cursor, countMinesAround);
            cursor = i + 1;
            if (cursor < getPointCount() && cursor % width != 0)
                countMinesAround = definingCountMinesAround(cursor, countMinesAround);
            cursor = i - width - 1;
            if (cursor >= 0 && (cursor + 1) % width != 0)
                countMinesAround = definingCountMinesAround(cursor, countMinesAround);
            cursor = i - width;
            if (cursor >= 0) countMinesAround = definingCountMinesAround(cursor, countMinesAround);
            cursor = i - width + 1;
            if (cursor >= 0 && cursor % width != 0)
                countMinesAround = definingCountMinesAround(cursor, countMinesAround);
            cursor = i + width - 1;
            if (cursor < getPointCount() && (cursor + 1) % width != 0)
                countMinesAround = definingCountMinesAround(cursor, countMinesAround);
            cursor = i + width;
            if (cursor < getPointCount())
                countMinesAround = definingCountMinesAround(cursor, countMinesAround);
            cursor = i + width + 1;
            if (cursor < getPointCount() && cursor % width != 0)
                countMinesAround = definingCountMinesAround(cursor, countMinesAround);
            pData[i] = "" + countMinesAround + "";
            if (countMinesAround == 0) {
                pData[i] = "";
            }
        }
        fillPoints();
    }

    private int definingCountMinesAround(int cursor, int countMinesAround) {
        if (pData[cursor].equals("*")) countMinesAround++;
        return countMinesAround;
    }

    private void fillPoints() {
        for (int i = 0; i < pData.length; i++) {
            points[i] = new Point(context, pData[i]);
        }
    }

    public class Point {
        private final Context context;
        private int pointColor;
        private boolean isEnabled, isFinished, dataVisibility, isEnabledPoint;
        private Drawable currentState;

        private String dat = "";

        public Point(Context context, String data) {
            this.context = context;
            setPointDuringFillingData(data);
        }

        public void setPointDuringFillingData(String data) {
            this.dat = data;
            this.currentState = context.getDrawable(R.drawable.rect_beforestart);
            this.dataVisibility = false;
            this.isEnabled = false;
            this.isFinished = false;
            pointsFinishedCount = 0;
            this.pointColor = setPointColor(data);
        }

        public void setPointStop() {
            this.currentState = context.getDrawable(R.drawable.rect_beforestart);
            this.dataVisibility = false;
            this.isEnabled = false;
            this.isFinished = false;
            pointsFinishedCount = 0;
        }

        public void setPointStart() {
            setDataVisibility(false);
            setCurrentState(context.getDrawable(R.drawable.rect_start));
            setEnabled(true);
            setEnabledPoint(true);
            setFinished(false);
            pointsFinishedCount = 0;
            setPointColor(this.dat);
        }

        private int setPointColor(String data) {
            int res;
            switch (data) {
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

        public void setPointClicked() {
            setDataVisibility(true);
            setCurrentState(context.getDrawable(R.drawable.rect_clicked));
            setEnabledPoint(false);
            setFinished(true);
            pointsFinishedCount++;
        }

        public void setPointLongClicked() {
            setCurrentState(context.getDrawable(R.drawable.rect_keepclicked));
            setEnabledPoint(false);
        }

        public void setCancelLongClicked() {
            setDataVisibility(false);
            setCurrentState(context.getDrawable(R.drawable.rect_start));
            setEnabledPoint(true);
        }

        public void setPointLost() {
            setCurrentState(context.getDrawable(R.drawable.rect_lost));
            setDataVisibility(true);
            setEnabledPoint(false);
            setEnabled(false);
        }

        public void setPointGoodCheckedMines() {
            setCurrentState(context.getDrawable(R.drawable.rect_goodmineschecked));
            setDataVisibility(true);
            setEnabledPoint(false);
            setFinished(true);
            setEnabled(false);
        }

        public void setPointWrongCheckedMines() {
            setCurrentState(context.getDrawable(R.drawable.rect_wrongmineschecked));
            setDataVisibility(true);
            setEnabledPoint(false);
            setFinished(true);
            setEnabled(false);
        }

        public Drawable getCurrentState() {
            return currentState;
        }

        public void setCurrentState(Drawable currentState) {
            this.currentState = currentState;
        }

        public boolean getDataVisibility() {
            return dataVisibility;
        }

        public void setDataVisibility(boolean dataVisibility) {
            this.dataVisibility = dataVisibility;
        }

        public String getData() {
            return dat;
        }

        public boolean isEnabled() {
            return isEnabled;
        }

        public void setEnabled(boolean enabled) {
            isEnabled = enabled;
        }

        public boolean isEnabledPoint() {
            return isEnabledPoint;
        }

        public void setEnabledPoint(boolean enabledPoint) {
            isEnabledPoint = enabledPoint;
        }

        public boolean isFinish() {
            return !isFinished;
        }

        public void setFinished(boolean finished) {
            isFinished = finished;
        }
    }
}
