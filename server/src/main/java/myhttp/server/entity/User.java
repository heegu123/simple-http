package myhttp.server.entity;

import javax.persistence.*;

/*
* User 엔티티는 사용자 정보를 나타내는 JPA 엔티티.
* 데이터베이스의 "users" 테이블과 매핑.
* */

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
