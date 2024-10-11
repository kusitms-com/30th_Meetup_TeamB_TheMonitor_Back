package the_monitor.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.common.BaseTimeEntity;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "accounts")
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column(name = "account_email", nullable = false)
    private String email;

    @Column(name = "account_password", nullable = false)
    private String password;

    @Column(name = "account_company_name")
    private String companyName;

    @Column(name = "account_manager_name")
    private String managerName;

    @Column(name = "account_manager_phone")
    private String managerPhone;

    @Column(name = "account_agreement")
    private boolean agreement;

    @Column(name = "account_email_verified", nullable = false)
    private boolean emailVerified = false;

    @Builder
    public Account(String email, String password, String companyName, String managerName, String managerPhone, boolean agreement, boolean emailVerified) {

        this.email = email;
        this.password = password;
        this.companyName = companyName;
        this.managerName = managerName;
        this.managerPhone = managerPhone;
        this.agreement = agreement;
        this.emailVerified = emailVerified;

    }

}
