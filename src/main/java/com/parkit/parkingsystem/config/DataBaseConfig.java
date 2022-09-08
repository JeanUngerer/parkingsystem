package com.parkit.parkingsystem.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;

public class DataBaseConfig {

    private static final Logger logger = LogManager.getLogger("DataBaseConfig");

    public Connection getConnection() throws ClassNotFoundException, SQLException, IOException {
    	StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
    	Properties props = new EncryptableProperties(encryptor);
    	FileInputStream propsFile = null;
    	try {
    		propsFile = new FileInputStream("src/main/resources/connect.properties");
			props.load(propsFile);
			
		} catch (FileNotFoundException e) {
			logger.error("Error while getting connection infos",e);
		} catch (IOException e) {
			logger.error("Error while getting connection infos",e);
		} finally {
			if (propsFile != null) {
				propsFile.close();
			}
		}
    	String datasourceUrl = props.getProperty("datasource.url");
    	String datasourceUsername = props.getProperty("datasource.username");
    	String datasourcePassword = props.getProperty("datasource.password");
    	String datasourceDriver = props.getProperty("datasource.driver");
    	
    	
        logger.info("Create DB connection");
        Class.forName(datasourceDriver);
        return DriverManager.getConnection(
        		datasourceUrl,datasourceUsername,datasourcePassword);
    }
    

    public void closeConnection(Connection con){
        if(con!=null){
            try {
                con.close();
                logger.info("Closing DB connection");
            } catch (SQLException e) {
                logger.error("Error while closing connection",e);
            }
        }
    }

    public void closePreparedStatement(PreparedStatement ps) {
        if(ps!=null){
            try {
                ps.close();
                logger.info("Closing Prepared Statement");
            } catch (SQLException e) {
                logger.error("Error while closing prepared statement",e);
            }
        }
    }

    public void closeResultSet(ResultSet rs) {
        if(rs!=null){
            try {
                rs.close();
                logger.info("Closing Result Set");
            } catch (SQLException e) {
                logger.error("Error while closing result set",e);
            }
        }
    }
}
