package nl.bioinf.gse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.chart.ChartUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Boxplot {

    private static DefaultBoxAndWhiskerCategoryDataset createDataset(List<GSEARecord> results) {
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        // Extract enrichment scores and filter out outliers
        List<Double> enrichmentScores = new ArrayList<>();
        for (GSEARecord pathway : results) {
            enrichmentScores.add(pathway.enrichmentScore());
        }

        // Filter out outliers
        List<Double> filteredScores = filterOutliers(enrichmentScores);

        // Add filtered enrichment scores under a single category label
        dataset.add(filteredScores, "Enrichment Scores", "Pathways");

        return dataset;
    }

    private static List<Double> filterOutliers(List<Double> scores) {
        // If there are fewer than 4 scores, return them as they are (not enough data to calculate outliers)
        if (scores.size() < 4) {
            return scores;
        }

        // Sort the scores to calculate quartiles
        List<Double> sortedScores = new ArrayList<>(scores);
        Collections.sort(sortedScores);

        // Calculate Q1 and Q3
        double q1 = sortedScores.get(sortedScores.size() / 4);
        double q3 = sortedScores.get(3 * sortedScores.size() / 4);
        double iqr = q3 - q1;

        // Define bounds for filtering
        double lowerBound = q1 - 1.5 * iqr;
        double upperBound = q3 + 1.5 * iqr;

        // Create a list to store non-outlier scores
        List<Double> filteredScores = new ArrayList<>();
        for (Double score : scores) {
            if (score >= lowerBound && score <= upperBound) {
                filteredScores.add(score);
            }
        }

        return filteredScores;
    }

    // Method to create the boxplot chart
    private static JFreeChart createChart(DefaultBoxAndWhiskerCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(
                "Pathway Enrichment Scores",   // chart title
                "Pathways",                    // domain axis label
                "Enrichment Score",            // range axis label
                dataset,                       // dataset
                true                           // show legend
        );

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setMeanVisible(false);

        // We can still set transparent color for artifact paint if needed, but it shouldn't be necessary now
        Color transparentColor = new Color(0, 0, 0, 0);  // Fully transparent color
        renderer.setArtifactPaint(transparentColor);     // Set artifact paint (outliers) to transparent

        plot.setRenderer(renderer);

        return chart;
    }

    // Method to display the chart in a JFrame, with an option to save as a PNG
    static void showChart(List<GSEARecord> results, boolean savePlot) {
        DefaultBoxAndWhiskerCategoryDataset dataset = createDataset(results);
        JFreeChart chart = createChart(dataset);

        // If savePlot is true, save the chart as a PNG file
        if (savePlot) {
            saveChartAsPNG(chart);
        }

        // Create and set up the window
        JFrame frame = new JFrame("Boxplot of Pathway Enrichment Scores");
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

    // Method to save the chart as a PNG file
    private static void saveChartAsPNG(JFreeChart chart) {
        try {
            // Specify the file where you want to save the image
            File file = new File("pathway_enrichment_scores.png");
            // Save the chart as a PNG file with a width of 800 pixels and height of 600 pixels
            ChartUtils.saveChartAsPNG(file, chart, 800, 600);
            System.out.println("Chart saved as PNG: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving chart as PNG: " + e.getMessage());
        }
    }
}
