package zookeepercontroller.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zookeepercontroller.bean.Connect;
import zookeepercontroller.bean.ZTree;

import java.util.List;
import java.util.Set;

/**
 * User: PageLiu
 * Date: 12-11-28
 * Time: 下午10:24
 */
public class Bean2ViewUtil {


    public static String cvtTreeChilds2Json(ZTree valueNode) {
        String result = "";
        if (valueNode != null && valueNode.getChildTrees() != null) {
            List<ZTree> valueNodeList = valueNode.getChildTrees();
            result = JSON.toJSONString(valueNodeList);
        }
        return result;
    }

    public static String cvtTree2Json(ZTree valueNode) {
        return JSON.toJSONString(valueNode);
    }

    public static String convert2RootNodes(List<Connect> connSet) {
        JSONArray jsonArray = new JSONArray();
        for(Connect conn:connSet){
            JSONObject jsonObject = new JSONObject();
            String showName="";
            if(StringUtil.isNotEmpty(conn.getName())){
            	showName+=conn.getName()+":";
            }
            showName+=conn.getStr();
            jsonObject.put("name",showName);
            jsonObject.put("connStr",conn.getStr());
            jsonObject.put("zpath","/");
            jsonObject.put("isParent",true);

            jsonObject.put("data","");
            jsonArray.add(jsonObject);
        }

        return jsonArray.toJSONString();
    }

}
