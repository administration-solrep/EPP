package fr.dila.st.core.io;

import fr.dila.st.api.io.STByteArrayOutputStream;
import fr.dila.st.core.util.SHA512Util;

public class STByteArrayOutputStreamImpl extends STByteArrayOutputStream {

	@Override
	public String getSHA512Hash() {
		return SHA512Util.getSHA512Hash(buf, 0, count);
	}
}
