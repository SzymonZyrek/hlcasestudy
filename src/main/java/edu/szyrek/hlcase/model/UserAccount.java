package edu.szyrek.hlcase.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.Date;


@Entity
@NamedQuery(name = "UserAccounts.findAll",
        query = "SELECT c FROM UserAccount c ORDER BY c.id")
public class UserAccount {
    @Id
    @SequenceGenerator(
            name = "userAccountSequence",
            sequenceName = "userAccountId_seq",
            allocationSize = 1,
            initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userAccountSequence")
    private Long id;
    @Column(length = 40, unique = true)
    @NotNull
    @Pattern(regexp = "[a-zA-Z0-9]*")
    private String username;
    @Column
    @NotNull
    @Min(value = 1)
    @Max(value = 200)
    private Long age;
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column
    private Gender gender;
    @Column
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

    public void setUsername(String name) {
        this.username = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
}