package com.ringme.common;



import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import utils.Hex;
import utils.Protocol;

@Log4j2
public class MTStub {
    public static final String [] CHARSETS = new String[] {"ASCII", "UTF-16", "UTF-8"};

    protected Protocol protocol;

    private Object lock = new Object();

    private HttpClient httpclient;

   // private Base64 encoder;

    private String xmlns;

    private String username;

    private String password;

    public MTStub(String url, String xmlns, String username, String password) {
        this.protocol = new Protocol(url);
        this.xmlns = xmlns;
        this.username = username;
        this.password = password;
        //this.encoder = new BASE64Encoder();
        instance();
    }

    public void close() {}

    public void instance() {
        this.httpclient = new HttpClient();
        if (this.httpclient != null) {
            HttpConnectionManager conMgr = this.httpclient.getHttpConnectionManager();
            HttpConnectionManagerParams conPars = conMgr.getParams();
            conPars.setConnectionTimeout(20000);
            conPars.setSoTimeout(90000);
        }
    }

    public void setHttpclientTimeout(int connectionTimeout, int soTimeout) {
        if (this.httpclient != null) {
            HttpConnectionManager conMgr = this.httpclient.getHttpConnectionManager();
            HttpConnectionManagerParams conPars = conMgr.getParams();
            conPars.setConnectionTimeout(connectionTimeout);
            conPars.setSoTimeout(soTimeout);
        }
    }

    public void reload(String url, String xmlns, String username, String password) {
        if (!this.protocol.getUrl().equals(url) || !this.xmlns.equals(xmlns) || !this.username.equals(username) || !this.password.equals(password)) {
            this.protocol.setUrl(url);
            this.xmlns = xmlns;
            this.username = username;
            this.password = password;
            close();
            instance();
        }
    }

    private int sendMT(String sessionId, String serviceId, String sender, String receiver, String contentType, String content, String status) {
        synchronized (this.lock) {
            int error;
            PostMethod post = new PostMethod(this.protocol.getUrl());
            String response = "";
            Integer httpCode = null;
            try {
                String soapAction = String.valueOf(this.xmlns) + "receiverMO";
                String reqContent =
                        "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">  <soap:Header>    <AuthHeader xmlns=\"" +

                                this.xmlns + "\">" +
                                "      <Username>" + this.username + "</Username>" +
                                "      <Password>" + this.password + "</Password>" +
                                "    </AuthHeader>" +
                                "  </soap:Header>" +
                                "  <soap:Body>" +
                                "    <sendMT xmlns=\"" + this.xmlns + "\">" +
                                "       <SessionId>" + sessionId + "</SessionId>" +
                                "       <ServiceId>" + serviceId + "</ServiceId>" +
                                "       <Sender>" + sender + "</Sender>" +
                                "       <Receiver>" + receiver + "</Receiver>" +
                                "       <ContentType>" + contentType + "</ContentType>" +
                                "       <Content>" + content + "</Content>" +
                                "       <Status>" + status + "</Status>" +
                                "    </sendMT>" +
                                "  </soap:Body>" +
                                "</soap:Envelope>";
                log.debug("send soap message to " + this.protocol.getUrl());
                log.debug("POST " + this.protocol.getServer() + " HTTP/1.1");
                log.debug("Content-Type: text/xml; charset=utf-8");
                log.debug("Connection: Keep-Alive");
                log.debug("Content-Length: " + reqContent.length());
                log.debug("SOAPAction: \"" + soapAction + "\"");
                log.debug("");
                log.debug(content);
                log.debug(reqContent);
                log.debug("");
                StringRequestEntity stringRequestEntity = new StringRequestEntity(reqContent, "text/xml", "UTF-8");
                post.setRequestEntity((RequestEntity)stringRequestEntity);
                post.setRequestHeader("SOAPAction", soapAction);
                log.debug("session " + sessionId + " send request to smsgw " + this.protocol.getUrl());
                httpCode = this.httpclient.executeMethod((HttpMethod)post);
                log.debug("HTTP code: " + httpCode + "session " + sessionId + " receive response from smsgw " + this.protocol.getUrl());
                response = post.getResponseBodyAsString();
                log.debug(response);
                int start = response.indexOf("<sendMTResult>") + "<sendMTResult>".length();
                int end = response.lastIndexOf("</sendMTResult>");
                error = Integer.parseInt(response.substring(start, end));
            } catch (Exception ex) {
                log.error("soap message error " + ex.getMessage(), ex);
                log.error("HTTP code: {}, response content: {}", httpCode, response);
                this.httpclient = new HttpClient();
                error = 1;
            } finally {
                post.releaseConnection();
            }
            return error;
        }
    }

    public int send(String sessionId, String serviceId, String sender, String receiver, String contentType, String content, String status) {
        if (content == null)
            content = "";
        content = Base64.encodeBase64String(content.getBytes());
        try {
            content = URLEncoder.encode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sendMT(sessionId, serviceId, sender, receiver, contentType, content, status);
    }

    public int send(String sessionId, String serviceId, String sender, String receiver, String contentType, byte[] content, String status) {
        if (content == null)
            content = new byte[0];
        String soapContent = Hex.encode(content);
        return sendMT(sessionId, serviceId, sender, receiver, contentType, soapContent, status);
    }

    public static String detectCharsetStr(String str) {
        String result = null;

        for (String charsetName : CHARSETS) {
            Charset charset = detectCharset(str.getBytes(), Charset.forName(charsetName));
            if (charset != null) {
                result = charsetName;
                break;
            }
        }

        return result;
    }

    private static Charset detectCharset(byte[] b, Charset charset) {
        try {
            BufferedInputStream input = new BufferedInputStream(new ByteArrayInputStream(b));

            CharsetDecoder decoder = charset.newDecoder();
            decoder.reset();

            byte[] buffer = new byte[512];
            boolean identified = false;
            while ((input.read(buffer) != -1) && (!identified)) {
                identified = identify(buffer, decoder);
            }

            input.close();

            if (identified) {
                return charset;
            } else {
                return null;
            }

        } catch (Exception e) {
            return null;
        }
    }
    private static boolean identify(byte[] bytes, CharsetDecoder decoder) {
        try {
            decoder.decode(ByteBuffer.wrap(bytes));
        } catch (CharacterCodingException e) {
            return false;
        }
        return true;
    }
}
