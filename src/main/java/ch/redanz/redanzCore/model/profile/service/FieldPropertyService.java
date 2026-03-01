package ch.redanz.redanzCore.model.profile.service;

import ch.redanz.redanzCore.model.profile.api.FieldPropertyDto;
import ch.redanz.redanzCore.model.profile.api.ListItemDto;
import ch.redanz.redanzCore.model.profile.entities.FieldProperty;
import ch.redanz.redanzCore.model.profile.repository.CountryRepo;
import ch.redanz.redanzCore.model.profile.repository.FieldPropertyRepo;
import ch.redanz.redanzCore.model.workshop.repository.LanguageRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FieldPropertyService {
  private final LanguageRepo languageRepo;
  private FieldPropertyRepo fieldPropertyRepo;
  private CountryRepo countryRepo;

  public List<FieldPropertyDto> findAllEnriched() {
    List<FieldProperty> props = fieldPropertyRepo.findAll();

    return props.stream()
      .map(this::toDtoEnriched)
      .collect(Collectors.toList());
  }

  private FieldPropertyDto toDtoEnriched(FieldProperty p) {
    List<ListItemDto> list = Collections.emptyList();

    if (isListType(p)) {
      list = resolveListItems(p);
    }

    return new FieldPropertyDto(
      p.getId(),
      p.getFieldKey(),
      p.getName(),
      p.isActive(),
      p.isRequired(),
      p.getType(),
      p.getListSource(),
      list
    );
  }

  private boolean isListType(FieldProperty p) {
    return p.getType() != null && p.getType().equalsIgnoreCase("list");
  }

  private List<ListItemDto> resolveListItems(FieldProperty p) {
    String src = p.getListSource();
    if (src == null || src.isBlank()) return List.of();

    if ("country".equalsIgnoreCase(src)) {
      return countryRepo.findAll().stream()
        .map(c -> new ListItemDto(
          c.getSortName(),
          c.getOutTextKey(),
          c.getId()
        ))
        .collect(Collectors.toList());
    } else if  ("language".equalsIgnoreCase(src)) {
      return languageRepo.findAll().stream()
        .map(c -> new ListItemDto(
          c.getLanguageKey(),
          c.getName(),
          null
        ))
        .collect(Collectors.toList());
    }
    // unknown source -> empty list
    return List.of();
  }

  public FieldProperty findByKey(String key) {
    return fieldPropertyRepo.findByFieldKey(key);
  }

  public Boolean existsByKey(String key) {
    return fieldPropertyRepo.existsByFieldKey(key);
  }

  public void save(FieldProperty fieldProperty) {
    fieldPropertyRepo.save(fieldProperty);
  }

  public List<FieldProperty> findAll() {
    return fieldPropertyRepo.findAll();
  }
  public List<String> displayedColumns() {
    return fieldPropertyRepo.findAllByActive(true)
      .stream()
      .map(FieldProperty::getFieldKey)
      .collect(Collectors.toList());
  }
}
