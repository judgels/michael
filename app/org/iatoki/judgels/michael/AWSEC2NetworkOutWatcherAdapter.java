package org.iatoki.judgels.michael;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.cloudwatch.model.Statistic;
import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.michael.views.html.machine.watcher.machineNetworkOutWatcherView;
import play.twirl.api.Html;

import java.util.Date;
import java.util.List;

public final class AWSEC2NetworkOutWatcherAdapter implements GraphMachineWatcherAdapter {

    private final Machine machine;
    private final AmazonCloudWatch amazonCloudWatch;

    public AWSEC2NetworkOutWatcherAdapter(Machine machine, AWSEC2WatcherConf awsec2WatcherConf) {
        this.machine = machine;
        if (awsec2WatcherConf.useKeyCredential) {
            this.amazonCloudWatch = new AmazonCloudWatchClient(new BasicAWSCredentials(awsec2WatcherConf.accessKey, awsec2WatcherConf.secretKey));
        } else {
            this.amazonCloudWatch = new AmazonCloudWatchClient();
        }
        this.amazonCloudWatch.setRegion(Region.getRegion(Regions.valueOf(awsec2WatcherConf.regionId)));
    }

    @Override
    public Html renderWatcher() {
        return machineNetworkOutWatcherView.render(machine.getDisplayName() + " - Network Out", org.iatoki.judgels.michael.controllers.apis.routes.MachineWatcherAPIController.getDataPoints(machine.getId(), getType().name()).toString(), 60000L);
    }

    @Override
    public List<DataPoint> getDataPoints(Date startTime, Date endTime, long period) {
        GetMetricStatisticsResult getMetricStatisticsResult = amazonCloudWatch.getMetricStatistics(
                new GetMetricStatisticsRequest()
                        .withDimensions(new Dimension().withName("InstanceId").withValue(machine.getInstanceName()))
                        .withNamespace("AWS/EC2")
                        .withMetricName("NetworkOut")
                        .withStartTime(startTime)
                        .withEndTime(endTime)
                        .withPeriod(Long.valueOf(period).intValue())
                        .withStatistics(new Statistic[] {Statistic.Average})
        );
        ImmutableList.Builder<DataPoint> dataPointBuilder = ImmutableList.builder();

        for (Datapoint datapoint : getMetricStatisticsResult.getDatapoints()) {
            dataPointBuilder.add(new DataPoint(datapoint.getTimestamp().getTime(), datapoint.getAverage(), datapoint.getUnit()));
        }

        return dataPointBuilder.build();
    }

    @Override
    public MachineWatcherType getType() {
        return MachineWatcherType.NETWORK_OUT;
    }
}
