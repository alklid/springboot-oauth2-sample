package test.oauth.domain.users;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    boolean existsByEmail(String email);
    UserEntity findByEmail(String email);
    
    List<UserEntity> findByEmailContaining(String email);
    List<UserEntity> findByNameContaining(String name);
}