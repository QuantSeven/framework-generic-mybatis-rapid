package framework.generic.mybatis.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import framework.generic.mybatis.annotation.Column;
import framework.generic.mybatis.annotation.Table;
import framework.generic.mybatis.exception.FrameworkException;
import framework.generic.mybatis.model.PersistentModel;

/**
 * 根据实体类对象生成SQL语句
 * 
 * @author quanyongan 2013-8-9 下午1:21:56
 */
public class CurdTemplate<T extends PersistentModel<PK>,PK extends Serializable> {

	public transient static Logger log = LoggerFactory.getLogger(CurdTemplate.class);

	protected transient static final String INSERTABLE = "insertable";
	protected transient static final String UPDATEABLE = "updateable";
	protected transient static final String ALL = "all";

	/**
	 * insert插入语句
	 * 
	 * @param entity
	 *            泛型对象
	 * @return String sql语句
	 */
	public String getInsertSql(T entity) {
		StringBuilder dbColumn = new StringBuilder();
		StringBuilder propertyColumn = new StringBuilder();
		LinkedHashMap<Object, Object> columnMap = getColumnMap(entity, INSERTABLE);
		int i = 0;
		for (Map.Entry<Object, Object> entry : columnMap.entrySet()) {
			if (i++ != 0) {
				dbColumn.append(',');
				propertyColumn.append(',');
			}
			propertyColumn.append("#{").append(entry.getKey()).append("}");
			dbColumn.append(entry.getValue());
		}
		SQL sql = new SQL();
		sql.INSERT_INTO(tablename(entity));
		sql.VALUES(String.valueOf(dbColumn), String.valueOf(propertyColumn));
		return sql.toString();
	}

	/**
	 * Update更新语句
	 * 
	 * @param entity
	 *            泛型对象
	 * @return String sql语句
	 */
	public String getUpdateSql(T entity) {
		StringBuilder setSql = new StringBuilder();
		StringBuilder whereSql = new StringBuilder();
		LinkedHashMap<Object, Object> idMap = getIdColumnMap(entity, UPDATEABLE);
		LinkedHashMap<Object, Object> columnMap = getColumnMap(entity, UPDATEABLE);
		if (columnMap != null && !columnMap.isEmpty()) {
			int i = 0;
			for (Map.Entry<Object, Object> entry : columnMap.entrySet()) {
				String key = String.valueOf(entry.getKey());
				if (idMap.get(key) != null) {
					continue;
				}
				if (i++ != 0) {
					setSql.append(',');
				}
				setSql.append(String.valueOf(entry.getValue())).append("=#{").append(key).append('}');
			}
		}
		if (idMap != null && !idMap.isEmpty()) {
			int j = 0;
			for (Map.Entry<Object, Object> entry : idMap.entrySet()) {
				String key = String.valueOf(entry.getKey());
				if (j++ != 0) {
					whereSql.append(" AND ");
				}
				whereSql.append(String.valueOf(entry.getValue())).append("=#{").append(key).append('}');
			}
		}
		SQL sql = new SQL();
		sql.UPDATE(tablename(entity));
		sql.SET(String.valueOf(setSql));
		sql.WHERE(String.valueOf(whereSql));
		return sql.toString();
	}

	/**
	 * Delete删除语句
	 * 
	 * @param entity
	 *            泛型对象
	 * @return String sql语句
	 */
	public String getDeleteSql(T entity) {
		StringBuilder whereSql = new StringBuilder();
		LinkedHashMap<Object, Object> idMap = getIdColumnMap(entity, INSERTABLE);
		if (idMap != null && !idMap.isEmpty()) {
			int j = 0;
			for (Map.Entry<Object, Object> entry : idMap.entrySet()) {
				String key = String.valueOf(entry.getKey());
				if (j++ != 0) {
					whereSql.append(" AND ");
				}
				whereSql.append(String.valueOf(entry.getValue())).append("=#{").append(key).append('}');
			}
		}
		SQL sql = new SQL();
		sql.DELETE_FROM(tablename(entity));
		sql.WHERE(String.valueOf(whereSql));
		return sql.toString();
	}
	
