package dev.undefinedteam.gensh1n.utils.network;

import com.google.gson.Gson;
import dev.undefinedteam.gensh1n.utils.json.GsonUtils;
import dev.undefinedteam.gensh1n.utils.json.JsonDateDeserializer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class Http {
    // GSON INSTANCE
    private static final Gson GSON = GsonUtils.custom()
            .registerTypeAdapter(Date.class, new JsonDateDeserializer())
            .create();

    public enum Method {
        GET,
        POST
    }

    public static Request to(Method method, String url) {
        return new Request(method, url);
    }

    public static Request get(String url) {
        return new Request(Method.GET, url);
    }

    public static Request post(String url) {
        return new Request(Method.POST, url);
    }

    public static class Request {
        public final HttpClientContext context = HttpClientContext.create();
        public final CookieStore cookieStore = new BasicCookieStore();
        private final HttpRequestBase httpClient;
        private final RequestConfig.Builder configBuilder = RequestConfig.custom();
        private boolean keepAlive = true;

        public Request(Method method, String url) {
            this.httpClient = method.equals(Method.GET) ? new HttpGet(url) : new HttpPost(url);

            this.cookieSpec(CookieSpecs.DEFAULT).context.setCookieStore(cookieStore);
            userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");
        }

        public Request diKeepAlive() {
            this.keepAlive = false;
            return this;
        }

        public Request userAgent(String agent) {
            this.httpClient.setHeader(HttpHeaders.USER_AGENT, agent);
            return this;
        }

        public Request header(String header, Object value) {
            this.httpClient.setHeader(header, value.toString());
            return this;
        }

        public Request cookieSpec(String spec) {
            this.configBuilder.setCookieSpec(spec);
            return this;
        }

        public Request cookies(String data) {
            this.httpClient.setHeader("Cookie", data);
            return this;
        }

        public Request bearer(String token) {
            return authorization("Bearer " + token);
        }

        public Request authorization(String token) {
            httpClient.setHeader(HttpHeaders.AUTHORIZATION,token);
            return this;
        }

        public Request bodyJson(String data) {
            httpClient.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
            if (this.httpClient instanceof HttpPost post) {
                post.setEntity(new StringEntity(data, ContentType.APPLICATION_JSON.withCharset(charset)));
            }
            return this;
        }

        public Request bodyForm(String data) {
            httpClient.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
            if (this.httpClient instanceof HttpPost post) {
                post.setEntity(new StringEntity(data, ContentType.APPLICATION_FORM_URLENCODED.withCharset(charset)));
            }
            return this;
        }

        public Request bodyForm(StringEntity data) {
            httpClient.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
            if (this.httpClient instanceof HttpPost post) {
                post.setEntity(data);
            }
            return this;
        }

        public Request bodyCrypt(Function<String, byte[]> function, String data) {
            return bodyBytes(function.apply(data));
        }

        public Request bodyCrypt(Function<String, byte[]> function, Object data) {
            return bodyBytes(function.apply(GSON.toJson(data)));
        }

        public Request bodyBytes(byte[] data) {
            httpClient.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.DEFAULT_BINARY.getMimeType());
            if (this.httpClient instanceof HttpPost post) {
                post.setEntity(new ByteArrayEntity(data));
            }
            return this;
        }

        public Request bodyJson(Object object) {
            return bodyJson(GSON.toJson(object));
        }

        public Request proxy(String ip, int port) {
            this.configBuilder.setProxy(new HttpHost(ip, port));
            return this;
        }

        public Request acceptJson() {
            return accept("application/json, text/json, text/x-json, text/javascript");
        }

        public Request acceptAll() {
            return accept("*/*");
        }

        public Request accept(String type) {
            httpClient.setHeader(HttpHeaders.ACCEPT, type);
            return this;
        }

        private <T> T _send(HttpResponseHandler<T> responseHandler) {
            try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
                if (!this.keepAlive) {
                    this.httpClient.setHeader(HttpHeaders.CONNECTION, "close");
                }
                this.httpClient.setConfig(this.configBuilder.build());
                CloseableHttpResponse response = client.execute(this.httpClient,context);
                return responseHandler.handle(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        public <T> T sendResponse(ResponseHandler<T> responseHandler) {
            try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
                if (!this.keepAlive) {
                    this.httpClient.setHeader(HttpHeaders.CONNECTION, "close");
                }
                this.httpClient.setConfig(this.configBuilder.build());
                CloseableHttpResponse response = client.execute(this.httpClient);
                return responseHandler.handle(response);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        public <T> T sendCtxResponse(CtxResponseHandler<T> responseHandler) {
            try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
                if (!this.keepAlive) {
                    this.httpClient.setHeader(HttpHeaders.CONNECTION, "close");
                }
                this.httpClient.setConfig(this.configBuilder.build());
                CloseableHttpResponse response = client.execute(this.httpClient);
                return responseHandler.handle(context,response);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        public void send() {
            _send(response -> null);
        }

        private String charset = "UTF-8";

        public Request setCoding(String charset) {
            this.charset = charset;
            return this;
        }

        public String sendString() {
            return new String(sendBytes(), Charset.forName(charset));
        }

        public HttpEntity sendHttpEntity() {
            return _send(entity -> entity);
        }

        public Stream<String> sendLines() {
            return Stream.of(sendString().split("\\r?\\n"));
        }

        public <T> T sendJson(Type type) {
            String response = sendString();
            return response == null ? null : GSON.fromJson(response, type);
        }

        public InputStream sendInputStream() {
            return _send(entity -> {
                try {
                    return entity.getContent();
                } catch (IOException e) {
                    return null;
                }
            });
        }

        public <T> T sendCryptJson(Function<byte[], String> function, Type type) {
            String response = function.apply(sendBytes());
            return response == null ? null : GSON.fromJson(response, type);
        }

        public <T> T sendCrypt(Function<byte[], T> function) {
            return function.apply(sendBytes());
        }

        public <T> T sendCryptJson(BiFunction<byte[], String, String> function, String key, Type type) {
            String response = function.apply(sendBytes(), key);
            return response == null ? null : GSON.fromJson(response, type);
        }

        public <T, R> T sendCrypt(BiFunction<byte[], R, T> function, R key) {
            return function.apply(sendBytes(), key);
        }

        public <T> T sendCryptJsonStr(Function<String, String> function, Type type) {
            String response = function.apply(sendString());
            return response == null ? null : GSON.fromJson(response, type);
        }

        public <T> T sendCryptStr(Function<String, T> function) {
            return function.apply(sendString());
        }

        public <T> T sendCryptJsonStr(BiFunction<String, String, String> function, String key, Type type) {
            String response = function.apply(sendString(), key);
            return response == null ? null : GSON.fromJson(response, type);
        }

        public <T, K> T sendCryptStr(BiFunction<String, K, T> function, K key) {
            return function.apply(sendString(), key);
        }

        public byte[] sendBytes() {
            return _send(entity -> {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[4 * 1024];
                    int read;
                    while ((read = entity.getContent().read(buffer)) > 0) out.write(buffer, 0, read);
                    return out.toByteArray();
                } catch (IOException e) {
                    return null;
                }
            });
        }

        public byte[] sendBytes(int per) {
            return _send(entity -> {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[per];
                    int read;
                    while ((read = entity.getContent().read(buffer)) > 0) out.write(buffer, 0, read);
                    return out.toByteArray();
                } catch (IOException e) {
                    return null;
                }
            });
        }

        public interface HttpResponseHandler<T> {
            T handle(HttpEntity response);
        }

        public interface ResponseHandler<T> {
            T handle(HttpResponse response);
        }

        public interface CtxResponseHandler<T> {
            T handle(HttpClientContext context,HttpResponse response);
        }
    }
}
