package zookeepercontroller.zkconn;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * User: PageLiu
 * Date: 12-11-28
 * Time: 上午2:22
 */
public class ZookeeperConnection {
    private String connectString;
    private int sessionTimeout;
    private int zkConnectAwaitMs=1000*10;
    private Watcher watcher;
    private ZooKeeper zooKeeper  = null;

    public int getZkConnectAwaitMs() {
		return zkConnectAwaitMs;
	}

	public void setZkConnectAwaitMs(int zkConnectAwaitMs) {
		this.zkConnectAwaitMs = zkConnectAwaitMs;
	}

	public String getConnectString() {
        return connectString;
    }

    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public Watcher getWatcher() {
        return watcher;
    }

    public void setWatcher(Watcher watcher) {
        this.watcher = watcher;
    }
    
    
    public ZooKeeper createZookeeper() throws IOException {
    	final CountDownLatch connectedSignal = new CountDownLatch(1);
		try {
				zooKeeper = new ZooKeeper(connectString,sessionTimeout, new Watcher() {
				public void process(WatchedEvent event) {
					System.out.println("createZookeeper:" + event.getPath() 
							+ "," + event.getType() + "," + event.getState());
					if (event.getState() == KeeperState.SyncConnected) {
				          connectedSignal.countDown();
				    }
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
			return zooKeeper;
		}
		
		try {
			connectedSignal.await(zkConnectAwaitMs,TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
        return zooKeeper;
    }
    
    
    
    public void close() throws InterruptedException {
        if(zooKeeper!=null){
            zooKeeper.close();
        }
    }
}
