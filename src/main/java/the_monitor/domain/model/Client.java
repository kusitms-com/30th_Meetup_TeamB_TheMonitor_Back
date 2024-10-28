package the_monitor.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.common.BaseTimeEntity;

import java.util.List;

@Entity
@Getter
@Table(name = "clients")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Client extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long id;

    @Column(name = "client_name", nullable = false)
    private String name;

    @Column(name = "client_manager_name", nullable = false)
    private String managerName;

    @Column(name = "client_logo", nullable = false)
    private String logo;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClientMailRecipient> clientMailRecipients;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClientMailCC> clientMailCCs;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Category> categories;

    @Builder
    public Client(String name,
                  String managerName,
                  String logo,
                  Account account) {

        this.name = name;
        this.managerName = managerName;
        this.logo = logo;
        this.account = account;

    }

}
