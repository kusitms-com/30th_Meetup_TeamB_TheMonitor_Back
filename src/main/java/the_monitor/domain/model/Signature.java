package the_monitor.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.common.BaseTimeEntity;

@Entity
@Getter
@Table(name = "signatures")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Signature extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "signature_id")
    private Long id;

    @Column(name = "signature_url",  nullable = true)
    private String signatureUrl;

    @OneToOne
    @JoinColumn(name = "client_id", nullable = false, unique = true)
    private Client client;

    @Builder
    public Signature(String signatureUrl, Client client) {
        this.signatureUrl = signatureUrl;
        this.client = client;
    }

    public void updateImageUrl(String newImageUrl) {
        this.signatureUrl = newImageUrl;
    }
}