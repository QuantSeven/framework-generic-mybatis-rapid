package framework.generic.mybatis.log;

import java.util.Date;

import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import framework.generic.mybatis.util.FrameworkConst;
import framework.generic.mybatis.util.FrameworkUtil;

/**
 * 日志处理类
 * 只提供info级别的日志
 * <p>项目名称：framework-generic</p>
 * <p>版权：2012-广州扬基信息科技有限公司</p>
 * @see framework.generic.log.extended.LoggingUtil
 * @version 1.0, 2012-12-19 上午11:18:37
 * @author quanyongan
 */
public class LoggingUtil {

	public static Logger log = LoggerFactory.getLogger(FrameworkConst.LOG);

	public static void info(String appId,String accountId,String ip,String action,String message) {
		if(FrameworkUtil.isNullOrEmpty(appId)) appId = "";
		if(FrameworkUtil.isNullOrEmpty(accountId)) accountId = "";
		if(FrameworkUtil.isNullOrEmpty(ip)) ip = "";
		if(FrameworkUtil.isNullOrEmpty(action)) action = "";
		if(FrameworkUtil.isNullOrEmpty(message)) message = "";
		MDC.put(LoggingConst.APP_ID, appId);
		MDC.put(LoggingConst.ACCOUNT_ID, accountId);
		MDC.put(LoggingConst.IP, ip);
		MDC.put(LoggingConst.ACTION, action);
		MDC.put(LoggingConst.CREATE_DATE, new Date(System.currentTimeMillis()));
		log.info(message);
	}
}
