package test.oauth.domain.users;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import test.oauth.common.SearchBean;

public class UserSpecificationsBuilder {
    private final List<SearchBean> params;

    public UserSpecificationsBuilder() {
        params = new ArrayList<SearchBean>();
    }

    public UserSpecificationsBuilder with(String key, String operation, Object value) {
        params.add(new SearchBean(key, operation, value));
        return this;
    }

    public Specification<UserEntity> build() {
        if (params.size() == 0) {
            return null;
        }

        List<Specification<UserEntity>> specs = new ArrayList<Specification<UserEntity>>();
        for (SearchBean param : params) {
            specs.add(new UserSpecification(param));
        }

        Specification<UserEntity> result = specs.get(0);
        for (int i = 1; i < specs.size(); i++) {
            result = Specification.where(result).and(specs.get(i));
        }
        return result;
    }
}
