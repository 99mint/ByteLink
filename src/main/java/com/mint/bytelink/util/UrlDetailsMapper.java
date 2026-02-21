package com.mint.bytelink.util;

import com.mint.bytelink.dto.UrlDetailsRequestDTO;
import com.mint.bytelink.dto.UrlDetailsResponseDTO;
import com.mint.bytelink.entity.UrlDetails;
import com.mint.bytelink.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UrlDetailsMapper {
    public UrlDetails toUrlDetailsEntity(UrlDetailsRequestDTO urlDetailsRequestDTO){
        UrlDetails urlDetails = new UrlDetails();
        urlDetails.setLongUrl(urlDetailsRequestDTO.getLongUrl());
        return urlDetails;
    }

    public UrlDetailsResponseDTO toResponseDto(UrlDetails urlDetails){
        UrlDetailsResponseDTO urlDetailsResponseDTO = new UrlDetailsResponseDTO();

        urlDetailsResponseDTO.setShortUrl(urlDetails.getShortUrl());
        urlDetailsResponseDTO.setLongUrl(urlDetails.getLongUrl());
        urlDetailsResponseDTO.setCreatedAt(urlDetails.getCreatedAt());
        urlDetailsResponseDTO.setExpiration(urlDetails.getActiveTill());

        return urlDetailsResponseDTO;
    }

    public List<UrlDetailsResponseDTO> toResponseDtoList(List<UrlDetails> urlDetails){
        if (urlDetails == null) throw new ResourceNotFoundException("No such Url found");
        return urlDetails.stream().map(this::toResponseDto).collect(Collectors.toList());
    }
}
