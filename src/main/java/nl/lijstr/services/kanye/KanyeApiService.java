package nl.lijstr.services.kanye;

import nl.lijstr.common.Utils;
import nl.lijstr.exceptions.LijstrException;
import nl.lijstr.processors.annotations.InjectRetrofitService;
import nl.lijstr.services.kanye.models.KanyeQuote;
import nl.lijstr.services.kanye.retrofit.KanyeRestService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * A service that provides access to the external api 'kanye.rest'.
 * An API that provides quotes made by Kanye West.
 *
 * This API is not rate-limited but be nice, try to limit it to a couple requests at the time.
 */
@Service
public class KanyeApiService {

    @InjectRetrofitService
    private KanyeRestService kanyeService;

    /**
     * Attempt to get quote made by Kanye.
     *
     * @return the quote (if successful)
     */
    public Optional<String> getQuote() {
        try {
            final KanyeQuote kanyeQuote = Utils.executeCall(kanyeService.getQuote());
            return Optional.of(kanyeQuote.getText());
        } catch (LijstrException e) {
            return Optional.empty();
        }
    }

}
