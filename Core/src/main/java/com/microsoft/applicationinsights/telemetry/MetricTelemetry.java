package com.microsoft.applicationinsights.telemetry;

import com.microsoft.applicationinsights.internal.schemav2.DataPoint;
import com.microsoft.applicationinsights.internal.schemav2.DataPointType;

import com.microsoft.applicationinsights.internal.schemav2.MetricData;

import com.google.common.base.Strings;

/**
 * Telemetry used to track events.
 */
public final class MetricTelemetry extends BaseTelemetry<MetricData> {
    private final MetricData data;
    private final DataPoint metric;

    public MetricTelemetry() {
        super();
        this.data = new MetricData();
        this.metric = new DataPoint();
        initialize(this.data.getProperties());
        this.data.getMetrics().add(this.metric);
    }

    public MetricTelemetry(String name, double value) {
        this();
        this.setName(name);
        this.metric.setValue(value);
    }

    public String getName() {
        return this.metric.getName();
    }

    public void setName(String name) {
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("The metric name cannot be null or empty");
        }

        this.metric.setName(name);
    }

    public double getValue() {
        return this.metric.getValue();
    }

    public void setValue(double value) {
        this.metric.setValue(value);
    }

    public void setCount(Integer count) {
        this.metric.setCount(count); updateKind();
    }

    public void setMin(Double value) {
        this.metric.setMin(value); updateKind();
    }

    public Double getMax() {
        return this.metric.getMax();
    }

    public void setMax(Double value) {
        this.metric.setMax(value); updateKind();
    }

    public void setStandardDeviation(Double value) {
        this.metric.setStdDev(value); updateKind();
    }

    @Override
    protected void additionalSanitize() {
        this.metric.setName(Sanitizer.sanitizeName(metric.getName()));
    }

    @Override
    protected MetricData getData() {
        return data;
    }

    private void updateKind() {
        boolean isAggregation =
            (metric.getCount() != null) ||
            (metric.getMin() != null) ||
            (metric.getMax() != null) ||
            (metric.getStdDev() != null);

        if ((metric.getCount() != null) && metric.getCount() == 1) {
            // Singular data point. This is not an aggregation.
            isAggregation = false;
        }

        this.metric.setKind(isAggregation ? DataPointType.Aggregation : DataPointType.Measurement);
    }
}