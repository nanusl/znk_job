package cn.bjznk.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "t_birthday_count")
public class BirthdayCount {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private Long count;

    private Integer age;

    private String birthday;

    public BirthdayCount() {

    }

    public BirthdayCount(Long count, String birthday, Integer age) {
        this.age = age;
        this.count = count;
        this.birthday = birthday;
    }
}
