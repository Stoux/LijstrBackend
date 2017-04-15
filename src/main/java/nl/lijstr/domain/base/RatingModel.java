package nl.lijstr.domain.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;
import javax.persistence.*;
import lombok.*;
import nl.lijstr.domain.users.User;

/**
 * A base model for a rating of something.
 *
 * @param <T> The target class of the rating
 */
@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class RatingModel<T extends IdModel> extends IdCmModel {

    @ManyToOne
    protected User user;

    @JsonIgnore
    @ManyToOne
    protected T target;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    protected Seen seen;

    @Column(precision = 3, scale = 1)
    protected BigDecimal rating;
    @Column(length = 5000)
    protected String comment;

    @Column(nullable = false)
    protected Boolean latest;

    /**
     * Create a new rating.
     *
     * @param user    The user
     * @param target  The target of the rating
     * @param seen    Has seen the target
     * @param rating  The actual rating (can be null for ?)
     * @param comment An optional comment
     */
    public RatingModel(User user, T target, Seen seen, BigDecimal rating, String comment) {
        this.user = user;
        this.target = target;
        this.seen = seen;
        this.rating = rating;
        this.comment = comment;
        this.latest = true;
    }

    /**
     * Seen status.
     */
    public enum Seen {
        YES,
        NO,
        UNKNOWN,
        TELEVISION; //Seen on television aka seen parts

        @JsonValue
        public int toValue() {
            return ordinal();
        }

    }

}
