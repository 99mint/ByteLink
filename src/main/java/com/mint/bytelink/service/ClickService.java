package com.mint.bytelink.service;

import com.mint.bytelink.entity.Click;
import com.mint.bytelink.entity.UrlDetails;
import com.mint.bytelink.exception.ResourceNotFoundException;
import com.mint.bytelink.repository.ClickRepository;
import com.mint.bytelink.repository.UrlDetailsRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ClickService {
    private final ClickRepository clickRepository;
    public ClickService(ClickRepository clickRepository , UrlDetailsRepository urlDetailsRepository){
        this.clickRepository = clickRepository;
    }

    public void recordClick(UrlDetails urlDetails , HttpServletRequest request){
        Click click = new Click();
        click.setClickedAt(LocalDateTime.now());
        click.setUserAgent(request.getHeader("User-Agent"));
        click.setUrlDetails(urlDetails);

        clickRepository.save(click);
    }
}
