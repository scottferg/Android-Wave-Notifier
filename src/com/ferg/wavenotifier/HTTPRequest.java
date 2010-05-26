package com.ferg.wavenotifier;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.net.URLEncoder;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.InputStream;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import org.json.*;

import android.util.Log;

/**
 *
 * Performs an HTTP request.
 *
 *
 */
public class HTTPRequest {
        
    private final static String TAG = "HTTP";
    private String REQUEST_URL;
    private String mHeader;
    private String mHeaderValue;

    public HTTPRequest(String aRequestUrl, String aHeader, String aHeaderValue) {

        REQUEST_URL = aRequestUrl;
        mHeader = aHeader;
        mHeaderValue = aHeaderValue;
    }

    /**
     * Static call for an HTTP request
     *
     * @param aParams parameters to send for the request
     *
     */
    public String request(HashMap aParams) {

        String result = null;

        try {
            DefaultHttpClient client = new DefaultHttpClient();
            
            client.addRequestInterceptor(new HttpRequestInterceptor() {

                public void process(final HttpRequest aRequest, final HttpContext aContext)
                    throws HttpException, IOException 
                {
                    if (!aRequest.containsHeader("Accept-Encoding")) {
                        aRequest.addHeader("Accept-Encoding", "gzip");
                    }

                    Log.i(TAG, "Encoded!!!");
                    Log.i(TAG, mHeader);
                    Log.i(TAG, mHeaderValue);
                    aRequest.addHeader(mHeader, mHeaderValue);
                }
            });

            client.addResponseInterceptor(new HttpResponseInterceptor() {

                public void process(final HttpResponse aResponse, final HttpContext aContext)
                    throws HttpException, IOException
                {
                    HttpEntity entity = aResponse.getEntity();
                    Header contentEncoding = entity.getContentEncoding();

                    if (contentEncoding != null) {
                        
                        HeaderElement[] codecs = contentEncoding.getElements();

                        for (HeaderElement codec : codecs) {

                            if (codec.getName().equalsIgnoreCase("gzip")) {
                                aResponse.setEntity(
                                    new GzipDecompressingEntity(aResponse.getEntity()));

                                return;
                            }
                        }
                    }
                }
            });
            
            HttpGet post = new HttpGet(REQUEST_URL + generateQueryString(aParams));

            Log.i(TAG, REQUEST_URL);
            Log.i(TAG, generateQueryString(aParams));

            HttpResponse response = client.execute(post);

            Log.i(TAG, "Requesteeeeedd!!!");

            HttpEntity entity = response.getEntity();

            if (entity != null) {

                result = EntityUtils.toString(entity);
            }

            client.getConnectionManager().shutdown();

        } catch (Exception e) {
            // TODO: Catch the error
            Log.i(TAG, e.toString());
        }

        return result;
    }

    /**
     * Generates a query string from a set of parameters
     *
     * @return URL encoded query string
     *
     */
    private String generateQueryString(HashMap aParams)
    {
        String result = "?";

        try {
            if (aParams != null) {
                // Loop over each parameter and add it to the query string
                Iterator iter = aParams.entrySet().iterator();

                while (iter.hasNext()) {
                    Map.Entry param = (Map.Entry) iter.next();

                    if (param.getValue() != "") {
                        result += URLEncoder.encode((String) param.getKey(), "UTF-8") + "=" + URLEncoder.encode((String) param.getValue(), "UTF-8");
                    } else {
                        result += URLEncoder.encode((String) param.getKey(), "UTF-8");
                    }

                    if (iter.hasNext()) {
                        result += "&";
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            Log.i("HTTP", e.toString());
        }
    
        return result;
    }

    private static class GzipDecompressingEntity extends HttpEntityWrapper {

        public GzipDecompressingEntity(final HttpEntity aEntity) {

            super(aEntity);
        }

        @Override
        public InputStream getContent()
            throws IOException, IllegalStateException 
        {

            InputStream wrappedIn = wrappedEntity.getContent();

            return new GZIPInputStream(wrappedIn);
        }

        @Override
        public long getContentLength() {

            return -1;
        }

    } 
}
