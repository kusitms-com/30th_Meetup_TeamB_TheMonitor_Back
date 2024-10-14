package the_monitor.application.service;

public interface CertifiedKeyService {

    public String generateCertifiedKey();

    public void saveCertifiedKey(String email, String key);

    public String getCertifiedKey(String email);

    public void deleteCertifiedKey(String email);

    public boolean isCertifiedKeyExpired(String email);

}
