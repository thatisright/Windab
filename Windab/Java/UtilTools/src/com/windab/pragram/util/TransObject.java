package com.windab.pragram.util;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
/**
 * 获取对象内部信息，包括属性、方法等
 * @author XiaoQingqing
 */
public class TransObject {
	// 基本数据类型集
	private Set<String> baseTypes;
	// 私有的单例对象
	private volatile static TransObject transObject;
	
	/**
	 * 私有的构造方法
	 */
	private TransObject() {
		baseTypes = new TreeSet<String>();
		baseTypes.add("byte");
		baseTypes.add("short");
		baseTypes.add("int");
		baseTypes.add("long");
		baseTypes.add("float");
		baseTypes.add("double");
		baseTypes.add("char");
		baseTypes.add("boolean");
	}
	
	/**
	 * 获取实例方法
	 * @return
	 */
    public static TransObject getInstance() {  
    	if (transObject == null) {  
    		synchronized (TransObject.class) {  
    			if (transObject == null) {  
    				transObject = new TransObject();  
    			}  
    		}  
    	}  
    	return transObject;  
    }  
	
	/**
	 * 深度遍历对象的各个属性类型，属性值
	 * @param object 遍历对象
	 * @param level 遍历层级
	 * @return 遍历结果
	 */
	public Map<String, Object> TransProperty(Object object, int level) {
		Map<String, Object> result = new Hashtable<String, Object>();
		if(object == null) return result;
		result.put("level", level++);
		Field[] fields = object.getClass().getDeclaredFields();
		for(int i=0,len=fields.length; i<len; i++) {
			Map<String, Object> sub = new Hashtable<String, Object>();
			result.put(fields[i].getName(), sub);
			try {
				fields[i].setAccessible(true);
				String type = fields[i].getType().getName();
				sub.put("type", type);
				if(isStringType(type) || isBaseType(type)) {
					sub.put("value", fields[i].get(object));
					continue;
				}
				sub.put("value", TransProperty(fields[i].get(object), level));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 判断属性名称是否是字符串
	 * @param type 属性名称
	 * @return 判断结果
	 */
	public boolean isStringType(String type) {
		return "java.lang.String".equals(type);
	}
	
	/**
	 * 判断属性名称是否是基本类型
	 * @param type 属性名称
	 * @return 判断结果
	 */
	public boolean isBaseType(String type) {
		return baseTypes.contains(type);
	}
}
