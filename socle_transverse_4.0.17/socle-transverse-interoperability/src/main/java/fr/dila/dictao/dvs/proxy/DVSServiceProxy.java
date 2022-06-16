package fr.dila.dictao.dvs.proxy;

import java.net.URL;

import javax.xml.namespace.QName;

import fr.dila.dictao.dvs.api.DVSService;
import fr.sword.wsdl.dictao.dvs.CustomizeTokenEx;
import fr.sword.wsdl.dictao.dvs.CustomizeTokenExResponse;
import fr.sword.wsdl.dictao.dvs.DVS;
import fr.sword.wsdl.dictao.dvs.DVSSoap;
import fr.sword.wsdl.dictao.dvs.GetArchiveEx;
import fr.sword.wsdl.dictao.dvs.GetArchiveExResponse;
import fr.sword.wsdl.dictao.dvs.GetAuthenticationChallengeEx;
import fr.sword.wsdl.dictao.dvs.GetAuthenticationChallengeExResponse;
import fr.sword.wsdl.dictao.dvs.PrepareAuthenticationRequestEx;
import fr.sword.wsdl.dictao.dvs.PrepareAuthenticationRequestExResponse;
import fr.sword.wsdl.dictao.dvs.VerifyAuthenticationEx;
import fr.sword.wsdl.dictao.dvs.VerifyAuthenticationExResponse;
import fr.sword.wsdl.dictao.dvs.VerifyCertificateEx;
import fr.sword.wsdl.dictao.dvs.VerifyCertificateExResponse;
import fr.sword.wsdl.dictao.dvs.VerifySignatureEx;
import fr.sword.wsdl.dictao.dvs.VerifySignatureExResponse;

public class DVSServiceProxy extends DVS implements DVSService {

	private static final String	targetNamespace	= "http://www.dictao.com/DVS/Interface";
	private static final String	serviceName		= "DVS";
	private static final String	portName		= "DVSSoap";

	public DVSServiceProxy(URL wsdlUrl) {
		super(wsdlUrl, new QName(targetNamespace, serviceName));
	}

	private DVSSoap getPort() {
		return super.getPort(new QName(targetNamespace, portName), DVSSoap.class);
	}

	@Override
	public VerifySignatureExResponse verifySignatureEx(VerifySignatureEx parameters) {
		return getPort().verifySignatureEx(parameters);
	}

	@Override
	public VerifyCertificateExResponse verifyCertificateEx(VerifyCertificateEx parameters) {
		return getPort().verifyCertificateEx(parameters);
	}

	@Override
	public VerifyAuthenticationExResponse verifyAuthenticationEx(VerifyAuthenticationEx parameters) {
		return getPort().verifyAuthenticationEx(parameters);
	}

	@Override
	public GetAuthenticationChallengeExResponse getAuthenticationChallengeEx(GetAuthenticationChallengeEx parameters) {
		return getPort().getAuthenticationChallengeEx(parameters);
	}

	@Override
	public GetArchiveExResponse getArchiveEx(GetArchiveEx parameters) {
		return getPort().getArchiveEx(parameters);
	}

	@Override
	public PrepareAuthenticationRequestExResponse prepareAuthenticationRequestEx(
			PrepareAuthenticationRequestEx parameters) {
		return getPort().prepareAuthenticationRequestEx(parameters);
	}

	@Override
	public CustomizeTokenExResponse customizeTokenEx(CustomizeTokenEx customizeTokenEx) {
		return getPort().customizeTokenEx(customizeTokenEx);
	}

}