	public String getByKeySql(T entity,PK pk) {
		StringBuilder dbColumn = new StringBuilder();
		StringBuilder whereSql = new StringBuilder();
		LinkedHashMap<Object, Object> columnMap = getColumnMap(entity, ALL);
		int i = 0;
		for (Map.Entry<Object, Object> entry : columnMap.entrySet()) {
			if (i++ != 0) {
				dbColumn.append(',');
			}
			dbColumn.append(entry.getValue());
		}
		LinkedHashMap<Object, Object> idMap = getIdColumnMap(entity, ALL);
		if (idMap != null && !idMap.isEmpty()) {
			int j = 0;
			for (Map.Entry<Object, Object> entry : idMap.entrySet()) {
				String key = String.valueOf(entry.getKey());
				if (j++ != 0) {
					whereSql.append(" AND ");
				}
				whereSql.append(String.valueOf(entry.getValue())).append("=#{").append(key).append('}');
			}
		}
		SQL sql = new SQL();
		sql.SELECT(String.valueOf(dbColumn));
		sql.FROM(tablename(entity));
		sql.WHERE(String.valueOf(whereSql));
		return sql.toString();
	}
	public String getCountSql(T entity,Object parameter) {
		return "";
	}

	/**
	 * 获取数据库的表名
	 * 
	 * @param entity
	 *            泛型对象
	 * @return 数据库表名
	 */
	protected String tablename(T entity) {
		Table table = entity.getClass().getAnnotation(Table.class);
		if (table != null) {
			return table.name();
		} else {
			log.error(new FrameworkException("undefine POJO @Table, need name(@Table(name)) 没有设置表名").toString());
			throw new FrameworkException("undefine POJO @Table, need name(@Table(name)) 没有设置表名");
		}
	}

	/**
	 * 获取主键
	 * 
	 * @param entity
	 *            泛型对象
	 * @return LinkedHashMap
	 */
	protected <E> LinkedHashMap<Object, Object> getIdColumnMap(T entity, String insertOrUpdate) {
		LinkedHashMap<Object, Object> idMap = new LinkedHashMap<Object, Object>();
		List<Field> fields = getAnnotationFieldLst(entity);
		if (fields != null && !fields.isEmpty()) {
			for (Field field : fields) {
				Column column = field.getAnnotation(Column.class);
				if (INSERTABLE.equals(insertOrUpdate)) {
					if (field.isAnnotationPresent(Column.class) && column.pk() && column.insertable()) {
						idMap.put(field.getName(), column.name());
					}
				} else if (UPDATEABLE.equals(insertOrUpdate)) {
					if (field.isAnnotationPresent(Column.class) && column.pk() && column.updatable()) {
						idMap.put(field.getName(), column.name());
					}
				} else {
					idMap.put(field.getName(), column.name());
				}
			}
		}
		return idMap;
	}

	/**
	 * 所有的Column注解属性
	 * 
	 * @param entity
	 *            泛型对象
	 * @return LinkedHashMap
	 */
	protected LinkedHashMap<Object, Object> getColumnMap(T entity, String insertOrUpdate) {
		LinkedHashMap<Object, Object> columnMap = new LinkedHashMap<Object, Object>();
		List<Field> fields = getAnnotationFieldLst(entity);
		if (fields != null && !fields.isEmpty()) {
			for (Field field : fields) {
				Column column = field.getAnnotation(Column.class);
				if (INSERTABLE.equals(insertOrUpdate)) {
					if (field.isAnnotationPresent(Column.class) && column.insertable()) {
						columnMap.put(field.getName(), column.name());
					}
				} else if (UPDATEABLE.equals(insertOrUpdate)) {
					if (field.isAnnotationPresent(Column.class) && column.updatable()) {
						columnMap.put(field.getName(), column.name());
					}
				} else {
					columnMap.put(field.getName(), column.name());
				}
			}
		}
		return columnMap;
	}

	/**
	 * 深度递归，多重继承
	 * 
	 * @param entity
	 * @return List
	 */
	private <E> List<Field> getAnnotationFieldLst(T entity) {
		List<Field> list = new ArrayList<Field>();
		Class<?> superClass = entity.getClass().getSuperclass();
		while (true) {
			if (superClass != null) {
				Field[] superFields = superClass.getDeclaredFields();
				if (superFields != null && superFields.length > 0) {
					for (Field field : superFields) {
						if (field.isAnnotationPresent(Column.class)) {
							list.add(field);
						}
					}
				}
				superClass = superClass.getSuperclass();
			} else {
				break;
			}
		}
		Field[] objFields = entity.getClass().getDeclaredFields();
		if (objFields != null && objFields.length > 0) {
			for (Field field : objFields) {
				if (field.isAnnotationPresent(Column.class)) {
					list.add(field);
				}
			}
		}
		return list;
	}
}