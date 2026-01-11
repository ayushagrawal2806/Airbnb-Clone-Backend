package com.airbnb.AirbnbClone.entity;

import com.airbnb.AirbnbClone.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private Integer age;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
