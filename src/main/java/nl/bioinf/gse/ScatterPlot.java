package nl.bioinf.gse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.ChartUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ScatterPlot {

    /**
     * Create dataset for the top 20 pathways based on enrichment score.
     *
     * @param results A list of GSEA results.
     * @return A dataset for the scatter plot, containing p-value and enrichment score data.
     */
    private static DefaultXYDataset createDataset(List<GSEARecord> results) {
        List<GSEARecord> topResults = results.stream()
                .sorted(Comparator.comparingDouble(GSEARecord::enrichmentScore).reversed())
                .limit(20)
                .collect(Collectors.toList());

        DefaultXYDataset dataset = new DefaultXYDataset();
        double[][] data = new double[2][topResults.size()]; // [0] for x (p-value), [1] for y (enrichment score)

        // Loop over the top 20 results and fill in the dataset with p-values and enrichment scores
        for (int i = 0; i < topResults.size(); i++) {
            GSEARecord record = topResults.get(i);
            data[0][i] = record.pValue();         // X-axis (p-value)
            data[1][i] = record.enrichmentScore(); // Y-axis (enrichment score)
        }

        dataset.addSeries("Top 20 Pathways", data);
        return dataset;
    }

    /**
     * Create a scatter plot chart based on the dataset.
     *
     * @param dataset Dataset to be plotted.
     * @param topResults List of top 20 results to customize the chart.
     * @return The generated scatter plot chart.
     */
    private static JFreeChart createChart(DefaultXYDataset dataset, List<GSEARecord> topResults) {
        JFreeChart chart = ChartFactory.createScatterPlot(
                "Top 20 Pathways by Enrichment Score",  // chart title
                "P-Value",                               // x-axis label
                "Enrichment Score",                      // y-axis label
                dataset,                                 // dataset
                PlotOrientation.VERTICAL,
                true,                                    // show legend
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        // Custom renderer to set circle shapes and unique colors for each point
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
        renderer.setDefaultShape(new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6));

        // Generate unique colors for each pathway and set them in the renderer
        Map<String, Color> colorMap = generateColorMap(topResults);
        for (int i = 0; i < topResults.size(); i++) {
            Color color = colorMap.get(topResults.get(i).description()); // Use description instead of ID for color mapping
            renderer.setSeriesPaint(i, color);
        }

        plot.setRenderer(renderer);

        // adding label per datapoint
        addLabels(plot, topResults);

        // Add a custom legend with pathway descriptions and colors, sorted by enrichment score
        LegendTitle legend = createCustomLegend(colorMap, topResults);
        chart.addLegend(legend);

        // Center the plot around the data points
        centerPlotAroundData(plot, topResults);

        return chart;
    }

    /**
     * Adjust plot ranges to center the data points.
     *
     * @param plot The XYPlot to adjust.
     * @param topResults The top 20 results to calculate axis ranges.
     */
    private static void centerPlotAroundData(XYPlot plot, List<GSEARecord> topResults) {
        // Find min and max values for p-value and enrichment score
        double minX = topResults.stream().mapToDouble(GSEARecord::pValue).min().orElse(0);
        double maxX = topResults.stream().mapToDouble(GSEARecord::pValue).max().orElse(1);
        double minY = topResults.stream().mapToDouble(GSEARecord::enrichmentScore).min().orElse(0);
        double maxY = topResults.stream().mapToDouble(GSEARecord::enrichmentScore).max().orElse(1);

        // Calculate ranges for x and y
        double xMargin = (maxX - minX) * 0.2;
        double yMargin = (maxY - minY) * 0.2;

        // Set the ranges for the axes
        plot.getDomainAxis().setRange(minX - xMargin, maxX + xMargin);
        plot.getRangeAxis().setRange(minY - yMargin, maxY + yMargin);
    }

    /**
     * Add labels to each data point.
     *
     * @param plot The XYPlot to add labels to.
     * @param topResults List of top 20 results to label.
     */
    private static void addLabels(XYPlot plot, List<GSEARecord> topResults) {
        for (GSEARecord record : topResults) {
            double x = record.pValue();
            double y = record.enrichmentScore();
            String label = record.description();

            XYTextAnnotation annotation = new XYTextAnnotation(label, x, y);
            annotation.setFont(new Font("SansSerif", Font.PLAIN, 10));
            annotation.setPaint(Color.BLACK); // Color of the label
            annotation.setTextAnchor(org.jfree.chart.ui.TextAnchor.HALF_ASCENT_CENTER);
            plot.addAnnotation(annotation);
        }
    }

    /**
     * Create a custom legend based on pathway descriptions and their colors.
     *
     * @param colorMap A map of pathway descriptions to colors.
     * @param topResults List of top 20 results for legend items.
     * @return A custom legend for the chart.
     */
    private static LegendTitle createCustomLegend(Map<String, Color> colorMap, List<GSEARecord> topResults) {
        LegendItemCollection legendItems = new LegendItemCollection();

        // Sort the legend items based on enrichment score by iterating through topResults
        for (GSEARecord record : topResults) {
            String description = record.description();
            Color color = colorMap.get(description);

            LegendItem legendItem = new LegendItem(description, description, null, null,
                    new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6), color);
            legendItems.add(legendItem);
        }


        LegendTitle legend = new LegendTitle(new LegendItemSource() {
            @Override
            public LegendItemCollection getLegendItems() {
                return legendItems;
            }
        });

        legend.setPosition(RectangleEdge.BOTTOM);
        legend.setMargin(new RectangleInsets(10, 10, 10, 10)); // Margin around the legend

        return legend;
    }

    /**
     * Generate a map of pathway descriptions to unique colors.
     *
     * @param topResults List of top 20 results to generate the color map.
     * @return A map of pathway descriptions to unique colors.
     */
    private static Map<String, Color> generateColorMap(List<GSEARecord> topResults) {
        Map<String, Color> colorMap = new HashMap<>();
        float hueStep = 1.0f / topResults.size(); // Spread colors across the color wheel

        for (int i = 0; i < topResults.size(); i++) {
            String description = topResults.get(i).description();
            colorMap.put(description, Color.getHSBColor(i * hueStep, 0.7f, 0.8f)); // Unique color for each description
        }

        return colorMap;
    }

    /**
     * Save the chart as a PNG file.
     *
     * @param chart The chart to save.
     */
    private static void saveChartAsPNG(JFreeChart chart) {
        try {
            // Specify the file where you want to save the image
            File file = new File("top_20_pathways_scatter_plot.png");
            // Save the chart as a PNG file with a width of 800 pixels and height of 600 pixels
            ChartUtils.saveChartAsPNG(file, chart, 800, 600);
            System.out.println("Chart saved as PNG: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving chart as PNG: " + e.getMessage());
        }
    }

    /**
     * Display the scatter plot and optionally save it as a PNG.
     *
     * @param results A list of GSEA results.
     * @param savePlot Whether to save the plot as a PNG file.
     */
    static void showChart(List<GSEARecord> results, boolean savePlot) {
        List<GSEARecord> topResults = results.stream()
                .sorted(Comparator.comparingDouble(GSEARecord::enrichmentScore).reversed())
                .limit(20)
                .collect(Collectors.toList());

        DefaultXYDataset dataset = createDataset(results);
        JFreeChart chart = createChart(dataset, topResults);

        // Display the chart in a Swing panel
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new java.awt.Dimension(800, 600));
        JFrame frame = new JFrame("Top 20 Pathways Scatter Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        if (savePlot) {
            saveChartAsPNG(chart); // Save the plot as a PNG if requested
        }
    }
}
