package zookeepercontroller.service.impl;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import zookeepercontroller.bean.ValueNode;
import zookeepercontroller.service.ZkOptionService;
import zookeepercontroller.zkconn.ZookeeperConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: PageLiu
 * Date: 12-11-28
 * Time: 下午9:01
 */
@Component
public class ZkOptionServiceImpl implements ZkOptionService{
	
	public ValueNode getPathValue(String path, ZookeeperConnection zkConnc) throws InterruptedException, KeeperException, IOException {
        ZooKeeper zkConn = zkConnc.createZookeeper();
        byte[] data = zkConn.getData(path,true,null);
        ValueNode vn = new ValueNode();
        vn.setZpath(path);
        if(data!=null){
        	vn.setValue(new String(data,"utf-8"));
        }else{
        	vn.setValue("");
        }
        vn.setConnStr(zkConnc.getConnectString());
        List<String> childs =  zkConn.getChildren(path, false);
        if(!CollectionUtils.isEmpty(childs))vn.setIsParent(true);
        zkConn.close();
        return vn;
    }
	

    public ValueNode getPathChildren(String path, ZookeeperConnection zkConnc) throws InterruptedException, KeeperException, IOException {
        ZooKeeper zkConn = zkConnc.createZookeeper();
        byte[] data = zkConn.getData(path,true,null);
        ValueNode vn = new ValueNode();
        vn.setZpath(path);
        if(data!=null){
        	vn.setValue(new String(data,"utf-8"));
        }else{
        	vn.setValue("");
        }
        vn.setConnStr(zkConnc.getConnectString());
        List<ValueNode> valueNodeList = new ArrayList<ValueNode>();
        List<String> childs =  zkConn.getChildren(path, false);
        for(String child:childs){
            String p = path+"/"+child;
            if("/".equals(path)){
                p = path+child;
            }
            byte[] dt = zkConn.getData(p,true,null);
            ValueNode cvn = new ValueNode();
            cvn.setZpath(p);
            if(dt!=null){
            	cvn.setValue(new String(dt,"utf-8"));
            }else{
            	cvn.setValue("");
            }
            cvn.setConnStr(zkConnc.getConnectString());
            List<String> childs2 = zkConn.getChildren(p,false) ;
            if(childs2.size()>0)cvn.setIsParent(true);
            valueNodeList.add(cvn);
        }
        zkConn.close();
        vn.setChildNodes(valueNodeList);

        if(valueNodeList.size()>0)vn.setIsParent(true);
        return vn;
    }

    public String createPath(String path, ZookeeperConnection zkConn) throws IOException {
        String actPath = "";
        if(zkConn!=null){
        try {
            actPath = zkConn.createZookeeper().create(path,"".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            zkConn.close();
        } catch (KeeperException e) {
            e.printStackTrace();
            return "";
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "";
        }
        }
        return actPath;
    }

    public String createPath(String path, byte[] data, ZookeeperConnection zkConn) throws IOException {
        String actPath = "";
        if(zkConn!=null){
        try {
            actPath = zkConn.createZookeeper().create(path,data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            zkConn.close();
        } catch (KeeperException e) {
            e.printStackTrace();
            return "";
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "";
        }
        }
        return actPath;
    }

    public boolean setData(ZookeeperConnection zkConn, String path, String content) throws IOException {
        if(zkConn!=null){
            try {
                Stat  stat = zkConn.createZookeeper().setData(path,content.getBytes(), -1);
                System.out.print("stat:aversion:"+stat.getAversion()+"|"+stat.getCversion());
                zkConn.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            } catch (KeeperException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public boolean deletePath(String path, ZookeeperConnection zkConn) throws IOException {
        if(zkConn!=null){
        try {
            zkConn.createZookeeper().delete(path, -1);
            zkConn.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (KeeperException e) {
            e.printStackTrace();
            return false;
        }
        }
    return true;
}
}
