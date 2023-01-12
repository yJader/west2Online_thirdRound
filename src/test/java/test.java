import com.yj.weather.pojo.City;
import com.yj.weather.pojo.Weather;
import com.yj.weather.service.ApiService;
import com.yj.weather.service.MainService;
import org.junit.jupiter.api.Test;
import sun.applet.Main;

import java.util.List;

/**
 * @Author yJade
 * @Date 2023-01-12 12:00
 * @Package PACKAGE_NAME
 */
public class test {
    @Test
    public void getCityByGeoAPITest() {
        City city = ApiService.getCityByGeoAPI("福州", "福建");
        System.out.println(city);
    }

    @Test
    public void selectCityTest() {
        MainService mainService = new MainService();
        mainService.selectCity("福州", "福建");
    }

    @Test
    public void getWeatherByAPITest() {
        MainService mainService = new MainService();
        City city = mainService.selectCity("福州", "福建");
        List<Weather> weathers = ApiService.getWeathersByAPI(city.getLocationId(), city.getLocation());
        for (Weather wea: weathers) {
            System.out.println(wea.showWeather());
        }
    }

    @Test
    public void selectWeathersTest() {
        MainService mainService = new MainService();
        City city = mainService.selectCity("福州", "福建");
        List<Weather> weathers = mainService.selectWeathers(city.getLocationId(), city.getLocation());
        for (Weather wea: weathers) {
            System.out.println(wea.showWeather());
        }
    }
}
