package nl.lijstr.services.kanye.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KanyeQuote {

    @SerializedName("quote")
    private String text;

}
