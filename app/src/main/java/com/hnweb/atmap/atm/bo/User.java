package com.hnweb.atmap.atm.bo;

public class User {
    String customer_name,customer_address,customer_lat,customer_long,request_user_id,request_amount
            ,request_status,request_barcode,request_time;

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_address() {
        return customer_address;
    }

    public void setCustomer_address(String customer_address) {
        this.customer_address = customer_address;
    }

    public String getCustomer_lat() {
        return customer_lat;
    }

    public void setCustomer_lat(String customer_lat) {
        this.customer_lat = customer_lat;
    }

    public String getCustomer_long() {
        return customer_long;
    }

    public void setCustomer_long(String customer_long) {
        this.customer_long = customer_long;
    }

    public String getRequest_user_id() {
        return request_user_id;
    }

    public void setRequest_user_id(String request_user_id) {
        this.request_user_id = request_user_id;
    }

    public String getRequest_amount() {
        return request_amount;
    }

    public void setRequest_amount(String request_amount) {
        this.request_amount = request_amount;
    }

    public String getRequest_status() {
        return request_status;
    }

    public void setRequest_status(String request_status) {
        this.request_status = request_status;
    }

    public String getRequest_barcode() {
        return request_barcode;
    }

    public void setRequest_barcode(String request_barcode) {
        this.request_barcode = request_barcode;
    }

    public String getRequest_time() {
        return request_time;
    }

    public void setRequest_time(String request_time) {
        this.request_time = request_time;
    }
}
