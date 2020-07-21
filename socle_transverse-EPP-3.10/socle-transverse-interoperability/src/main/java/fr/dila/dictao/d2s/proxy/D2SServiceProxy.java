package fr.dila.dictao.d2s.proxy;

import java.net.URL;

import javax.xml.namespace.QName;

import fr.dila.dictao.d2s.api.D2SService;
import fr.sword.wsdl.dictao.d2s.D2S;
import fr.sword.wsdl.dictao.d2s.D2SSoap;
import fr.sword.wsdl.dictao.d2s.GetArchiveEx;
import fr.sword.wsdl.dictao.d2s.GetArchiveExResponse;
import fr.sword.wsdl.dictao.d2s.PrepareSignatureEx;
import fr.sword.wsdl.dictao.d2s.PrepareSignatureExResponse;
import fr.sword.wsdl.dictao.d2s.SignatureEx;
import fr.sword.wsdl.dictao.d2s.SignatureExResponse;

public class D2SServiceProxy extends D2S implements D2SService {

	private static final String	targetNamespace	= "http://www.dictao.com/D2S/Interface";
	private static final String	serviceName		= "D2S";
	private static final String	portName		= "D2SSoap";

	public D2SServiceProxy(URL wsdlUrl) {
		super(wsdlUrl, new QName(targetNamespace, serviceName));
	}

	public D2SSoap getPort() {
		return super.getPort(new QName(targetNamespace, portName), D2SSoap.class);
	}

	@Override
	public SignatureExResponse signatureEx(SignatureEx parameters) {
		return this.getPort().signatureEx(parameters);
	}

	@Override
	public GetArchiveExResponse getArchiveEx(GetArchiveEx parameters) {
		return getPort().getArchiveEx(parameters);
	}

	@Override
	public PrepareSignatureExResponse prepareSignatureEx(PrepareSignatureEx parameters) {
		return getPort().prepareSignatureEx(parameters);
	}

}
