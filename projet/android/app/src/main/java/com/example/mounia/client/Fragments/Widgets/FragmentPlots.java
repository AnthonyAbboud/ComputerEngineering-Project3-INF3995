package com.example.mounia.client.Fragments.Widgets;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.Plot;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.example.mounia.client.CommunicationClientServer.CANEnums;
import com.example.mounia.client.CommunicationClientServer.CANMessage;
import com.example.mounia.client.CommunicationClientServer.Communication;
import com.example.mounia.client.Observateurs.Observateur;
import com.example.mounia.client.R;

import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by mounianordine on 18-03-09.
 *  Source : https://bitbucket.org/androidplot/androidplot/src/9088a7b1ae536fceb9b570ec07c2eb39fa0a13a0/demoapp/src/main/java/com/androidplot/demos/DynamicXYPlotActivity.java?fileviewer=file-view-default
 *  Source : https://javapapers.com/android/android-chart-using-androidplot/
 */


public class FragmentPlots extends FragmentWidget implements Observateur {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private XYPlot dynamicPlot;
    private MyPlotUpdater plotUpdater;
    SampleDynamicXYDatasource data;
    private static final int nbSeries = 6;
    private Thread myThread;


    public FragmentPlots() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.layout_plot, container, false);

        dynamicPlot = (XYPlot) layout.findViewById(R.id.dynamicXYPlot);

        plotUpdater = new MyPlotUpdater(dynamicPlot);

        dynamicPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new DecimalFormat("0"));

        dynamicPlot.setRenderMode(Plot.RenderMode.USE_BACKGROUND_THREAD);
        data = new SampleDynamicXYDatasource();
        List<SampleDynamicSeries> sineSeries = new ArrayList<SampleDynamicSeries>(nbSeries);

        int[][] colors = new int[6][3];
        colors[0][0] = 200;
        colors[0][1] = 0;
        colors[0][2] = 0;

        colors[1][0] = 0;
        colors[1][1] = 200;
        colors[1][2] = 0;

        colors[2][0] = 0;
        colors[2][1] = 0;
        colors[2][2] = 200;

        colors[3][0] = 200;
        colors[3][1] = 0;
        colors[3][2] = 200;

        colors[4][0] = 200;
        colors[4][1] = 200;
        colors[4][2] = 0;

        colors[5][0] = 0;
        colors[5][1] = 200;
        colors[5][2] = 200;

        //Color.rgb(0,0,200)
        for(int i = 0; i < nbSeries; i++){
            sineSeries.add(new SampleDynamicSeries(data, i, "Sines"));
            LineAndPointFormatter formatter = new LineAndPointFormatter(Color.rgb(colors[i][0],colors[i][1],colors[i][2]),null,null,null);
            formatter.getLinePaint().setStrokeJoin(Paint.Join.ROUND);
            formatter.getLinePaint().setStrokeWidth(1);
            dynamicPlot.addSeries(sineSeries.get(i),formatter);
        }

        data.addObserver(plotUpdater);

        dynamicPlot.setDomainStepMode(StepMode.INCREMENT_BY_VAL);
        dynamicPlot.setDomainStepValue(1);

        dynamicPlot.setRangeStepMode(StepMode.INCREMENT_BY_VAL);
        dynamicPlot.setDomainStepValue(10);

        dynamicPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new DecimalFormat(("###.#")));

        dynamicPlot.setRangeBoundaries(-100, 100, BoundaryMode.AUTO);

        // create a dash effect for domain and range grid lines:
        DashPathEffect dashFx = new DashPathEffect(
                new float[] {PixelUtils.dpToPix(3), PixelUtils.dpToPix(3)}, 0);
        dynamicPlot.getGraph().getDomainGridLinePaint().setPathEffect(dashFx);
        dynamicPlot.getGraph().getRangeGridLinePaint().setPathEffect(dashFx);


        return layout;
    }

    @Override
    public void onResume() {
        // kick off the data generating thread:
        myThread = new Thread(data);
        myThread.start();
        super.onResume();
    }

    @Override
    public void onPause() {
        data.stopThread();
        super.onPause();
    }

    public static  FragmentPlots newInstance(int sectionNumber) {
        FragmentPlots fragment = new FragmentPlots();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public double data1 = 0.0;
    public double data2 = 0.0;
    public double data3 = 0.0;
    public double data4 = 0.0;
    public double data5 = 0.0;
    public double data6 = 0.0;

    @Override
    public void mettreAJour() {
        for (int i=0; i< Communication.data.size(); i++) {
            CANMessage msg = Communication.data.get(i);
            /*try {
                sem.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            if(msg.msgID == CANEnums.CANSid.L3G_RATE_X){
                data1 = (double) msg.data1;
                Log.i("PLOT_DATA", String.valueOf(data1));
            }
            else if(msg.msgID == CANEnums.CANSid.L3G_RATE_Y){
                data2 = (double) msg.data1;
            }
            else if(msg.msgID == CANEnums.CANSid.L3G_RATE_Z){
                data3 = (double) msg.data1;
            }
            else if(msg.msgID == CANEnums.CANSid.LSM_ACC_X){
                data4 = (double) msg.data1;
            }
            else if(msg.msgID == CANEnums.CANSid.LSM_ACC_Y){
                data5 = (double) msg.data1;
            }
            else if(msg.msgID == CANEnums.CANSid.LSM_ACC_Z){
                data6 = (double) msg.data1;
            }
            //sem.release();
        }
    }

    private class MyPlotUpdater implements Observer
    {
        Plot plot;
        public MyPlotUpdater(Plot plot)
        {
            this.plot = plot;
        }

        @Override
        public void update(Observable o, Object arg) {
            plot.redraw();
        }
    }

    class SampleDynamicXYDatasource implements Runnable {

        // encapsulates management of the observers watching this datasource for update events:
        class MyObservable extends Observable {
            @Override
            public void notifyObservers() {
                setChanged();
                super.notifyObservers();
            }
        }

        private static final double FREQUENCY = 10; // larger is lower frequency
        private static final int MAX_AMP_SEED = 100;
        private static final int MIN_AMP_SEED = 10;
        private static final int AMP_STEP = 1;
        public static final int SERIE1 = 0;
        public static final int SERIE2 = 1;
        public static final int SERIE3 = 2;
        public static final int SERIE4 = 3;
        public static final int SERIE5 = 4;
        public static final int SERIE6 = 5;
        private static final int SAMPLE_SIZE = 31;
        private int phase = 0;
        private int sinAmp = 1;
        private MyObservable notifier;
        private boolean keepRunning = false;

        {
            notifier = new MyObservable();
        }

        public void stopThread() {
            keepRunning = false;
        }

        //@Override
        public void run() {
            try {
                keepRunning = true;
                boolean isRising = true;
                while (keepRunning) {

                    Thread.sleep(100); // decrease or remove to speed up the refresh rate.
                    phase++;
                    if (sinAmp >= MAX_AMP_SEED) {
                        isRising = false;
                    } else if (sinAmp <= MIN_AMP_SEED) {
                        isRising = true;
                    }

                    if (isRising) {
                        sinAmp += AMP_STEP;
                    } else {
                        sinAmp -= AMP_STEP;
                    }
                    notifier.notifyObservers();
                    Log.i("PLOT_DATA", String.valueOf(data1));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public int getItemCount(int series) {
            return SAMPLE_SIZE;
        }

        public Number getX(int series, int index) {
            if (index >= SAMPLE_SIZE) {
                throw new IllegalArgumentException();
            }
            return index;
        }

        public Number getY(int series, int index) {
            if (index >= SAMPLE_SIZE) {
                throw new IllegalArgumentException();
            }
            double angle = (index + (phase))/FREQUENCY;
            double amp;
            switch (series) {
                case SERIE1:
                    amp = sinAmp * Math.sin(angle);
                    return data1;
                case SERIE2:
                    amp = sinAmp * Math.cos(angle);
                    return data2;
                case SERIE3:
                    amp = 5;
                    return data3;
                case SERIE4:
                    amp = -7;
                    return data4;
                case SERIE5:
                    amp = sinAmp * Math.tan(angle);
                    return data5;
                case SERIE6:
                    amp = sinAmp * Math.tan(angle);
                    return data6;
                default:
                    throw new IllegalArgumentException();
            }
        }

        public void addObserver(Observer observer) {
            notifier.addObserver(observer);
        }

        public void removeObserver(Observer observer) {
            notifier.deleteObserver(observer);
        }

    }

    class SampleDynamicSeries implements XYSeries {
        private SampleDynamicXYDatasource datasource;
        private int seriesIndex;
        private String title;

        public SampleDynamicSeries(SampleDynamicXYDatasource datasource, int seriesIndex, String title) {
            this.datasource = datasource;
            this.seriesIndex = seriesIndex;
            this.title = title;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public int size() {
            return datasource.getItemCount(seriesIndex);
        }

        @Override
        public Number getX(int index) {
            return datasource.getX(seriesIndex, index);
        }

        @Override
        public Number getY(int index) {
            return datasource.getY(seriesIndex, index);
        }
    }


}