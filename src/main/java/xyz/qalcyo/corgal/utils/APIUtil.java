package xyz.qalcyo.corgal.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import xyz.qalcyo.corgal.Corgal;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Scanner;

/**
 * Stolen from Skytils under AGPLv3
 * https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md
 */
public class APIUtil {

    public static final HttpClientBuilder builder = HttpClients.custom().setUserAgent(Corgal.ID + "/" + Corgal.VER)
            .addInterceptorFirst(((HttpRequest request, HttpContext context) -> {
                if (!request.containsHeader("Pragma")) request.addHeader("Pragma", "no-cache");
                if (!request.containsHeader("Cache-Control")) request.addHeader("Cache-Control", "no-cache");
            }));
    private static final JsonParser parser = new JsonParser();

    /**
     * Gets a JsonObject from online.
     *
     * @param url The URL to get the JsonObject.
     */
    public static JsonObject getJSONResponse(String url) {
        try (CloseableHttpClient client = builder.build()) {
            HttpGet request = new HttpGet(URI.create(url));
            request.setProtocolVersion(HttpVersion.HTTP_1_1);
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() == 200) {
                return parser.parse(EntityUtils.toString(entity)).getAsJsonObject();
            } else {
                if (StringUtils.startsWithAny(url, "https://api.ashcon.app/mojang/v2/user/",
                        "https://api.hypixel.net/")
                ) {
                    Scanner scanner = new Scanner(entity.getContent());
                    scanner.useDelimiter("\\Z");
                    String error = scanner.next();
                    if (error.startsWith("{")) {
                        return parser.parse(error).getAsJsonObject();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return new JsonObject();
    }

    public static boolean download(String url, File file) {
        url = url.replace(" ", "%20");
        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            HttpResponse downloadResponse = builder.build().execute(new HttpGet(url));
            byte[] buffer = new byte[1024];

            int read;
            while ((read = downloadResponse.getEntity().getContent().read(buffer)) > 0) {
                fileOut.write(buffer, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}