package com.itwill.teamfourmen.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Builder;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {

	private String region;

	@Bean
	public AmazonS3Client  amazonS3Client() {

		return (AmazonS3Client) AmazonS3ClientBuilder
				.standard()
				.withRegion(region)
				.build();

	}

}
