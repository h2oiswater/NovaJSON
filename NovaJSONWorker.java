package com.yxst.yoptalk.data.json;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class NovaJSONWorker {
	final static String SUPPORT_TYPE[] = new String[]{"int","Integer","long","Long","String","boolean","Boolean"};
	
	final String TAG = "NovaJSONWorker";
	
	public final static int DEFAULT_GROUP = -1;
	

	public NovaJSONWorker(){
	}
	
	/**
	 * covert all field marked NovaJSON
	 * @param obj
	 * @return
	 */
	public static JSONObject toJSON(Object obj){
		return toJSON(obj,DEFAULT_GROUP);
	}
	
	/**
	 * covert all field marked NovaJSON and group equals group written in NovaJSON
	 * @param obj
	 * @param group
	 * @return
	 */
	public static JSONObject toJSON(Object obj,int group){
		List<Field> fields = getAllField(obj.getClass());
		
		NovaJSON novaAnn = null;
		String pName = "";
		String type = "";
		
		JSONObject data = null;
		data = new JSONObject();
		
		
		if(fields!=null && fields.size()>0){
			
			for(Field field : fields){
				try {
					field.setAccessible(true);
					
					novaAnn  = field.getAnnotation(NovaJSON.class);
					
					if( novaAnn!=null && novaAnn.AddToJSON() && isInGroup(novaAnn.Group(), group) ){
						// According type to create different
						
						pName = novaAnn.NameInJSON();
						
						type = field.getType().getSimpleName();
						
						if( pName == null || pName.length() == 0 ){
							pName = field.getName();
						}
						
						if( isNormalSupportType(type) ){

							// if value null
							// set a default value
							if( field.get(obj) == null ){
								data.put(pName,getSuitDefaultValue(type));
							}else{
								data.put(pName, field.get(obj));
							}
							
						}else{
							if( field.get(obj) == null ){
								data.put(field.getName(),new JSONObject());
								continue;
							}
							// Maybe have sub class
							if( novaAnn.IsObject() ){
								data.put(pName,toJSON(field.get(obj), group));
							}
						}
					}
					
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
		
		return data;
	}
	
	private static Object getSuitDefaultValue(String type) {
		if( type.equals("int") 
				|| type.equals("Integer") ){
			return 0;
		}
		
		if( type.equals("long") 
				|| type.equals("Long") ){
			return 0l;
		}
		
		if( type.equals("String") ){
			return "";
		}
		
		if( type.equals("boolean") 
				|| type.equals("Boolean") ){
			return false;
		}
		
		return null;
	}

	private static boolean isNormalSupportType(String type) {
		for(String str:SUPPORT_TYPE){
			if(str.equals(type)){
				return true;
			}
		}
		return false;
	}
	
	private static List<Field> getAllField(Class<?> cls){
		List<Field> list = new ArrayList<Field>();
		
		Field[] fields = cls.getDeclaredFields();
		
		for(Field f:fields){
			list.add(f);
		}
		
		if( cls.getSuperclass() != null ){
			list.addAll(getAllField(cls.getSuperclass()));
		}
		
		return list;
	}
	
	
	private static boolean isInGroup(int[] groups,int group){
		
		// convert all
		if( group == -1 )return true;
		// convert not marked
		if(groups.length==1 && groups[0] == -1)return true;
		
		for(int i:groups){
			if( i == group )return true;
		}
		return false;
	}
}
