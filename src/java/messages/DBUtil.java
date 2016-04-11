/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author iwuvhugs
 */
public class DBUtil {

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        String host = "localhost";
        String port = "3306";
        String db = "messages";
        String user = "root";
        String pass = "";
        String jdbc = "jdbc:mysql://" + host + ":" + port + "/" + db;
        return DriverManager.getConnection(jdbc, user, pass);
    }

}
