package com.agents.builder.common.script.groovy;



import com.agents.builder.common.script.common.FunctionExecutor;
import groovy.lang.GroovyClassLoader;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class GroovyFactory {


	private static GroovyFactory groovyFactory = new GroovyFactory();
	public static GroovyFactory getInstance(){
		return groovyFactory;
	}
	public static void refreshInstance(int type){
		if (type == 0) {
			groovyFactory = new GroovyFactory();
		} else if (type == 1) {
			groovyFactory = new SpringGroovyFactory();
		}
	}


	/**
	 * groovy class loader
	 */
	private GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
	private ConcurrentMap<String, Class<?>> CLASS_CACHE = new ConcurrentHashMap<>();

	/**
	 * load new instance, prototype
	 *
	 * @param codeSource
	 * @return
	 * @throws Exception
	 */
	public FunctionExecutor loadNewInstance(String codeSource) throws Exception{
		if (codeSource!=null && codeSource.trim().length()>0) {
			Class<?> clazz = getCodeSourceClass(codeSource);
			if (clazz != null) {
				Object instance = clazz.newInstance();
				if (instance!=null) {
					if (instance instanceof FunctionExecutor) {
						this.injectService(instance);
						return (FunctionExecutor) instance;
					} else {
						throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, "
								+ "cannot convert from instance["+ instance.getClass() +"] to WarningJudgeHandler");
					}
				}
			}
		}
		throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, instance is null");
	}
	private Class<?> getCodeSourceClass(String codeSource){
		try {
			// md5
			byte[] md5 = MessageDigest.getInstance("MD5").digest(codeSource.getBytes());
			String md5Str = new BigInteger(1, md5).toString(16);

			Class<?> clazz = CLASS_CACHE.get(md5Str);
			if(clazz == null){
				clazz = groovyClassLoader.parseClass(codeSource);
				CLASS_CACHE.putIfAbsent(md5Str, clazz);
			}
			return clazz;
		} catch (Exception e) {
			return groovyClassLoader.parseClass(codeSource);
		}
	}

	/**
	 * inject service of bean field
	 *
	 * @param instance
	 */
	public void injectService(Object instance) {
		// do something
	}

}
