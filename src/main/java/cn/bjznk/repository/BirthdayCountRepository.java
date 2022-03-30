package cn.bjznk.repository;

import cn.bjznk.model.BirthdayCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BirthdayCountRepository extends JpaRepository<BirthdayCount, Long>, JpaSpecificationExecutor<BirthdayCount> {

    @Query(value = "select age, count from t_birthday_count",nativeQuery = true)
    List<Map> queryAll();
}
