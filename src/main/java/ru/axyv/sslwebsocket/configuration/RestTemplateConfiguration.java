package ru.axyv.sslwebsocket.configuration;

import ch.qos.logback.core.net.ssl.KeyStoreFactoryBean;
import lombok.SneakyThrows;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;

@Configuration
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate getRestTemplateForHTTPS(SSLContext sslContext) {

        SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
//                new DefaultHostnameVerifier());

//        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(new AuthScope("localhost", 443), new UsernamePasswordCredentials("user", "pass"));

        HttpClient httpClient = HttpClientBuilder.create()
                .setSSLSocketFactory(connectionFactory)
//                .setDefaultCredentialsProvider(credentialsProvider)
                .build();

        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(httpRequestFactory);
    }

    @Bean
    @SneakyThrows
    public SSLContext sslContext() {
        KeyStoreFactoryBean keyStore = new KeyStoreFactoryBean();
        keyStore.setLocation("jks/server_keystore.jks");
        keyStore.setPassword("12345678");

        KeyStoreFactoryBean trustStore = new KeyStoreFactoryBean();
        trustStore.setLocation("jks/client_truststore.jks");
        trustStore.setPassword("12345678");

        return SSLContexts.custom()
                .loadKeyMaterial(
                        keyStore.createKeyStore(), "12345678".toCharArray())
                .loadTrustMaterial(
                        trustStore.createKeyStore(), //null
                        null)//new TrustAllStrategy()
                .build();
    }
    /*
    openssl pkcs12 -export -in client-cert.pem -inkey private/client-key.pem -certfile cacert.pem -name "Client" -out client-cert.p12


    openssl pkcs12 -export -in /etc/apache2/ssl/server-cert.pem -inkey /etc/apache2/ssl/private/server-key.pem -certfile /etc/apache2/ssl/cacert.pem -name "Server" -out server-cert.p12
    keytool -importkeystore -deststorepass 12345678 -destkeypass 12345678 -destkeystore server_keystore.jks -srckeystore server-cert.p12 -srcstoretype PKCS12 -srcstorepass 12345678 -alias server
     */
}
