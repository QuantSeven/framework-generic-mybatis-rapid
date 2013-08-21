package framework.generic.mybatis.page;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 分页工具类，
 * 
 * @author quanyongan
 * 
 */
public class Pagination {

	// -- 公共变量 --//
	public static final String ASC = "asc";
	public static final String DESC = "desc";

	// -- 分页参数 --//
	protected int pageNo = 0;
	protected int pageSize = 10;
	protected String orderBy = null;
	protected String order = DESC;
	protected Map<String, Object> parameter = new TreeMap<String, Object>();
	@SuppressWarnings("rawtypes")
	protected List<?> result = new LinkedList();
	protected int totalCount = 0;

	public int getPageNo() {
		int index = pageNo <= 0 ? 1 : pageNo;
		return (index - 1) * pageSize;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public Map<String, Object> getParameter() {
		parameter.put("orderBy", getOrderBy());
		parameter.put("order", getOrder());
		parameter.put("pageNo", getPageNo());
		parameter.put("pageSize", getPageSize());
		return parameter;
	}

	public void setParameter(Map<String, Object> parameter) {
		this.parameter = parameter;
	}

	public List<?> getResult() {
		return result;
	}

	public void setResult(List<?> result) {
		this.result = result;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
