package ch.redanz.redanzCore.model.profile.api;

import lombok.Value;

import java.io.Serializable;

@Value
public class ListItemDto implements Serializable {
  String itemKey;
  String name;
  Long itemId;
}
