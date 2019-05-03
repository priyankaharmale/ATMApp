package com.hnweb.atmap.multipartRequest;

/**
 * Created by Hnweb on 6/21/2017.
 */

public interface OnEventListener<T> {
    public void onSuccess(T object);
    public void onFailure(Exception e);
}