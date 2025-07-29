package ch.ubs.juniorlab.repository;

import ch.ubs.juniorlab.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByGpn(String gpn);
    Optional<Person> findByEmail(String email);
    List<Person> findByPrename(String prename);
    Optional<Person> findByPrenameAndName(String prename, String name);
}
