package com.flickzz.desk.security;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TwoFactorAuthenticationService_old {


//	<dependency>
//		<groupId>dev.samstevens.totp</groupId>
//		<artifactId>totp</artifactId>
//		<version>1.7.1</version>
//	</dependency>
//	public String generateNewSecret() {
////        return new DefaultSecretGenerator().generate();
//		byte[] buffer = new byte[10];
//	    new SecureRandom().nextBytes(buffer);
//	    return new Base32().encodeToString(buffer);
//    }
//	
//	public void generateQrCode(String uri, String filePath) throws Exception {
//	    QRCodeWriter qrCodeWriter = new QRCodeWriter();
//	    BitMatrix bitMatrix = qrCodeWriter.encode(uri, BarcodeFormat.QR_CODE, 300, 300);
//
//	    Path path = FileSystems.getDefault().getPath(filePath);
//	    MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
//	}
//	
//	public String generateQrCodeImageUri(String secret, String userName) throws Exception {
//		
//		String uri = String.format(
//			    "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=6&period=30",
//			    "FlickzzDesk", userName, secret, "FlickzzDesk"
//			);
//	    QRCodeWriter qrCodeWriter = new QRCodeWriter();
//	    BitMatrix bitMatrix = qrCodeWriter.encode(uri, BarcodeFormat.QR_CODE, 300, 300);
//
//	    ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
//	    MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
//	    String base64 = Base64.getEncoder().encodeToString(pngOutputStream.toByteArray());
//
//	    return "data:image/png;base64," + base64;
//	}
//
//	
////	 public String generateQrCodeImageUri(String secret, String userName) {
////		 
////		 String data = String.format(
////				    "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=6&period=30",
////				    "FlickzzDesk", userName, secret, "FlickzzDesk"
////				);
//////		 generateQrCode(uri, "qrcode.png");
////
//////	        QrData data = new QrData.Builder()
//////	                .label("FlickzzDesk:"+userName)
//////	                .secret(secret)
//////	                .issuer("FlickzzDesk")
//////	                .algorithm(HashingAlgorithm.SHA1)
//////	                .digits(6)
//////	                .period(30)
//////	                .build();
//////
////	        QrGenerator generator = new ZxingPngQrGenerator();
////	        byte[] imageData = new byte[0];
////	        try {
////	            imageData = generator.generate(data);
////	        } catch (QrGenerationException e) {
////	            e.printStackTrace();
////	            log.error("Error while generating QR-CODE");
////	        }
////
////	        return getDataUriForImage(imageData, generator.getImageMimeType());
////	    }
//
//	    public boolean isOtpValid(String secret, String code) {
//	        TimeProvider timeProvider = new SystemTimeProvider();
//	        CodeGenerator codeGenerator = new DefaultCodeGenerator();
//	        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
//	        return verifier.isValidCode(secret, code);
//	    }
//
//	    public boolean isOtpNotValid(String secret, String code) {
//	        return !this.isOtpValid(secret, code);
//	    }
}
