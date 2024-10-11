package the_monitor.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.common.BaseTimeEntity;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "user")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_email", nullable = false)
    private String email;

    @Column(name = "user_password", nullable = false)
    private String password;

    @Column(name = "user_company_name")
    private String companyName;

    @Column(name = "user_manager_name")
    private String managerName;

    @Column(name = "user_manager_phone")
    private String managerPhone;

    @Column(name = "user_agreement")
    private boolean agreement;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Builder
    public User(String email, String password, String companyName, String managerName, String managerPhone, boolean agreement, boolean emailVerified) {

        this.email = email;
        this.password = password;
        this.companyName = companyName;
        this.managerName = managerName;
        this.managerPhone = managerPhone;
        this.agreement = agreement;
        this.emailVerified = emailVerified;

    }

}
