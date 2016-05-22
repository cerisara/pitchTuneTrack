/*
 * Copyright 2016 Christophe Cerisara <cerisara@loria.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.xtof54.pitchTuneTrack;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.*;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public class PitchTuneTrack extends Activity {
    private PitchSource pitchPoster;
    private TextView frequencyDisplay;
    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        frequencyDisplay = (TextView) findViewById(R.id.frequencyDisplay);

        chart = (LineChart) findViewById(R.id.chart1);
        chart.setDrawGridBackground(true);
        chart.setData(new LineData());
        chart.invalidate();
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "DataSet 1");
        set.setLineWidth(2.5f);
        set.setCircleRadius(4.5f);
        set.setColor(Color.rgb(240, 99, 99));
        set.setCircleColor(Color.rgb(240, 99, 99));
        set.setHighLightColor(Color.rgb(190, 190, 190));
        set.setAxisDependency(AxisDependency.LEFT);
        set.setValueTextSize(10f);
        return set;
    }

    private void addVal(float v) {
        LineData data = chart.getData();
        if(data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            data.addXValue(set.getEntryCount() + "");
            int randomDataSetIndex = (int) (Math.random() * data.getDataSetCount());
            data.addEntry(new Entry(v, set.getEntryCount()), randomDataSetIndex);
            chart.notifyDataSetChanged();
            chart.setVisibleXRangeMaximum(6);
            chart.setVisibleYRangeMaximum(15, AxisDependency.LEFT);
            // this automatically refreshes the chart (calls invalidate())
            chart.moveViewTo(data.getXValCount()-7, 50f, AxisDependency.LEFT);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pitchPoster.stopSampling();
        pitchPoster = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        pitchPoster = new MicrophonePitchSource();
        pitchPoster.setHandler(new UIUpdateHandler());
        pitchPoster.startSampling();
    }

    private final class UIUpdateHandler extends Handler {
        public void handleMessage(Message msg) {
            final MeasuredPitch data = (MeasuredPitch) msg.obj;
            if (data != null && data.decibel > -30) {
                frequencyDisplay.setText(String.format("%.0fHz", data.frequency));
            }
        }
    }
}
