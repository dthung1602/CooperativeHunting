package CooperativeHunting;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.locks.Lock;

class ChartDrawer extends JFrame {
    private final static int DATA_MAX_LENGTH = 100; // how many previous iterations to display
    private final static int UPDATE_RATE = 10;      // how many times the chart update per second

    private final static int CHART_WIDTH = 1000;
    private final static int CHART_HEIGHT = 250;

    private static boolean isDisplaying = false;

    private List<XChartPanel<XYChart>> chartPanels;

    private ChartDrawer(List<XYChart> charts) {
        super("Graphs");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));
        add(rootPanel);

        chartPanels = new ArrayList<>();
        for (XYChart chart : charts) {
            XChartPanel<XYChart> panel = new XChartPanel<>(chart);
            panel.setSize(CHART_WIDTH, CHART_HEIGHT);
            rootPanel.add(panel);
            chartPanels.add(panel);
        }

        pack();
    }

    private void repaintCharts() {
        for (XChartPanel<XYChart> chartPanel : chartPanels) {
            chartPanel.revalidate();
            chartPanel.repaint();
        }
    }

    synchronized static void display(Queue<Double> rawPredatorPopulation, Queue<Double> rawPreyPopulation,
                                     Queue<Double> rawAvgFoodGain, Lock lock, Integer iterationCount) {
        // check for empty data
        if (rawAvgFoodGain.size() == 0) {
            GUI.alert("Warning", "There's no data to create charts");
            return;
        }

        // only one chart window can exist
        if (isDisplaying)
            return;
        isDisplaying = true;

        double[][] initData = generateData(rawPredatorPopulation, rawPreyPopulation, rawAvgFoodGain,
                lock, iterationCount);

        // create charts
        final XYChart populationChart = new XYChartBuilder()
                .title("Predator and Prey population")
                .xAxisTitle("Iteration").yAxisTitle("Population")
                .width(CHART_WIDTH).height(CHART_HEIGHT)
                .build();
        final XYChart ratioChart = new XYChartBuilder()
                .title("Ratio of population between prey and predator")
                .xAxisTitle("Iteration").yAxisTitle("Ratio")
                .width(CHART_WIDTH).height(CHART_HEIGHT)
                .build();
        final XYChart avgFoodGainChart = new XYChartBuilder()
                .title("Average food gained per iteration")
                .xAxisTitle("Iteration").yAxisTitle("Food gained")
                .width(CHART_WIDTH).height(CHART_HEIGHT)
                .build();
        List<XYChart> charts = Arrays.asList(populationChart, ratioChart, avgFoodGainChart);

        // add series to charts
        XYSeries series1 = populationChart.addSeries("Predator's population", initData[0], initData[1], null);
        series1.setLineColor(Color.GREEN);
        series1.setMarker(SeriesMarkers.NONE);
        XYSeries series2 = populationChart.addSeries("Prey's population", initData[0], initData[2], null);
        series2.setLineColor(Color.BLUE);
        series2.setMarker(SeriesMarkers.NONE);
        XYSeries series3 = ratioChart.addSeries("Prey/Predator Ratio", initData[0], initData[3], null);
        series3.setLineColor(Color.ORANGE);
        series3.setMarker(SeriesMarkers.NONE);
        XYSeries series4 = avgFoodGainChart.addSeries("Average food gained", initData[0], initData[4], null);
        series4.setLineColor(Color.MAGENTA);
        series4.setMarker(SeriesMarkers.NONE);

        // show chart on another thread
        Thread thread = new Thread(() -> {
            ChartDrawer chartDrawer = new ChartDrawer(charts);
            chartDrawer.setVisible(true);

            // update
            while (chartDrawer.isVisible()) {
                try {
                    Thread.sleep(1000 / UPDATE_RATE);
                } catch (InterruptedException e) {
                    break;
                }
                final double[][] data = generateData(rawPredatorPopulation, rawPreyPopulation, rawAvgFoodGain,
                        lock, iterationCount);
                populationChart.updateXYSeries("Predator's population", data[0], data[1], null);
                populationChart.updateXYSeries("Prey's population", data[0], data[2], null);
                ratioChart.updateXYSeries("Prey/Predator Ratio", data[0], data[3], null);
                avgFoodGainChart.updateXYSeries("Average food gained", data[0], data[4], null);
                chartDrawer.repaintCharts();
            }

            isDisplaying = false;
        });

        thread.start();
    }

    private static synchronized double[][] generateData(Queue<Double> predatorQueue, Queue<Double> preyQueue,
                                                        Queue<Double> avgQueue, Lock lock, Integer numberOfIteration) {
        try {
            lock.lock();

            // truncate data size
            while (predatorQueue.size() > DATA_MAX_LENGTH)
                predatorQueue.remove();
            while (preyQueue.size() > DATA_MAX_LENGTH)
                preyQueue.remove();
            while (avgQueue.size() > DATA_MAX_LENGTH)
                avgQueue.remove();

            int size = predatorQueue.size();

            // process data
            double[] iterationData = new double[size];
            double[] predatorData = new double[size];
            double[] preyData = new double[size];
            double[] ratioData = new double[size];
            double[] avgFoodGainedData = new double[size];
            Iterator iteratorPredator = predatorQueue.iterator();
            Iterator iteratorPrey = preyQueue.iterator();
            Iterator iteratorAvgFood = avgQueue.iterator();

            for (int i = 0; i < size; i++) {
                iterationData[i] = numberOfIteration - predatorData.length + 1 + i;
                predatorData[i] = (double) iteratorPredator.next();
                preyData[i] = (double) iteratorPrey.next();
                avgFoodGainedData[i] = (double) iteratorAvgFood.next();
                ratioData[i] = (predatorData[i] == 0) ? 0 : preyData[i] / predatorData[i];
            }

            return new double[][]{iterationData, predatorData, preyData, ratioData, avgFoodGainedData};
        } finally {
            lock.unlock();
        }
    }
}
