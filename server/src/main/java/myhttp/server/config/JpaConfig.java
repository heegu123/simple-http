package myhttp.server.config;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/*
    * JPA (Java Persistence API) 설정을 위한 클래스
    * 이 클래스는 EntityManagerFactory를 생성하고 관리
    * EntityManagerFactory는 JPA를 사용하여 데이터베이스와 상호작용하는 데 필요한 객체
 */

public class JpaConfig {

    // persistence.xml에서 정의한 persistence-unit 이름
    private static final String PERSISTENCE_UNIT_NAME = "myhttp";
    // EntityManagerFactory는 JPA의 인터페이스로, 데이터베이스와의 연결을 관리
    private static EntityManagerFactory emf;

    // EntityManagerFactory를 생성하는 메서드
    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) { // emf가 null인 경우에만 생성
            // Persistence.createEntityManagerFactory() 메서드를 사용하여 EntityManagerFactory를 생성
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME); //
        }
        return emf;
    }
    // EntityManagerFactory를 닫는 메서드
    public static void shutdown() {
        if (emf != null) {
            emf.close();
        }
    }
}
