package org.abondar.experimental.wsboard.ws.service;


import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Basic web service interface
 */
@SwaggerDefinition(
        info = @Info(
                title = "Web Scrum Board",
                version = "1.0",
                description = "Web Scrum Board REST API",
                license = @License(name = "MIT"),
                contact = @Contact(url = "https://github.com/WebScrumBoard/", name = "Alex Bondar",
                        email = "abondar1992@gmail.com")
        ),
        tags = {
                @Tag(name = "WSB")
        },
        consumes = {"application/json"},
        produces = {"application/json"},
        schemes = {SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS}
)
public interface RestService {

    /**
     * Method for converting string to date
     *
     * @param strDate - date as string
     * @return Date
     * @throws DataCreationException - string parsing failed
     */
    default Date convertDate(String strDate) throws DataCreationException {
        var format = new SimpleDateFormat("dd/MM/yyyy");

        try {
            return format.parse(strDate);
        } catch (ParseException ex) {
            throw new DataCreationException(LogMessageUtil.PARSE_DATE_FAILED);
        }

    }

}
