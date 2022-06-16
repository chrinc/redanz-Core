package ch.redanz.redanzCore.model.workshop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
public class OutTextId implements Serializable {

    @Column(length=50, name="out_text_key")
    private String outTextKey;

    @Column(length=2)
    private String outTextLanguageKey;

    public OutTextId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OutTextId)) return false;
        OutTextId outTextId = (OutTextId) o;
        return getOutTextKey().equals(outTextId.getOutTextKey()) && getOutTextLanguageKey().equals(outTextId.getOutTextLanguageKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOutTextKey(), getOutTextLanguageKey());
    }
}
