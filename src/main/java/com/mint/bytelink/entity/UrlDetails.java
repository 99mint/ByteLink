package com.mint.bytelink.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "urlDetails" , cascade = CascadeType.ALL)
    List<Click> clicks = new ArrayList<>();

    private String longUrl;

    @Column(unique = true)
    private String shortUrl;

    private LocalDateTime createdAt;

    private LocalDateTime activeTill;

    private Long clickCounts = 0L;
}
