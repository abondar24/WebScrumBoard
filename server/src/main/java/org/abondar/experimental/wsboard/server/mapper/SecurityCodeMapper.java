package org.abondar.experimental.wsboard.server.mapper;

import org.abondar.experimental.wsboard.server.datamodel.SecurityCode;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;

@Mapper
@Qualifier("securityCodeMapper")
public interface SecurityCodeMapper {

    void insertCode(@Param("securityCode") SecurityCode code);

    SecurityCode getCodeByUserId(@Param("id") long id);

    Integer checkCodeExists(@Param("code") long code);

    void deleteCode(@Param("id") long id);


    @Delete(" DELETE FROM security_code")
    void deleteCodes();


}
