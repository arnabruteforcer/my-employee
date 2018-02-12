package application.factory;

import application.data.EmployeeObject;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class EmployeeObjectFactory implements PooledObjectFactory<EmployeeObject> {

  private int activeCount = 0;
  private long makeLatency = 0;
  private long destroyLatency = 0;
  private boolean exceptionOnDestroy = false;

  @Override
  public PooledObject<EmployeeObject> makeObject() {
    final int maxTotal = 40;
    final long waitLatency;
    synchronized (this) {
      if (activeCount > maxTotal) {
        throw new IllegalStateException(
            "Too many active instances. Total count cannot be greater than " + maxTotal);
      }
      waitLatency = makeLatency;
    }
    if (waitLatency > 0) {
      doWait(waitLatency);
    }
    synchronized (this) {
      activeCount++;
      return new DefaultPooledObject<>(new EmployeeObject.Builder().build());
    }
  }

  @Override
  public void destroyObject(PooledObject<EmployeeObject> pooledObject) throws Exception {
    final long waitLatency;
    final boolean hurl;
    synchronized (this) {
      waitLatency = destroyLatency;
      hurl = exceptionOnDestroy;
    }
    if (waitLatency > 0) {
      doWait(waitLatency);
    }
    synchronized (this) {
      activeCount--;
    }
    if (hurl) {
      throw new Exception();
    }
  }

  @Override
  public boolean validateObject(PooledObject<EmployeeObject> pooledObject) {
      DefaultPooledObject<EmployeeObject> defaultEmployeePoolObject = (DefaultPooledObject<EmployeeObject>) pooledObject;
      EmployeeObject employeeObject = defaultEmployeePoolObject.getObject();
      if (employeeObject.getFirstName().equals("Unsub") || employeeObject.getLastName()
          .equals("Unsub") || employeeObject.getAge() == 0 || employeeObject.getEmployeeId() == 0) {
        return true;
      }
    return false;
  }

  @Override
  public void activateObject(PooledObject<EmployeeObject> pooledObject) {
    // Only required if we are dealing with session or state management.
  }

  @Override
  public void passivateObject(PooledObject<EmployeeObject> pooledObject) {
    if (pooledObject instanceof EmployeeObject) {
      ((EmployeeObject) pooledObject).setFirstName("Unsub");
      ((EmployeeObject) pooledObject).setLastName("Unsub");
      ((EmployeeObject) pooledObject).setAge(0);
      ((EmployeeObject) pooledObject).setEmployeeId(0);
    }
  }

  public synchronized void setMakeLatency(final long makeLatency) {
    this.makeLatency = makeLatency;
  }

  public synchronized void setThrowExceptionOnDestroy(final boolean bool) {
    this.exceptionOnDestroy = bool;
  }

  public synchronized void setDestroyLatency(final long destroyLatency) {
    this.destroyLatency = destroyLatency;
  }

  private void doWait(final long latency) {
    try {
      Thread.sleep(latency);
    } catch (InterruptedException ex) {
      System.out.print("Could not wait, and here's the reason why... ");
      ex.printStackTrace();
    }
  }
}