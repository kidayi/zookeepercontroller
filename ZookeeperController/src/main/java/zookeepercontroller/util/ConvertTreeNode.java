package zookeepercontroller.util;

import zookeepercontroller.bean.ValueNode;
import zookeepercontroller.bean.ZTree;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

/**
 * User: PageLiu
 * Date: 12-11-28
 * Time: 下午10:05
 */
public class ConvertTreeNode {
    public static ZTree  convertValueNode(ValueNode valueNode){
        String path = valueNode.getZpath();
        String data = valueNode.getValue();
        
        ZTree root = new ZTree();
        root.setData(data);
        root.setName(getName(path));
        root.setZpath(path);
        root.setConnStr(valueNode.getConnStr());
        root.setParent(valueNode.getIsParent());
        List<ValueNode> valueNodeList= valueNode.getChildNodes();
        if(!CollectionUtils.isEmpty(valueNodeList)){
	        List<ZTree> zTreeList = new ArrayList<ZTree>();
	        for(ValueNode valueN:valueNodeList){
	            ZTree child = new ZTree();
	            child.setData(valueN.getValue());
	            child.setName(getName(valueN.getZpath()));
	            child.setZpath(valueN.getZpath());
	            child.setParent(valueN.getIsParent());
	            child.setConnStr(valueN.getConnStr());
	            zTreeList.add(child);
	        }
	        root.setChildTrees(zTreeList);
        }
        
        return root;
    }

    private static String getName(String path) {
        if(StringUtil.isNotEmpty(path)){
            int pos =path.lastIndexOf('/');
            if(pos==-1){
                return path;
            }else{
                 return path.substring(pos+1);
            }
        }
        return "";  //To change body of created methods use File | Settings | File Templates.
    }
}
