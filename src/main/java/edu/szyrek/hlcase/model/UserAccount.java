package edu.szyrek.hlcase.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.Date;

@Entity
@Table(name = "user_account")
public class UserAccount {

    @Id
    @SequenceGenerator(
            name = "userAccountSequence",
            sequenceName = "user_account_id_seq",
            allocationSize = 1,
            initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userAccountSequence")
    private Long id;

    @Column(length = 40, unique = true)
    @NotNull
    @Pattern(regexp = "[a-zA-Z0-9]*")
    private String username;

    @NotNull
    @Min(1)
    @Max(200)
    private Long age;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Gender gender;

    private Date creationTime;

    @PrePersist
    void createdAt() {
        this.creationTime = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
}
