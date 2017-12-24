package com.zooplus.currency_converter;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;

@SpringBootApplication
@EnableCaching
@EnableCircuitBreaker
public class Application extends WebMvcConfigurerAdapter {

	public static void main(String[] args) throws Exception {
		new SpringApplicationBuilder(Application.class).run(args);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("login");
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		// Do any additional configuration here
		return builder.build();
	}

	@Configuration
	protected static class CachingConfig {

		@Value("${cache.current_rates.ttl}")
		private int currentRatesTTL;

		@Value("${cache.historical_rates.ttl}")
		private int historicalRatesTTL;

		@Bean
		public CacheManager cacheManager(Ticker ticker) {
			CaffeineCache currentRates = buildCache("currentRates", ticker, currentRatesTTL, 1000);
			CaffeineCache historicalRates = buildCache("historicalRates", ticker, historicalRatesTTL, 10000);
			SimpleCacheManager manager = new SimpleCacheManager();
			manager.setCaches(Arrays.asList(currentRates, historicalRates));
			return manager;
		}

		private CaffeineCache buildCache(String name, Ticker ticker, int minutesToExpire, int maximumSize) {
			return new CaffeineCache(name, Caffeine.newBuilder()
					.expireAfterWrite(minutesToExpire, TimeUnit.MINUTES)
					.maximumSize(100)
					.ticker(ticker)
					.build());
		}

		@Bean
		public Ticker ticker() {
			return Ticker.systemTicker();
		}

	}

	@Configuration
	protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

		@Autowired
		private UserDetailsService userDetailsService;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests()
					.antMatchers("/css/**").permitAll()
			        .antMatchers("/register").permitAll()
					.anyRequest().fullyAuthenticated()
					.and().formLogin().loginPage("/login")
					.failureUrl("/login?error").permitAll()
					.and().logout().permitAll();
		}

		@Autowired
		public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(userDetailsService);
		}

	}

}
