package nl.bioinf.gse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.chart.annotations.XYTextAnnotation;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ScatterPlot {

    // Method to create a dataset from the top 20 GSEA records
    private static DefaultXYDataset createDataset(List<GSEARecord> results) {
        // Sort the results by enrichment score in descending order and take the top 20
        List<GSEARecord> topResults = results.stream()
                .sorted(Comparator.comparingDouble(GSEARecord::enrichmentScore).reversed())
                .limit(20)
                .collect(Collectors.toList());

        // Prepare the dataset
        DefaultXYDataset dataset = new DefaultXYDataset();
        double[][] data = new double[2][topResults.size()]; // [0] for x (p-value), [1] for y (enrichment score)

        for (int i = 0; i < topResults.size(); i++) {
            GSEARecord record = topResults.get(i);
            data[0][i] = record.pValue();         // X-axis (p-value)
            data[1][i] = record.enrichmentScore(); // Y-axis (enrichment score)
        }

        dataset.addSeries("Top 20 Pathways", data);
        return dataset;
    }

    // Method to add labels (pathway IDs) to each point in the scatter plot
    private static void addLabels(XYPlot plot, List<GSEARecord> topResults) {
        for (GSEARecord record : topResults) {
            double x = record.pValue();
            double y = record.enrichmentScore();
            String label = record.pathwayID(); // Assuming pathwayID is a method that returns the pathway ID

            // Create an annotation for each point
            XYTextAnnotation annotation = new XYTextAnnotation(label, x, y);
            annotation.setFont(new Font("SansSerif", Font.PLAIN, 10));
            annotation.setTextAnchor(TextAnchor.HALF_ASCENT_CENTER); // Adjust positioning of the label

            // Add the annotation to the plot
            plot.addAnnotation(annotation);
        }
    }

    // Method to create the scatter plot chart
    private static JFreeChart createChart(DefaultXYDataset dataset, List<GSEARecord> topResults) {
        JFreeChart chart = ChartFactory.createScatterPlot(
                "Top 20 Pathways by Enrichment Score",  // chart title
                "P-Value",                               // x-axis label
                "Enrichment Score",                      // y-axis label
                dataset,                                 // dataset
                PlotOrientation.VERTICAL,
                true,                                    // include legend
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        addLabels(plot, topResults);

        // Set the x-axis range explicitly
        plot.getDomainAxis().setRange(-0.1, 0.5);

        return chart;
    }

    // Method to display the chart in a JFrame
    static void showChart(List<GSEARecord> results) {
        // Sort and select top 20 results
        List<GSEARecord> topResults = results.stream()
                .sorted(Comparator.comparingDouble(GSEARecord::enrichmentScore).reversed())
                .limit(20)
                .collect(Collectors.toList());

        DefaultXYDataset dataset = createDataset(topResults);
        JFreeChart chart = createChart(dataset, topResults);

        // Create and set up the window
        JFrame frame = new JFrame("Scatter Plot of Top 20 Pathways");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Add the chart to a ChartPanel and then add it to the frame
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        frame.add(chartPanel, BorderLayout.CENTER);

        // Display the window
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true);
    }
}
