package framework.generic.mybatis.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrameworkUtil {

	public static Logger LogException = LoggerFactory.getLogger(FrameworkConst.EXCEPTION);

	private static final String PROPERTIES_FILE_NAME = "app.properties";

	/**
	 * 读取指定路 径文件的指定属性的值
	 * 
	 * @param name
	 *            属性名
	 * @return 属性值
	 */
	public static String getProperty(String name) {
		String value = null;
		InputStream input = FrameworkUtil.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
		Properties prop = new Properties();
		try {
			prop.load(input);
			value = prop.getProperty(name);
		} catch (IOException e) {
			LogException.error(e.getMessage());
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 判断是否为空，或者null
	 * 
	 * @param obj
	 *            需要判断的对象
	 * @return boolean
	 * @since 1.0
	 */
	public static boolean isNullOrEmpty(Object obj) {
		return obj == null || "".equals(obj.toString());
	}

	/**
	 * 线程工具操作，直接调用shutdownNow的方法.
	 */
	public static void normalShutdown(ExecutorService pool, int timeout, TimeUnit timeUnit) {
		try {
			pool.shutdownNow();
			if (!pool.awaitTermination(timeout, timeUnit)) {
				System.err.println("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
			LogException.error(ie.getMessage());
		}
	}

	/**
	 * 线程工具操作,自定义ThreadFactory,可定制线程池的名称.
	 */
	public static class CustomizableThreadFactory implements ThreadFactory {

		private final String namePrefix;
		private final AtomicInteger threadNumber = new AtomicInteger(1);

		public CustomizableThreadFactory(String poolName) {
			namePrefix = poolName + "-";
		}

		public Thread newThread(Runnable runable) {
			return new Thread(runable, namePrefix + threadNumber.getAndIncrement());
		}
	}

	/**
	 * 获取obj对象fieldName的Field
	 * 
	 * @param obj
	 * @param fieldName
	 * @return
	 */
	public static Field getFieldByFieldName(Object obj, String fieldName) {
		for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				return superClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
			}
		}
		return null;
	}

	/**
	 * 获取obj对象fieldName的属性
	 * 
	 * @param obj
	 * @param fieldName
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static Object getValueByFieldName(Object obj, String fieldName) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = getFieldByFieldName(obj, fieldName);
		Object value = null;
		if (field != null) {
			if (field.isAccessible()) {
				value = field.get(obj);
			} else {
				field.setAccessible(true);
				value = field.get(obj);
				field.setAccessible(false);
			}
		}
		return value;
	}

	/**
	 * 设置obj对象fieldName的属性
	 * 
	 * @param obj
	 * @param fieldName
	 * @param value
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void setValueByFieldName(Object obj, String fieldName, Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = obj.getClass().getDeclaredField(fieldName);
		if (field.isAccessible()) {
			field.set(obj, value);
		} else {
			field.setAccessible(true);
			field.set(obj, value);
			field.setAccessible(false);
		}
	}

	public static int countNumber(String str, String regex) { // 统计方法
		int count = 0;
		String[] k = str.split(regex); // 将字符串通过s断开返回数组k
		if (str.lastIndexOf(regex) == (str.length() - regex.length())) { // 如果y最后一个包含s的索引等于y的长度-要的长度，那么出现的次数就等于k的长度
			count = k.length;
		} else {
			count = k.length - 1;// 否则k长度-1，因为s不是单字符是多个
		}
		return count;
	}

	public static String[] spit(String str, String regex) {
		if (isNullOrEmpty(str)) {
			return null;
		}
		if (str.contains(regex)) {
			return str.split(regex);
		} else {
			return new String[] { str };
		}
	}
}
