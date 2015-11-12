package oidc.saml;

import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.ISOChronology;
import org.junit.Test;

import static org.junit.Assert.*;

public class KeyStoreLocatorTest {

  @Test
  public void testCreateKeyStore() throws Exception {
    String digest = new String(DigestUtils.md5Hex("https://test.sp"));
    System.out.println(digest);

    DateTime time = new DateTime("2015-11-12T15:40:43Z", ISOChronology.getInstanceUTC());

    System.out.println(time.toDate());
    Thread.sleep(25);
    long reference = System.currentTimeMillis();
    //DateTime reference = DateTime.now(time.getZone());//System.currentTimeMillis();
//    boolean before = time.isBefore(reference.plus(60 * 1000));
    boolean before = time.isBefore(reference + (60 * 1000));
    System.out.println(before);

  }
}