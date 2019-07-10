package org.abondar.experimental.wsboard.test.ws.impl;

import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestRestServiceImpl {

    protected Date convertDate(String strDate) throws DataCreationException {
        var format = new SimpleDateFormat("dd/MM/yyyy");

        try {
            return format.parse(strDate);
        } catch (ParseException ex) {
            throw new DataCreationException(LogMessageUtil.PARSE_DATE_FAILED);
        }

    }
}
