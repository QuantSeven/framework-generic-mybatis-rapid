package framework.generic.mybatis.dialect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySQLPageHepler {
	/**
	 * 得到查询总数的sql
	 */
	public static String getCountString(String querySelect) {

		querySelect = getLineSql(querySelect);
		// int orderIndex = getLastOrderInsertPoint(querySelect);

		int formIndex = getAfterFormInsertPoint(querySelect);
		String select = querySelect.substring(0, formIndex);

		// 如果SELECT 中包�? DISTINCT 只能在外层包含COUNT
		if (select.toLowerCase().indexOf("select distinct") != -1 || querySelect.toLowerCase().indexOf("group by") != -1) {
			return new StringBuffer(querySelect.length()).append("select count(0) from (").append(querySelect.substring(0)).append(" ) t").toString();
		} else {
			return new StringBuffer(querySelect.length()).append("select count(0) ").append(querySelect.substring(formIndex)).toString();
		}
	}

	/**
	 * 将SQL语句变成一条语句，并且每个单词的间隔都�?1个空�?
	 * 
	 * @param sql
	 *            SQL语句
	 * @return 如果sql是NULL返回空，否则返回转化后的SQL
	 */
	private static String getLineSql(String sql) {
		return sql.replaceAll("[\r\n]", " ").replaceAll("\\s{2,}", " ");
	}

	/**
	 * 得到SQL第一个正确的FROM的的插入
	 */
	private static int getAfterFormInsertPoint(String querySelect) {
		String regex = "\\s+FROM\\s+";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(querySelect);
		while (matcher.find()) {
			int fromStartIndex = matcher.start(0);
			String text = querySelect.substring(0, fromStartIndex);
			if (isBracketCanPartnership(text)) {
				return fromStartIndex;
			}
		}
		return 0;
	}

	/**
	 * 判断括号"()"是否匹配,并不会判断排列顺序是否正
	 * 
	 * @param text
	 *            要判断的文本
	 * @return 如果匹配返回TRUE,否则返回FALSE
	 */
	private static boolean isBracketCanPartnership(String text) {
		if (text == null || (getIndexOfCount(text, '(') != getIndexOfCount(text, ')'))) {
			return false;
		}
		return true;
	}

	/**
	 * 得到一个字符在另一个字符串中出现的次数
	 * 
	 * @param text
	 *            文本
	 * @param ch
	 *            字符
	 */
	private static int getIndexOfCount(String text, char ch) {
		int count = 0;
		for (int i = 0; i < text.length(); i++) {
			count = (text.charAt(i) == ch) ? count + 1 : count;
		}
		return count;
	}
}
