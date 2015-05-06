package org.iatoki.judgels.michael;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.InstanceStatus;
import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.michael.views.html.machines.watchers.machineStateWatcherView;
import play.twirl.api.Html;

public final class AWSEC2StateWatcherAdapter implements MachineWatcherAdapter {

    private final Machine machine;
    private final AmazonEC2 amazonEC2;

    public AWSEC2StateWatcherAdapter(Machine machine, AWSEC2WatcherConf awsec2WatcherConf) {
        this.machine = machine;
        if (awsec2WatcherConf.useKeyCredential) {
            this.amazonEC2 = new AmazonEC2Client(new BasicAWSCredentials(awsec2WatcherConf.accessKey, awsec2WatcherConf.secretKey));
        } else {
            this.amazonEC2 = new AmazonEC2Client();
        }
        this.amazonEC2.setRegion(Region.getRegion(Regions.valueOf(awsec2WatcherConf.regionId)));
    }

    @Override
    public Html renderWatcher() {
        return machineStateWatcherView.render(getState());
    }

    private String getState() {
        DescribeInstanceStatusResult result = amazonEC2.describeInstanceStatus(
              new DescribeInstanceStatusRequest()
                     .withInstanceIds(ImmutableList.of(machine.getInstanceName()))
        );
        String state = null;
        for (InstanceStatus instanceStatus : result.getInstanceStatuses()) {
            state = instanceStatus.getInstanceState().getName();
        }
        return state;
    }

    @Override
    public MachineWatcherTypes getType() {
        return MachineWatcherTypes.STATE;
    }
}
