/**
 * Copyright (C) 2015-2016 Jeeva Kandasamy (jkandasa@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mycontroller.standalone.metrics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.mycontroller.standalone.TIME_REF;
import org.mycontroller.standalone.api.jaxrs.mapper.MetricsCsvDownload;
import org.mycontroller.standalone.db.AGGREGATION_TYPE;
import org.mycontroller.standalone.db.DaoUtils;
import org.mycontroller.standalone.db.tables.MetricsBinaryTypeDevice;
import org.mycontroller.standalone.db.tables.MetricsDoubleTypeDevice;
import org.mycontroller.standalone.db.tables.Sensor;
import org.mycontroller.standalone.db.tables.SensorVariable;
import org.mycontroller.standalone.metrics.TypeUtils.METRIC_TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeeva Kandasamy (jkandasa)
 * @since 0.0.2
 */
public class MetricsCsvEngine {
    private static final Logger _logger = LoggerFactory.getLogger(MetricsCsvEngine.class.getName());

    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a");

    public MetricsCsvDownload getMetricsCSV(int sensorValueId, int aggregationTypeId) {
        SensorVariable sensorVariable = DaoUtils.getSensorVariableDao().get(sensorValueId);
        AGGREGATION_TYPE aggrType = AGGREGATION_TYPE.get(aggregationTypeId);
        return new MetricsCsvDownload(
                getFileName(sensorVariable, aggrType),
                getData(sensorVariable, aggrType));
    }

    private String getFileName(SensorVariable sensorVariable, AGGREGATION_TYPE aggrType) {
        Sensor sensor = DaoUtils.getSensorDao().getById(sensorVariable.getSensor().getId());
        StringBuilder builder = new StringBuilder();
        builder.append(sensor.getNode().getName())
                .append("_").append(sensor.getName())
                .append("_Nid-").append(sensor.getNode().getEui())
                .append("_Sid-").append(sensor.getSensorId())
                .append("_").append(sensorVariable.getVariableType().getText());
        if (sensorVariable.getMetricType() == METRIC_TYPE.DOUBLE) {
            builder.append("_").append("SampleType_").append(aggrType);
        }
        builder.append("_").append(new SimpleDateFormat("yyyy-MMM-dd_hh-mm-ss").format(new Date()))
                .append(".csv");
        return builder.toString().replaceAll(" ", "_");
    }

    private String getData(SensorVariable sensorVariable, AGGREGATION_TYPE aggrType) {
        METRIC_TYPE type = sensorVariable.getMetricType();
        MetricsAggregationBase aggregationBase = new MetricsAggregationBase();
        switch (type) {
            case BINARY:
                return getBinaryData(sensorVariable, aggrType, aggregationBase);
            case DOUBLE:
                return getDoubleData(sensorVariable, aggrType, aggregationBase);
            default:
                _logger.debug("Unknown Metric Type[{}]", type);
                throw new RuntimeException("Unknown Metric Type[" + type + "]");
        }
    }

    private String getBinaryData(SensorVariable sensorVariable, AGGREGATION_TYPE aggrType,
            MetricsAggregationBase aggrBase) {
        Long fromTime = null;
        switch (aggrType) {
            case RAW:
                fromTime = System.currentTimeMillis() - TIME_REF.ONE_HOUR;
                break;
            case ONE_MINUTE:
                fromTime = System.currentTimeMillis() - TIME_REF.ONE_HOUR * 6;
                break;
            case FIVE_MINUTES:
                fromTime = System.currentTimeMillis() - TIME_REF.ONE_DAY;
                break;
            case ONE_HOUR:
                fromTime = System.currentTimeMillis() - TIME_REF.ONE_DAY * 30;
                break;
            default:
                break;
        }
        List<MetricsBinaryTypeDevice> metrics = aggrBase.getMetricsBinaryData(
                sensorVariable, fromTime);

        StringBuilder builder = new StringBuilder();
        //Headers
        builder.append("Timestamp").append(",");
        builder.append("Time").append(",");
        builder.append("Status");

        //Update data
        for (MetricsBinaryTypeDevice metric : metrics) {
            builder.append("\n");
            builder.append(metric.getTimestamp());
            builder.append(",").append(timeFormat.format(new Date(metric.getTimestamp())));
            builder.append(",").append(metric.getState());
        }
        return builder.toString();
    }

    private String getDoubleData(SensorVariable sensorVariable, AGGREGATION_TYPE aggrType,
            MetricsAggregationBase aggrBase) {
        List<MetricsDoubleTypeDevice> metrics = aggrBase.getMetricsDoubleData(
                sensorVariable,
                aggrType,
                null);
        StringBuilder builder = new StringBuilder();

        if (aggrType == AGGREGATION_TYPE.RAW) {
            //Headers
            builder.append("Timestamp").append(",");
            builder.append("Time").append(",");
            builder.append("Value");

            //Update data
            for (MetricsDoubleTypeDevice metric : metrics) {
                builder.append("\n");
                builder.append(metric.getTimestamp());
                builder.append(",").append(timeFormat.format(new Date(metric.getTimestamp())));
                builder.append(",").append(metric.getAvg());
            }
        } else {
            //Headers
            builder.append("Timestamp").append(",");
            builder.append("Time").append(",");
            builder.append("Samples").append(",");
            builder.append("Minimum").append(",");
            builder.append("Average").append(",");
            builder.append("Maximum");

            //Update data
            for (MetricsDoubleTypeDevice metric : metrics) {
                builder.append("\n");
                builder.append(metric.getTimestamp());
                builder.append(",").append(timeFormat.format(new Date(metric.getTimestamp())));
                builder.append(",").append(metric.getSamples());
                builder.append(",").append(metric.getMin());
                builder.append(",").append(metric.getAvg());
                builder.append(",").append(metric.getMax());
            }
        }
        return builder.toString();
    }
}
