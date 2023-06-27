package nl.lijstr.domain.imdb;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.lijstr.domain.base.IdModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SpokenLanguage extends IdModel {

    @Column(unique = true, nullable = false)
    private String language;

}
