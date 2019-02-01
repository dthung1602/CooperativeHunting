package CooperativeHunting;

import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;

public class GraphDrawer {
    public static void play(LinkedList<Double> rawPredatorQueue, LinkedList<Double> rawPreyQueue) throws Exception {

        double[][] initdata = generateData(rawPredatorQueue, rawPreyQueue);

        // Create Chart
        XYChart chart2 = new XYChartBuilder().width(800).height(600).title("Ratio of population between predator and prey").xAxisTitle("Iteration").yAxisTitle("Population").build();

        // Add series
        XYSeries series1 = chart2.addSeries("Predator's population", initdata[0], initdata[1], null);
        series1.setLineColor(Color.GREEN);
        series1.setMarker(SeriesMarkers.NONE);
        XYSeries series2 = chart2.addSeries("Prey's population", initdata[0], initdata[2], null);
        series2.setLineColor(Color.BLUE);
        series2.setMarker(SeriesMarkers.NONE);
        XYSeries series3 = chart2.addSeries("Visual ratio line", initdata[0], initdata[3], null);
        series3.setLineColor(Color.ORANGE);
        series3.setMarker(SeriesMarkers.NONE);
        // Show it
        final SwingWrapper<XYChart> sw = new SwingWrapper<XYChart>(chart2);
        sw.displayChart();

        while (true) {

            Thread.sleep(100);

            final double[][] data = generateData(rawPredatorQueue, rawPreyQueue);

            chart2.updateXYSeries("Predator's population", data[0], data[1], null);
            chart2.updateXYSeries("Prey's population", data[0], data[2], null);
            chart2.updateXYSeries("Visual ratio line", data[0], data[3], null);
            sw.repaintChart();
        }

    }

    private static double[][] generateData(LinkedList<Double> predatorQueue, LinkedList<Double> preyQueue) {

        while(predatorQueue.size() > 10){
            predatorQueue.remove();
        }
        while(preyQueue.size() > 10){
            preyQueue.remove();
        }
        double[] iterationData = new double[predatorQueue.size()];
        double[] predatorData = new double[predatorQueue.size()];
        double[] preyData = new double[preyQueue.size()];
        double[] ratioLine = new double[predatorQueue.size()];
        Iterator iteratorPredator = predatorQueue.iterator();
        Iterator iteratorPrey = preyQueue.iterator();
        for (int i = 0; i < predatorData.length; i++) {
            iterationData[i] = Map.numberOfIteration - predatorData.length + 1 + i;
            predatorData[i] = (double) iteratorPredator.next();
            preyData[i] = (double) iteratorPrey.next();
            ratioLine[i] = (predatorData[i] - (predatorData[i] - preyData[i])*(preyData[i]/(predatorData[i] + preyData[i])));
        }
        return new double[][] { iterationData, predatorData, preyData, ratioLine };
    }
}
