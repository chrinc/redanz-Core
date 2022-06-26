package ch.redanz.redanzCore.model.profile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="language")
public class Language {
    @Id
    @Column(length=2, name="language_key")
    private String languageKey;

    private String name;

}
