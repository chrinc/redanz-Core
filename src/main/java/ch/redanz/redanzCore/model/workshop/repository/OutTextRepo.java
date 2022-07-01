package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.OutText;
import ch.redanz.redanzCore.model.workshop.entities.OutTextId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OutTextRepo extends JpaRepository<OutText, OutTextId> {
  OutText findAllByOutTextIdOutTextKeyAndOutTextIdOutTextLanguageKey(String outTextKey, String languageKey);

  List<OutText> findAllByType(String type);
}
