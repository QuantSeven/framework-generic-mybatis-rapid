package framework.generic.mybatis.dao;

import java.io.Serializable;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import framework.generic.mybatis.model.PersistentModel;

public interface GenericCurdTemplate<T extends PersistentModel<PK>, PK extends Serializable> {
	/**
	 * 插入一个实体 (采用注解的方式自动生成sql语句)
	 * 
	 * @param entity
	 *            实体对象
	 * @return 插入后的实体对象 T
	 */
	@InsertProvider(type = CurdTemplate.class, method = "getInsertSql")
	abstract Integer insertObject(T entity);

	/**
	 * 修改一个实体对象 (采用注解的方式自动生成sql语句)
	 * 
	 * @param entity
	 *            实体对象
	 * @return 修改后的实体对象 T
	 */
	@InsertProvider(type = CurdTemplate.class, method = "getUpdateSql")
	abstract Integer updateObject(T entity);

	/**
	 * 根据传入的对象删除记录 (采用注解的方式自动生成sql语句)
	 * 
	 * @param entity
	 *            实体对象
	 * @return 删除的对象个数，正常情况=1
	 */
	@InsertProvider(type = CurdTemplate.class, method = "getDeleteSql")
	abstract Integer deleteObject(T entity);

	@SelectProvider(type = CurdTemplate.class, method = "getByKeySql")
	abstract T findByKey(T entity,PK key);
	
	/*-------------------------------------调用存储过程---------------------------------------*/

	/**
	 * 获取给定表名的ID最大值（存储过程的名称为：spBas_GetMaxID）
	 * 
	 * @param tableName
	 *            表名
	 * @param len
	 *            产生的ID长度
	 * @return 一个在给出表名和长度的ID
	 */
	@Select("{CALL spBas_GetMaxID(#{tableName,jdbcType=VARCHAR,mode=IN},#{len,jdbcType=INTEGER,mode=IN})}")
	abstract String selectPrimaryKey(@Param("tableName") String tableName, @Param("len") int len);

	/**
	 * 获取指定存储过程名称，给定表名的ID或者CODE最大值
	 * 
	 * @param procedureName
	 *            存储过程的名称
	 * @param tableName
	 *            表名
	 * @param len
	 *            产生的CODE或者ID长度
	 * @return 一个在指定存储过程名称，给出表名和长度的ID或者CODE
	 */
	@Select("{CALL ${procedureName}(#{tableName,jdbcType=VARCHAR,mode=IN},#{len,jdbcType=INTEGER,mode=IN})}")
	abstract String selectMaxSeq(@Param("procedureName") String procedureName, @Param("tableName") String tableName, @Param("len") int len);

	/**
	 * 根据存储过程的名称，表名，字段，生成的序列长度，生成序列号
	 * 
	 * @param procedureName
	 *            存储过程的名称
	 * @param tableName
	 *            表名
	 * @param fieldName
	 *            字段
	 * @param len
	 *            产生的CODE或者ID长度
	 * @return 一个在指定存储过程名称，给出表名，字段和长度的ID或者CODE
	 */
	@Select("{CALL ${procedureName}(#{tableName,jdbcType=VARCHAR,mode=IN},#{fieldName,jdbcType=VARCHAR,mode=IN},#{len,jdbcType=INTEGER,mode=IN})}")
	abstract String selectMaxFieldSeq(@Param("procedureName") String procedureName, @Param("tableName") String tableName, @Param("fieldName") String fieldName, @Param("len") int len);

	/**
	 * 
	 * 根据存储过程的名称，表名，组织编码，生成的序列长度，生成序列号
	 * 
	 * @param procedureName
	 *            存储过程的名称
	 * @param tableName
	 *            表名
	 * @param orgId
	 *            组织编号
	 * @param preFix
	 *            单据前缀
	 * @param len
	 *            产生的长度
	 * @return 单据号
	 */
	@Select("{CALL ${procedureName}(#{tableName,jdbcType=VARCHAR,mode=IN},#{orgId,jdbcType=VARCHAR,mode=IN},#{preFix,jdbcType=VARCHAR,mode=IN},#{dateStr,jdbcType=VARCHAR,mode=IN},#{len,jdbcType=INTEGER,mode=IN})}")
	abstract String selectBillNoMaxSeq(@Param("procedureName") String procedureName, @Param("tableName") String tableName, @Param("orgId") String orgId, @Param("preFix") String preFix, @Param("dateStr") String dateStr, @Param("len") int len);

	/**
	 * 检查是否有关联
	 * 
	 * @param deleteTableName
	 *            需要删除数据所在的表名
	 * @param fieldValue
	 *            删除的值
	 * @return String 如果失败成返回关联的标明，成功返回空字符串
	 */
	@Select("{CALL PROC_DELETE_CHECK(#{pTableName,jdbcType=VARCHAR,mode=IN},#{pFieldValue,jdbcType=VARCHAR,mode=IN})}")
	abstract String checkAllowDeletions(@Param("pTableName") String deleteTableName, @Param("pFieldValue") String fieldValue);

}
