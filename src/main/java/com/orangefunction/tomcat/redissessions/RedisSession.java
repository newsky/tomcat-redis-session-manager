package com.orangefunction.tomcat.redissessions;

import org.apache.catalina.Manager;
import org.apache.catalina.SessionListener;
import org.apache.catalina.session.StandardSession;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class RedisSession extends StandardSession {

  private static final Log log = LogFactory.getLog(RedisSession.class);

  protected static Boolean manualDirtyTrackingSupportEnabled = false;

  public static void setManualDirtyTrackingSupportEnabled(Boolean enabled) {
    manualDirtyTrackingSupportEnabled = enabled;
  }

  protected static String manualDirtyTrackingAttributeKey = "__changed__";

  public static void setManualDirtyTrackingAttributeKey(String key) {
    manualDirtyTrackingAttributeKey = key;
  }


//  protected HashMap<String, Object> changedAttributes;
  protected Boolean dirty;

  protected int sessionAttributesHash;

  public RedisSession() {
    // TODO Auto-generated constructor stub
    super(null);
    resetDirtyTracking();
  }

  public RedisSession(Manager manager) {
    super(manager);
    resetDirtyTracking();
  }

  public void setSessionAttributesHash(int sessionAttributesHash) {
    this.sessionAttributesHash = sessionAttributesHash;
  }

  public int getSessionAttributesHash() {
    return sessionAttributesHash;
  }

  public Boolean isDirty() {
//    return dirty || !changedAttributes.isEmpty();
    return dirty;
  }

//  public HashMap<String, Object> getChangedAttributes() {
//    return changedAttributes;
//  }

  public void resetDirtyTracking() {
//    changedAttributes = new HashMap<>();
    dirty = false;
  }

  @Override
  public void setAttribute(String key, Object value) {
    if (manualDirtyTrackingSupportEnabled && manualDirtyTrackingAttributeKey.equals(key)) {
      dirty = true;
      return;
    }

    Object oldValue = getAttribute(key);
    super.setAttribute(key, value);

    if ( (value != null || oldValue != null)
         && ( value == null && oldValue != null
              || oldValue == null && value != null
              || !value.getClass().isInstance(oldValue)
              || !value.equals(oldValue) ) ) {
      if (this.manager instanceof RedisSessionManager
          && ((RedisSessionManager)this.manager).getSaveOnChange()) {
        try {
          ((RedisSessionManager)this.manager).save(this, true);
        } catch (IOException ex) {
          log.error("Error saving session on setAttribute (triggered by saveOnChange=true): " + ex.getMessage());
        }
      } else {
//        changedAttributes.put(key, value);
        dirty=true;
      }
    }
  }

  @Override
  public void removeAttribute(String name) {
    super.removeAttribute(name);
    if (this.manager instanceof RedisSessionManager
        && ((RedisSessionManager)this.manager).getSaveOnChange()) {
      try {
        ((RedisSessionManager)this.manager).save(this, true);
      } catch (IOException ex) {
        log.error("Error saving session on setAttribute (triggered by saveOnChange=true): " + ex.getMessage());
      }
    } else {
      dirty = true;
    }
  }

  @Override
  public void setId(String id) {
    // Specifically do not call super(): it's implementation does unexpected things
    // like calling manager.remove(session.id) and manager.add(session).

    this.id = id;
  }

  @Override
  public void setPrincipal(Principal principal) {
    dirty = true;
    super.setPrincipal(principal);
  }

  @Override
  public void writeObjectData(java.io.ObjectOutputStream out) throws IOException {
    super.writeObjectData(out);
    out.writeLong(this.getCreationTime());
  }

  @Override
  public void readObjectData(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
    super.readObjectData(in);
    this.setCreationTime(in.readLong());
  }

  public boolean exclude(String name) {
    return super.exclude(name);
  }
  public void removeAttributeInternal(String name, boolean notify) {
    super.removeAttributeInternal(name, notify);
  }

  public void setLastAccessedTime(long lastAccessedTime) {
    this.lastAccessedTime = lastAccessedTime;
  }

  public void setIsNew(boolean isNew) {
    this.isNew = isNew;
  }

  public void setIsValid(boolean isValid) {
    this.isValid = isValid;
  }

  public void setThisAccessedTime(long thisAccessedTime) {
    this.thisAccessedTime = thisAccessedTime;
  }

  public Map<String, Object> getAttrbutes() {
    return this.attributes;
  }

  public void setAttrbutes(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  public List<SessionListener> getListeners() {
    return listeners;
  }

  public void setListeners(ArrayList<SessionListener> listeners) {
    this.listeners = listeners;
  }

  public Map<String, Object> getNotes() {
    return notes;
  }

  public void setNotes(Map<String, Object> notes) {
    this.notes = notes;
  }

  @Override
  protected boolean isAttributeDistributable(String name, Object value) {
    return true;
  }
}
