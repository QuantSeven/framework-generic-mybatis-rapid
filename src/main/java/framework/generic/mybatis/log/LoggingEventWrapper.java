package framework.generic.mybatis.log;

import java.util.Date;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import framework.generic.mybatis.util.FrameworkUtil;

/**
 * Log4j LoggingEvent的包装类,并添加需要扩展的字段, 提供默认的toString函数及更直观的属性访问方法.
 * 
 * <p>项目名称：framework-generic</p>
 * <p>版权：2012-广州扬基信息科技有限公司</p>
 * @see framework.generic.log.appender.LoggingEventWrapper
 * @version 1.0, 2012-12-10 上午8:52:32
 * @author quanyongan
 */
public class LoggingEventWrapper {

	public static final PatternLayout DEFAULT_PATTERN_LAYOUT = new PatternLayout("%l %c %d [%t] %-5p - %m");

	private final LoggingEvent event;
	private String accountId;
	private String appId;
	private String ipAddress;
	private String action;
	private String message;
	private Date createDate;

	public LoggingEventWrapper(LoggingEvent event) {
		this.event = event;
		if (!FrameworkUtil.isNullOrEmpty(event.getMDC(LoggingConst.ACCOUNT_ID)))
			this.accountId = (String) event.getMDC(LoggingConst.ACCOUNT_ID);
		if (!FrameworkUtil.isNullOrEmpty(event.getMDC(LoggingConst.APP_ID)))
			this.appId = (String) event.getMDC(LoggingConst.APP_ID);
		if (!FrameworkUtil.isNullOrEmpty(event.getMDC(LoggingConst.IP)))
			this.ipAddress = (String) event.getMDC(LoggingConst.IP);
		if (!FrameworkUtil.isNullOrEmpty(event.getMDC(LoggingConst.ACTION)))
			this.action = (String) event.getMDC(LoggingConst.ACTION);
		if (!FrameworkUtil.isNullOrEmpty(event.getMDC(LoggingConst.CREATE_DATE)))
			this.createDate = (Date) event.getMDC(LoggingConst.CREATE_DATE);
		this.message = (String) event.getMessage();
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

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}
