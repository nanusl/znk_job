package cn.bjznk.T1;

import cn.binarywang.tools.generator.ChineseNameGenerator;
import cn.bjznk.ZnkJobApplicationTests;
import cn.bjznk.model.User;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * 创建1000w测试数据
 *
 * 也可以使用： https://pan.baidu.com/s/16HGj4sWZaKfmfd9zjWBJPQ 分享码：6xry
 */
public class GenDataTest extends ZnkJobApplicationTests {

    private static final SecureRandom SECURE_RANDOM = RandomUtil.getSecureRandom();

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Test
    void init() {

        ChineseNameGenerator nameGenerator = ChineseNameGenerator.getInstance();

        User user;
        List<User> arrayList = Lists.newArrayList();
        for (int i = 0; i < 100000; i++) {
            user = new User();
            user.setName(nameGenerator.generate());
            user.setBirthday(genBirthday());
            arrayList.add(user);
        }

        List<List<User>> partition = ListUtil.partition(arrayList, 10000);

        ExecutorService executor = ThreadUtil.newExecutor(16);

        CountDownLatch count = new CountDownLatch(partition.size());

        for (List<User> subList : partition) {
            executor.execute(() -> {
                System.err.println("execute: " + Thread.currentThread().getName());
                try {
                    save(subList);
//                    ThreadUtil.sleep(RandomUtil.randomLong(100, 1000));
                } finally {
                    count.countDown();
                }
            });
        }

        try {
            count.await();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    private LocalDate genBirthday() {

        int minDay = (int) LocalDate.of(1922, 1, 1).toEpochDay(),
                maxDay = (int) LocalDate.of(2022, 3, 29).toEpochDay();

        return LocalDate.ofEpochDay(minDay + SECURE_RANDOM.nextInt(maxDay - minDay));
    }

    public void save(List<User> list) {

        String sql = "INSERT INTO `t_user` (`name`, `birthday`) VALUES (?, ?);";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public int getBatchSize() {
                return list.size();
            }

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                User user = list.get(i);
                ps.setString(1, user.getName());
                ps.setDate(2, Date.valueOf(user.getBirthday()));
            }
        });
    }
}
