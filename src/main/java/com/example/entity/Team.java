package com.example.entity;

import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;

    @OneToMany(mappedBy = "team")
    List<Member> members = new ArrayList<>();

    public Team() {
    }

    public Team(String label) {
        this.label = label;
    }
}
