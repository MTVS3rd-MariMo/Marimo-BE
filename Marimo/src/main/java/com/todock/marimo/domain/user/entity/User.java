package com.todock.marimo.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;



@Entity
@Table(name="tbl_user")
public class User {

    @Id
    private Long user_id;
}
