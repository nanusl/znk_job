package cn.bjznk.repository;

import cn.bjznk.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query(value = "select DATE_FORMAT(birthday,'%Y-%m-%d') as birthday, TIMESTAMPDIFF(YEAR, birthday, CURDATE()) age, count(1) as count from t_user group by birthday", nativeQuery = true)
    List<Map> findAllBirthdayGroupBy();
}
