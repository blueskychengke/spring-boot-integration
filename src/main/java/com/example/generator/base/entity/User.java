package com.example.generator.base.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.example.generator.base.XbootBaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Czs
 */
@Data
@Entity
@Table(name = "t_user")
@TableName("t_user")
public class User extends XbootBaseEntity {

    private static final long serialVersionUID = 1L;
    // public String email;
    // public String mobile;
    // public String nick_name;
    // public String password;
    // public String username;
    // public String department_id;
    // public String street;
    // public String pass_strength;
    // public Integer sex;
    // public Integer status;
    @Column(name ="address")
    private String address;
    @Column(name ="avatar")
    private String avatar;
    @Column(name ="description")
    private String description;
    @Column(name ="email")
    private String email;
    @Column(name ="mobile")
    private String mobile;
    @Column(name ="nick_name")
    private String nickName;
    @Column(name ="password")
    private String password;
    @Column(name ="sex")
    private int sex;
    @Column(name ="status")
    private int status;
    @Column(name ="type")
    private int type;
    @Column(name ="username")
    private String username;
    @Column(name ="department_id")
    private String departmentId;
    @Column(name ="street")
    private String street;
    @Column(name ="pass_strength")
    private String passStrength;

}
