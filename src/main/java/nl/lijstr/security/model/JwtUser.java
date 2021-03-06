package nl.lijstr.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * A JwtUser that implements UserDetails for authentication.
 * This is a copy of the {@link nl.lijstr.domain.users.User}'s data at a certain point in time.
 * Extra keys are added for validation/invalidation.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JwtUser implements UserDetails {

    /**
     * User's ID.
     */
    @SerializedName("i")
    private Long id;

    /**
     * User's username.
     */
    @SerializedName("u")
    private String username;

    private transient String password;

    /**
     * Collection of permissions.
     */
    @SerializedName("a")
    private List<JwtGrantedAuthority> authorities;

    /**
     * Unrestricted access till a certain date.
     * <p>
     * This allows for a JWT to be valid in a 'RememberMe' like way; while still allowing for limited time-frame.
     * If this value has been passed the User will have to request a new Token which is still possible
     * with the current token.
     */
    @SerializedName("at")
    private LocalDateTime accessTill;

    @SerializedName("vt")
    private LocalDateTime validTill;

    /**
     * A validating key.
     * <p>
     * This allows a user to reset all tokens (that have passed their accessTill value).
     */
    @Setter
    @SerializedName("vk")
    private long validatingKey;

    /**
     * Set the accessTill.
     * This resets the nanoseconds to 0.
     *
     * @param accessTill The date time
     */
    public void setAccessTill(LocalDateTime accessTill) {
        this.accessTill = accessTill.withNano(0);
    }

    /**
     * Set the validTill.
     * This resets the nanoseconds to 0.
     *
     * @param validTill The date time
     */
    public void setValidTill(LocalDateTime validTill) {
        this.validTill = validTill.withNano(0);
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
