    public void updateAnalytics(Map<String, Number> typeCounts, Map<String, Number> tagCounts) {
        typeBarChart.getData().clear();
        var typeSeries = new BarChart.Series<String, Number>();
        typeCounts.forEach((type, count) -> {
            BarChart.Data<String, Number> data = new BarChart.Data<>(type, count);
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-bar-fill: #4F8EF7;");
                }
            });
            typeSeries.getData().add(data);
        });
        typeBarChart.getData().add(typeSeries);

        tagPieChart.getData().clear();
        int colorIdx = 0;
        String[] pieColors = {"#F7B32B", "#F72B50", "#4F8EF7", "#2BF7B3", "#B32BF7", "#2B7BF7"};
        for (Map.Entry<String, Number> entry : tagCounts.entrySet()) {
            PieChart.Data data = new PieChart.Data(entry.getKey(), entry.getValue().doubleValue());
            tagPieChart.getData().add(data);
            int idx = colorIdx++ % pieColors.length;
            data.getNode().setStyle("-fx-pie-color: " + pieColors[idx] + ";");
        }
    }
package com.boozer.nexus.desktop.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

/**
 * Analytics dashboard panel for rich data insights.
 */
public class AnalyticsDashboardView {
    private final BorderPane view;
    private final ComboBox<String> workspaceSelector;
    private final BarChart<String, Number> typeBarChart;
    private final PieChart tagPieChart;
    private final Button exportButton;
    private final ProgressIndicator loadingIndicator;

    public AnalyticsDashboardView(List<String> workspaces,
                                  Map<String, Number> typeCounts,
                                  Map<String, Number> tagCounts,
                                  Runnable exportHandler) {
    view = new BorderPane();
    view.setPadding(new Insets(24));
    loadingIndicator = new ProgressIndicator();
    loadingIndicator.setVisible(false);

    workspaceSelector = new ComboBox<>(FXCollections.observableArrayList(workspaces));
    workspaceSelector.setPromptText("Select workspace/project");
    workspaceSelector.getSelectionModel().selectFirst();
    workspaceSelector.setTooltip(new Tooltip("Choose a workspace/project to view analytics"));

    Label heading = new Label("Analytics Dashboard");
    heading.getStyleClass().add("dashboard-heading");
    heading.setTooltip(new Tooltip("Rich analytics and insights for your selected workspace"));

        typeBarChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        typeBarChart.setTitle("Operation Types");
        typeBarChart.setLegendVisible(false);
        typeBarChart.setMinHeight(220);
        typeBarChart.setAnimated(false);
        var typeSeries = new BarChart.Series<String, Number>();
        typeCounts.forEach((type, count) -> typeSeries.getData().add(new BarChart.Data<>(type, count)));
        typeBarChart.getData().add(typeSeries);

        tagPieChart = new PieChart();
        tagPieChart.setTitle("Top Tags");
        tagPieChart.setMinHeight(220);
        tagPieChart.setLegendVisible(true);
        tagCounts.forEach((tag, count) -> tagPieChart.getData().add(new PieChart.Data(tag, count.doubleValue())));

    exportButton = new Button("Export CSV");
    exportButton.setOnAction(e -> exportHandler.run());
    exportButton.getStyleClass().add("accent-button");
    exportButton.setTooltip(new Tooltip("Export current analytics data to CSV"));

        HBox topBar = new HBox(16, heading, workspaceSelector, exportButton);
        topBar.setPadding(new Insets(0, 0, 18, 0));

    VBox charts = new VBox(24, typeBarChart, tagPieChart);
    charts.setPadding(new Insets(12, 0, 0, 0));
    charts.setStyle("-fx-background-color: #FFFFFF; -fx-border-radius: 8; -fx-background-radius: 8;");

    view.setTop(topBar);
    view.setCenter(charts);
    view.setBottom(loadingIndicator);
    public void setLoading(boolean loading) {
        loadingIndicator.setVisible(loading);
    }
    }

    public Node getView() {
        return view;
    }

    public ComboBox<String> getWorkspaceSelector() {
        return workspaceSelector;
    }

    public BarChart<String, Number> getTypeBarChart() {
        return typeBarChart;
    }

    public PieChart getTagPieChart() {
        return tagPieChart;
    }

    public Button getExportButton() {
        return exportButton;
    }
}
