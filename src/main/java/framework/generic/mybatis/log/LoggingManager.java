package framework.generic.mybatis.log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 *  处理数据
 * <p>项目名称：framework-generic</p>
 * <p>版权：2012-广州扬基信息科技有限公司</p>
 * @see framework.generic.log.appender.LoggingManager
 * @version 1.0, 2012-12-21 上午10:00:47
 * @author quanyongan
 */
public class LoggingManager extends LoggingPeriodConsumer {

	protected String sql = "INSERT INTO COM_LOG(ACCOUNT_ID,IP_ADDRESS,APP_ID,ACTION,MESSAGE,CREATE_DATE) values(:account_id,:ip_address,:app_id,:action,:message,:create_date)";

	protected List<LoggingEventWrapper> eventsBuffer = Lists.newArrayList();
	protected NamedParameterJdbcTemplate jdbcTemplate;
	protected TransactionTemplate transactionTemplate;

	/**
	 * 带Named Parameter的insert sql.
	 * 
	 * Named Parameter的名称见AppenderUtils中的常量定义.
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}


	/**
	 * 根据注入的DataSource创建jdbcTemplate.
	 */
	@Resource
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * 根据注入的PlatformTransactionManager创建transactionTemplate.
	 */
	@Resource
	public void setDefaultTransactionManager(PlatformTransactionManager defaultTransactionManager) {
		transactionTemplate = new TransactionTemplate(defaultTransactionManager);
	}

	/**
	 * 消息处理函数,将消息放入buffer,当buffer达到batchSize时执行批量更新函数.
	 */
	@Override
	protected void processMessageList(List<LoggingEventWrapper> messageList) {
		eventsBuffer.addAll(messageList);
		//已到达BufferSize则执行批量插入操作
		/*if (eventsBuffer.size() >= batchSize) {
			
		}*/
		if(eventsBuffer.size() > 0) {
			updateBatch();
		}
	}

	/**
	 * 将Buffer中的事件列表批量插入数据库.
	 */
	@SuppressWarnings("rawtypes")
	public void updateBatch() {
		try {
			//分析事件列表, 转换为jdbc批处理参数.
			int i = 0;
			Map[] paramMapArray = new HashMap[eventsBuffer.size()];
			for (LoggingEventWrapper event : eventsBuffer) {
				paramMapArray[i++] = parseEvent(event);
			}
			final SqlParameterSource[] batchParams = SqlParameterSourceUtils.createBatch(paramMapArray);

			//执行批量插入,如果失败调用失败处理函数.
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					try {
						jdbcTemplate.batchUpdate(getActualSql(), batchParams);
						if (logger.isDebugEnabled()) {
							for (LoggingEventWrapper event : eventsBuffer) {
								logger.debug("saved event: {}", event.convertToString());
							}
						}
					} catch (DataAccessException e) {
						status.setRollbackOnly();
						handleDataAccessException(e, eventsBuffer);
					}
				}
			});

			//清除已完成的Buffer
			eventsBuffer.clear();
		} catch (Exception e) {
			logger.error("批量提交任务时发生错误.", e);
		}
	}

	/**
	 * 退出清理函数,完成buffer中未完成的消息.
	 */
	@Override
	protected void clean() {
		if (!eventsBuffer.isEmpty()) {
			updateBatch();
		}
		logger.debug("cleaned task {}", this);
	}

	/**
	 * 分析Event, 建立Parameter Map, 用于绑定sql中的Named Parameter.
	 */
	protected Map<String, Object> parseEvent(LoggingEventWrapper event) {
		
		Map<String, Object> parameterMap = Maps.newHashMap();
		parameterMap.put(LoggingConst.APP_ID, event.getAppId());
		parameterMap.put(LoggingConst.ACCOUNT_ID, event.getAccountId());
		parameterMap.put(LoggingConst.IP, event.getIpAddress());
		parameterMap.put(LoggingConst.ACTION, event.getAction());
		parameterMap.put(LoggingConst.CREATE_DATE, event.getCreateDate());
		parameterMap.put(LoggingConst.MESSAGE, event.getMessage());
		return parameterMap;
	}

	/**
	 * 可被子类重载的数据访问错误处理函数,如将出错的事件持久化到文件.
	 */
	protected void handleDataAccessException(DataAccessException e, List<LoggingEventWrapper> errorEventBatch) {
		if (e instanceof DataAccessResourceFailureException) {
			logger.error("database connection error", e);
		} else {
			logger.error("other database error", e);
		}

		for (LoggingEventWrapper event : errorEventBatch) {
			logger.error("event insert to database error, ignore it: "
					+ event.convertToString(), e);
		}
	}

	/**
	 * 可被子类重载的sql提供函数,可对sql语句进行特殊处理，如日志表的表名可带日期后缀 LOG_2012_12_10.
	 */
	protected String getActualSql() {
		return sql;
	}

}
