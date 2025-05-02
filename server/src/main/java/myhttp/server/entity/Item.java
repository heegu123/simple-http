package myhttp.server.entity;

import javax.persistence.*;

/*
 * Item 엔티티는 사용자 정보를 나타내는 JPA 엔티티.
 * 데이터베이스의 "items" 테이블과 매핑.
 * */
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상품 ID, PK

    private String name; // 상품명
    private Integer price; // 가격

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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
