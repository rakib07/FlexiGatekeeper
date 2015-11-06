/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db;

import java.sql.SQLException;
import org.bdlions.db.exceptions.DBSetupException;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.db.query.helper.QueryManager;
import org.bdlions.db.util.DbQueryProvider;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author alamgir
 */
public class DatabaseTest {
    
    public DatabaseTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    @Test
    public void setUpDatabase() throws DBSetupException, SQLException{
        Database.getInstance();
    }
    @Test
    public void createService() throws DBSetupException, SQLException{
        EasyStatement stmt = new EasyStatement(QueryManager.ADD_SERVICE);
        stmt.setString("title", "Super string");
        stmt.setInt("id", 12);
        stmt.executeUpdate();
    }
}
