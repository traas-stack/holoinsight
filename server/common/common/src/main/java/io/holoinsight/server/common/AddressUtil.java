/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * <p>
 * AddressUtil class.
 * </p>
 *
 * @version 1.0: AddressUtil.java, v 0.1 2022年02月22日 7:25 下午 jinsong.yjs Exp $
 * @author xzchaoo
 */
@Slf4j
public class AddressUtil {

  /** Constant <code>myIp="UUID.randomUUID().toString()"</code> */
  public static String myIp = UUID.randomUUID().toString();

  private static String localHostIPV4 = null;
  private static String localHostIPV6 = null;
  private static String localHostName = null;
  private static Set<String> localHostIPV4Lists = new HashSet<>();
  private static Set<String> localHostIPV6Lists = new HashSet<>();

  static {
    InetAddress addr = null;
    try {
      addr = InetAddress.getLocalHost();
      myIp = addr.getHostAddress();// 获得本机IP
    } catch (UnknownHostException e) {
      log.error("get local ip error, Critical error", e);
    }
    log.info("get myIp is :" + myIp);
  }

  static {
    try {
      // 多块网卡时获取
      Enumeration<NetworkInterface> interfaces = null;
      interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        NetworkInterface ni = interfaces.nextElement();
        Enumeration<InetAddress> addresss = ni.getInetAddresses();
        while (addresss.hasMoreElements()) {
          InetAddress nextElement = addresss.nextElement();
          String hostAddress = nextElement.getHostAddress();
          boolean isLoopAddress = nextElement.isLoopbackAddress();
          boolean isIPV4 = InetAddressValidator.getInstance().isValidInet4Address(hostAddress);
          boolean isIPV6 = InetAddressValidator.getInstance().isValidInet6Address(hostAddress);

          if (isIPV4 && !isIPV6) {
            localHostIPV4Lists.add(hostAddress);
            if (!isLoopAddress) {
              localHostIPV4 = hostAddress;
              localHostName = nextElement.getHostName();
            }
          }

          if (isIPV6 && !isIPV4) {
            localHostIPV6Lists.add(hostAddress);
            if (!isLoopAddress) {
              localHostIPV6 = hostAddress;
              // localHostName = nextElement.getHostName();
            }
          }
        }
      }
    } catch (Exception e) {
      log.error("Get local ips error, Critical error", e);
    }
  }

  /**
   * 优先获取非loop的IPV4地址，如果地址为空就获取默认的IP地址
   */
  public static String getHostAddress() {
    return StringUtils.isNotBlank(localHostIPV4) ? localHostIPV4 : myIp;
  }

  /**
   * 本机非Loop的IPV4地址
   */
  public static String getLocalHostIPV4() {
    return localHostIPV4;
  }

  /**
   * 本机主机名
   */
  public static String getLocalHostName() {
    if (StringUtils.isNotBlank(localHostName)) {
      return localHostName;
    }
    if (StringUtils.isBlank(localHostName) && StringUtils.isNotBlank(localHostIPV4)) {
      return localHostIPV4;
    }
    if (StringUtils.isBlank(localHostName) && StringUtils.isNotBlank(localHostIPV6)) {
      return localHostIPV6;
    }
    return "-";
  }

  /**
   * 本机非Loop的IPV6地址
   */
  public static String getLocalHostIPV6() {
    return localHostIPV6;
  }

  /**
   * 本机的所有IPV4的网卡地址
   */
  public static Set<String> getLocalHostIPV4Sets() {
    return localHostIPV4Lists;
  }

  /**
   * 本机的所有IPV6的网卡地址
   */
  public static Set<String> getLocalHostIPV6Sets() {
    return localHostIPV6Lists;
  }

  /**
   * <p>
   * isIP.
   * </p>
   */
  public static boolean isIP(String str) {
    try {
      String[] tokens = StringUtils.split(str, ".");
      if (tokens.length != 4) {
        return false;
      }

      for (int i = 0; i < tokens.length; i++) {
        if (Integer.parseInt(tokens[i]) > 255 || Integer.parseInt(tokens[i]) < 0) {
          return false;
        }
      }

      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * IPV4 转 int
   *
   * @param ip
   */
  public static int Ip4ToInt(String ip) {
    if (!isIP(ip)) {
      return 0;
    }
    String[] segs = StringUtils.split(ip, '.');
    int ipInt = 0;
    for (int i = 0; i < segs.length; i++) {
      int seg = Integer.valueOf(segs[i]);
      ipInt = ipInt | (seg << ((3 - i) * 8));
    }
    return ipInt;
  }

  /**
   * int的值转为 IPV4地址
   *
   * @param ipInt
   */
  public static String intToIPV4(int ipInt) {
    StringBuilder strs = new StringBuilder();
    strs.append(ipInt >>> 24).append(".");
    strs.append((ipInt & 0xFF0000) >> 16).append(".");
    strs.append((ipInt & 0xFF00) >> 8).append(".");
    strs.append(ipInt & 0xFF);
    return strs.toString();
  }
}
