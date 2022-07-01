package ch.redanz.redanzCore.model.workshop.response;

import ch.redanz.redanzCore.model.workshop.entities.OutText;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class OutTextResponse {
  HashMap outText;

  public OutTextResponse(OutText outText) {
    this.outText = new HashMap<>();
    this.outText.put(
      outText.getOutTextId().getOutTextLanguageKey() + outText.getOutTextId().getOutTextKey(),
      outText.getOutText()
    );
  }
}
