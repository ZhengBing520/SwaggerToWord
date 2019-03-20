package com.tool.dto;

import java.util.List;

/**
 * Created by XiuYin.Cui on 2018/1/11.
 */
public class Table {

    /**
     * 大标题
     */
    private String title;
    /**
     * 小标题
     */
    private String tag;
    /**
     * url
     */
    private String url;

    /**
     * 请求参数格式
     */
    private String requestForm;

    /**
     * 响应参数格式
     */
    private String responseForm;

    /**
     * 请求方式
     */
    private String requestType;

    /**
     * 请求体
     */
    private List<Request> requestList;

    /**
     * 返回体
     */
    private List<Response> responseList;

    /**
     * 请求参数
     */
    private String requestParam;



    private List<Body> bodyList;

    /**
     * 对象名称
     */
    private String bodyName;

    /**
     * 返回对象中的data里面的对象名
     * 例如：
     * JsonMessage<List<User>>
     * responseName = User
     */
    private String responseName;

    /**
     * data里面的属性
     * 如：
     * User {
     *     属性名 ： name
     *     type   ： String
     *     描述   ： 姓名
     * }
     */
    private List<Body> responseNameList;

    /**
     * 分页名称
     */
    private String pageName;

    /**
     * 分页数据
     */
    private List<Body> pageNameList;

    /**
     * 返回参数
     */
    private String responseParam;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResponseForm() {
        return responseForm;
    }

    public void setResponseForm(String responseForm) {
        this.responseForm = responseForm;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public List<Request> getRequestList() {
        return requestList;
    }

    public void setRequestList(List<Request> requestList) {
        this.requestList = requestList;
    }

    public List<Response> getResponseList() {
        return responseList;
    }

    public void setResponseList(List<Response> responseList) {
        this.responseList = responseList;
    }

    public String getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(String requestParam) {
        this.requestParam = requestParam;
    }

    public String getResponseParam() {
        return responseParam;
    }

    public void setResponseParam(String responseParam) {
        this.responseParam = responseParam;
    }

    public String getRequestForm() {
        return requestForm;
    }

    public void setRequestForm(String requestForm) {
        this.requestForm = requestForm;
    }

    public List<Body> getBodyList() {
        return bodyList;
    }

    public void setBodyList(List<Body> bodyList) {
        this.bodyList = bodyList;
    }

    public String getBodyName() {
        return bodyName;
    }

    public void setBodyName(String bodyName) {
        this.bodyName = bodyName;
    }

    public String getResponseName() {
        return responseName;
    }

    public void setResponseName(String responseName) {
        this.responseName = responseName;
    }

    public List<Body> getResponseNameList() {
        return responseNameList;
    }

    public void setResponseNameList(List<Body> responseNameList) {
        this.responseNameList = responseNameList;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public List<Body> getPageNameList() {
        return pageNameList;
    }

    public void setPageNameList(List<Body> pageNameList) {
        this.pageNameList = pageNameList;
    }
}
