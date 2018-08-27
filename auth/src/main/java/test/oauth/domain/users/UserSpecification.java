package test.oauth.domain.users;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import test.oauth.common.SearchBean;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class UserSpecification implements Specification<UserEntity> {
	
	private static final String[] findList = {"%", "_"};
	private static final String[] replList = {"\\%","\\_"};

    @Autowired
    private SearchBean searchBean;

    public UserSpecification(SearchBean searchBean) {
        this.searchBean = searchBean;
    }

	@Override
	public Predicate toPredicate(Root<UserEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

    	searchBean.setValue(StringUtils.replaceEach(searchBean.getValue().toString(), this.findList, this.replList));
		
		if (searchBean.getOperation().equalsIgnoreCase(">")) {
			return builder.greaterThanOrEqualTo(root.<String>get(searchBean.getKey()),
					searchBean.getValue().toString());
		} else if (searchBean.getOperation().equalsIgnoreCase("<")) {
			return builder.lessThanOrEqualTo(root.<String>get(searchBean.getKey()), searchBean.getValue().toString());
		} else if (searchBean.getOperation().equalsIgnoreCase(":")) {
			if (root.get(searchBean.getKey()).getJavaType() == String.class) {
				return builder.like(root.<String>get(searchBean.getKey()), "%" + searchBean.getValue() + "%");
			} else {
				return builder.equal(root.get(searchBean.getKey()), searchBean.getValue());
			}
		} else if (searchBean.getOperation().equalsIgnoreCase(";")) {
			boolean manage = Boolean.valueOf(searchBean.getValue().toString());
			if (manage) {
				return builder.isTrue(root.get(searchBean.getKey()));
			} else {
				return builder.isFalse(root.get(searchBean.getKey()));
			}
		}
		return null;
	}
}
