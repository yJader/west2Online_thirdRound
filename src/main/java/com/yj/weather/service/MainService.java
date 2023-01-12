package com.yj.weather.service;

import com.yj.weather.mapper.CityMapper;
import com.yj.weather.mapper.WeatherMapper;
import com.yj.weather.pojo.City;
import com.yj.weather.pojo.CityExample;
import com.yj.weather.pojo.Weather;
import com.yj.weather.pojo.WeatherExample;
import org.apache.ibatis.session.SqlSession;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * @Author yJade
 * @Date 2023-01-12 11:58
 * @Package com.yj.mybatis.service
 */
public class MainService {
    String msg = null;
    Date today = new Date(); //按照系统时间

    public static void main(String[] args) {
        System.out.println("欢迎使用天气查询");
        System.out.println("请输入一个城市 和 所属一级行政区域(注意:行政区域要在末尾添加省/市)");
        Scanner scanner = new Scanner(System.in);
        String location = scanner.next();
        String adm = scanner.next();

    }

    //根据城市名 在表中查询城市相关信息
    public City selectCity(String location, String adm) {
        SqlSession sqlSession = SqlSessionUtils.getSqlSession();
        CityMapper cityMapper = sqlSession.getMapper(CityMapper.class);

        CityExample example = new CityExample();
        example.createCriteria().andLocationEqualTo(location).andAdmEqualTo(adm);
        List<City> cities = cityMapper.selectByExample(example);

        City city = null;
        if (cities.size() == 0) { //未找到 通过Url查询 并存入数据库
            city = ApiService.getCityByGeoAPI(location, adm);
            City selectByPrimaryKey = cityMapper.selectByPrimaryKey(city.getLocationId());
            if (selectByPrimaryKey == null) cityMapper.insert(city);
        } else {
            city = cities.get(0);
        }
//        System.out.println(city);
        return city;
    }

    //根据城市的locationId 在表中查询城市相关信息 并自动更新
    public List<Weather> selectWeathers(String locationId, String location) {
        SqlSession sqlSession = SqlSessionUtils.getSqlSession();
        WeatherMapper mapper = sqlSession.getMapper(WeatherMapper.class);

        WeatherExample example = new WeatherExample();
        example.createCriteria().andLocationIdEqualTo(locationId);
        List<Weather> weathers = mapper.selectByExample(example);

        //数据库中还没有相关数据
        if(weathers.size() == 0) {
            weathers = ApiService.getWeathersByAPI(locationId, location);
            for (Weather wea : weathers) mapper.insert(wea);
            return weathers;
        }
        weathers.sort(Comparator.comparing(Weather::getDayOfData));
        if (!weathers.get(0).getFxDate().equals(today)) {
            for (Weather wea : weathers) mapper.updateByPrimaryKey(wea);
        }
        return weathers;
    }

    public Date getToday() {
        return today;
    }

    public void setToday(Date today) {
        this.today = today;
    }
}