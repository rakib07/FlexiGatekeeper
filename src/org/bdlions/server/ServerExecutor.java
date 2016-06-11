/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.server;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bdlions.activemq.MessageQServer;
import org.bdlions.db.Database;
import org.bdlions.exceptions.DBSetupException;

/**
 *
 * @author alamgir
 */
public class ServerExecutor {
    public static void main(String[] args){
        //initializing database
        try {
            Database.getInstance();
        } catch (DBSetupException ex) {
            System.out.println("Database setup exception. Please contact System Administrator.");
            return;            
        }
        //run Sample java web server
        VertxOptions options = new VertxOptions(); 
        //server execution time
        options.setMaxEventLoopExecuteTime(Long.MAX_VALUE);
        
        //run Authentication server
        Vertx authVerticle = Vertx.vertx();
        authVerticle.deployVerticle(new AuthServer());
        
        //run keepalive server
        Vertx keepAliveVerticle = Vertx.vertx();
        keepAliveVerticle.deployVerticle(new KeepAliveServer());
        
        //run serviceAPI server
        Vertx serviceAPIVerticle = Vertx.vertx();
        serviceAPIVerticle.deployVerticle(new ServiceAPIServer());
        System.out.println("Server has started.");
        MessageQServer.getInstance().start(); 
        
    }
}
