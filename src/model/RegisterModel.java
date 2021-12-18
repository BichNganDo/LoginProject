package model;

import client.MysqlClient;
import common.ErrorCode;
import entity.User;
import helper.SecurityHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class RegisterModel {

    private static final MysqlClient dbClient = MysqlClient.getMysqlCli();
    private final String NAMETABLE = "users";
    final static Logger logger = Logger.getLogger(RegisterModel.class);
    public static RegisterModel INSTANCE = new RegisterModel();

    public RegisterModel() {
        PropertyConfigurator.configure("log4j.properties");
    }


    public User getUserRegisterByID(int id) {

        User result = new User();
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return result;
            }
            PreparedStatement getUserRegisterByIdStmt = conn.prepareStatement("SELECT * FROM `" + NAMETABLE + "` WHERE id = ? ");
            getUserRegisterByIdStmt.setInt(1, id);

            ResultSet rs = getUserRegisterByIdStmt.executeQuery();

            if (rs.next()) {
                result.setId(rs.getInt("id"));
                result.setName(rs.getString("name"));
                result.setEmail(rs.getString("email"));
                result.setPassword(rs.getString("password"));
                result.setFavoriteColor(rs.getString("favorite_color"));
                result.setDob(rs.getString("dob"));
                result.setAvatar(rs.getString("avatar"));
            }

            return result;
        } catch (Exception e) {
            logger.error(e.getMessage());
//            System.out.println(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }
        return result;
    }

    public boolean isExistEmail(String email) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return false;
            }

            PreparedStatement isExistEmailStmt = conn.prepareStatement("SELECT * FROM `" + NAMETABLE + "` WHERE `email` = ?");
            isExistEmailStmt.setString(1, email);

            ResultSet rs = isExistEmailStmt.executeQuery();
            if (rs.next()) {
                return true;
            }

            return false;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }

        return false;
    }

    public int addUserRegister(String name, String email, String password, String favColor, String dob, String avatar) {
        Connection conn = null;
        Boolean isExistEmail = INSTANCE.isExistEmail(email);
        if (isExistEmail == true) {
            return ErrorCode.EXIST_ACCOUNT.getValue();
        }
        password = SecurityHelper.getMD5Hash(password);
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return ErrorCode.CONNECTION_FAIL.getValue();
            }

            PreparedStatement addStmt = conn.prepareStatement("INSERT INTO `" + NAMETABLE + "` (name, email, password, favorite_color, dob, avatar) "
                    + "VALUES (?, ?, ?, ?, ?, ?)");
            addStmt.setString(1, name);
            addStmt.setString(2, email);
            addStmt.setString(3, password);
            addStmt.setString(4, favColor);
            addStmt.setString(5, dob);
            addStmt.setString(6, avatar);
            int rs = addStmt.executeUpdate();
            return rs;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }

        return ErrorCode.FAIL.getValue();
    }

    public int editUserRegister(int id, String name, String email, String password, String favColor, String dob, String avatar) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return ErrorCode.CONNECTION_FAIL.getValue();
            }

            PreparedStatement editStmt = conn.prepareStatement("UPDATE `" + NAMETABLE + "` "
                    + "SET name = ?, email = ?, password = ?, favorite_color = ?, dob = ?, avatar = ? "
                    + "WHERE id = ? ");
            editStmt.setString(1, name);
            editStmt.setString(2, email);
            editStmt.setString(3, password);
            editStmt.setString(4, favColor);
            editStmt.setString(5, dob);
            editStmt.setString(6, avatar);
            editStmt.setInt(7, id);

            int rs = editStmt.executeUpdate();

            return rs;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }
        return ErrorCode.FAIL.getValue();
    }

    public User checkLogin(String email, String password) {
        User user = new User();
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return user;
            }
            PreparedStatement checkLoginStmt = conn.prepareStatement("SELECT * FROM `" + NAMETABLE + "` WHERE email = ? AND password = ?");
            checkLoginStmt.setString(1, email);
            checkLoginStmt.setString(2, password);
            ResultSet rs = checkLoginStmt.executeQuery();
            if (rs.next()) {
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setFavoriteColor(rs.getString("favorite_color"));
                user.setDob(rs.getString("dob"));
                user.setAvatar(rs.getString("avatar"));
            }

            return user;

        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }

        return user;
    }
}
