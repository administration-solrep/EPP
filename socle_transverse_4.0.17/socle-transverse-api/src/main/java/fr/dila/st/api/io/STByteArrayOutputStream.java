package fr.dila.st.api.io;

import java.io.ByteArrayOutputStream;

public abstract class STByteArrayOutputStream extends ByteArrayOutputStream {

	public abstract String getSHA512Hash();
}
