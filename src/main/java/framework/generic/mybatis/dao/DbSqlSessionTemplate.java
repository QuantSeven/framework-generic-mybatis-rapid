package framework.generic.mybatis.dao;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import framework.generic.mybatis.model.PersistentModel;

public class DbSqlSessionTemplate {

	protected SqlSessionFactory sqlSessionFactory;

	public DbSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public Object execute(SqlSessionCallback action) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			Object result = action.doInSession(session);
			return result;
		}finally {
			if (session != null)
				session.close();
		}
	}

	public Object selectOne(final String statement) {
		return execute(new SqlSessionCallback() {
			public Object doInSession(SqlSession session) {
				return session.selectOne(statement);
			}
		});
	}

	public Object selectOne(final String statement, final Object parameter) {
		return execute(new SqlSessionCallback() {
			public Object doInSession(SqlSession session) {
				return session.selectOne(statement, parameter);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> selectList(final String statement, final Object parameter, final int offset, final int limit) {
		return (List<T>) execute(new SqlSessionCallback() {
			public Object doInSession(SqlSession session) {
				return session.selectList(statement, parameter, new RowBounds(offset, limit));
			}
		});
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> selectList(final String statement) {
		return (List<T>) execute(new SqlSessionCallback() {
			public List<T> doInSession(SqlSession session) {
				return (List<T>) session.selectList(statement);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> selectList(final String statement, final Object parameter) {
		return (List<T>) execute(new SqlSessionCallback() {
			public List<T> doInSession(SqlSession session) {
				return (List<T>) session.selectList(statement, parameter);
			}
		});
	}

	public int delete(final String statement, final Object parameter) {
		return (Integer) execute(new SqlSessionCallback() {
			public Object doInSession(SqlSession session) {
				return session.delete(statement, parameter);
			}
		});
	}

	public int update(final String statement, final Object parameter) {
		return (Integer) execute(new SqlSessionCallback() {
			public Object doInSession(SqlSession session) {
				return session.update(statement, parameter);
			}
		});
	}

	public int insert(final String statement, final Object parameter) {
		return (Integer) execute(new SqlSessionCallback() {
			public Object doInSession(SqlSession session) {
				return session.insert(statement, parameter);
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends PersistentModel> int deleteEntity(final T entity) {
		return (Integer) execute(new SqlSessionCallback() {
			public Object doInSession(SqlSession session) {
				return session.getMapper(GenericCurdTemplate.class).deleteObject(entity);
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends PersistentModel> T insertEntity(final T entity) {
		return (T) execute(new SqlSessionCallback() {
			public Object doInSession(SqlSession session) {
				int insertRecord = session.getMapper(GenericCurdTemplate.class).insertObject(entity);
				if (insertRecord != 1) {
					return null;
				}
				return entity;
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends PersistentModel> T updateEntity(final T entity) {
		return (T) execute(new SqlSessionCallback() {
			public Object doInSession(SqlSession session) {
				int updatedRecord = session.getMapper(GenericCurdTemplate.class).updateObject(entity);
				if (updatedRecord != 1) {
					return null;
				}
				return entity;
			}
		});
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends PersistentModel,PK extends Serializable> T findByKey(final T entity,final PK key) {
		return (T) execute(new SqlSessionCallback() {
			public Object doInSession(SqlSession session) {
				return session.getMapper(GenericCurdTemplate.class).findByKey(entity, key);
			}
		});
	}
	public static interface SqlSessionCallback {
		public Object doInSession(SqlSession session);
	}
}