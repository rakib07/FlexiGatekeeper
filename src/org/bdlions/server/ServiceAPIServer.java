/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.bdlions.bean.TransactionInfo;
import org.bdlions.db.TransactionManager;

/**
 *
 * @author alamgir
 */
public class ServiceAPIServer extends AbstractVerticle {

    @Override
    public void start() {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route("/").handler((RoutingContext routingContext) -> {
            HttpServerResponse response = routingContext.response();
            response.end("ServiceAPI server");
        });
        
        router.route("/addtransaction").handler((RoutingContext routingContext) -> {
            String userId = "";
            String sessionId = "";
            //validate userId and sessionId from the hashmap
            
            TransactionInfo transactionInfo = new TransactionInfo();
            transactionInfo.setAPIKey("vr22old1v415kipdk9uob4kv07");
            transactionInfo.setBalanceOut(100);

            TransactionManager transactionManager = new TransactionManager();
            String transactionId = transactionManager.addTransaction(transactionInfo);
            
            HttpServerResponse response = routingContext.response();
            response.end(transactionId);
        });

        server.requestHandler(router::accept).listen(3030);
    }
}
