package framework.generic.mybatis.exception;

import java.util.Date;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import framework.generic.mybatis.util.FrameworkUtil;

/**
 * Log4j LoggingEvent的包装类, 并扩展其他的属性。 提供默认的toString函数及更直观的属性访问方法.
 * 
 * <p>项目名称：framework-generic</p>
 * <p>版权：2012-广州扬基信息科技有限公司</p>
 * @see framework.generic.exception.appender.ExceptionEventWrapper
 * @version 1.0, 2012-12-10 上午8:52:32
 * @author quanyongan
 */
public class ExceptionEventWrapper {
	public static final PatternLayout DEFAULT_PATTERN_LAYOUT = new PatternLayout("%l %c %d [%t] %-5p - %m");

	private final LoggingEvent event;
	private String className;
	private String methodName;
	private String lineNum;
	private String appId;
	private Date createDate;
	private String message;

	public ExceptionEventWrapper(LoggingEvent event) {
		this.event = event;
		if(!FrameworkUtil.isNullOrEmpty(event.getMDC(ExceptionConst.APP_ID)))
			this.appId = (String)event.getMDC(ExceptionConst.APP_ID);
		if(!FrameworkUtil.isNullOrEmpty(event.getMDC(ExceptionConst.CLASS_NAME)))
			this.className = (String)event.getMDC(ExceptionConst.CLASS_NAME);
		if(!FrameworkUtil.isNullOrEmpty(event.getMDC(ExceptionConst.CLASS_NAME)))
			this.methodName = (String)event.getMDC(ExceptionConst.METHOD_NAME);
		if(!FrameworkUtil.isNullOrEmpty(event.getMDC(ExceptionConst.LINE_NUM)))
			this.lineNum = event.getMDC(ExceptionConst.LINE_NUM).toString();
		if(!FrameworkUtil.isNullOrEmpty(event.getMDC(ExceptionConst.CREATE_DATE)))
			this.createDate = (Date)event.getMDC(ExceptionConst.CREATE_DATE);
		
		this.message = (String)event.getMessage();
			
	}
	/**
	 * 使用默认的layoutPattern转换事件到日志字符串.
	 *
	 * @return
	 * @since 1.0
	 */
	public String convertToString() {
		return DEFAULT_PATTERN_LAYOUT.format(event);
	}
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getLineNum() {
		return lineNum;
	}
	public void setLineNum(String lineNum) {
		this.lineNum = lineNum;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
