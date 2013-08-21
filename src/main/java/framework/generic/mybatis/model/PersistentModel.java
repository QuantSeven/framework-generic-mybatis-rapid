package framework.generic.mybatis.model;

import java.io.Serializable;

public interface PersistentModel<PK extends Serializable> extends Serializable {

	public abstract PK getKey();
}
