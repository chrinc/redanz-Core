package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.OutText;
import ch.redanz.redanzCore.model.workshop.entities.OutTextId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface OutTextRepo extends JpaRepository<OutText, OutTextId> {
  Optional<OutText> findAllByOutTextIdOutTextKeyAndOutTextIdOutTextLanguageKey(String outTextKey, String languageKey);
  List<OutText> findAllByOutTextIdOutTextKey(String outTextKey);
  List<OutText> findAllByType(String type);
  List<OutText> findAllByOutTextIdOutTextLanguageKeyAndType(String languageKey, String type);
//  Boolean existsByOutText(String outText);
//  OutText findAllByOutText(String outText);
}
