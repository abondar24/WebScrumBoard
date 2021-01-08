package org.abondar.experimental.wsboard.server.datamodel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * POJO for security code table
 *
 * @author a.bondar
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class SecurityCode {

    private static final long serialVersionUID = -235433L;

    private long id;

    private long code;

    private long userId;


    public SecurityCode(long code, long userId) {
        this.code = code;
        this.userId = userId;
    }


}
