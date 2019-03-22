package org.abondar.experimental.wsboard.base.dao;

import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.datamodel.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest(classes = Main.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class DaoTest {

    private static Logger logger = LoggerFactory.getLogger(MapperTest.class);

    @Autowired
    private DataMapper mapper;

    @Autowired
    private DAO dao;

    @Test
    public void createUserTest() throws Exception{
        logger.info("Create user test");

        var login="login";
        var email="email@email.com";
        var password="pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(),UserRole.DevOps.name());

        var usr = dao.createUser(login,password,email,firstName,lastName,roles);

        assertNull(usr.getMessage());
        assertTrue(usr.getObject().getId()>0);

        mapper.deleteUsers();
    }


    @Test
    public void createUserLoginExistsTest() throws Exception{
        logger.info("Create user test");

        var login="login";
        var email="email@email.com";
        var password="pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(),UserRole.DevOps.name());

        var usr = dao.createUser(login,password,email,firstName,lastName,roles);
        var usr1 = dao.createUser(login,password,email,firstName,lastName,roles);


        assertEquals(ErrorMessageUtil.USER_EXISTS,usr1.getMessage());
        assertNull(usr1.getObject());

        mapper.deleteUsers();
    }

    @Test
    public void createUserLoginNoRolesTest() throws Exception{
        logger.info("Create user test");

        var login="login";
        var email="email@email.com";
        var password="pwd";
        var firstName = "fname";
        var lastName = "lname";
        List<String> roles = List.of();

        var usr = dao.createUser(login,password,email,firstName,lastName,roles);

        assertEquals(ErrorMessageUtil.NO_ROLES,usr.getMessage());
        assertNull(usr.getObject());

        mapper.deleteUsers();
    }

    @Test
    public void updateUserLoginTest() throws Exception{
        logger.info("Update user login test");

        var login="login";
        var email="email@email.com";
        var password="pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(),UserRole.DevOps.name());

        var usr = dao.createUser(login,password,email,firstName,lastName,roles);
        usr = dao.updateLogin("login1",usr.getObject().getId());
        assertNull(usr.getMessage());

        mapper.deleteUsers();
    }

    @Test
    public void updateUserLoginExistsTest() throws Exception{
        logger.info("Update user login exists test");

        var login="login";
        var email="email@email.com";
        var password="pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(),UserRole.DevOps.name());

        var usr = dao.createUser(login,password,email,firstName,lastName,roles);
        usr = dao.updateLogin(login,usr.getObject().getId());
        assertEquals(ErrorMessageUtil.USER_EXISTS,usr.getMessage());

        mapper.deleteUsers();
    }


    @Test
    public void updateUserLoginNotExistsTest() {
        logger.info("Update user login not exists test");

        var usr = dao.updateLogin("login",1);
        assertEquals(ErrorMessageUtil.USER_NOT_EXIST,usr.getMessage());

    }

    @Test
    public void updatePasswordTest() throws Exception{
        logger.info("Update user password test");

        var login="login";
        var email="email@email.com";
        var password="pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(),UserRole.DevOps.name());

        var usr = dao.createUser(login,password,email,firstName,lastName,roles);
        usr = dao.updatePassword(password,"newPed",usr.getObject().getId());
        assertNull(usr.getMessage());

        mapper.deleteUsers();
    }

    @Test
    public void updatePasswordUserNotFoundTest() throws Exception{
        logger.info("Update user password user not found test");

        var usr = dao.updatePassword("pwd","newPed",100);
        assertEquals(ErrorMessageUtil.USER_NOT_EXIST,usr.getMessage());

    }

    @Test
    public void updatePasswordUnathorizedTest() throws Exception{
        logger.info("Update user password unauthorized test");

        var login="login";
        var email="email@email.com";
        var password="pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(),UserRole.DevOps.name());

        var usr = dao.createUser(login,password,email,firstName,lastName,roles);
        usr = dao.updatePassword("randomPwd","newPed",usr.getObject().getId());
        assertEquals(ErrorMessageUtil.UNAUTHORIZED,usr.getMessage());

        mapper.deleteUsers();
    }

    @Test
    public void updateUserTest() throws Exception{
        logger.info("Update user password test");

        var login="login";
        var email="email@email.com";
        var password="pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(),UserRole.DevOps.name());

        var usr = dao.createUser(login,password,email,firstName,lastName,roles);

        usr = dao.updateUser(usr.getObject().getId(),"name1","name2","email1@email.com",List.of(UserRole.Manager.name()));
        assertNull(usr.getMessage());


        mapper.deleteUsers();
    }


    @Test
    public void updateUserNullFieldTest() throws Exception{
        logger.info("Update user password test");

        var login="login";
        var email="email@email.com";
        var password="pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(),UserRole.DevOps.name());

        var usr = dao.createUser(login,password,email,firstName,lastName,roles);

        usr = dao.updateUser(usr.getObject().getId(),"name1","name2",null,List.of(UserRole.Manager.name()));
        assertNull(usr.getMessage());


        mapper.deleteUsers();
    }

    @Test
    public void updateUserEmptyFieldTest() throws Exception{
        logger.info("Update user password test");

        var login="login";
        var email="email@email.com";
        var password="pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(),UserRole.DevOps.name());

        var usr = dao.createUser(login,password,email,firstName,lastName,roles);

        usr = dao.updateUser(usr.getObject().getId(),"name1","",null,List.of());
        assertNull(usr.getMessage());


        mapper.deleteUsers();
    }


    @Test
    public void updateUserAvatarTest() throws Exception{
        logger.info("Update user password test");

        var login="login";
        var email="email@email.com";
        var password="pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(),UserRole.DevOps.name());

        var usr = dao.createUser(login,password,email,firstName,lastName,roles);

        var avatar = new byte[512];
        usr = dao.updateUserAvatar(usr.getObject().getId(),avatar);
        assertNull(usr.getMessage());

        mapper.deleteUsers();
    }

    @Test
    public void updateUserAvatarNullTest() throws Exception{
        logger.info("Update user password test");

        var login="login";
        var email="email@email.com";
        var password="pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(),UserRole.DevOps.name());

        var usr = dao.createUser(login,password,email,firstName,lastName,roles);

        usr = dao.updateUserAvatar(usr.getObject().getId(),null);
        assertEquals(ErrorMessageUtil.USER_AVATAR_EMPTY ,usr.getMessage());

        mapper.deleteUsers();
    }

    @Test
    public void updateUserAvatarEmptyTest() throws Exception{
        logger.info("Update user password test");

        var login="login";
        var email="email@email.com";
        var password="pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(),UserRole.DevOps.name());

        var usr = dao.createUser(login,password,email,firstName,lastName,roles);

        var avatar = new byte[]{};
        usr = dao.updateUserAvatar(usr.getObject().getId(),avatar);
        assertEquals(ErrorMessageUtil.USER_AVATAR_EMPTY ,usr.getMessage());

        mapper.deleteUsers();
    }

    @Test
    public void deleteUserTest() throws Exception{
        logger.info("Update user password test");

        var login="login";
        var email="email@email.com";
        var password="pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(),UserRole.DevOps.name());

        var usr = dao.createUser(login,password,email,firstName,lastName,roles);

        usr = dao.updateUserAvatar(usr.getObject().getId(),new byte[512]);
        usr = dao.deleteUser(usr.getObject().getId());

        assertNull(usr.getMessage());
        assertEquals("deleted",usr.getObject().getLogin());

        mapper.deleteUsers();
    }

    @Test
    public void loginUserTest() throws Exception{
        logger.info("Update user password test");

        var login="login";
        var email="email@email.com";
        var password="pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(),UserRole.DevOps.name());

        dao.createUser(login,password,email,firstName,lastName,roles);
        var res = dao.loginUser(login,password);

        assertTrue(res.isBlank());

        mapper.deleteUsers();
    }

    @Test
    public void logoutUserTest() throws Exception{
        logger.info("Update user password test");

        var login="login";
        var email="email@email.com";
        var password="pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(),UserRole.DevOps.name());

        var usr = dao.createUser(login,password,email,firstName,lastName,roles);
        var res = dao.logoutUser(usr.getObject().getId());

        assertTrue(res.isBlank());

        mapper.deleteUsers();
    }




    @Test
    public void createProjectTest() {
        logger.info("Create project test");

        var name="test";
        var startDate=new Date();

        var prj = dao.createProject(name,startDate);

        assertNull(prj.getMessage());
        assertTrue(prj.getObject().getId()>0);

        mapper.deleteProjects();
    }


    @Test
    public void createProjectExistsTest() {
        logger.info("Create project test");

        var name="test";
        var startDate=new Date();

        var prj = dao.createProject(name,startDate);
        var prj1 = dao.createProject(name,startDate);


        assertEquals(ErrorMessageUtil.PROJECT_EXISTS,prj1.getMessage());
        assertNull(prj1.getObject());

        mapper.deleteProjects();
    }


    @Test
    public void updateProjectTest(){
        logger.info("Update project test");

        var name="test";
        var startDate=new Date();
        var prj = dao.createProject(name,startDate);

        prj = dao.updateProject(prj.getObject().getId(),"newTest","github.com/aaaa/aaa.git",true,null);

        assertNull(prj.getMessage());
        mapper.deleteProjects();
    }

    @Test
    public void updateProjectInactiveTest(){
        logger.info("Update project test");

        var name="test";
        var startDate=new Date();
        var prj = dao.createProject(name,startDate);

        prj = dao.updateProject(prj.getObject().getId(),"newTest","github.com/aaaa/aaa.git",false,new Date());

        assertNull(prj.getMessage());
        mapper.deleteProjects();
    }

    @Test
    public void updateProjectInactiveNullTest(){
        logger.info("Update project inactive null end date test");

        var name="test";
        var startDate=new Date();
        var prj = dao.createProject(name,startDate);

        prj = dao.updateProject(prj.getObject().getId(),"newTest","github.com/aaaa/aaa.git",false,null);

        assertEquals(ErrorMessageUtil.WRONG_END_DATE,prj.getMessage());
        mapper.deleteProjects();
    }

    @Test
    public void updateProjectInactiveWrongDateTest(){
        logger.info("Update project inactive wrong end date test");

        var name="test";
        var startDate=new Date();
        var prj = dao.createProject(name,startDate);

        prj = dao.updateProject(prj.getObject().getId(),"newTest","github.com/aaaa/aaa.git",false,yesterday());

        assertEquals(ErrorMessageUtil.WRONG_END_DATE,prj.getMessage());
        mapper.deleteProjects();
    }



    @Test
    public void deleteProjectTest(){
        logger.info("Delete project test");

        var name="test";
        var startDate=new Date();
        var prj = dao.createProject(name,startDate);

        var res = dao.deleteProject(prj.getObject().getId());

        assertNull(res.getMessage());
        assertEquals(prj.getObject().getId(),(long)res.getObject());

        mapper.deleteProjects();
    }

    @Test
    public void findProjectByIdTest(){
        logger.info("Find project by id test");

        var name="test";
        var startDate=new Date();
        var prj = dao.createProject(name,startDate);

        var res = dao.findProjectById(prj.getObject().getId());
        assertEquals(prj.getObject().getName(),res.getObject().getName());
        assertEquals(prj.getObject().getStartDate(),res.getObject().getStartDate());

        mapper.deleteProjects();

    }


    @Test
    public void findProjectNotFoundByIdTest(){
        logger.info("Find project not found by id test");

        var prj = dao.findProjectById(100);

        assertEquals(ErrorMessageUtil.PROJECT_NOT_EXISTS,prj.getMessage());
        assertNull(prj.getObject());

        mapper.deleteProjects();

    }


    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

}
