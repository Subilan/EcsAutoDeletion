package org.sotap.Hibernate;

import java.io.File;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.ecs.model.v20140526.DeleteInstanceRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeInstancesRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeInstancesResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;

public final class DeleteInstance {
    public DefaultProfile profile;
    public String instanceName;
    public static String backupScriptPath = null;
    public static String region;
    public static String accessKeyId;
    public static String accessKeySecret;

    public DeleteInstance(String name) {
        DefaultProfile profile = DefaultProfile.getProfile(region, accessKeyId, accessKeySecret);
        this.profile = profile;
        this.instanceName = name;
    }

    public String getId() {
        IAcsClient client = new DefaultAcsClient(profile);
        DescribeInstancesRequest req = new DescribeInstancesRequest();
        req.setInstanceName(instanceName);
        try {
            DescribeInstancesResponse res = client.getAcsResponse(req);
            DescribeInstancesResponse.Instance i = res.getInstances().get(0);
            return i.getInstanceId();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("Unable to get instance info.");
            System.out.println("ErrCode: " + e.getErrCode());
            System.out.println("ErrMsg: " + e.getErrMsg());
            System.out.println("RequestId: " + e.getRequestId());
        }
        return null;
    }

    public boolean d() {
        var client = new DefaultAcsClient(profile);
        var req = new DeleteInstanceRequest();
        var id = this.getId();
        if (null == id)
            return false;
        req.setInstanceId(id);
        // Kill the instance immediately,
        // with no (persistent or temporary) data left.
        req.setForce(true);
        if (!backupScriptPath.equals(null)) {
            try {
                var pb = new ProcessBuilder(backupScriptPath);
                pb.directory(new File("/"));
                Process p = pb.start();
                p.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Unable to run backup script.");
                return false;
            }
        } else {
            System.out.println("Backup script path is set to null.");
        }
        try {
            @SuppressWarnings("unused")
            var res = client.getAcsResponse(req);
            return true;
        } catch (ServerException e) {
            e.printStackTrace();
            return false;
        } catch (ClientException e) {
            if (e.getErrCode().equals("LastTokenProcessing")) {
                // However, it actually does deleted the instance.
                return true;
            }
            System.out.println("Unable to delete instance.");
            System.out.println("ErrCode: " + e.getErrCode());
            System.out.println("ErrMsg: " + e.getErrMsg());
            System.out.println("RequestId: " + e.getRequestId());
            return false;
        }
    }
}
