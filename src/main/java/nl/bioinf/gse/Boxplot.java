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

    /**
     * Creates a dataset for the boxplot chart using the given GSEA results.
     *
     * @param results A list of GSEARecord objects containing pathway data.
     * @return A DefaultBoxAndWhiskerCategoryDataset containing the filtered enrichment scores.
     */
    private static DefaultBoxAndWhiskerCategoryDataset createDataset(List<GSEARecord> results) {
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        // Extract enrichment scores from the results and filter out outliers
        List<Double> enrichmentScores = new ArrayList<>();
        for (GSEARecord pathway : results) {
            enrichmentScores.add(pathway.enrichmentScore());
        }

        // Filter out outliers from the enrichment scores
        List<Double> filteredScores = filterOutliers(enrichmentScores);

        // Add the filtered enrichment scores to the dataset under a single category pathways
        dataset.add(filteredScores, "Enrichment Scores", "Pathways");

        return dataset;
    }

    /**
     * Filters out outliers from a list of enrichment scores using the IQR method.
     *
     * @param scores A list of enrichment scores.
     * @return A list of filtered scores excluding outliers.
     */
    private static List<Double> filterOutliers(List<Double> scores) {
        // Return the scores as they are if there are fewer than 4 data points (not enough data to calculate outliers)
        if (scores.size() < 4) {
            return scores;
        }

        // Sort the scores to calculate the quarts
        List<Double> sortedScores = new ArrayList<>(scores);
        Collections.sort(sortedScores);

        // Calculate the first Q1 and third Q3 quarts
        double q1 = sortedScores.get(sortedScores.size() / 4);
        double q3 = sortedScores.get(3 * sortedScores.size() / 4);
        double iqr = q3 - q1;

        // Define the bounds for filtering outliers based on the IQR
        double lowerBound = q1 - 1.5 * iqr;
        double upperBound = q3 + 1.5 * iqr;

        // Create a new list for scores that fall within the bounds (non-outliers)
        List<Double> filteredScores = new ArrayList<>();
        for (Double score : scores) {
            if (score >= lowerBound && score <= upperBound) {
                filteredScores.add(score);
            }
        }

        return filteredScores;
    }

    /**
     * Creates a boxplot chart using the given dataset.
     *
     * @param dataset The dataset containing the filtered enrichment scores.
     * @return The created JFreeChart boxplot chart.
     */
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
        renderer.setMeanVisible(false); // Hide the mean value on the plot

        // Set transparent color for outlier artifacts, making them invisible
        Color transparentColor = new Color(0, 0, 0, 0);
        renderer.setArtifactPaint(transparentColor);     // Set artifact paint (outliers) to transparent

        plot.setRenderer(renderer);

        return chart;
    }

    /**
     * Displays the boxplot chart in a JFrame and optionally saves it as a PNG file.
     *
     * @param results A list of GSEARecord objects containing pathway data.
     * @param savePlot If true, the chart is saved as a PNG file.
     */
    static void showChart(List<GSEARecord> results, boolean savePlot) {
        DefaultBoxAndWhiskerCategoryDataset dataset = createDataset(results);  // Create the dataset
        JFreeChart chart = createChart(dataset);  // Create the boxplot

        // save the chart as a PNG file
        if (savePlot) {
            saveChartAsPNG(chart);
        }

        // Create and set up the JFrame frame to display the chart
        JFrame frame = new JFrame("Boxplot of Pathway Enrichment Scores");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Add the chart to a ChartPanel and then to the frame
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        frame.add(chartPanel, BorderLayout.CENTER);

        // Display the frame
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Saves the chart as a PNG file with a fixed file name.
     *
     * @param chart The JFreeChart to be saved.
     */
    private static void saveChartAsPNG(JFreeChart chart) {
        try {
            // Specify the file where the PNG will be saved
            File file = new File("pathway_enrichment_scores.png");
            // Save the chart as a PNG
            ChartUtils.saveChartAsPNG(file, chart, 800, 600);
            System.out.println("Chart saved as PNG: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving chart as PNG: " + e.getMessage());
        }
    }
}
