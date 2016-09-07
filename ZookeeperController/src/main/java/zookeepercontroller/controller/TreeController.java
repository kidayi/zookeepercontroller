package zookeepercontroller.controller;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.zookeeper.KeeperException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import zookeepercontroller.EventWatcher;
import zookeepercontroller.bean.Connect;
import zookeepercontroller.bean.ValueNode;
import zookeepercontroller.bean.ZTree;
import zookeepercontroller.service.ZkOptionService;
import zookeepercontroller.util.Bean2ViewUtil;
import zookeepercontroller.util.ConvertTreeNode;
import zookeepercontroller.util.JSONFile;
import zookeepercontroller.util.StringUtil;
import zookeepercontroller.zkconn.ZookeeperConnection;

/**
 * User: PageLiu
 * Date: 12-11-28
 * Time: 下午10:33
 */
@Controller
public class TreeController {
    @Resource
    private ZkOptionService zkOptionServiceImpl;


    public ZookeeperConnection getConnection(){
        ZookeeperConnection connection;
        connection = new ZookeeperConnection();
        connection.setSessionTimeout(3000);
        connection.setWatcher(new EventWatcher());
        return connection;
    }

    @RequestMapping(value = "/getNodeData")
    public String getNodeData(@RequestParam(required = false)String path,@RequestParam(required = true)String zpath,@RequestParam(required = true)String connectStr,HttpServletRequest request,HttpServletResponse response) throws IOException, InterruptedException, KeeperException {

        ZookeeperConnection connection = getConnection();
        connection.setConnectString(connectStr);
        ValueNode vn = zkOptionServiceImpl.getPathValue(zpath, connection);
        ZTree zTree = ConvertTreeNode.convertValueNode(vn);
        String json = Bean2ViewUtil.cvtTree2Json(zTree);
        response.setContentType("application/json;charset=UTF-8");
        response.getOutputStream().write(json.getBytes());
        return null;
    }

    @RequestMapping(value = "/getPathData")
    public String showTreeData(@RequestParam(required = false)String path,@RequestParam(required = true)String zpath,@RequestParam(required = true)String connectStr,HttpServletRequest request,HttpServletResponse response) throws IOException, InterruptedException, KeeperException {

        ZookeeperConnection connection = getConnection();
        connection.setConnectString(connectStr);
        ValueNode vn = zkOptionServiceImpl.getPathChildren(zpath, connection);
        ZTree zTree = ConvertTreeNode.convertValueNode(vn);
        String json = Bean2ViewUtil.cvtTreeChilds2Json(zTree);
        response.setContentType("application/json;charset=UTF-8");
        response.getOutputStream().write(json.getBytes());
        return null;
    }
    @RequestMapping(value = "/removeZKNode")
    public String removeZKNode(@RequestParam(required = true)String zpath,@RequestParam(required = true)String connectStr,HttpServletRequest request,HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        if("/".equals(zpath)){
            Set connSet  = null;
             removeConnSet(request, connectStr);
            response.getOutputStream().write("{\"result\":1}".getBytes());
            return null;
        }
        ZookeeperConnection connection = getConnection();
        connection.setConnectString(connectStr);
        boolean result = zkOptionServiceImpl.deletePath(zpath,connection);

        if(result) {
            response.getOutputStream().write("{\"result\":1}".getBytes());
        }
        else{
            response.getOutputStream().write("{\"result\":0}".getBytes());
        }
        return null;
    }

    private List removeConnSet(HttpServletRequest request, String connectStr) throws IOException {
        List<Connect> connSet = JSONFile.readConnects();
        if(StringUtil.isEmpty(connectStr)){
        	return  connSet;
        }
        connectStr=connectStr.trim();
        Iterator<Connect> it=connSet.iterator();
        while(it.hasNext()){
        	if(connectStr.equalsIgnoreCase(it.next().getStr())){
        		it.remove();
                JSONFile.persistConnect(connSet);
            }
		}
        
        return  connSet;
    }

    @RequestMapping(value = "/addZKNode")
    public String addZKNode(@RequestParam(required = true)String nodeName,@RequestParam(required = true)String nodeValue,@RequestParam(required = true)String zpath,@RequestParam(required = true)String connectStr,HttpServletRequest request,HttpServletResponse response) throws IOException, InterruptedException, KeeperException {
        ZookeeperConnection connection = getConnection();
        connection.setConnectString(connectStr);
        String path = zkOptionServiceImpl.createPath(zpath+("/".equals(zpath)?"":"/")+nodeName,nodeValue.getBytes(),connection);
        response.setContentType("application/json;charset=UTF-8");
        if(StringUtil.isNotEmpty(path)) {
            ValueNode vn = zkOptionServiceImpl.getPathChildren(zpath,connection);
            ZTree zTree = ConvertTreeNode.convertValueNode(vn);
            String json = Bean2ViewUtil.cvtTree2Json(zTree);
            response.getOutputStream().write(("{\"result\":1,\"node\":" + json + "}").getBytes());
        }
        else{
            response.getOutputStream().write("{\"result\":0}".getBytes());
        }
        return null;
    }
    @RequestMapping(value = "/addRootNode")
    public String addRootNode(@RequestParam(required = true)String connectStr,
    		@RequestParam(required = false)String connectName,HttpServletRequest request,HttpServletResponse response) throws IOException, InterruptedException, KeeperException {
        Connect conn=new Connect();
        conn.setName(connectName);
        conn.setStr(connectStr);
    	List connSet  = createConnSet(request,conn);
        response.getOutputStream().write(("{\"result\":1,\"rootNodes\":" + Bean2ViewUtil.convert2RootNodes(connSet) + "}").getBytes());

        return null;
    }

    private List createConnSet(HttpServletRequest request,Connect connect) throws IOException {
    	List<Connect> connSet = JSONFile.readConnects();
    	if(StringUtil.isEmpty(connect.getStr())){
    		return connSet;
    	}
    	connect.setStr(connect.getStr().trim());
    	for (Connect connect2 : connSet) {
    		if(connect.getStr().equalsIgnoreCase(connect2.getStr())){
                return  connSet;
            }
		}
    	connSet.add(connect);
        JSONFile.persistConnect(connSet);
        return  connSet;
       
    }

    @RequestMapping(value = "/modifyNode")
    public String modifyNode(@RequestParam(required = false)String connectStr,@RequestParam(required = false)String zpath,@RequestParam(required = false)String content,HttpServletRequest request,HttpServletResponse response) throws IOException {
        ZookeeperConnection connection = getConnection();
        connection.setConnectString(connectStr);
        boolean result = zkOptionServiceImpl.setData(connection,zpath,content);
        response.setContentType("application/json;charset=UTF-8");
        if(result){
            response.getOutputStream().write(("{\"result\":1}").getBytes());
        }else{
            response.getOutputStream().write(("{\"result\":0}").getBytes());
        }

        return null;
    }

    @RequestMapping(value = "/index")
    public String show(@RequestParam(required = false)String connectStr,
    		@RequestParam(required = false)String connectName,HttpServletRequest request) throws IOException {
    	Connect conn=new Connect();
        conn.setName(connectName);
        conn.setStr(connectStr);
    	List connSet  = createConnSet(request,conn);
        request.setAttribute("rootNodes",Bean2ViewUtil.convert2RootNodes(connSet));

        return "showData";
    }
}
