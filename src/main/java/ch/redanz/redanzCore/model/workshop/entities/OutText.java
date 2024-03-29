package ch.redanz.redanzCore.model.workshop.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
@Entity
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Table(name="out_text")
public class OutText implements Serializable  {

    @EmbeddedId
    private OutTextId outTextId = new OutTextId();

    @Lob
    private String outText;

    @JsonIgnore
    private String type;

    public OutText(OutTextId outTextId, String outText, String type) {
        this.outTextId = outTextId;
        this.outText = outText;
        this.type = type;
    }
}
