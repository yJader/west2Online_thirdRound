package com.yj.weather.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.yj.weather.pojo.City;
import com.yj.weather.pojo.Weather;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * @Author yJade
 * @Date 2023-01-12 12:03
 * @Package com.yj.mybatis.service
 */

//进行api请求 并解析json返回实体类
public class ApiService {
    static String cityUrl;
    static String weatherUrl;
    static String key;
    static {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/qWeather.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cityUrl = properties.getProperty("cityUrl");
        weatherUrl = properties.getProperty("weatherUrl");
        key = properties.getProperty("key");
    }

    private static String getJson(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse httpResponse = null;
        String json = null;

        try {
            httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                json = EntityUtils.toString(entity, "UTF-8").trim();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            httpGet.abort();
        }

        return json;
    }

    public static City getCityByGeoAPI(String location, String adm) {
        City city = new City();
        String url = cityUrl+"location="+location+"&adm="+adm+"&key="+key;
        String json = getJson(url);

        //解析json
        JSONObject jsonObject = JSON.parseObject(json);
        JSONArray locationArray = jsonObject.getJSONArray("location");
        List<City> cities = JSON.parseArray(locationArray.toJSONString(), City.class);

        if (cities.size() == 0) throw new RuntimeException("查询错误");

        return cities.get(0);
    }

    public static List<Weather> getWeathersByAPI(String locationId, String location) {
        Weather weather = new Weather();
        String url = weatherUrl+"location="+locationId+"&key="+key;
        String json = getJson(url);

        //解析json
        JSONObject jsonObject = JSON.parseObject(json);
        JSONArray locationArray = jsonObject.getJSONArray("daily");
        List<Weather> weathers = JSON.parseArray(locationArray.toJSONString(), Weather.class);

        //由于返回的json只有天气信息 只能自行set剩余信息
        int i=0;
        for (Weather wea:weathers) {
            wea.setLocationId(locationId);
            wea.setLocation(location);
            wea.setDayOfData(i++);
        }

        return weathers;
    }
}
