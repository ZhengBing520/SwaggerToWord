package com.tool.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tool.dto.Body;
import com.tool.dto.Request;
import com.tool.dto.Response;
import com.tool.dto.Table;
import com.tool.service.TableService;
import com.tool.util.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by XiuYin.Cui on 2018/1/12.
 */
@Service
public class TableServiceImpl implements TableService {

    private static Map<String, Object> MAP = new HashMap<>(256);
    private static Map<String, Object> DEFINITIONS = new HashMap<>(256); //自定义对象集

    static {
        try {
            //解析json
            ClassLoader classLoader = TableService.class.getClassLoader();
            URL resource = classLoader.getResource("data.json");
            MAP = new ObjectMapper().readValue(resource, Map.class);
            DEFINITIONS = (LinkedHashMap) MAP.get("definitions");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<Table> tableList() {

        List<Table> list = new LinkedList();
        //得到host，用于模拟http请求
        String host = String.valueOf(MAP.get("host"));
        //解析paths
        LinkedHashMap<String, LinkedHashMap> paths = (LinkedHashMap) MAP.get("paths");
        Map<String, String> numMap = new HashMap<>();
        LinkedHashMap<String, String> titleMap = new LinkedHashMap<>();
        String titleKey = "";
        String titleValue = "";

        List<LinkedHashMap<String, String>> m1 = (ArrayList) MAP.get("tags");

        LinkedHashMap<String, LinkedHashMap> definitions = (LinkedHashMap) MAP.get("definitions");

        for (LinkedHashMap<String, String> linkedHashMap : m1) {
            Iterator iterator = linkedHashMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = linkedHashMap.get(key);
                if ("name".equals(key)) {
                    titleKey = value;
                }

                if ("description".equals(key)) {
                    titleValue = value;
                }
            }
            titleMap.put(titleKey, titleValue);
        }

        if (paths != null) {
            int x = 0; // 大章节
            int y = 0; // 小章节

            Iterator<Map.Entry<String, LinkedHashMap>> iterator = paths.entrySet().iterator();
            while (iterator.hasNext()) {

                String body = "";
                Table table = new Table();
                List<Request> requestList = new LinkedList<>();
                List<Response> responseList = new LinkedList<>();
                List<Body> bodyList = new LinkedList<>();
                List<Body> responseNameList = new ArrayList<>();
                List<Body> PageNameList = new ArrayList<>();
                String requestForm = ""; //请求参数格式，类似于 multipart/form-data
                String responseForm = ""; //响应参数格式
                String requestType = ""; //请求方式，类似为 get/post/delete/put 这样
                String url; //请求路径
                String title; //大标题（类说明）
                String tag; //小标题 （方法说明）
                String requestParam = ""; //请求参数
                String responseParam = ""; //返回参数

                Map.Entry<String, LinkedHashMap> next = iterator.next();
                url = next.getKey();

                LinkedHashMap<String, LinkedHashMap> value = next.getValue();
                Set<String> requestTypes = value.keySet();
                for (String str : requestTypes) {
                    requestType += str + "、";
                }

                Iterator<Map.Entry<String, LinkedHashMap>> iterator2 = value.entrySet().iterator();
                //不管有几种请求方式，都只解析第一种
                Map.Entry<String, LinkedHashMap> get = iterator2.next();
                LinkedHashMap getValue = get.getValue();
                title = (String) ((List) getValue.get("tags")).get(0);
                List<String> consumes = (List) getValue.get("consumes");
                if (consumes != null && consumes.size() > 0) {
                    for (String consume : consumes) {
                        requestForm += consume + "、";
                    }
                }
                List<String> produces = (List) getValue.get("produces");
                if (produces != null && produces.size() > 0) {
                    for (String produce : produces) {
                        responseForm += produce + "、";
                    }
                }

                tag = String.valueOf(getValue.get("summary"));
                //请求体
                List parameters = (ArrayList) getValue.get("parameters");
                if (parameters != null && parameters.size() > 0) {
                    for (int i = 0; i < parameters.size(); i++) {
                        Request request = new Request();
                        LinkedHashMap<String, Object> param = (LinkedHashMap) parameters.get(i);
                        request.setName(String.valueOf(param.get("name")));
                        request.setType(param.get("type") == null ? "Object" : param.get("type").toString());
                        request.setParamType(String.valueOf(param.get("in")));
                        request.setRequire((Boolean) param.get("required"));
                        request.setRemark(String.valueOf(param.get("description")));
                        requestList.add(request);
                        // 查询是否有body，有就做标记
                        LinkedHashMap<String, String> linkedHashMap = (LinkedHashMap<String, String>) param.get("schema");
                        if (null != linkedHashMap && null != linkedHashMap.get("$ref")) {
                            String str = linkedHashMap.get("$ref");
                            body = str.substring(str.lastIndexOf("/") + 1);
                        }
                    }
                }
                //返回体
                LinkedHashMap<String, Object> responses = (LinkedHashMap) getValue.get("responses");
                // 返回的是对象
                String responseName = "";
                String PageName = "";
                Iterator<Map.Entry<String, Object>> iterator3 = responses.entrySet().iterator();
                while (iterator3.hasNext()) {
                    Response response = new Response();
                    Map.Entry<String, Object> entry = iterator3.next();
                    String status = entry.getKey(); //状态码 200 201 401 403 404 这样
                    LinkedHashMap<String, Object> statusInfo = (LinkedHashMap) entry.getValue();
                    LinkedHashMap<String, String> map = (LinkedHashMap<String, String>)statusInfo.get("schema");
                    if (null != map) {
                        String str = map.get("$ref");
                        if (null != str) {
                            String responseStr = str.substring(str.lastIndexOf("/") + 1);
                            // 获取返回对象
                            LinkedHashMap<String, LinkedHashMap> linkedHashMap = definitions.get(responseStr);
                            if (null != linkedHashMap) {
                                LinkedHashMap<String, LinkedHashMap> map1 = linkedHashMap.get("properties");
                                Iterator<String> iterator1 = map1.keySet().iterator();
                                while (iterator1.hasNext()) {
                                    Response response2 = new Response();
                                    // (id,name,...)
                                    String key = iterator1.next();
                                    // 假如key=“id” 则linkedHashMap2为："type" : "integer"
                                    LinkedHashMap<String, Object> linkedHashMap2 = map1.get(key);
                                    String type = (String) linkedHashMap2.get("type");
                                    String description = (String) linkedHashMap2.get("description");
                                    String $ref1 = (String)linkedHashMap2.get("$ref");
                                    if (null != $ref1) {
                                        PageName = $ref1.substring($ref1.lastIndexOf("/") + 1);
                                    }
                                    response2.setName(key);
                                    response2.setDescription(type);
                                    response2.setRemark(description);
                                    responseList.add(response2);
                                    LinkedHashMap<String, String> itemsMaps = (LinkedHashMap<String, String>)linkedHashMap2.get("items");
                                    if (null != itemsMaps) {
                                        String $ref = itemsMaps.get("$ref");
                                        if (null != $ref)
                                        responseName = $ref.substring($ref.lastIndexOf("/") + 1);
                                    }
                                }
                            }
                        }
                    }


                    /*response.setName(status);
                    response.setDescription(statusDescription);
                    response.setRemark(null);*/

                }

                //模拟一次HTTP请求,封装请求体和返回体，如果是Restful的文档可以再补充
                /*String request;
                request = StringUtils.remove(url, "{");
                request = StringUtils.remove(request, "}");//去掉路径中的{}参数请求
                if (requestType.contains("post")) {
                    Map<String, Object> strMap = otherRequestParam(requestList);
                    requestParam = strMap.toString();
                    responseParam = HttpClientUtil.post(host + request, null, strMap, null, "utf-8");
                } else if (requestType.contains("get")) {
                    String s = getRequestParam(requestList);
                    requestParam = s;
                    responseParam = HttpClientUtil.get(host + request + s, null, null, "utf-8");
                }*/

                //封装Table
                title = titleMap.get(title);
                if (null == numMap.get(title)) {
                    x++;
                    y = 1;
                    numMap.put(title, "");
                    table.setTitle("4." + x + " " + title);
                } else {
                    y++;
                }

                table.setUrl(url);
                table.setTag("4." + x + "." + y + " " + tag);
                table.setRequestForm(StringUtils.removeEnd(requestForm, "、"));
                table.setResponseForm(StringUtils.removeEnd(responseForm, "、"));
                table.setRequestType(StringUtils.removeEnd(requestType, "、"));
                table.setRequestList(requestList);
                table.setResponseList(responseList);
                if (!"".equals(body)) {
                    //System.out.println(body);
                    LinkedHashMap<String, LinkedHashMap> linkedHashMap = definitions.get(body);
                    if (null != linkedHashMap) {
                        table.setBodyName(body);
                        LinkedHashMap<String, LinkedHashMap> map = linkedHashMap.get("properties");
                        if (null != map) {
                            Iterator<String> iterator1 = map.keySet().iterator();
                            while (iterator1.hasNext()) {
                                Body body1 = new Body();
                                // (id,name,...)
                                String key = iterator1.next();
                                // 假如key=“id” 则linkedHashMap2为："type" : "integer"
                                LinkedHashMap<String, String> linkedHashMap2 = map.get(key);
                                String type = linkedHashMap2.get("type");
                                String description = linkedHashMap2.get("description");
                                body1.setName(key);
                                body1.setType(type);
                                body1.setRemark(description);
                                bodyList.add(body1);
                            }
                        } else {
                            System.out.println("出错的body --》 "+body);
                        }

                    }
                }

                if (!"".equals(PageName)) {
                    // 返回的data里面是object
                    System.out.println("PageName -> " + PageName);
                    LinkedHashMap<String, LinkedHashMap> linkedHashMap = definitions.get(PageName);
                    if (null != linkedHashMap) {
                        table.setPageName(PageName);
                        LinkedHashMap<String, LinkedHashMap> map = linkedHashMap.get("properties");
                        Iterator<String> iterator1 = map.keySet().iterator();
                        while (iterator1.hasNext()) {
                            Body body1 = new Body();
                            // (id,name,...)
                            String key = iterator1.next();
                            // 假如key=“id” 则linkedHashMap2为："type" : "integer"
                            LinkedHashMap<String, Object> linkedHashMap2 = map.get(key);
                            String type = (String) linkedHashMap2.get("type");
                            String description = (String) linkedHashMap2.get("description");
                            body1.setName(key);
                            body1.setType(type);
                            body1.setRemark(description);
                            PageNameList.add(body1);
                            LinkedHashMap<String, String> itemsMaps = (LinkedHashMap<String, String>)linkedHashMap2.get("items");
                            if (null != itemsMaps) {
                                String $ref = itemsMaps.get("$ref");
                                if (null != $ref)
                                    responseName = $ref.substring($ref.lastIndexOf("/") + 1);
                            }

                        }
                    }
                }
                 if (!"".equals(responseName)) {
                    // 返回的data里面是object
                     System.out.println("responseName -> " + responseName);
                     LinkedHashMap<String, LinkedHashMap> linkedHashMap = definitions.get(responseName);
                     if (null != linkedHashMap) {
                         table.setResponseName(responseName);
                         LinkedHashMap<String, LinkedHashMap> map = linkedHashMap.get("properties");
                         Iterator<String> iterator1 = map.keySet().iterator();
                         while (iterator1.hasNext()) {
                             Body body1 = new Body();
                             // (id,name,...)
                             String key = iterator1.next();
                             // 假如key=“id” 则linkedHashMap2为："type" : "integer"
                             LinkedHashMap<String, String> linkedHashMap2 = map.get(key);
                             String type = linkedHashMap2.get("type");
                             String description = linkedHashMap2.get("description");
                             body1.setName(key);
                             body1.setType(type);
                             body1.setRemark(description);
                             responseNameList.add(body1);
                         }
                     }
                 }
                //table.setRequestParam(requestParam);
                //table.setResponseParam(responseParam);
                table.setBodyList(bodyList);
                table.setResponseNameList(responseNameList);
                table.setPageNameList(PageNameList);
                list.add(table);
            }
        }
        return list;
    }

    /**
     * 封装post请求体
     *
     * @param list
     * @return
     */
    private Map<String, Object> otherRequestParam(List<Request> list) {
        Map<String, Object> map = new HashMap<>(16);
        if (list != null && list.size() > 0) {
            for (Request request : list) {
                String name = request.getName();
                String type = request.getType();
                switch (type) {
                    case "string":
                        map.put(name, "string");
                        break;
                    case "integer":
                        map.put(name, "0");
                        break;
                    case "double":
                        map.put(name, "0.0");
                        break;
                    case "boolean":
                        map.put(name, "true");
                    default:
                        map.put(name, "null");
                        break;
                }
            }
        }
        return map;
    }

    /**
     * 封装get参数
     *
     * @param list
     * @return
     */
    private String getRequestParam(List<Request> list) {
        StringBuffer stringBuffer = new StringBuffer();
        if (list != null && list.size() > 0) {
            for (Request request : list) {
                String name = request.getName();
                String type = request.getType();
                switch (type) {
                    case "string":
                        stringBuffer.append("&" + name + "=string");
                        break;
                    case "integer":
                        stringBuffer.append("&" + name + "=0");
                        break;
                    case "double":
                        stringBuffer.append("&" + name + "=0.0");
                        break;
                    case "boolean":
                        stringBuffer.append("&" + name + "=true");
                    default:
                        stringBuffer.append("&" + name + "=null");
                        break;
                }
            }
        }
        String s = stringBuffer.toString();
        if ("".equalsIgnoreCase(s)) {
            return "";
        }
        return "?" + StringUtils.removeStart(s, "&");
    }


}
