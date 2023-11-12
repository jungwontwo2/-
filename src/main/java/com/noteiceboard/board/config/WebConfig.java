package com.noteiceboard.board.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration//설정정보라는 뜻
public class WebConfig implements WebMvcConfigurer {
    private String resourcePath="/upload/**";//view에서 접근할 경로
    private String savePath="file:///C:/notice_board/";//실제 파일 저장 경로
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        //sourcePath와 savePath를 설정하는 모습
        registry.addResourceHandler(resourcePath).addResourceLocations(savePath);
    }
}
