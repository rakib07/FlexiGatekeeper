package org.bdlions.bean;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author nazmul hasan
 */
public class ServiceInfo {

    private int id;
    private String title;

    public ServiceInfo() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return json;
    }

    public static ServiceInfo getServiceInfo(String jsonContent) {
        ServiceInfo serviceInfo = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            serviceInfo = mapper.readValue(jsonContent, ServiceInfo.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return serviceInfo;
    }

}
