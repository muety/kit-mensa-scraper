package edu.kit.aifb.atks.mensascraper.lib;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class KITMensaScraperTest {

    @Mock
    private HttpResponse<InputStream> mockResponse;

    @Spy
    private HttpClient httpClient;

    private KITMensaScraper sut;

    @BeforeEach
    void setUp() throws IllegalAccessException, IOException, InterruptedException {
        sut = new KITMensaScraper();
        FieldUtils.writeField(sut, "http", httpClient, true);
        FieldUtils.writeField(sut, "clock", Clock.fixed(Instant.parse("2023-05-09T17:00:00.00Z"), ZoneId.systemDefault()), true);

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.uri()).thenReturn(URI.create("https://example.org"));
        when(mockResponse.body()).then(i -> KITMensaScraperTest.class.getClassLoader().getResourceAsStream("example_adenauerring.html"));
        when(httpClient.send(any(), any(HttpResponse.BodyHandlers.ofInputStream().getClass()))).thenReturn(mockResponse);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFetchMeals() throws IOException, InterruptedException {
        final ArgumentCaptor<HttpRequest> captor1 = ArgumentCaptor.forClass(HttpRequest.class);

        var tuesday = sut.fetchMeals(MensaLocation.ADENAUERRING, LocalDate.of(2023, Month.MAY, 9));
        var wednesday = sut.fetchMeals(MensaLocation.ADENAUERRING, LocalDate.of(2023, Month.MAY, 10));

        verify(httpClient, times(2)).send(captor1.capture(), any());
        assertEquals("kw=19", captor1.getValue().uri().getQuery());

        assertEquals(23, tuesday.size());
        assertEquals(24, wednesday.size());
        assertEquals("Mini Frühlingsrollen mit Sweet Chili Soße und Mienudeln", tuesday.get(2).getName());
        assertEquals(3.8f, tuesday.get(2).getPrice());
        assertEquals(1147.0f, tuesday.get(2).getKcal());
        assertEquals(23.0f, tuesday.get(2).getProteins());
        assertEquals(MensaLine.LINIE_2, tuesday.get(2).getLine());
        assertEquals(MensaMealType.VEGAN, tuesday.get(2).getType());
    }

    @Test
    void testFetchMealsFailPast() {
        assertThrows(MensaScraperException.class, () -> sut.fetchMeals(MensaLocation.ADENAUERRING, LocalDate.of(2023, Month.MAY, 8)));
    }

    @Test
    void testFetchMealsEmptyMenu() throws IOException, InterruptedException {
        final ArgumentCaptor<HttpRequest> captor1 = ArgumentCaptor.forClass(HttpRequest.class);

        final var result = sut.fetchMeals(MensaLocation.ADENAUERRING, LocalDate.of(2023, Month.MAY, 15));
        assertTrue(result.isEmpty());

        verify(httpClient, times(1)).send(captor1.capture(), any());
        assertEquals("kw=20", captor1.getValue().uri().getQuery());
    }

    @Test
    void testCaching() throws IOException, InterruptedException {
        final var result1 = sut.fetchMeals(MensaLocation.ADENAUERRING, LocalDate.of(2023, Month.MAY, 9));
        final var result2 = sut.fetchMeals(MensaLocation.ADENAUERRING, LocalDate.of(2023, Month.MAY, 9));
        final var result3 = sut.fetchMeals(MensaLocation.ADENAUERRING, LocalDate.of(2023, Month.MAY, 10));

        verify(httpClient, times(2)).send(any(), any());
        assertEquals(result1, result2);  // list overrides equals to compare element-wise
        assertEquals(result1.get(0), result2.get(0));
        assertNotEquals(System.identityHashCode(result1), System.identityHashCode(result2));
        assertNotEquals(System.identityHashCode(result1.get(0)), System.identityHashCode(result2.get(0)));
    }
}