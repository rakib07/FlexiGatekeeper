/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db;

import java.sql.SQLException;
import org.bdlions.db.exceptions.DBSetupException;

/**
 *
 * @author alamgir
 */
public class Main {
    public static void main(String[] args) throws SQLException, DBSetupException{
        Database.getInstance().getConnection();
    }
}
