package CooperativeHunting;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Handle displaying charts in GUI
 */
class ChartDrawer extends JFrame {
    /**
     * How many previous iterations to display
     */
    private final static int DATA_MAX_LENGTH = 500;

    /**
     * How many times the chart update per second
     */
    private final static int UPDATE_RATE = 10;

    /**
     * Chart width in Pixel
     */
    private final static int CHART_WIDTH = 1000;

    /**
     * Chart width in Pixel
     */
    private final static int CHART_HEIGHT = 250;

    /**
     * Whether the chart window is displaying
     */
    private static boolean isDisplaying = false;

    /**
     * Holds 3 chart panels in the window
     */
    private List<XChartPanel<XYChart>> chartPanels;

    /**
     * ChartDrawer constructor
     *
     * @param charts list of charts to draw
     */
    private ChartDrawer(List<XYChart> charts) {
        super("Graphs");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));
        add(rootPanel);

        // create chart panel for each chart
        chartPanels = new ArrayList<>();
        for (XYChart chart : charts) {
            XChartPanel<XYChart> panel = new XChartPanel<>(chart);
            panel.setSize(CHART_WIDTH, CHART_HEIGHT);
            rootPanel.add(panel);
            chartPanels.add(panel);
        }

        pack();
    }

    /**
     * Repaint every chart panels
     */
    private void repaintCharts() {
        for (XChartPanel<XYChart> chartPanel : chartPanels) {
            chartPanel.revalidate();
            chartPanel.repaint();
        }
    }

    /**
     * Show the Chart window. At anytime, there's only one chart window
     *
     * @param map Map object to get data from
     */
    synchronized static void display(Map map) {
        // check for empty data
        if (map.avgFoodGainedPerIteration.size() == 0) {
            GUI.alert("Warning", "There's no data to create charts");
            return;
        }

        // only one chart window can exist
        if (isDisplaying)
            return;
        isDisplaying = true;

        double[][] initData = generateData(map);

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

                final double[][] data = generateData(map);
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

    /**
     * Extract and process data from map
     *
     * @param map map to get data
     * @return double array of [iteration data, predator count, prey count, prey/predator ratio, avg food gain]
     */
    private static synchronized double[][] generateData(Map map) {
        try {
            map.outputChartDataLock.lock();

            // truncate data size
            while (map.predatorPopulationPerIteration.size() > DATA_MAX_LENGTH)
                map.predatorPopulationPerIteration.remove();
            while (map.preyPopulationPerIteration.size() > DATA_MAX_LENGTH)
                map.preyPopulationPerIteration.remove();
            while (map.avgFoodGainedPerIteration.size() > DATA_MAX_LENGTH)
                map.avgFoodGainedPerIteration.remove();

            int size = map.predatorPopulationPerIteration.size();

            // process data
            double[] iterationData = new double[size];
            double[] predatorData = new double[size];
            double[] preyData = new double[size];
            double[] ratioData = new double[size];
            double[] avgFoodGainedData = new double[size];
            Iterator iteratorPredator = map.predatorPopulationPerIteration.iterator();
            Iterator iteratorPrey = map.preyPopulationPerIteration.iterator();
            Iterator iteratorAvgFood = map.avgFoodGainedPerIteration.iterator();

            for (int i = 0; i < size; i++) {
                iterationData[i] = map.numberOfIteration - predatorData.length + 1 + i;
                predatorData[i] = (double) iteratorPredator.next();
                preyData[i] = (double) iteratorPrey.next();
                avgFoodGainedData[i] = (double) iteratorAvgFood.next();
                ratioData[i] = (predatorData[i] == 0) ? 0 : (preyData[i] / predatorData[i]);
            }

            return new double[][]{iterationData, predatorData, preyData, ratioData, avgFoodGainedData};
        } finally {
            map.outputChartDataLock.unlock();
        }
    }
}
