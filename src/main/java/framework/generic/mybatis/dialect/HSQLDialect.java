package framework.generic.mybatis.dialect;

public class HSQLDialect extends Dialect
{
  public boolean supportsLimit()
  {
    return true;
  }

  public boolean supportsLimitOffset() {
    return true;
  }

  @SuppressWarnings("unused")
public String getLimitString(String sql, int offset, String offsetPlaceholder, int limit, String limitPlaceholder) {
		boolean hasOffset = offset > 0;
    return new StringBuffer(sql.length() + 10).append(sql).insert(sql.toLowerCase().indexOf("select") + 6, " top " + limitPlaceholder).toString();
  }
}