import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;

import javax.net.ssl.X509TrustManager;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import sun.security.validator.ValidatorException;

/**
 * This Trust Manager is "naive" because it trusts everyone.
 **/
public class SelfTrustManager implements X509TrustManager {

	private X509TrustManager externalTrustManager;
	
	private static String _(String text) { 
		return Translations.get(text);
	}

	private static String _(String key, Object insert) {
		return Translations.get(key, insert);
	}

	public SelfTrustManager(X509TrustManager externalTrustManager) {
		if (externalTrustManager == null) throw new IllegalArgumentException(_("No X509-Trust Manager found."));
		this.externalTrustManager = externalTrustManager;
	}

	/**
	 * Doesn't throw an exception, so this is how it approves a certificate.
	 * 
	 * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], String)
	 **/
	public void checkClientTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
		try {
			System.out.print("checkClientTrusted(");
			externalTrustManager.checkClientTrusted(certificates, authType);
		} catch (ValidatorException ve) {
			if (certificates == null || certificates.length == 0) throw new IllegalArgumentException(_("Empty certificate chain supplied!"));
			if (authType == null || authType.isEmpty()) throw new IllegalArgumentException(_("No authType given!"));
			for (X509Certificate certificate : certificates) {
				certificate.checkValidity();
				int decision=JOptionPane.showConfirmDialog(null, formatCert(certificate), getCertPart(certificate,"CN"), JOptionPane.YES_NO_OPTION);
				if (decision==JOptionPane.YES_OPTION){
					System.out.println("trust!");		
				} else {
					throw ve;
				}
			}
			System.exit(-1);
		}
	}
	
	String getCertPart(X509Certificate certificate, String key){
		if (key==null) System.out.println(certificate.getIssuerX500Principal());
		String[] parts = certificate.getIssuerX500Principal().toString().split(", ");
		for (String part:parts){
			String[] map = part.split("=");
			if (map[0].equals(key)) return map[1];
		}
		return "";
	}

	/**
	 * Doesn't throw an exception, so this is how it approves a certificate.
	 * 
	 * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], String)
	 **/
	public void checkServerTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
		try {
			externalTrustManager.checkServerTrusted(certificates, authType);
		} catch (ValidatorException ve) {
			if (certificates == null || certificates.length == 0) throw new IllegalArgumentException("Empty certificate chain supplied!");
			if (authType == null || authType.isEmpty()) throw new IllegalArgumentException("No authType given!");
			for (X509Certificate certificate : certificates) {
				certificate.checkValidity();
				int decision=JOptionPane.showConfirmDialog(null, formatCert(certificate), getCertPart(certificate,"CN"), JOptionPane.YES_NO_OPTION);
				if (decision==JOptionPane.YES_OPTION){
					System.out.println("trust!");		
				} else {
					throw ve;
				}
			}			
			System.exit(-1);
		}
	}

	private VerticalPanel formatCert(X509Certificate certificate) {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");           
		VerticalPanel result=new VerticalPanel();
		result.add(new JLabel(_("This website uses a certificate, which is not in your list of trusted certificates!")));
		VerticalPanel info=new VerticalPanel(_("Certificate Information"));
		info.add(new JLabel(_("Common Name:")+" "+getCertPart(certificate, "CN")));
		info.add(new JLabel(_("Organization:")+" "+getCertPart(certificate, "O")));
		info.add(new JLabel(_("Organizational Unit:")+" "+getCertPart(certificate, "OU")));
		info.add(new JLabel(_("Location:")+" "+getCertPart(certificate, "L")));
		info.add(new JLabel(_("State:")+" "+getCertPart(certificate, "ST")));
		info.add(new JLabel(_("Country:")+" "+getCertPart(certificate, "C")));
		info.add(new JLabel(_("Email:")+" "+getCertPart(certificate, "EMAILADDRESS")));
		String sn=certificate.getSerialNumber().toString(16).toUpperCase().replaceAll("(.{2})(?!$)", "$1:"); // hex form
		info.add(new JLabel(_("Serial Number:")+" "+sn));
		info.add(new JLabel(_("Validity period:")+" "+df.format(certificate.getNotBefore())+" - "+df.format(certificate.getNotAfter())));
		info.scale();
		result.add(info);
		result.add(new JLabel(_("Do you trust this certificate/website?")));
		result.scale();
		return result;
	}

	/**
	 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
	 **/
	public X509Certificate[] getAcceptedIssuers() {
		return externalTrustManager.getAcceptedIssuers();
	}
}