package com.example.config;

import javax.jms.ConnectionFactory;
import javax.jms.QueueConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jms.JmsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;

@Configuration
public class TibcoJmsConfig {

    @Bean(name = "tibcoConnectionFactory")
    public ConnectionFactory tibcoConnectionFactory() throws Exception {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.tibco.tibjms.naming.TibjmsInitialContextFactory");
        env.put(Context.PROVIDER_URL, "tcp://localhost:7222");
        env.put(Context.SECURITY_PRINCIPAL, "your-username");
        env.put(Context.SECURITY_CREDENTIALS, "your-password");
        
        InitialContext ctx = new InitialContext(env);
        return (ConnectionFactory) ctx.lookup("ConnectionFactory");
    }

    @Bean(name = "cachingConnectionFactory")
    public CachingConnectionFactory cachingConnectionFactory(ConnectionFactory tibcoConnectionFactory) {
        return new CachingConnectionFactory(tibcoConnectionFactory);
    }

    @Bean(name = "tibcoJmsConfiguration")
    public JmsConfiguration jmsConfiguration(CachingConnectionFactory cachingConnectionFactory) {
        JmsConfiguration jmsConfiguration = new JmsConfiguration();
        jmsConfiguration.setConnectionFactory(cachingConnectionFactory);
        jmsConfiguration.setTransacted(false);
        jmsConfiguration.setAcknowledgementModeName("AUTO_ACKNOWLEDGE");
        return jmsConfiguration;
    }

    @Bean(name = "tibcoJmsComponent")
    public JmsComponent tibcoJmsComponent(JmsConfiguration jmsConfiguration) {
        return JmsComponent.jmsComponent(jmsConfiguration);
    }
}
