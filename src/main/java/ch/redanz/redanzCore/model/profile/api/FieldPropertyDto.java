package ch.redanz.redanzCore.model.profile.api;

import lombok.Value;

import java.io.Serializable;
import java.util.List;

@Value
public class FieldPropertyDto implements Serializable {
  Long id;
  String fieldKey;
  String name;
  boolean active;
  boolean required;
  String type;
  String listSource;
  List<ListItemDto> list;
}
