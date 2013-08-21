package framework.generic.mybatis.exception;

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
 * 将Queue中的log4j event写入数据库的消费者任务.
 * 
 * 即时阻塞的读取Queue中的事件,达到缓存上限后使用Jdbc批量写入模式.
 * <p>
 * framework-generic-mybatis
 * </p>
 * <p>
 * 项目名称：2013-广州扬基信息科技有限公司
 * </p>
 * 
 * @see framework.generic.mybatis.exception.ExceptionManager
 * @version 1.0, 2013-8-14 上午10:39:19
 * @author quanyongan
 */
public class ExceptionManager extends ExceptionPeriodConsumer {

	protected String sql = "INSERT INTO COM_EXCEPTION_LOG(APP_ID,CLASS_NAME,METHOD_NAME,LINE_NUM,MESSAGE,CREATE_DATE) VALUES(:app_id,:class_name,:method_name,:line_num,:message,:create_date)";

	protected List<ExceptionEventWrapper> eventsBuffer = Lists.newArrayList();
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
	protected void processMessageList(List<ExceptionEventWrapper> messageList) {
		eventsBuffer.addAll(messageList);
		// 已到达BufferSize则执行批量插入操作
		/*
		 * if (eventsBuffer.size() >= batchSize) {
		 * 
		 * }
		 */
		if (eventsBuffer.size() > 0) {
			updateBatch();
		}
	}

	/**
	 * 将Buffer中的事件列表批量插入数据库.
	 */
	@SuppressWarnings("rawtypes")
	public void updateBatch() {
		try {
			// 分析事件列表, 转换为jdbc批处理参数.
			int i = 0;
			Map[] paramMapArray = new HashMap[eventsBuffer.size()];
			for (ExceptionEventWrapper event : eventsBuffer) {
				paramMapArray[i++] = parseEvent(event);
			}
			final SqlParameterSource[] batchParams = SqlParameterSourceUtils.createBatch(paramMapArray);

			// 执行批量插入,如果失败调用失败处理函数.
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					try {
						jdbcTemplate.batchUpdate(getActualSql(), batchParams);
						if (logger.isDebugEnabled()) {
							for (ExceptionEventWrapper event : eventsBuffer) {
								logger.debug("saved event: {}", event.convertToString());
							}
						}
					} catch (DataAccessException e) {
						status.setRollbackOnly();
						handleDataAccessException(e, eventsBuffer);
					}
				}
			});

			// 清除已完成的Buffer
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
	protected Map<String, Object> parseEvent(ExceptionEventWrapper eventWrapper) {
		Map<String, Object> parameterMap = Maps.newHashMap();

		parameterMap.put(ExceptionConst.CLASS_NAME, eventWrapper.getClassName());
		parameterMap.put(ExceptionConst.METHOD_NAME, eventWrapper.getMethodName());
		parameterMap.put(ExceptionConst.LINE_NUM, eventWrapper.getLineNum());
		parameterMap.put(ExceptionConst.APP_ID, eventWrapper.getAppId());
		parameterMap.put(ExceptionConst.CREATE_DATE, eventWrapper.getCreateDate());
		parameterMap.put(ExceptionConst.MESSAGE, eventWrapper.getMessage());
		return parameterMap;
	}

	/**
	 * 可被子类重载的数据访问错误处理函数,如将出错的事件持久化到文件.
	 */
	protected void handleDataAccessException(DataAccessException e, List<ExceptionEventWrapper> errorEventBatch) {
		if (e instanceof DataAccessResourceFailureException) {
			logger.error("database connection error", e);
		} else {
			logger.error("other database error", e);
		}

		for (ExceptionEventWrapper event : errorEventBatch) {
			logger.error("event insert to database error, ignore it: " + event.convertToString(), e);
		}
	}

	/**
	 * 可被子类重载的sql提供函数,可对sql语句进行特殊处理，如日志表的表名可带日期后缀 LOG_2012_12_10.
	 */
	protected String getActualSql() {
		return sql;
	}

}
