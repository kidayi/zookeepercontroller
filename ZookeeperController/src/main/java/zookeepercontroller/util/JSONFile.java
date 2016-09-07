package zookeepercontroller.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zookeepercontroller.bean.Connect;

/**
 * User: PageLiu Date: 12-11-28 Time: 下午8:56
 */
public class JSONFile {

	public static void persistConnect(List<Connect> connSet) throws IOException {
		if (connSet != null) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("conns", connSet);

			File jsonFile = new File(JSONFile.class.getResource("/").getFile());
			if (jsonFile.exists() && jsonFile.isDirectory()) {
				File file = new File(jsonFile, ".zkcontroller");
				file.mkdir();
				File jFile = new File(file, "conns.json");
				FileWriter fw = new FileWriter(jFile);
				fw.write(jsonObject.toJSONString());
				fw.close();
			} else {
				throw new IOException("没有找到用户路径！");
			}
		}
	}

	public static List<Connect> readConnects() throws IOException {
		File jsonFile = new File(JSONFile.class.getResource("/").getFile());
		List<Connect> connSet = new ArrayList<Connect>();
		if (jsonFile.exists() && jsonFile.isDirectory()) {
			File file = new File(jsonFile, ".zkcontroller");
			if (!file.exists() || !file.isDirectory()) {
				file.mkdir();

			}

			File jFile = new File(file, "conns.json");
			if (jFile.exists()) {
				FileReader fr = new FileReader(jFile);
				char[] chas = new char[1024];
				StringBuffer sbs = new StringBuffer();
				int n = 0;
				while ((n = fr.read(chas)) != -1) {
					sbs.append(chas, 0, n);
				}
				fr.close();
				JSONObject jsonObject = JSON.parseObject(sbs.toString());
				JSONArray jsonArray = jsonObject.getJSONArray("conns");
				for (int i = 0; i < jsonArray.size(); i++) {
					Connect conn = jsonArray.getObject(i, Connect.class);
					connSet.add(conn);
				}

			} else {
				InputStream inputStream = JSONFile.class.getResourceAsStream("/settings.properties");
				if (inputStream != null) {
					try {
						Properties properties = new Properties();
						properties.load(inputStream);
						String defaultConns = (String) properties.get("defaultConns");
						if(defaultConns!=null){
							String[] conns = defaultConns.split(",");
							if(conns!=null){
								for (String conn : conns) {
									Connect connObj = new Connect();
									connObj.setStr(conn);
									connSet.add(connObj);
								}
							}
						}
					} catch (NullPointerException e) {
						e.printStackTrace();
					} finally {
						inputStream.close();
					}
				}

			}
		} else {
			throw new IOException("没有找到用户路径！");
		}
		return connSet;
	}

	public static void main(String[] args) {
		System.out.println(System.getProperties());
	}

}
