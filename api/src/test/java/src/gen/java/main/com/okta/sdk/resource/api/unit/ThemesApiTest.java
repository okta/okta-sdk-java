package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.resource.api.ThemesApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.ImageUploadResponse;
import com.okta.sdk.resource.model.ThemeResponse;
import com.okta.sdk.resource.model.UpdateThemeRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class ThemesApiTest {

    private ApiClient apiClient;
    private ThemesApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new ThemesApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");
        when(apiClient.parameterToPair(anyString(), any())).thenReturn(new ArrayList<Pair>());
    }

    /* Stubs */
    private void stubInvokeVoidDelete() throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenReturn(null);
    }

    private void stubInvokeThemeResponse(String method, ThemeResponse value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq(method),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    private void stubInvokeThemeList(List<ThemeResponse> value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    private void stubInvokeImageUploadResponse(ImageUploadResponse value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    /* deleteBrandThemeBackgroundImage */
    @Test
    public void testDeleteBackgroundImage_Success() throws Exception {
        stubInvokeVoidDelete();
        api.deleteBrandThemeBackgroundImage("B1","T1");
        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/B1/themes/T1/background-image"),
            eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), anyString(),
            any(String[].class), isNull()
        );
    }

    @Test
    public void testDeleteBackgroundImage_WithHeaders() throws Exception {
        stubInvokeVoidDelete();
        Map<String,String> hdr = Collections.singletonMap("X-D","v");
        api.deleteBrandThemeBackgroundImage("B2","T2", hdr);
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("DELETE"), anyList(), anyList(), anyString(), any(),
            cap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), isNull());
        assertEquals("v", cap.getValue().get("X-D"));
    }

    @Test
    public void testDeleteBackgroundImage_MissingParams() {
        expect400(() -> api.deleteBrandThemeBackgroundImage(null,"T"));
        expect400(() -> api.deleteBrandThemeBackgroundImage("B",null));
    }

    /* getBrandTheme */
    @Test
    public void testGetBrandTheme_Success() throws Exception {
        ThemeResponse expected = new ThemeResponse();
        stubInvokeThemeResponse("GET", expected);
        ThemeResponse actual = api.getBrandTheme("B3","T3");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/B3/themes/T3"),
            eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testGetBrandTheme_MissingParams() {
        expect400(() -> api.getBrandTheme(null,"T"));
        expect400(() -> api.getBrandTheme("B",null));
    }

    /* listBrandThemes */
    @Test
    public void testListBrandThemes_Success() throws Exception {
        List<ThemeResponse> expected = Arrays.asList(new ThemeResponse(), new ThemeResponse());
        stubInvokeThemeList(expected);
        List<ThemeResponse> actual = api.listBrandThemes("B4");
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/B4/themes"),
            eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testListBrandThemes_MissingParam() {
        expect400(() -> api.listBrandThemes(null));
    }

    /* replaceBrandTheme */
    @Test
    public void testReplaceBrandTheme_Success() throws Exception {
        ThemeResponse expected = new ThemeResponse();
        stubInvokeThemeResponse("PUT", expected);
        UpdateThemeRequest req = new UpdateThemeRequest();
        ThemeResponse actual = api.replaceBrandTheme("B5","T5", req);
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/B5/themes/T5"),
            eq("PUT"),
            anyList(), anyList(),
            anyString(), eq(req),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), eq("application/json"),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testReplaceBrandTheme_WithHeaders() throws Exception {
        stubInvokeThemeResponse("PUT", new ThemeResponse());
        UpdateThemeRequest req = new UpdateThemeRequest();
        api.replaceBrandTheme("B6","T6", req, Collections.singletonMap("X-R","1"));
        ArgumentCaptor<Map> cap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("PUT"), anyList(), anyList(), anyString(), any(),
            cap.capture(), anyMap(), anyMap(), anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertEquals("1", cap.getValue().get("X-R"));
    }

    @Test
    public void testReplaceBrandTheme_MissingParams() {
        expect400(() -> api.replaceBrandTheme(null,"T", new UpdateThemeRequest()));
        expect400(() -> api.replaceBrandTheme("B",null, new UpdateThemeRequest()));
        expect400(() -> api.replaceBrandTheme("B","T", null));
    }

    /* uploadBrandThemeLogo (representative upload) */
    @Test
    public void testUploadBrandThemeLogo_Success() throws Exception {
        ImageUploadResponse expected = new ImageUploadResponse();
        stubInvokeImageUploadResponse(expected);
        File f = new File("logo.png");
        ImageUploadResponse actual = api.uploadBrandThemeLogo("B7","T7", f);
        assertSame(expected, actual);
        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/B7/themes/T7/logo"),
            eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            eq("application/json"), anyString(),
            any(String[].class), any(TypeReference.class)
        );
    }

    @Test
    public void testUploadBrandThemeLogo_WithHeadersAndFileForm() throws Exception {
        ImageUploadResponse resp = new ImageUploadResponse();
        stubInvokeImageUploadResponse(resp);
        File f = new File("f.png");
        api.uploadBrandThemeLogo("B8","T8", f, Collections.singletonMap("X-U","h"));
        ArgumentCaptor<Map> headersCap = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<Map> formCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("POST"), anyList(), anyList(), anyString(), any(),
            headersCap.capture(), anyMap(), formCap.capture(), anyString(), anyString(), any(String[].class), any(TypeReference.class));
        assertEquals("h", headersCap.getValue().get("X-U"));
        assertTrue(formCap.getValue().containsKey("file"));
    }

    @Test
    public void testUploadBrandThemeLogo_MissingParams() {
        File f = new File("x.png");
        expect400(() -> api.uploadBrandThemeLogo(null,"T", f));
        expect400(() -> api.uploadBrandThemeLogo("B",null, f));
        expect400(() -> api.uploadBrandThemeLogo("B","T", null));
    }

    /* ApiException propagation */
    @Test
    public void testApiExceptionPropagates_GetBrandTheme() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(502,"bad"));
        try {
            api.getBrandTheme("B9","T9");
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(502, e.getCode());
        }
    }

    @Test
    public void testApiExceptionPropagates_UploadLogo() throws Exception {
        when(apiClient.invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenThrow(new ApiException(503,"down"));
        try {
            api.uploadBrandThemeLogo("B10","T10", new File("a.png"));
            fail("Expected");
        } catch (ApiException e) {
            assertEquals(503, e.getCode());
        }
    }

    /* Header negotiation */
    @Test
    public void testSelectHeaderAcceptCalled_GetBrandTheme() throws Exception {
        stubInvokeThemeResponse("GET", new ThemeResponse());
        api.getBrandTheme("B11","T11");
        verify(apiClient).selectHeaderAccept(any(String[].class),
            eq("/api/v1/brands/B11/themes/T11"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    @Test
    public void testSelectHeaderAcceptCalled_UploadLogo() throws Exception {
        stubInvokeImageUploadResponse(new ImageUploadResponse());
        api.uploadBrandThemeLogo("B12","T12", new File("b.png"));
        verify(apiClient).selectHeaderAccept(any(String[].class),
            eq("/api/v1/brands/B12/themes/T12/logo"));
        verify(apiClient).selectHeaderContentType(any(String[].class));
    }

    /* ObjectMapper configuration */
    @Test
    public void testGetObjectMapperConfiguration() throws Exception {
        Method m = ThemesApi.class.getDeclaredMethod("getObjectMapper");
        m.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) m.invoke(null);
        assertNotNull(mapper);
        assertFalse(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.getDeserializationConfig()
            .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
    }

    /* helper */
    private void expect400(Runnable r) {
        try {
            r.run();
            fail("Expected 400");
        } catch (ApiException e) {
            assertEquals(400, e.getCode());
        }
    }
}
