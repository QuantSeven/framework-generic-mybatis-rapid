package framework.generic.mybatis.dao;

import java.io.Serializable;
import java.util.List;

import framework.generic.mybatis.model.PersistentModel;
import framework.generic.mybatis.page.Pagination;

/**
 * MyBatis的CRUD基接口类
 * <p>
 * 项目名称：framework-generic-mybatis
 * </p>
 * <p>
 * 版权：2013-广州扬基信息科技有限公司
 * </p>
 * 
 * @see framework.generic.mybatis.dao.GenericDao
 * @version 1.0, 2013-8-14 上午10:13:57
 * @author quanyongan
 */
public interface GenericDao<T extends PersistentModel<PK>, PK extends Serializable> {

	/**
	 * /** 插入一个实体（在数据库INSERT一条记录）
	 * 
	 * @param entity
	 *            实体对象
	 */
	abstract T insertEntity(T entity);

	/**
	 * 修改一个实体对象（MODIFY一条记录）
	 * 
	 * @param entity
	 *            实体对象
	 * @return 修改的对象个数，正常情况=1
	 */
	abstract T updateEntity(T entity);

	/**
	 * 根据传入的对象删除记录
	 * 
	 * @param entity
	 *            实体对象
	 * @return 删除的对象个数，正常情况=1
	 */
	abstract Integer deleteEntity(T entity);

	abstract T insert(T entity);

	abstract T update(T entity);

	abstract Integer delete(T entity);

	abstract List<T> findListByParam(String statement, Object parameter);

	abstract T findByParam(String statement, Object parameter);

	abstract Pagination findByPage(Pagination pagination);

	abstract Pagination findByPage(String statementName, Pagination pagination);

	abstract T findByPk(PK pk);

	abstract List<T> findAll();

	abstract Integer deleteByPk(PK pk);

	abstract Integer count(Object parameter);

}
