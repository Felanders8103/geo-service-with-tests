import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.geo.GeoService;
import ru.netology.geo.GeoServiceImpl;
import ru.netology.i18n.LocalizationServiceImpl;
import ru.netology.sender.MessageSenderImpl;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MainTest {

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.CsvSource({
            "96.44.183.149, New York, USA, ' 10th Avenue', 32",
            "96.21.101.101, New York, USA, , 0"
    })
    public void testByIpReturnCorrectLocation(String ip, String city, Country country,
                                              String street, int building) {
        GeoServiceImpl testGeoService = new GeoServiceImpl();
        Location result = testGeoService.byIp(ip);
        assertEquals(city, result.getCity());
        assertEquals(country, result.getCountry());
        assertEquals(street, result.getStreet());
        assertEquals(building, result.getBuiling());
    }


    @org.junit.jupiter.api.Test
    public void testLocaleReturnCorrectText() {
        LocalizationServiceImpl testLocalizationService = new LocalizationServiceImpl();
        Country testCountry = Country.RUSSIA;
        String result = testLocalizationService.locale(testCountry);
        assertEquals("Добро пожаловать", result);

        Country testCountry2 = Country.BRAZIL;
        String result2 = testLocalizationService.locale(testCountry2);
        assertEquals("Welcome", result2);
    }

    @Test
    public void testMessageSenderReturnRussianWithRussianIp() {
        GeoService testGeoService = Mockito.mock(GeoServiceImpl.class);
        Mockito.when(testGeoService.byIp(Mockito.anyString())).thenReturn(
                new Location("Moscow", Country.RUSSIA, "Lenina", 15));

        LocalizationServiceImpl testLocalizationService = Mockito
                .mock(LocalizationServiceImpl.class);
        Mockito.when(testLocalizationService.locale(Country.RUSSIA))
                .thenReturn("Добро пожаловать");

        MessageSenderImpl testSender = new MessageSenderImpl(testGeoService, testLocalizationService);
        Map<String, String> testHeaders = new HashMap<String, String>();
        testHeaders.put(MessageSenderImpl.IP_ADDRESS_HEADER, "172.123.12.19");
        String result = testSender.send(testHeaders);

        String expected = "Добро пожаловать";
        assertEquals(expected, result);
    }

    @Test
    public void testMessageSenderReturnEnglishWithAmericanIp() {
        GeoService testGeoService = Mockito.mock(GeoServiceImpl.class);
        Mockito.when(testGeoService.byIp(Mockito.anyString())).thenReturn(
                new Location("New York", Country.USA, " 10th Avenue", 32));

        LocalizationServiceImpl testLocalizationService = Mockito
                .mock(LocalizationServiceImpl.class);
        Mockito.when(testLocalizationService.locale(Country.USA)).thenReturn("Welcome");

        MessageSenderImpl testSender = new MessageSenderImpl(testGeoService, testLocalizationService);
        Map<String, String> testHeaders = new HashMap<String, String>();
        testHeaders.put(MessageSenderImpl.IP_ADDRESS_HEADER, "96.44.183.149");
        String result = testSender.send(testHeaders);

        String expected = "Welcome";
        assertEquals(expected, result);
    }

    @org.junit.jupiter.api.Test
    public void testByCoordinatesWorkCorrectly() {
        GeoService testGeoService = new GeoServiceImpl();
        assertThrows(RuntimeException.class, () -> {
            testGeoService.byCoordinates(41.767578, -34.434423);
        });
    }
}
