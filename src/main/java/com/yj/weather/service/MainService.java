package com.yj.weather.service;

import com.yj.weather.mapper.CityMapper;
import com.yj.weather.mapper.WeatherMapper;
import com.yj.weather.pojo.City;
import com.yj.weather.pojo.CityExample;
import com.yj.weather.pojo.Weather;
import com.yj.weather.pojo.WeatherExample;
import com.yj.weather.utils.SqlSessionUtils;
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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Boolean flag=true;

        System.out.println("欢迎使用天气查询");
        while (flag){
            System.out.println("请输入一个城市 和 所属一级行政区域(注意:行政区域要在末尾添加省/市)");

            String location = scanner.next();
            String adm = scanner.next();
            City city = selectCity(location, adm);
            List<Weather> weathers = selectWeathers(city.getLocationId(), city.getLocation());
            System.out.println(city.showCity());
            for (Weather wea : weathers) {
                System.out.println(wea.showWeather());
            }
            System.out.println("继续查询? true/false");
            flag = scanner.nextBoolean();
        }
        if(flag = false) System.out.println("欢迎下次继续使用");
    }

    //根据城市名 在表中查询城市相关信息
    public static City selectCity(String location, String adm) {
        SqlSession sqlSession = SqlSessionUtils.getSqlSession();
        CityMapper cityMapper = sqlSession.getMapper(CityMapper.class);

        CityExample example = new CityExample();
        example.createCriteria().andLocationEqualTo(location).andAdmEqualTo(adm);
        List<City> cities = cityMapper.selectByExample(example);

        City city = null;
        if (cities.size() == 0) { //未找到 通过Url查询 并存入数据库
            city = ApiService.getCityByGeoAPI(location, adm);
            City selectByPrimaryKey = cityMapper.selectByPrimaryKey(city.getLocationId());
            if (selectByPrimaryKey == null) {
                cityMapper.insert(city);
                sqlSession.commit();
            }
        } else {
            city = cities.get(0);
        }
//        System.out.println(city);
        return city;
    }

    //根据城市的locationId 在表中查询城市相关信息 并自动更新
    public static List<Weather> selectWeathers(String locationId, String location) {
        SqlSession sqlSession = SqlSessionUtils.getSqlSession();
        WeatherMapper mapper = sqlSession.getMapper(WeatherMapper.class);

        WeatherExample example = new WeatherExample();
        example.createCriteria().andLocationIdEqualTo(locationId);
        List<Weather> weathers = mapper.selectByExample(example);

        //数据库中还没有相关数据 插入
        if(weathers.size() == 0) {
            weathers = ApiService.getWeathersByAPI(locationId, location);
            for (Weather wea : weathers) mapper.insert(wea);
            sqlSession.commit();
            return weathers;
        }

        //判断数据是否过期 否则修改
        weathers.sort(Comparator.comparing(Weather::getDayOfData));
        if (!weathers.get(0).getFxDate().equals(new Date())) {
            for (Weather wea : weathers) mapper.updateByPrimaryKey(wea);
            sqlSession.commit();
        }
        return weathers;
    }
}