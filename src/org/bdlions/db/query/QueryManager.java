/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db.query;

/**
 *
 * @author alamgir
 */
public final class QueryManager {

    public static final String CREATE_USER = "db.query.create-user";
    public static final String CREATE_SUBSCRIBER = "db.query.create-subscriber";
    public static final String UPDATE_SUBSCRIBER = "db.query.update-subscriber-info";
    public static final String ADD_SUBSCRIBER_SERVICE = "db.query.add-subscriber-service";
    public static final String ADD_CALLBACK_FUNCTION = "db.query.add-callback-function";
    public static final String GET_ALL_SUBSCRIBERS = "db.query.get-all-subscribers";
    public static final String GET_SUBSCRIBER_INFO = "db.query.get-subscriber-info";
    public static final String GET_SUBSCRIBER_INFO_BY_USER_ID = "db.query.get-subscriber-info-by-user_id";
    public static final String GET_SUBSCRIBER_TOTAL_MEMBERS = "db.query.get-subscriber-total-members";
    public static final String GET_USER_SUBSCRIBER_SERVICE_INFO = "db.query.get-user-subscriber-service-info";
    public static final String CREATE_TRANSACTION = "db.query.create-transaction";
    public static final String UPDATE_TRANSACTION = "db.query.update-transaction";
    public static final String DELETE_TRANSACTION = "db.query.delete-transaction-info";
    public static final String GET_ALL_TRANSACTIONS = "db.query.get-all-transactions";
    public static final String GET_TRANSACTION_INFO = "db.query.get-transaction-info";
    public static final String GET_CURRENT_BALANCE = "db.query.get-current-balance";

    public static final String ADD_SERVICE = "db.query.add-service";
    public static final String GET_ALL_SERVICES = "db.query.get-all-services";
    public static final String GET_SERVICE_INFO = "db.query.get-service-info";
    public static final String UPDATE_SERVICE_INFO = "db.query.update-service-info";
    public static final String GET_ALL_SUBSCRIBER_PAYMENTS = "db.query.get_all_subscriber_payments";
}
