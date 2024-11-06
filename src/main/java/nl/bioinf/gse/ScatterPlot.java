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

    // Method to create a dataset from the top 20 GSEA records
    private static DefaultXYDataset createDataset(List<GSEARecord> results, String dataType) {
        List<GSEARecord> topResults;

        if ("avglogfoldchange".equalsIgnoreCase(dataType)) {
            // Sort by average log fold change and get the top 20 records
            topResults = results.stream()
                    .sorted(Comparator.comparingDouble(GSEARecord::avgLogFoldChange).reversed())
                    .limit(20)
                    .collect(Collectors.toList());
        } else {
            // Sort by enrichment score and get the top 20 records
            topResults = results.stream()
                    .sorted(Comparator.comparingDouble(GSEARecord::enrichmentScore).reversed())
                    .limit(20)
                    .collect(Collectors.toList());
        }

        DefaultXYDataset dataset = new DefaultXYDataset();
        double[][] data = new double[2][topResults.size()];

        for (int i = 0; i < topResults.size(); i++) {
            GSEARecord record = topResults.get(i);
            data[0][i] = record.pValue();  // X-axis is always p-value

            // Set Y-axis based on dataType
            if ("avglogfoldchange".equalsIgnoreCase(dataType)) {
                data[1][i] = record.avgLogFoldChange(); // Y-axis is average log fold change
            } else {
                data[1][i] = record.enrichmentScore();  // Y-axis is enrichment score
            }
        }

        dataset.addSeries("Top 20 Pathways", data);
        return dataset;
    }

    // Method to create the scatter plot chart with dynamic axis titles based on dataType
    private static JFreeChart createChart(DefaultXYDataset dataset, List<GSEARecord> topResults, String dataType) {
        // Set chart title and Y-axis label based on dataType
        String yAxisLabel = "enrichmentscore".equalsIgnoreCase(dataType) ? "Enrichment Score" : "Average Log Fold Change";
        String chartTitle = "Top 20 Pathways by " + yAxisLabel;

        JFreeChart chart = ChartFactory.createScatterPlot(
                chartTitle,                    // chart title
                "P-Value",                     // x-axis label
                yAxisLabel,                    // y-axis label
                dataset,                       // dataset
                PlotOrientation.VERTICAL,
                true,                          // include legend
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        // Custom renderer to set circular shapes and unique colors for each point
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
        renderer.setDefaultShape(new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6)); // Circular shape

        // Generate unique colors for each pathway and set them in the renderer
        Map<String, Color> colorMap = generateColorMap(topResults);
        for (int i = 0; i < topResults.size(); i++) {
            Color color = colorMap.get(topResults.get(i).description()); // Use description instead of ID for color mapping
            renderer.setSeriesPaint(i, color);
        }

        plot.setRenderer(renderer);

        // Add labels to each data point with pathway descriptions
        addLabels(plot, topResults, dataType);

        // Add a custom legend with pathway descriptions and colors, sorted by enrichment score or avg log fold change
        LegendTitle legend = createCustomLegend(colorMap, topResults);
        chart.addLegend(legend);

        // Automatically center the plot around the data points
        centerPlotAroundData(plot, topResults, dataType);

        return chart;
    }

    // Method to center the plot around data points
    private static void centerPlotAroundData(XYPlot plot, List<GSEARecord> topResults, String dataType) {
        double minX = topResults.stream().mapToDouble(GSEARecord::pValue).min().orElse(0);
        double maxX = topResults.stream().mapToDouble(GSEARecord::pValue).max().orElse(1);

        // Set Y-axis min and max based on dataType
        double minY, maxY;
        if ("avglogfoldchange".equalsIgnoreCase(dataType)) {
            minY = topResults.stream().mapToDouble(GSEARecord::avgLogFoldChange).min().orElse(0);
            maxY = topResults.stream().mapToDouble(GSEARecord::avgLogFoldChange).max().orElse(1);
        } else {
            minY = topResults.stream().mapToDouble(GSEARecord::enrichmentScore).min().orElse(0);
            maxY = topResults.stream().mapToDouble(GSEARecord::enrichmentScore).max().orElse(1);
        }

        // Calculate ranges for x and y with a small margin
        double xMargin = (maxX - minX) * 0.2;
        double yMargin = (maxY - minY) * 0.2;

        plot.getDomainAxis().setRange(minX - xMargin, maxX + xMargin);
        plot.getRangeAxis().setRange(minY - yMargin, maxY + yMargin);
    }
    // Helper method to add labels to each point
    private static void addLabels(XYPlot plot, List<GSEARecord> topResults, String dataType) {
        for (GSEARecord record : topResults) {
            double x = record.pValue();
            double y;

            // Set Y-axis value based on dataType
            if ("avglogfoldchange".equalsIgnoreCase(dataType)) {
                y = record.avgLogFoldChange();
            } else {
                y = record.enrichmentScore();
            }

            String label = record.description();

            XYTextAnnotation annotation = new XYTextAnnotation(label, x, y);
            annotation.setFont(new Font("SansSerif", Font.PLAIN, 10));
            annotation.setPaint(Color.BLACK); // Color of the label
            annotation.setTextAnchor(org.jfree.chart.ui.TextAnchor.HALF_ASCENT_CENTER);
            plot.addAnnotation(annotation);
        }
    }

    // Helper method to create a custom legend with color mapping, sorted by enrichment score
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

        // Wrap the LegendItemCollection in a LegendItemSource
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

    // Method to generate a map of pathway descriptions to unique colors
    private static Map<String, Color> generateColorMap(List<GSEARecord> topResults) {
        Map<String, Color> colorMap = new HashMap<>();
        float hueStep = 1.0f / topResults.size(); // Spread colors across the color wheel

        for (int i = 0; i < topResults.size(); i++) {
            String description = topResults.get(i).description();
            colorMap.put(description, Color.getHSBColor(i * hueStep, 0.7f, 0.8f)); // Unique color for each description
        }

        return colorMap;
    }

    // Method to save the chart as a PNG file
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

    // Method to display the chart in a JFrame, with an option to save as a PNG
    static void showChart(List<GSEARecord> results, boolean savePlot, String dataType) {
        List<GSEARecord> topResults = results.stream()
                .sorted(Comparator.comparingDouble(
                                "avglogfoldchange".equalsIgnoreCase(dataType)
                                        ? GSEARecord::avgLogFoldChange
                                        : GSEARecord::enrichmentScore)
                        .reversed())
                .limit(20)
                .collect(Collectors.toList());

        DefaultXYDataset dataset = createDataset(topResults, dataType);
        JFreeChart chart = createChart(dataset, topResults, dataType);

        if (savePlot) {
            saveChartAsPNG(chart);
        }

        JFrame frame = new JFrame("Scatter Plot of Top 20 Pathways");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        frame.add(chartPanel, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true);
    }
}
