// src/main/java/hello/hscp/domain/application/repository/ApplicationRepository.java
package hello.hscp.domain.application.repository;

import hello.hscp.domain.application.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByClub_IdOrderByIdDesc(Long clubId);
    Optional<Application> findByIdAndClub_Id(Long id, Long clubId);
}
