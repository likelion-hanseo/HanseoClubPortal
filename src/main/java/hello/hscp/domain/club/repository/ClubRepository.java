// src/main/java/hello/hscp/domain/club/repository/ClubRepository.java
package hello.hscp.domain.club.repository;

import hello.hscp.domain.club.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ClubRepository extends JpaRepository<Club, Long> {

    @Query("""
        select c from Club c
        where (:q is null or :q = '' or c.name like concat('%', :q, '%'))
          and (
             (:state = 'PRE' and :now < c.recruitStartAt)
          or (:state = 'OPEN' and :now >= c.recruitStartAt and :now < c.recruitEndAt)
          or (:state = 'CLOSED' and :now >= c.recruitEndAt)
          )
        order by c.name asc
    """)
    List<Club> searchPublic(@Param("q") String q,
                            @Param("state") String state,
                            @Param("now") LocalDateTime now);
}

