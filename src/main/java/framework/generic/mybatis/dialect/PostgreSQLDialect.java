package framework.generic.mybatis.dialect;

public class PostgreSQLDialect extends Dialect
{
  public boolean supportsLimit()
  {
    return true;
  }

  public boolean supportsLimitOffset() {
    return true;
  }

  public String getLimitString(String sql, int offset, String offsetPlaceholder, int limit, String limitPlaceholder)
  {
    return sql.length() + 20 + sql + (offset > 0 ? " limit " + limitPlaceholder + " offset " + offsetPlaceholder : new StringBuilder().append(" limit ").append(limitPlaceholder).toString());
  }
}