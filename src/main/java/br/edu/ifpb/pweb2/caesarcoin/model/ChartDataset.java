package br.edu.ifpb.pweb2.caesarcoin.model;

import java.util.List;

public class ChartDataset {
    private String label;
    private List<Double> data;
    private String borderColor;
    private String backgroundColor;

    public ChartDataset(String label, List<Double> data, String borderColor, String backgroundColor) {
        this.label = label;
        this.data = data;
        this.borderColor = borderColor;
        this.backgroundColor = backgroundColor;
    }

    // Getters and Setters
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Double> getData() {
        return data;
    }

    public void setData(List<Double> data) {
        this.data = data;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}