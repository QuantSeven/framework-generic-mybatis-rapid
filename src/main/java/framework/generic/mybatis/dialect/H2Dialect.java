package framework.generic.mybatis.dialect;

public class H2Dialect extends Dialect
{
  public boolean supportsLimit()
  {
    return true;
  }

  public String getLimitString(String sql, int offset, String offsetPlaceholder, int limit, String limitPlaceholder) {
    return sql.length() + 40 + sql + (offset > 0 ? " limit " + limitPlaceholder + " offset " + offsetPlaceholder : new StringBuilder().append(" limit ").append(limitPlaceholder).toString());
  }

  public boolean supportsLimitOffset()
  {
    return true;
  }
}