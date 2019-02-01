package CooperativeHunting;

import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import java.util.Iterator;
import java.util.LinkedList;

public class GraphDrawer {
    //static LinkedList<Double> queue = new LinkedList<>();
    public static void play(LinkedList<Double> rawQueue) throws Exception {

        //queue.add(0.0);
        double[][] initdata = generateData(rawQueue);

        // Create Chart
        final XYChart chart = QuickChart.getChart("Ratio of population between predator and prey", "Iteration", "Ratio", "ratio", initdata[0], initdata[1]);

        // Show it
        final SwingWrapper<XYChart> sw = new SwingWrapper<XYChart>(chart);
        sw.displayChart();

        while (true) {

            //phase += 2 * Math.PI * 2 / 20.0;

            Thread.sleep(100);

            final double[][] data = generateData(rawQueue);

            chart.updateXYSeries("ratio", data[0], data[1], null);
            sw.repaintChart();
        }

    }

    private static double[][] generateData(LinkedList<Double> queue) {

        while(queue.size() > 10){
            queue.remove();
        }
        double[] xData = new double[queue.size()];
        double[] yData = new double[queue.size()];
        Iterator iterator = queue.iterator();
        for (int i = 0; i < xData.length; i++) {
            double radians = Map.numberOfIteration - xData.length + 1 + i;
            xData[i] = radians;
            yData[i] = (double) iterator.next();
        }
        return new double[][] { xData, yData };
    }
}
