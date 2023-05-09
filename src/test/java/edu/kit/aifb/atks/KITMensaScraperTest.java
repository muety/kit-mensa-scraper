package edu.kit.aifb.atks;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KITMensaScraperTest {

    @Mock
    private HttpResponse<InputStream> mockResponse;

    @Spy
    private HttpClient httpClient;

    private KITMensaScraper sut;

    @BeforeEach
    void setUp() throws IllegalAccessException {
        sut = new KITMensaScraper();
        FieldUtils.writeField(sut, "http", httpClient, true);
        FieldUtils.writeField(sut, "clock", Clock.fixed(Instant.parse("2023-05-09T17:00:00.00Z"), ZoneId.systemDefault()), true);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFetchMeals() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.uri()).thenReturn(URI.create("https://example.org"));
        when(mockResponse.body()).then(i -> KITMensaScraperTest.class.getClassLoader().getResourceAsStream("example_adenauerring.html"));
        when(httpClient.send(any(), any(HttpResponse.BodyHandlers.ofInputStream().getClass()))).thenReturn(mockResponse);

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
    void testFetchMealsFailDayNotFound() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.uri()).thenReturn(URI.create("https://example.org"));
        when(mockResponse.body()).then(i -> KITMensaScraperTest.class.getClassLoader().getResourceAsStream("example_adenauerring.html"));
        when(httpClient.send(any(), any(HttpResponse.BodyHandlers.ofInputStream().getClass()))).thenReturn(mockResponse);
        final ArgumentCaptor<HttpRequest> captor1 = ArgumentCaptor.forClass(HttpRequest.class);

        assertThrows(MensaScraperException.class, () -> sut.fetchMeals(MensaLocation.ADENAUERRING, LocalDate.of(2023, Month.MAY, 15)));

        verify(httpClient, times(1)).send(captor1.capture(), any());
        assertEquals("kw=20", captor1.getValue().uri().getQuery());
    }
}