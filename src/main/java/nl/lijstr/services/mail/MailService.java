package nl.lijstr.services.mail;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import nl.lijstr.common.Utils;
import nl.lijstr.domain.other.ApprovedFor;
import nl.lijstr.domain.other.MemeImage;
import nl.lijstr.domain.other.MemeQuote;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.LijstrException;
import nl.lijstr.processors.annotations.InjectLogger;
import nl.lijstr.processors.annotations.InjectRetrofitService;
import nl.lijstr.repositories.other.MemeImageRepository;
import nl.lijstr.repositories.other.MemeQuoteRepository;
import nl.lijstr.services.mail.model.MailGunResponse;
import nl.lijstr.services.mail.model.MailTemplate;
import nl.lijstr.services.mail.retrofit.MailGunApiService;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import retrofit2.Call;

/**
 * A service that allows for mailing.
 * Uses MailGun API for sending mail.
 */
@Service
public class MailService {

    @InjectLogger
    private Logger logger;

    @Value("${host.app}")
    private String appHost;

    @Value("classpath:mails/mail.template.inline.html")
    private Resource templateResource;
    private String templateMail;

    @Value("${mailgun.from}")
    private String from;

    @Autowired
    private MemeQuoteRepository memeQuoteRepository;

    @Autowired
    private MemeImageRepository memeImageRepository;

    @InjectRetrofitService
    private MailGunApiService mailGunApiService;

    /**
     * Send a mail using the default template.
     *
     * @param subject      The subject
     * @param user         The user who is going to receive the mail
     * @param mailTemplate Template values
     * @param tag          Optional tag
     *
     * @return a Mail response
     */
    public MailGunResponse sendMail(String subject, User user, MailTemplate mailTemplate, String tag) {
        //Build field map
        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("from", from);
        fieldMap.put("to", user.getEmail());
        fieldMap.put("subject", subject);
        fieldMap.put("html", buildTemplateMail(
                subject, user, mailTemplate.getMessage(), mailTemplate.getButton(), mailTemplate.getButtonUrlPath())
        );
        fieldMap.put("text", buildTemplateText(user, mailTemplate));
        fieldMap.put("o:tag", tag);

        //Send mail
        Call<MailGunResponse> sendCall = mailGunApiService.sendMail(fieldMap);
        return Utils.executeCall(sendCall);
    }


    private String buildTemplateMail(String title, User user, String message, String button, String buttonPath) {
        //Get a random quote
        List<MemeQuote> memeQuotes = memeQuoteRepository.findRandomForApproved(user.getApprovedFor());
        String footer = memeQuotes.isEmpty() ? "Absolutely nothing." : memeQuotes.get(0).getQuote();

        //Get random GIF
        List<MemeImage> memeImages = memeImageRepository.findRandomForApproved(user.getApprovedFor());
        MemeImage memeImage = memeImages.get(0); //TODO: What if there's no image?

        //Create the template
        return templateMail
                .replace("$$title$$", title)
                .replace("$$user$$", user.getDisplayName())
                .replace("$$subtitle$$",
                        user.getApprovedFor() == ApprovedFor.EVERYONE ? "There's new stuff!" : "You got shit to do")
                .replace("$$message$$", message)
                .replace("$$button$$", button)
                .replace("$$button-url$$", appHost + buttonPath)
                .replace("$$gif-width$$", memeImage.getImgWidth().toString())
                .replace("$$gif-height$$", memeImage.getImgHeight().toString())
                .replace("$$gif-src$$", memeImage.getImgSrc())
                .replace("$$gif-subtitle$$", memeImage.getImgSubtitle())
                .replace("$$footer$$", footer);
    }

    private String buildTemplateText(User user, MailTemplate template) {
        return "Hi " + user.getDisplayName() + "\r\n\r\n"
                + template.getMessage() + "\r\n"
                + template.getButton() + ": " + appHost + template.getButtonUrlPath() + "\r\n\r\n"
                + "kbai";
    }


    @PostConstruct
    private void getTemplateMail() {
        try (InputStreamReader reader = new InputStreamReader(
                templateResource.getInputStream(), Charset.forName("UTF-8"))
        ) {
            templateMail = FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            logger.fatal("Failed to read template mail: {}", e.getMessage());
            logger.fatal(e);
            throw new LijstrException("Failed to read templateMail:" + e.getMessage());
        }
    }


}
