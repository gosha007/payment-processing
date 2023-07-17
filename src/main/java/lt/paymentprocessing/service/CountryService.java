package lt.paymentprocessing.service;

public interface CountryService {
    String resolveClientCountryByIP(String ip);
}