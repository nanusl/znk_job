package cn.bjznk.T1;

import cn.bjznk.ZnkJobApplicationTests;
import cn.bjznk.model.BirthdayCount;
import cn.bjznk.repository.BirthdayCountRepository;
import cn.bjznk.repository.UserRepository;
import cn.hutool.core.map.MapUtil;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryTest extends ZnkJobApplicationTests {

    @Resource
    private UserRepository userRepository;

    @Resource
    private BirthdayCountRepository birthdayCountRepository;


    /**
     * 由定时任务执行
     */
    @Test
    void cronQuery() {
        syncBirthdayCountTable();
    }

    /**
     * 面向用户实时查询
     */
    @Test
    void Query() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<Map> countData = birthdayCountRepository.queryAll();
        for (Map countDatum : countData) {
            System.out.printf("年龄：%s，人数：%s%n", MapUtil.getInt(countDatum, "age"), MapUtil.getInt(countDatum, "count"));
        }
        stopWatch.stop();

        System.out.printf("===耗时: %s 秒%n", stopWatch.getTotalTimeSeconds());
    }

    /**
     * 全量同步
     */
    void syncBirthdayCountTable() {

        birthdayCountRepository.deleteAll();

        List<Map> allBirthdayGroupBy = userRepository.findAllBirthdayGroupBy();

        List<BirthdayCount> list = allBirthdayGroupBy
                .parallelStream()
                .map(map -> new BirthdayCount(MapUtil.getLong(map, "count"), MapUtil.getStr(map, "birthday"), MapUtil.getInt(map, "age")))
                .collect(Collectors.toList());

        birthdayCountRepository.saveAll(list);
    }
}
