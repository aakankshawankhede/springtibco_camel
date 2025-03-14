package com.example.config;

import javax.jms.ConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jms.JmsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jndi.JndiTemplate;

import java.util.Properties;

@Configuration
public class TibcoJmsConfig {

    @Bean(name = "jndiTemplate")
    public JndiTemplate jndiTemplate() {
        Properties env = new Properties();
        env.setProperty("java.naming.factory.initial", "com.tibco.tibjms.naming.TibjmsInitialContextFactory");
        env.setProperty("java.naming.provider.url", "tcp://localhost:7222");
        env.setProperty("java.naming.security.principal", "your-username");
        env.setProperty("java.naming.security.credentials", "your-password");
        return new JndiTemplate(env);
    }

 @Bean(name = "tibcoConnectionFactory")
public ConnectionFactory tibcoConnectionFactory(JndiTemplate jndiTemplate) throws Exception {
    InitialContext ctx = new InitialContext(jndiTemplate.getEnvironment());
    
    // Print all available JNDI names
    NamingEnumeration<NameClassPair> list = ctx.list("");
    while (list.hasMore()) {
        NameClassPair nc = list.next();
        System.out.println("JNDI Entry: " + nc.getName() + " -> " + nc.getClassName());
    }
    
    return (ConnectionFactory) ctx.lookup("ConnectionFactory");  // Update this after finding the correct name
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
