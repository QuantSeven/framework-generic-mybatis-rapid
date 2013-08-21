package framework.generic.mybatis.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DaoSupport;
import org.springframework.util.Assert;

import framework.generic.mybatis.model.PersistentModel;
import framework.generic.mybatis.page.Pagination;
import framework.generic.mybatis.util.PropertyUtils;

public abstract class DbSqlSessionDaoSupport<T extends PersistentModel<PK>, PK extends Serializable> extends DaoSupport implements GenericDao<T, PK> {
	protected final Log log = LogFactory.getLog(getClass());

	private SqlSessionFactory sqlSessionFactory;

	private DbSqlSessionTemplate dbSqlSessionTemplate;

	protected void checkDaoConfig() throws IllegalArgumentException {
		Assert.notNull("sqlSessionFactory must be not null");
	}

	public abstract String getMybatisMapperNamesapce();

	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	@Autowired(required = true)
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
		this.dbSqlSessionTemplate = new DbSqlSessionTemplate(sqlSessionFactory);
	}

	public DbSqlSessionTemplate getDbSqlSessionTemplate() {
		return dbSqlSessionTemplate;
	}

	@Override
	public T insertEntity(T entity) {
		prepareObjectForSaveOrUpdate(entity);
		entity = getDbSqlSessionTemplate().insertEntity(entity);
		if (entity == null) {
			return null;
		}
		return (T) findByPk(entity.getKey());
	}

	@Override
	public T updateEntity(T entity) {
		prepareObjectForSaveOrUpdate(entity);
		entity = getDbSqlSessionTemplate().updateEntity(entity);
		if (entity == null) {
			return null;
		}
		return (T) getDbSqlSessionTemplate().findByKey(entity, entity.getKey());
	}

	@Override
	public Integer deleteEntity(T entity) {
		return getDbSqlSessionTemplate().deleteEntity(entity);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findListByParam(String statement, Object parameter) {
		return (List<T>) getDbSqlSessionTemplate().selectList(getStatement(statement), parameter);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T findByParam(String statement, Object parameter) {
		return (T) getDbSqlSessionTemplate().selectOne(getStatement(statement), parameter);
	}

	@Override
	public Pagination findByPage(Pagination pagination) {
		return findByPage(getPageQuery(), pagination);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Pagination findByPage(String statementName, Pagination pagination) {
		Number totalCount = (Number) getDbSqlSessionTemplate().selectOne(getCountQuery(), pagination.getParameter());
		pagination.setTotalCount(totalCount.intValue());
		// 其它分页参数,用于不喜欢或是因为兼容性而不使用方言(Dialect)的分页用户使用.
		Map<Object, Object> filters = new HashMap<Object, Object>();
		filters.put("pageNo", pagination.getPageNo());
		filters.put("pageSize", pagination.getPageSize());
		filters.put("orderBy", pagination.getOrderBy());
		filters.put("order", pagination.getOrder());
		// 混合两个filters为一个filters,MapAndObject.get()方法将在两个对象取值,Map如果取值为null,则再在Bean中取值
		if (pagination.getParameter() instanceof Map) {
			filters.putAll((Map) pagination.getParameter());
		} else {
			Map<Object, Object> parameterObject = PropertyUtils.describe(pagination.getParameter());
			filters.putAll(parameterObject);
		}
		List<T> list = getDbSqlSessionTemplate().selectList(statementName, filters, pagination.getPageNo(), pagination.getPageSize());
		pagination.setResult(list);
		return pagination;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T findByPk(PK pk) {
		return (T) getDbSqlSessionTemplate().selectOne(getByPrimaryKeyQuery(), pk);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findAll() {
		return (List<T>) getDbSqlSessionTemplate().selectList(getAllQuery());
	}

	@Override
	public Integer deleteByPk(PK pk) {
		return (Integer) getDbSqlSessionTemplate().delete(getDeleteByPkQuery(), pk);
	}

	@Override
	public Integer delete(T entity) {
		return (Integer) getDbSqlSessionTemplate().deleteEntity(entity);
	}

	@Override
	public T update(T entity) {
		prepareObjectForSaveOrUpdate(entity);
		Integer updateRecord = getDbSqlSessionTemplate().update(getUpdateQuery(), entity);
		if (updateRecord != 1) {
			return null;
		}
		return (T) getDbSqlSessionTemplate().findByKey(entity, (PK)entity.getKey());
	}

	@Override
	public T insert(T entity) {
		prepareObjectForSaveOrUpdate(entity);
		Integer updateRecord = getDbSqlSessionTemplate().insert(getInsertQuery(), entity);
		if (updateRecord != 1) {
			return null;
		}
		return (T) findByPk(entity.getKey());
	}

	@Override
	public Integer count(Object parameter) {
		return (Integer) getDbSqlSessionTemplate().selectOne(getCountQuery(), parameter);
	}

	/**
	 * 用于子类覆盖,在insert,update之前调用
	 * 
	 * @param o
	 */
	protected void prepareObjectForSaveOrUpdate(T o) {
	}

	protected String getStatement(String statement) {
		return getMybatisMapperNamesapce() + "." + statement;
	}

	protected String getByPrimaryKeyQuery() {
		return getMybatisMapperNamesapce() + ".findByPk";
	}

	protected String getInsertQuery() {
		return getMybatisMapperNamesapce() + ".insert";
	}

	protected String getUpdateQuery() {
		return getMybatisMapperNamesapce() + ".update";
	}

	public String getDeleteQuery() {
		return getMybatisMapperNamesapce() + ".delete";
	}

	protected String getDeleteByPkQuery() {
		return getMybatisMapperNamesapce() + ".deleteByPk";
	}

	protected String getCountQuery() {
		return getMybatisMapperNamesapce() + ".count";
	}

	protected String getAllQuery() {
		return getMybatisMapperNamesapce() + ".findAll";
	}

	protected String getPageQuery() {
		return getMybatisMapperNamesapce() + ".findByPage";
	}
}
