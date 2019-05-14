module org.abondar.experimental.wsboard.dataModel {

    requires io.swagger.v3.oas.annotations;

    exports org.abondar.experimental.wsboard.datamodel;
    exports org.abondar.experimental.wsboard.datamodel.task;
    exports org.abondar.experimental.wsboard.datamodel.user;

    opens org.abondar.experimental.wsboard.datamodel;
}
