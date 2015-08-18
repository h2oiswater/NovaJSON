package com.yxst.yoptalk.data.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NovaJSON {
	
	 public boolean IsObject() 		default false;

	 public boolean AddToJSON() 	default true;
	 
	 public String  NameInJSON() 	default "";

	 int[] Group() default {NovaJSONWorker.DEFAULT_GROUP};
	 
}
