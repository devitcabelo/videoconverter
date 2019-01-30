package br.com.videoconverter.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AmazonConfiguration {
	private static final String ACCESS_KEY="AKIAIWOH2ZF2PT6HB4SA";
	private static final String SECRET_KEY="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";
	private static final String REGION="us-east-1";
	
	@Bean
	public BasicAWSCredentials basicAWSCredentials() {
		return new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
	}
	
	@Bean
	public AmazonS3 amazonS3() {
		return AmazonS3ClientBuilder.standard().withRegion(REGION).withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials())).build();
	}
}