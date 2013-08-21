package framework.generic.mybatis.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PropertiesHelper
{
  public static final int SYSTEM_PROPERTIES_MODE_NEVER = 0;
  public static final int SYSTEM_PROPERTIES_MODE_FALLBACK = 1;
  public static final int SYSTEM_PROPERTIES_MODE_OVERRIDE = 2;
  Properties p;
  private int systemPropertiesMode = 0;

  public PropertiesHelper(Properties p) {
    setProperties(p);
  }

  public PropertiesHelper(Properties p, int systemPropertiesMode) {
    setProperties(p);
    if ((systemPropertiesMode != 0) && (systemPropertiesMode != 1) && (systemPropertiesMode != 2)) {
      throw new IllegalArgumentException("error systemPropertiesMode mode:" + systemPropertiesMode);
    }
    this.systemPropertiesMode = systemPropertiesMode;
  }

  public Properties getProperties() {
    return p;
  }

  public void setProperties(Properties props) {
    if (props == null) throw new IllegalArgumentException("properties must be not null");
    p = props;
  }

  public String getRequiredString(String key) {
    String value = getProperty(key);
    if (isBlankString(value)) {
      throw new IllegalStateException("required property is blank by key=" + key);
    }
    return value;
  }

  public String getNullIfBlank(String key) {
    String value = getProperty(key);
    if (isBlankString(value)) {
      return null;
    }
    return value;
  }

  public String getNullIfEmpty(String key) {
    String value = getProperty(key);
    if ((value == null) || ("".equals(value))) {
      return null;
    }
    return value;
  }

  public String getAndTryFromSystem(String key)
  {
    String value = getProperty(key);
    if (isBlankString(value)) {
      value = getSystemProperty(key);
    }
    return value;
  }

  private String getSystemProperty(String key)
  {
    String value = System.getProperty(key);
    if (isBlankString(value)) {
      value = System.getenv(key);
    }
    return value;
  }

  public Integer getInteger(String key) {
    String v = getProperty(key);
    if (v == null) {
      return null;
    }
    return Integer.valueOf(Integer.parseInt(v));
  }

  public int getInt(String key, int defaultValue) {
    if (getProperty(key) == null) {
      return defaultValue;
    }
    return Integer.parseInt(getRequiredString(key));
  }

  public int getRequiredInt(String key) {
    return Integer.parseInt(getRequiredString(key));
  }

  public Long getLong(String key) {
    if (getProperty(key) == null) {
      return null;
    }
    return Long.valueOf(Long.parseLong(getRequiredString(key)));
  }

  public long getLong(String key, long defaultValue) {
    if (getProperty(key) == null) {
      return defaultValue;
    }
    return Long.parseLong(getRequiredString(key));
  }

  public Long getRequiredLong(String key) {
    return Long.valueOf(Long.parseLong(getRequiredString(key)));
  }

  public Boolean getBoolean(String key) {
    if (getProperty(key) == null) {
      return null;
    }
    return Boolean.valueOf(Boolean.parseBoolean(getRequiredString(key)));
  }

  public boolean getBoolean(String key, boolean defaultValue) {
    if (getProperty(key) == null) {
      return defaultValue;
    }
    return Boolean.parseBoolean(getRequiredString(key));
  }

  public boolean getRequiredBoolean(String key) {
    return Boolean.parseBoolean(getRequiredString(key));
  }

  public Float getFloat(String key) {
    if (getProperty(key) == null) {
      return null;
    }
    return Float.valueOf(Float.parseFloat(getRequiredString(key)));
  }

  public float getFloat(String key, float defaultValue) {
    if (getProperty(key) == null) {
      return defaultValue;
    }
    return Float.parseFloat(getRequiredString(key));
  }

  public Float getRequiredFloat(String key) {
    return Float.valueOf(Float.parseFloat(getRequiredString(key)));
  }

  public Double getDouble(String key) {
    if (getProperty(key) == null) {
      return null;
    }
    return Double.valueOf(Double.parseDouble(getRequiredString(key)));
  }

  public double getDouble(String key, double defaultValue) {
    if (getProperty(key) == null) {
      return defaultValue;
    }
    return Double.parseDouble(getRequiredString(key));
  }

  public Double getRequiredDouble(String key) {
    return Double.valueOf(Double.parseDouble(getRequiredString(key)));
  }

  public Object setProperty(String key, int value)
  {
    return setProperty(key, String.valueOf(value));
  }

  public Object setProperty(String key, long value) {
    return setProperty(key, String.valueOf(value));
  }

  public Object setProperty(String key, float value) {
    return setProperty(key, String.valueOf(value));
  }

  public Object setProperty(String key, double value) {
    return setProperty(key, String.valueOf(value));
  }

  public Object setProperty(String key, boolean value) {
    return setProperty(key, String.valueOf(value));
  }

  public String[] getStringArray(String key) {
    String v = p.getProperty(key);
    if (v == null) {
      return new String[0];
    }
    return StringTokenizerUtils.split(v, ",");
  }

  public int[] getIntArray(String key)
  {
    String[] array = getStringArray(key);
    int[] result = new int[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = Integer.parseInt(array[i]);
    }
    return result;
  }

  public String getProperty(String key, String defaultValue)
  {
    return p.getProperty(key, defaultValue);
  }

  public String getProperty(String key) {
    String propVal = null;
    if (systemPropertiesMode == 2) {
      propVal = getSystemProperty(key);
    }
    if (propVal == null) {
      propVal = p.getProperty(key);
    }
    if ((propVal == null) && (systemPropertiesMode == 1)) {
      propVal = getSystemProperty(key);
    }
    return propVal;
  }

  public Object setProperty(String key, String value) {
    return p.setProperty(key, value);
  }

  public void clear() {
    p.clear();
  }

  public Set<Map.Entry<Object, Object>> entrySet() {
    return p.entrySet();
  }

  public Enumeration<?> propertyNames() {
    return p.propertyNames();
  }

  public boolean contains(Object value) {
    return p.contains(value);
  }

  public boolean containsKey(Object key) {
    return p.containsKey(key);
  }

  public boolean containsValue(Object value) {
    return p.containsValue(value);
  }

  public Enumeration<Object> elements() {
    return p.elements();
  }

  public Object get(Object key) {
    return p.get(key);
  }

  public boolean isEmpty() {
    return p.isEmpty();
  }

  public Enumeration<Object> keys() {
    return p.keys();
  }

  public Set<Object> keySet() {
    return p.keySet();
  }

  public void list(PrintStream out) {
    p.list(out);
  }

  public void list(PrintWriter out) {
    p.list(out);
  }

  public void load(InputStream inStream) throws IOException {
    p.load(inStream);
  }

  public void loadFromXML(InputStream in) throws IOException, InvalidPropertiesFormatException
  {
    p.loadFromXML(in);
  }

  public Object put(Object key, Object value) {
    return p.put(key, value);
  }

  public void putAll(Map<? extends Object, ? extends Object> t) {
    p.putAll(t);
  }

  public Object remove(Object key) {
    return p.remove(key);
  }
  /** @deprecated */
  public void save(OutputStream out, String comments) {
    p.save(out, comments);
  }

  public int size() {
    return p.size();
  }

  public void store(OutputStream out, String comments) throws IOException {
    p.store(out, comments);
  }

  public void storeToXML(OutputStream os, String comment, String encoding) throws IOException
  {
    p.storeToXML(os, comment, encoding);
  }

  public void storeToXML(OutputStream os, String comment) throws IOException {
    p.storeToXML(os, comment);
  }

  public Collection<Object> values() {
    return p.values();
  }

  public String toString() {
    return p.toString();
  }

  private static boolean isBlankString(String value) {
    return (value == null) || ("".equals(value.trim()));
  }
}