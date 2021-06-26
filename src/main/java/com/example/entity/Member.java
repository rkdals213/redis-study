package com.example.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "team_id")
    Team team;

    public Member() {
    }

    public Member(String name, Team team) {
        this.name = name;
        this.team = team;
    }
}
