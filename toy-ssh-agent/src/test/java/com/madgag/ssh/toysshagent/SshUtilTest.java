package com.madgag.ssh.toysshagent;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.PublicKey;

import net.schmizz.sshj.common.Base64;

import org.junit.Before;
import org.junit.Test;
import org.spongycastle.openssl.PEMReader;

public class SshUtilTest {

	SshUtil sshUtil = new SshUtil();
	KeyPair keyPair;
	
	@Before
	public void setup() throws Exception {
		InputStream privateRsaKeyStream = SshUtil.class.getResourceAsStream("/assets/id_rsa");
		assertThat(privateRsaKeyStream, notNullValue());
		PEMReader r = new PEMReader(new InputStreamReader(privateRsaKeyStream));
		keyPair = (KeyPair) r.readObject();
	}
	
	@Test
	public void shouldEncodeSshKeysCorrectly() throws Exception {
		PublicKey publicKey = keyPair.getPublic();
		byte[] keyEncodedInOpenSshFormat = sshUtil.sshEncode(publicKey);
		String keyCorrectlyEncodedInSshFormat="AAAAB3NzaC1yc2EAAAADAQABAAABAQC8aO4pVPJglaCsmkV4CBY/IIPVSaNDhT6+bj7CgBw9adoZ/xu9tWVMMsW6nTOp4rCf9f5DjEsSgmGJoNd9lQeXILIIAl9PFtc+/RpQ59C1kCj1hDOQu5HNYo3KtWsAX8yGdJ1jweeL8xm0o2RSH0RbCWNz71vnFVxVqpaToXbTe4TBRxvqvkNPlw5P7fIs5c4flXRSLm/379xdM2Z/atat5+IUFtuEje0SCzWjnZ05SG0q4Efg4nWpWfY5VMHhvaeRfY9qsI8R8sWpb0lIp8aEUpaNV0HTTbMa3MlRuKk4g8VwY9OmyYvwyLYYMnpyvCo03H/jcnnlqbfb1wvsY+gT";
		
		assertThat(Base64.encodeBytes(keyEncodedInOpenSshFormat), equalTo(keyCorrectlyEncodedInSshFormat));
	}
	
	@Test
	public void shouldEncodeSignatures() throws Exception {
		byte[] dataToSign = Base64.decode("AAAAFKt3+AmyWTVYzKRV7J40wjc5jMe7MgAAAAAAAAAOc3NoLWNvbm5lY3Rpb24AAAAJcHVibGlja2V5AQAAAAdzc2gtcnNhAAABFwAAAAdzc2gtcnNhAAAAAwEAAQAAAQEAvGjuKVTyYJWgrJpFeAgWPyCD1UmjQ4U+vm4+woAcPWnaGf8bvbVlTDLFup0zqeKwn/X+Q4xLEoJhiaDXfZUHlyCyCAJfTxbXPv0aUOfQtZAo9YQzkLuRzWKNyrVrAF/MhnSdY8Hni/MZtKNkUh9EWwljc+9b5xVcVaqWk6F203uEwUcb6r5DT5cOT+3yLOXOH5V0Ui5v9+/cXTNmf2rWrefiFBbbhI3tEgs1o52dOUhtKuBH4OJ1qVn2OVTB4b2nkX2ParCPEfLFqW9JSKfGhFKWjVdB002zGtzJUbipOIPFcGPTpsmL8Mi2GDJ6crwqNNx/43J55am329cL7GPoEw==");
		byte[] signatureEncodedInOpenSshFormat = sshUtil.sign(dataToSign, keyPair.getPrivate());
		String signatureCorrectlyEncodedInSshFormat="AAAAB3NzaC1yc2EAAAEAH9hwEXcTfYfG8iau0ZefTWPAkMwXwOgr1ZQ2nZpCAZT+lBlFIGfa6dpfux+wo8pWT6nZ9sTFUYmmjYuJrjgwIGo2Zfh6QBBvSu0WDT8vG5l6BKbJTjfpnTYjgpMaBpx8ryh7MnRr6VDcu6JvfmenFtSulPPdIFFrf70448XXzU+x6uOv+6+Bg66wyVSL89UGIZSIaj/1UuW6Nz4sAzmlLCt6Ew36BC9PGO2dE5Skfm06Hjj8F9DK5J+XquitOAoz88QkTUJlMy2CgkD5/Y0MLGYn1qmnGCgGcfEwYe9uUJ7jc1nzyt9hK7arI/Uf2N8zLRUSQUW5uNMt8uYkDp6fjw==";
		assertThat(Base64.encodeBytes(signatureEncodedInOpenSshFormat), equalTo(signatureCorrectlyEncodedInSshFormat));
	}
	
	
	@Test
	public void shouldEncodeRawSignatures() throws Exception {
		byte[] dataToSign = Base64.decode("AAAAFHcEmPBot9evlmZ9aljreU8Hd6/1MgAAAAAAAAAOc3NoLWNvbm5lY3Rpb24AAAAJcHVibGlja2V5AQAAAAdzc2gtcnNhAAABFwAAAAdzc2gtcnNhAAAAAwEAAQAAAQEAvGjuKVTyYJWgrJpFeAgWPyCD1UmjQ4U+vm4+woAcPWnaGf8bvbVlTDLFup0zqeKwn/X+Q4xLEoJhiaDXfZUHlyCyCAJfTxbXPv0aUOfQtZAo9YQzkLuRzWKNyrVrAF/MhnSdY8Hni/MZtKNkUh9EWwljc+9b5xVcVaqWk6F203uEwUcb6r5DT5cOT+3yLOXOH5V0Ui5v9+/cXTNmf2rWrefiFBbbhI3tEgs1o52dOUhtKuBH4OJ1qVn2OVTB4b2nkX2ParCPEfLFqW9JSKfGhFKWjVdB002zGtzJUbipOIPFcGPTpsmL8Mi2GDJ6crwqNNx/43J55am329cL7GPoEw==");
		String correctRawSignature="UX+wi4NXP+SdWzeZUrwjHZPpAnfdn4Hl2mT+8r/HC9J/vWbUBvqHxHZMas5YRetEAoTajZhEohBIXVJa528gAOFZpT8ItpjBxDHfeu/ArWZ7NwtS0QuJ7qTeo16bhMGkI7OeqKngSGsZdMGORwnVSuQF2mzrztkvT+4/d30lRw34/v35eeLdXffUoiKlszEPr1zLJvziQzqPdfuQtabY4HB8nVQct9lBSNjAJCcFP8Dl+S/g3++bWG8mozq/x4xtNdyXBh009bXHlt72Fxk/o56rPBKprE5+dA+dwwCbkOzfsjTX4rAz4nt6paJrvrag20uVCpXLQf9gBy1pu4grng==";
		byte[] rawSignature = sshUtil.rawSignatureFor(dataToSign, keyPair.getPrivate());
		assertThat(Base64.encodeBytes(rawSignature), equalTo(correctRawSignature));
	}
}
