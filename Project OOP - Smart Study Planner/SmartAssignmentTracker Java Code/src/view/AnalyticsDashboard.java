package view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;

public class AnalyticsDashboard {

    public void showDashboard() {
        JFrame frame = new JFrame("Productivity & Urgency Analytics");
        frame.setSize(700, 500);

        // 1. Create the Dataset (This links to your UrgencyCalculator!)
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // In the real app, you will loop through your database and pull these numbers.
        // For now, here is mock data representing your dynamic calculations:
        dataset.addValue(85.0, "Urgency Score", "BITP 3113 Project");
        dataset.addValue(40.0, "Urgency Score", "Lab Report 2");
        dataset.addValue(95.0, "Urgency Score", "Math Quiz");
        dataset.addValue(20.0, "Urgency Score", "Reading Assignment");

        // 2. Generate the Bar Chart using JFreeChart
        JFreeChart barChart = ChartFactory.createBarChart(
                "Task Urgency Tracker",    // Chart Title
                "Assignments",             // X-Axis Label
                "Urgency Score (0-100)",   // Y-Axis Label
                dataset,                   // The data we just created
                PlotOrientation.VERTICAL,
                true, true, false);

        // 3. Add the Chart to a Swing Panel
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        
        frame.add(chartPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
