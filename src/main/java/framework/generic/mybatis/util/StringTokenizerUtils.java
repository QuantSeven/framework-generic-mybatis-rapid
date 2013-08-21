package framework.generic.mybatis.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class StringTokenizerUtils
{
  @SuppressWarnings({ "rawtypes", "unchecked" })
public static String[] split(String str, String seperators)
  {
    StringTokenizer tokenlizer = new StringTokenizer(str, seperators);
    List result = new ArrayList();

    while (tokenlizer.hasMoreElements()) {
      Object s = tokenlizer.nextElement();
      result.add(s);
    }
    return (String[])(String[])result.toArray(new String[result.size()]);
  }
}