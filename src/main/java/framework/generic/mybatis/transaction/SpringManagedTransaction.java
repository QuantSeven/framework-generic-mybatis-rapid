package framework.generic.mybatis.transaction;

import static org.springframework.jdbc.datasource.DataSourceUtils.isConnectionTransactional;
import static org.springframework.jdbc.datasource.DataSourceUtils.releaseConnection;
import static org.springframework.util.Assert.notNull;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.transaction.Transaction;
import org.springframework.jdbc.datasource.DataSourceUtils;


public class SpringManagedTransaction implements Transaction {

  private final Log logger = LogFactory.getLog(getClass());

  private final DataSource dataSource;

  private Connection connection;

  private boolean isConnectionTransactional;

  private boolean autoCommit;

  public SpringManagedTransaction(DataSource dataSource) {
    notNull(dataSource, "No DataSource specified");
    this.dataSource = dataSource;
  }

  /**
   * {@inheritDoc}
   */
  public Connection getConnection() throws SQLException {
    if (this.connection == null) {
      openConnection();
    }
    return this.connection;
  }

  private void openConnection() throws SQLException {
    this.connection = DataSourceUtils.getConnection(this.dataSource);
    this.autoCommit = this.connection.getAutoCommit();
    this.isConnectionTransactional = isConnectionTransactional(this.connection, this.dataSource);

    if (this.logger.isDebugEnabled()) {
      this.logger.debug(
          "JDBC Connection ["
              + this.connection
              + "] will"
              + (this.isConnectionTransactional ? " " : " not ")
              + "be managed by Spring");
    }
  }

  /**
   * {@inheritDoc}
   */
  public void commit() throws SQLException {
    if (this.connection != null && !this.isConnectionTransactional && !this.autoCommit) {
      if (this.logger.isDebugEnabled()) {
        this.logger.debug("Committing JDBC Connection [" + this.connection + "]");
      }
      this.connection.commit();
    }
  }

  /**
   * {@inheritDoc}
   */
  public void rollback() throws SQLException {
    if (this.connection != null && !this.isConnectionTransactional && !this.autoCommit) {
      if (this.logger.isDebugEnabled()) {
        this.logger.debug("Rolling back JDBC Connection [" + this.connection + "]");
      }
      this.connection.rollback();
    }
  }

  /**
   * {@inheritDoc}
   */
  public void close() throws SQLException {
    releaseConnection(this.connection, this.dataSource);
  }

}
