package dev.fringe.eip;

import java.util.Properties;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizationAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import lombok.Data;

@EnableAutoConfiguration(exclude = { TransactionManagerCustomizationAutoConfiguration.class,
		TaskSchedulingAutoConfiguration.class })
@Data
public class App implements InitializingBean {

	private final CamelContext camelContext;

	public static void main(String[] args) throws InterruptedException {
		if (args.length == 0) {
			System.out.println("첫번째 인자는 파일이 있는 경로명 입니다");
			return;
		}
		System.setProperty("from", args[0]);
		if (args.length == 1) {
			System.out.println("두번째 인자는 파일이 복제되 경로명 입니다");
			return;
		}
		System.setProperty("to", args[1]);
		new AnnotationConfigApplicationContext(App.class);
		while (true) {
		}
	}

	public void afterPropertiesSet() throws Exception {
		PropertiesComponent component = new PropertiesComponent();
		Properties p = new Properties();
		p.setProperty("from", System.getProperty("from"));
		p.setProperty("to", System.getProperty("to"));
		component.setLocalProperties(p);
		camelContext.setPropertiesComponent(component);
		camelContext.addRoutes(new RouteBuilder() {
			public void configure() throws Exception {
				from("file:{{from}}?noop=true&recursive=true").to("file:{{to}}");
			}
		});
		camelContext.start();
	}
}