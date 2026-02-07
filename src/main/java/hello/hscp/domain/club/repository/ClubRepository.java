// src/main/java/hello/hscp/domain/club/repository/ClubRepository.java
package hello.hscp.domain.club.repository;

import hello.hscp.domain.club.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ClubRepository extends JpaRepository<Club, Long> {

    @Query("""
        select c from Club c
        where (:q is null or :q = '' or c.name like concat('%', :q, '%'))
          and (
                :state is null
             or (:state = 'UNKNOWN' and (c.recruitStartAt is null or c.recruitEndAt is null))
             or (:state = 'PRE' and c.recruitStartAt is not null and :today < c.recruitStartAt)
             or (:state = 'OPEN' and c.recruitStartAt is not null and c.recruitEndAt is not null
                    and :today >= c.recruitStartAt and :today <= c.recruitEndAt)
             or (:state = 'CLOSED' and c.recruitEndAt is not null and :today > c.recruitEndAt)
          )
        order by c.name asc
    """)
    List<Club> searchPublic(
            @Param("q") String q,
            @Param("state") String state,
            @Param("today") LocalDate today
    );
}
