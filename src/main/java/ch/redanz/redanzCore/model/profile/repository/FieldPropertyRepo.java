package ch.redanz.redanzCore.model.profile.repository;


import ch.redanz.redanzCore.model.profile.entities.FieldProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldPropertyRepo extends JpaRepository<FieldProperty, Long> {
  FieldProperty findByFieldKey(String fieldKey);
  Boolean existsByFieldKey(String fieldKey);
  List<FieldProperty> findAll();
  List<FieldProperty> findAllByActive(boolean active);
}
