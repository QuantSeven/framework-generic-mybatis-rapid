package framework.generic.mybatis.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface Column {

	/**
	 * 对应的数据库字段名称
	 * 
	 * @return
	 */
	String name() default "";

	/**
	 * 是否主键
	 * 
	 * @return
	 */
	boolean pk() default false;

	/**
	 * 字段主键的顺序，方便索引
	 * 
	 * @return
	 */
	int order() default 0;

	/**
	 * 在产生了插入语句中，true:包含该字段,false:不包括该字段
	 * 
	 * @return
	 */
	boolean insertable() default true;

	/**
	 * 在产生了更新语句中，true:包含该字段,false:不包括该字段
	 * 
	 * @return boolean
	 */
	boolean updatable() default true;
	
}
