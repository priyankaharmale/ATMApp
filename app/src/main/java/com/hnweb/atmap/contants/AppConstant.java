package com.hnweb.atmap.contants;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Priyanka H on 13/06/2018.
 */
public class AppConstant {


    /*============================================Register==================================================*/
    public static final String KEY_NAME = "full_name";
    public static final String KEY_ID = "u_id";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE = "mobile_no";
    public static final String KEY_STREET = "u_street";
    public static final String KEY_CITY = "u_city";
    public static final String KEY_STATE = "u_state";
    public static final String KEY_COUNTRY = "u_country";
    public static final String KEY_ZIPCODE = "u_zipcode";
    public static final String KEY_USERTYPE = "user_type";
    public static final String KEY_ADDRESS = "cust_address";
    public static final String KEY_PASSWORD = "cust_password";
    public static final String KEY_IMAGE = "img";
    public static final String KEY_DEVICETYPE = "device_type";
    public static final String KEY_DEVICETOKEN = "device_token";


    public static final String BASE_URL = "http://tech599.com/tech599.com/johnsum/atmpro/api/index.php/api/";

    /*******************************Customer***************************************************************************************************/
    /*=================================================Register User=========================================================*/
    public static final String API_REGISTER_USER = BASE_URL + "register";
    /*=================================================Login User=========================================================*/
    public static final String API_LOGIN_USER = BASE_URL + "login";
    /*=================================================Forgot Password User=========================================================*/
    public static final String API_FORGOTPWD_USER = BASE_URL + "forgotpassword";

    /*=================================================get profile=========================================================*/
    public static final String API_GET_PROFILE = BASE_URL + "get_profile";
    /*=================================================update profile=========================================================*/
    public static final String API_UPDATE_PROFILE = BASE_URL + "update_profile";

    /*=================================================GET AGENT LIST=========================================================*/
    public static final String API_GET_AGENTLIST= BASE_URL + "cust_dashboard";

    public static String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }


}
