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
import java.util.ArrayList;
import java.util.List;
import org.bdlions.bean.UserInfo;
import org.bdlions.bean.UserServiceInfo;
import org.bdlions.constants.Services;
import org.bdlions.db.AuthManager;

/**
 *
 * @author alamgir
 */
public class AuthServer extends AbstractVerticle {

    @Override
    public void start() {

        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        router.route("/").handler((RoutingContext routingContext) -> {
            HttpServerResponse response = routingContext.response();
            response.end("Authentication server");
        });
        
        router.route("/registersubscriber").handler((RoutingContext routingContext) -> {
            //System.out.println(routingContext.request().getParam("param1"));
            
            UserInfo userInfo = new UserInfo();
            userInfo.setReferenceUserName("ru30");
            userInfo.setReferencePassword("rp30");
            userInfo.setMaxMembers(3);
            userInfo.setRegistrationDate(12345);
            userInfo.setExpiredDate(123456789);
            userInfo.setIpAddress("192.168.1.30");

            UserServiceInfo userServiceInfo = new UserServiceInfo();
            userServiceInfo.setServiceId(Services.SERVICE_TYPE_ID_BKASH_SEND_MONEY);
            userServiceInfo.setRegistrationDate(12345);
            userServiceInfo.setExpiredDate(123456789); 
            userServiceInfo.setCallbackFunction("callback30");
            List<UserServiceInfo> userServiceInfoList = new ArrayList<>();
            userServiceInfoList.add(userServiceInfo);
            
            AuthManager authManager = new AuthManager();
            authManager.createSubscriber(userInfo, userServiceInfoList);
            
            
            HttpServerResponse response = routingContext.response();
            response.end("Authentication Registration");
        });
        
        router.route("/registermember").handler((RoutingContext routingContext) -> {
            //System.out.println(routingContext.request().getParam("param1"));
            
            UserInfo userInfo = new UserInfo();
            userInfo.setReferenceUserName("ru31");
            userInfo.setReferencePassword("rp31");
            userInfo.setIpAddress("192.168.1.30");
            
            try
            {
                AuthManager authManager = new AuthManager();
                authManager.createUser(userInfo);
            }
            catch(Exception ex)
            {
            
            }
            
            
            
            HttpServerResponse response = routingContext.response();
            response.end("Authentication Registration");
        });
        
        router.route("/getsessioninfo").handler((RoutingContext routingContext) -> {
            String result = "";
            UserInfo userInfo = new UserInfo();
            userInfo.setReferenceUserName("ru31");
            userInfo.setReferencePassword("rp31");
            userInfo.setIpAddress("192.168.1.30");
            
            try
            {
                AuthManager authManager = new AuthManager();
                result = authManager.getSessionInfo(userInfo, "6ak74cdap0p5s44gf7935e5imb");
            }
            catch(Exception ex)
            {
            
            }            
            HttpServerResponse response = routingContext.response();
            response.end(result);
        });
        
        server.requestHandler(router::accept).listen(5050);
    }

}
